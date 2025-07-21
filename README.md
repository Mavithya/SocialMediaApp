# 🌐 Socialite - Spring Boot Social Media App

A lightweight social media web application built using **Spring Boot**, **PostgreSQL**, and **Thymeleaf**. Developed as a group project for **CSCI 22082 - Object-Oriented Programming II (2022/2023)**.

---

## 📌 Project Overview

This application allows users to:
- Register and log in securely
- Create and view text-only posts
- Maintain session state with logout functionality
- Send friend requests, accept/decline them, and like/unlike posts

The project adheres to:
- **MVC architecture**
- **OOP** and **SOLID** design principles
- Best GitHub collaboration practices

---

## 🛠️ Technologies Used

### ✅ Backend
- Java 21
- Spring Boot (Web, Security, JPA, Validation)
- PostgreSQL

### ✅ Frontend
- Thymeleaf (template engine)
- Tailwind CSS (CSS framework)

### ✅ Tools
- Spring Security
- Hibernate / JPA
- BCrypt password hashing
- Git & GitHub for version control

---

## ✨ Features

### 🔐 Authentication
- Login and Registration pages
- Form validation and error handling
- BCrypt password encryption
- Spring Security session handling

### 🏠 Home Feed
- Create text-only posts
- View own posts
- Display post content, timestamp, and author

### 🔓 Logout
- Logout button visible on all authenticated pages
- Invalidates session and redirects to login

---

## 🌟 Optional Features 

### 👥 Friend Management
- Browse user directory
- Send, accept, or decline friend requests
- View friends’ posts merged in feed

### 👍 Like/Unlike Posts
- Like or unlike posts with dynamic updates (AJAX)
- Visual indicators and like counters

---

## ⚙️ Setup Instructions

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/SpringBootSocialMediaApp/social-media-app.git
cd social-media-app
````
## 📽️ Demo Video

🎥 **Watch the demo of the working application:**  
🔗 https://drive.google.com/drive/folders/1xhJijODwpacX5V8anaed78QoDAzqWlog

The demo includes:
- ✅ Login & registration
- ✅ Post creation and feed updates
- ✅ Database operations in real time
- ✅ Friends and likes feature

---

## 🔄 GitHub Workflow

- **Main Branch Protection:** No direct pushes to `main`
- **Feature Branches:** Named like `feature/user-auth`, `feature/posts`
- **Pull Requests:**
  - Every feature or bugfix is submitted as a PR
  - PRs contain clear descriptions of changes
  - Peer review before merging is encouraged
- **Commits:**
  - Use clear and meaningful commit messages  
    e.g., `Add post validation logic`

---

## 🧠 Design & Principles Followed

- ✅ **MVC Architecture**
- ✅ **DTOs** for request/response objects
- ✅ **OOP Principles:** Inheritance, Encapsulation, Polymorphism
  
