// WebSocket Notification Client
class NotificationWebSocket {
    constructor() {
        this.stompClient = null;
        this.connected = false;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 5000; // 5 seconds
        this.heartbeatInterval = null;

        this.init();
    }

    init() {
        // Add delay to ensure page is fully loaded
        setTimeout(() => {
            this.connect();
            this.setupHeartbeat();
        }, 1000);
    }

    connect() {
        console.log('Attempting to connect to WebSocket...');

        // Use SockJS for WebSocket connection
        const socket = new SockJS('/ws');
        this.stompClient = Stomp.over(socket);

        // Enable debug logs to see what's happening
        this.stompClient.debug = (str) => {
            console.log('STOMP Debug:', str);
        };

        const connectCallback = (frame) => {
            console.log('✅ Connected to WebSocket successfully:', frame);
            this.connected = true;
            this.reconnectAttempts = 0;
            this.subscribeToNotifications();
        };

        const errorCallback = (error) => {
            console.error('❌ WebSocket connection error:', error);
            this.connected = false;
            this.handleReconnect();
        };

        // Add headers for authentication
        const headers = {};

        // Get CSRF token
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        this.stompClient.connect(headers, connectCallback, errorCallback);
    }

    subscribeToNotifications() {
        if (!this.stompClient || !this.connected) {
            console.error('❌ Cannot subscribe - not connected to WebSocket');
            return;
        }

        console.log('📡 Subscribing to notification channels...');

        // Subscribe to individual notifications
        this.stompClient.subscribe('/user/queue/notifications', (message) => {
            console.log('🔔 New notification received via WebSocket:', message.body);
            try {
                const notification = JSON.parse(message.body);
                this.handleNewNotification(notification);
            } catch (error) {
                console.error('❌ Error parsing notification message:', error);
            }
        });

        // Subscribe to notification count updates
        this.stompClient.subscribe('/user/queue/notification-count', (message) => {
            console.log('📊 Notification count update received:', message.body);
            try {
                const count = parseInt(message.body);
                this.updateNotificationCount(count);
            } catch (error) {
                console.error('❌ Error parsing notification count:', error);
            }
        });

        console.log('✅ Subscribed to notification channels successfully');
    }

    handleNewNotification(notification) {
        console.log('🔔 Processing new notification:', notification);

        // Show browser notification if permission is granted
        this.showBrowserNotification(notification);

        // Update UI with new notification
        this.addNotificationToUI(notification);

        // Play notification sound (optional)
        this.playNotificationSound();

        // Also trigger a manual refresh of the notification dropdown
        this.refreshNotificationDropdown();
    }

    refreshNotificationDropdown() {
        // Trigger the notification loading function if it exists
        if (typeof loadNotifications === 'function') {
            console.log('🔄 Refreshing notification dropdown...');
            loadNotifications();
        } else {
            console.log('⚠️ loadNotifications function not found');
        }
    }

