// Notification Handler for loading user-specific notifications

document.addEventListener('DOMContentLoaded', function() {
    // Get notification button and dropdown elements
    const notificationBtn = document.getElementById('notificationBtn');
    const notificationDropdown = document.getElementById('notificationDropdown');
    const notificationsContainer = document.getElementById('notifications-container');
    const notificationsLoading = document.getElementById('notifications-loading');
    const notificationsEmpty = document.getElementById('notifications-empty');
    const markAllReadBtn = document.getElementById('markAllReadBtn');

    // Track if dropdown is open
    let isDropdownOpen = false;

    // Track if notifications have been loaded
    let notificationsLoaded = false;

    // Add click event to notification button
    if (notificationBtn) {
        notificationBtn.addEventListener('click', function() {
            console.log('Notification button clicked');
            loadNotifications();
        });
    }

    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        if (isDropdownOpen && notificationDropdown && !notificationDropdown.contains(e.target) &&
            notificationBtn && !notificationBtn.contains(e.target)) {
            isDropdownOpen = false;
        }
    });

    // Add click event to "Mark all as read" button
    if (markAllReadBtn) {
        markAllReadBtn.addEventListener('click', function(e) {
            markAllNotificationsAsRead();
            e.stopPropagation(); // Prevent dropdown from closing
        });
    }

    // Function to load user notifications
    function loadNotifications() {
        // Show loading state
        if (notificationsLoading) {
            notificationsLoading.classList.remove('hidden');
        }

        // Hide empty state and clear notifications container
        if (notificationsEmpty) {
            notificationsEmpty.classList.add('hidden');
        }

        if (notificationsContainer) {
            notificationsContainer.innerHTML = '';
        }

        // Get CSRF token for secure requests
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

        const headers = {
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        // Fetch notifications from the server
        console.log('Fetching notifications from: /api/notifications/unread');
        console.log('Headers:', JSON.stringify(headers));

        fetch('/api/notifications/unread', {
            method: 'GET',
            headers: headers
        })
        .then(response => {
            console.log('Response status:', response.status);
            console.log('Response headers:', JSON.stringify([...response.headers.entries()]));
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log('Notifications loaded:', Array.isArray(data) ? data.length : 0);

            // Hide loading state
            if (notificationsLoading) {
                notificationsLoading.classList.add('hidden');
            }

            // Process notifications
            if (data && data.length > 0) {
                displayNotifications(data);
            } else {
                // Show empty state if no notifications
                if (notificationsEmpty) {
                    notificationsEmpty.classList.remove('hidden');
                }
            }

            notificationsLoaded = true;
        })
        .catch(error => {
            console.error('Error loading notifications:', error);

            // Hide loading state and show error message
            if (notificationsLoading) {
                notificationsLoading.classList.add('hidden');
            }

            if (notificationsContainer) {
                notificationsContainer.innerHTML = `
                    <div class="text-center py-8 px-4">
                        <p class="text-red-500 font-medium">Error loading notifications</p>
                        <p class="text-gray-500 text-sm mt-2">Please try again later</p>
                    </div>
                `;
            }
        });
    }

    // Function to display notifications in the dropdown
    function displayNotifications(notifications) {
        if (!notificationsContainer) return;

        notificationsContainer.innerHTML = '';

        notifications.forEach(notification => {
            const notificationElement = createNotificationElement(notification);
            notificationsContainer.appendChild(notificationElement);
        });
    }

    // Function to create a notification element
    function createNotificationElement(notification) {
        const div = document.createElement('div');
        div.className = `notification-item p-4 hover:bg-gray-50 cursor-pointer ${!notification.isRead ? 'bg-blue-50 border-l-4 border-blue-500' : ''} border-b border-gray-200`;
        div.setAttribute('data-notification-id', notification.id);

        const timeAgo = getTimeAgo(new Date(notification.createdAt));

        div.innerHTML = `
            <div class="flex items-start space-x-3">
                <div class="flex-shrink-0">
                    ${getNotificationIcon(notification.type)}
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

        // Add click handler to mark as read and navigate if applicable
        div.addEventListener('click', () => {
            markNotificationAsRead(notification.id);

            // If the notification has a link, navigate to it
            if (notification.link) {
                window.location.href = notification.link;
            }
        });

        return div;
    }

    // Function to get notification icon based on type
    function getNotificationIcon(type) {
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
            case 'FRIEND_REQUEST_ACCEPTED':
                return `<div class="${iconClasses} bg-purple-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path d="M9 6a3 3 0 11-6 0 3 3 0 016 0zM17 6a3 3 0 11-6 0 3 3 0 016 0zM12.93 17c.046-.327.07-.66.07-1a6.97 6.97 0 00-1.5-4.33A5 5 0 0119 16v1h-6.07zM6 11a5 5 0 015 5v1H1v-1a5 5 0 015-5z"/>
                            </svg>
                        </div>`;
            case 'POST_SHARED':
                return `<div class="${iconClasses} bg-indigo-500">
                            <svg fill="currentColor" viewBox="0 0 20 20">
                                <path d="M15 8a3 3 0 10-2.977-2.63l-4.94 2.47a3 3 0 100 4.319l4.94 2.47a3 3 0 10.895-1.789l-4.94-2.47a3.027 3.027 0 000-.74l4.94-2.47C13.456 7.68 14.19 8 15 8z"/>
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

    // Function to mark a notification as read
    function markNotificationAsRead(notificationId) {
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
                console.log('Notification marked as read');
                // Update UI to show notification as read
                const notificationElement = document.querySelector(`[data-notification-id="${notificationId}"]`);
                if (notificationElement) {
                    notificationElement.classList.remove('bg-blue-50', 'border-l-4', 'border-blue-500');
                    const unreadIndicator = notificationElement.querySelector('.bg-blue-500');
                    if (unreadIndicator) {
                        unreadIndicator.remove();
                    }
                }

                // Update notification count
                updateNotificationCount();
            }
        })
        .catch(error => console.error('Error marking notification as read:', error));
    }

    // Function to mark all notifications as read
    function markAllNotificationsAsRead() {
        const csrfToken = document.querySelector("meta[name='_csrf']")?.getAttribute("content");
        const csrfHeader = document.querySelector("meta[name='_csrf_header']")?.getAttribute("content");

        const headers = {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };

        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }

        fetch('/api/notifications/read-all', {
            method: 'POST',
            headers: headers
        })
        .then(response => {
            if (response.ok) {
                console.log('All notifications marked as read');

                // Update UI to show all notifications as read
                const unreadNotifications = document.querySelectorAll('.notification-item.bg-blue-50');
                unreadNotifications.forEach(notification => {
                    notification.classList.remove('bg-blue-50', 'border-l-4', 'border-blue-500');
                    const unreadIndicator = notification.querySelector('.bg-blue-500');
                    if (unreadIndicator) {
                        unreadIndicator.remove();
                    }
                });

                // Hide notification badge
                const badge = document.getElementById('notification-badge');
                if (badge) {
                    badge.classList.add('hidden');
                }
            }
        })
        .catch(error => console.error('Error marking all notifications as read:', error));
    }

    // Function to update notification count badge
    function updateNotificationCount() {
        fetch('/api/notifications/count', {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(payload => {
            const count = typeof payload === 'object' && payload ? payload.unreadCount : 0;
            const badge = document.getElementById('notification-badge');
            if (badge) {
                if (count > 0) {
                    badge.textContent = count > 99 ? '99+' : count;
                    badge.classList.remove('hidden');
                } else {
                    badge.classList.add('hidden');
                }
            }
        })
        .catch(error => console.error('Error updating notification count:', error));
    }

    // Helper function to format time ago
    function getTimeAgo(date) {
        const now = new Date();
        const diffInSeconds = Math.floor((now - date) / 1000);

        if (diffInSeconds < 60) return 'Just now';
        if (diffInSeconds < 3600) return `${Math.floor(diffInSeconds / 60)}m ago`;
        if (diffInSeconds < 86400) return `${Math.floor(diffInSeconds / 3600)}h ago`;
        if (diffInSeconds < 604800) return `${Math.floor(diffInSeconds / 86400)}d ago`;

        return date.toLocaleDateString();
    }

    // Ensure loading spinner is hidden by default
    if (notificationsLoading && !notificationsLoading.classList.contains('hidden')) {
        notificationsLoading.classList.add('hidden');
    }

    // Update badge on load
    try { updateNotificationCount(); } catch (e) { /* noop */ }

    // Make loadNotifications function available globally
    window.loadNotifications = loadNotifications;
});