    showBrowserNotification(notification) {
        if (Notification.permission === 'granted') {
            const options = {
                body: notification.message,
                icon: '/static/css/notification-icon.png',
                badge: '/static/css/badge-icon.png',
                tag: `notification-${notification.id}`,
                requireInteraction: false,
                silent: false
            };

            new Notification('New Notification', options);
        } else if (Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    this.showBrowserNotification(notification);
                }
            });
        }
    }

    addNotificationToUI(notification) {
        console.log('🎨 Adding notification to UI:', notification);

        // Remove loading indicator if present
        const loadingIndicator = document.querySelector('.notifications-loading');
        if (loadingIndicator) {
            loadingIndicator.remove();
        }

        // Add notification to the notification dropdown/list
        const notificationsList = document.getElementById('notifications-list') ||
                                 document.getElementById('notifications-container') ||
                                 document.getElementById('notificationsContainer');

        if (notificationsList) {
            const notificationElement = this.createNotificationElement(notification);
            notificationsList.insertBefore(notificationElement, notificationsList.firstChild);
            console.log('✅ Notification added to UI successfully');
        } else {
            console.error('❌ Notification container not found in DOM');
        }
    }

    createNotificationElement(notification) {
        const div = document.createElement('div');
        div.className = `notification-item p-4 hover:bg-gray-50 cursor-pointer ${!notification.isRead ? 'bg-blue-50 border-l-4 border-blue-500' : ''} border-b border-gray-200`;
        div.setAttribute('data-notification-id', notification.id);

        const timeAgo = this.getTimeAgo(new Date(notification.createdAt));

        div.innerHTML = `
            <div class="flex items-start space-x-3">
                <div class="flex-shrink-0">
                    ${this.getNotificationIcon(notification.type)}
                </div>
                <div class="flex-1 min-w-0">
                    <p class="text-sm font-medium text-gray-900 break-words">
                        ${notification.message}
                    </p>
                    <p class="text-xs text-gray-500 mt-1">${timeAgo}</p>
                </div>
                ${!notification.isRead ? '<div class="w-2 h-2 bg-blue-500 rounded-full"></div>' : ''}
            </div>
        `;

        // Add click handler to mark as read
        div.addEventListener('click', () => {
            this.markNotificationAsRead(notification.id);
        });

        return div;
    }

    getNotificationIcon(type) {
        const iconClasses = "w-8 h-8 p-1.5 rounded-full text-white";

        switch (type) {
            case 'POST_LIKED':
                return `<div class="${iconClasses} bg-red-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M3.172 5.172a4 4 0 015.656 0L10 6.343l1.172-1.171a4 4 0 115.656 5.656L10 17.657l-6.828-6.829a4 4 0 010-5.656z" clip-rule="evenodd"/>
                            </svg>
                        </div>`;
            case 'POST_COMMENTED':
                return `<div class="${iconClasses} bg-blue-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M18 10c0 3.866-3.582 7-8 7a8.841 8.841 0 01-4.083-.98L2 17l1.338-3.123C2.493 12.767 2 11.434 2 10c0-3.866 3.582-7 8-7s8 3.134 8 7zM7 9H5v2h2V9zm8 0h-2v2h2V9zM9 9h2v2H9V9z" clip-rule="evenodd"/>
                            </svg>
                        </div>`;
            case 'FRIEND_REQUEST_RECEIVED':
                return `<div class="${iconClasses} bg-green-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z"/>
                            </svg>
                        </div>`;
            default:
                return `<div class="${iconClasses} bg-gray-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z" clip-rule="evenodd"/>
                            </svg>
                        </div>`;
        }
    }

    updateNotificationCount(count) {
        console.log('📊 Updating notification count to:', count);

        // Update notification badge
        const badge = document.getElementById('notification-badge') || document.getElementById('notificationBadge');
        if (badge) {
            if (count > 0) {
                badge.textContent = count > 99 ? '99+' : count;
                badge.classList.remove('hidden');
                console.log('✅ Notification badge updated and shown');
            } else {
                badge.classList.add('hidden');
                console.log('✅ Notification badge hidden (no notifications)');
            }
        } else {
            console.error('❌ Notification badge element not found');
        }

        // Update notification count in title if needed
        this.updatePageTitle(count);
    }

    updatePageTitle(count) {
        const baseTitle = 'Social Media App';
        if (count > 0) {
            document.title = `(${count}) ${baseTitle}`;
        } else {
            document.title = baseTitle;
        }
    }

    markNotificationAsRead(notificationId) {
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

        const headers = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        fetch(`/api/notifications/${notificationId}/read`, {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                console.log('✅ Notification marked as read');
                // Update UI to show notification as read
                const notificationElement = document.querySelector(`[data-notification-id="${notificationId}"]`);
                if (notificationElement) {
                    notificationElement.classList.remove('bg-blue-50', 'border-l-4', 'border-blue-500');
                    const unreadIndicator = notificationElement.querySelector('.bg-blue-500');
                    if (unreadIndicator) {
                        unreadIndicator.remove();
                    }
                }
            }
        })
        .catch(error => console.error('❌ Error marking notification as read:', error));
    }

    playNotificationSound() {
        // Optional: Play a subtle notification sound
        try {
            const audio = new Audio('/static/css/notification-sound.mp3');
            audio.volume = 0.3;
            audio.play().catch(e => {
                // Ignore errors if sound cannot be played
            });
        } catch (e) {
            // Ignore sound errors
        }
    }

    setupHeartbeat() {
        // Send ping every 30 seconds to keep connection alive
        this.heartbeatInterval = setInterval(() => {
            if (this.connected && this.stompClient) {
                this.stompClient.send('/app/ping', {}, 'ping');
            }
        }, 30000);
    }

    handleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`🔄 Attempting to reconnect... (${this.reconnectAttempts}/${this.maxReconnectAttempts})`);

            setTimeout(() => {
                this.connect();
            }, this.reconnectInterval);
        } else {
            console.error('❌ Max reconnection attempts reached. Please refresh the page.');
            this.showReconnectionError();
        }
    }

    showReconnectionError() {
        // Show a user-friendly message about connection issues
        const errorDiv = document.createElement('div');
        errorDiv.className = 'fixed top-4 right-4 bg-red-500 text-white p-3 rounded-lg shadow-lg z-50';
        errorDiv.innerHTML = `
            <div class="flex items-center">
                <span>Connection lost. Please refresh the page.</span>
                <button onclick="location.reload()" class="ml-3 bg-red-600 hover:bg-red-700 px-2 py-1 rounded text-sm">
                    Refresh
                </button>
            </div>
        `;
        document.body.appendChild(errorDiv);
    }

    getTimeAgo(date) {
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);

        if (diffInSeconds < 60) return 'Just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
        if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;

        return date.toLocaleDateString();
    }

    disconnect() {
        if (this.stompClient) {
            this.stompClient.disconnect();
        }
        if (this.heartbeatInterval) {
            clearInterval(this.heartbeatInterval);
        }
        this.connected = false;
    }
}

// Initialize WebSocket connection when page loads
let notificationWS;

document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Page loaded, initializing notification system...');

    // Only initialize if user is authenticated
    const authMarker = document.querySelector('[data-user-authenticated="true"]');
    const userEmail = document.querySelector('#currentUserEmail');

    if (authMarker && userEmail && userEmail.value) {
        console.log('✅ User authenticated, starting WebSocket connection...');
        notificationWS = new NotificationWebSocket();

        // Request notification permission
        if ('Notification' in window && Notification.permission === 'default') {
            Notification.requestPermission();
        }
    } else {
        console.log('⚠️ User not authenticated or user email not found');
    }
});

// Clean up connection when page unloads
window.addEventListener('beforeunload', function() {
    if (notificationWS) {
        notificationWS.disconnect();
    }
});
