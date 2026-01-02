# 🚲 AutoCare Hub – Smart Bike Service Booking App

AutoCare Hub is a **smart Android-based bike service booking application** designed to simplify how bike owners discover workshops, compare services, and book maintenance appointments. The app bridges the gap between **customers** and **service providers** by offering a transparent, reliable, and user-friendly platform.

---

## 📌 Project Overview

Bike owners often struggle with finding reliable workshops, comparing service costs, and managing maintenance schedules. Traditional booking systems are inefficient and lack transparency.

**AutoCare Hub** solves these problems by providing:

* Multiple workshop listings
* Service & spare part comparisons
* Online & offline booking
* Notifications and reminders
* Ratings & feedback system

The app is built using **Android Studio (Kotlin/XML)** for the mobile application and **VS Code** for backend/API development.

---

## 🎯 Objectives

* Browse and compare multiple bike workshops
* View services, spare parts, rates, availability, and reviews
* Search, filter, and sort workshops
* Book service appointments
* Maintain complete service history
* Enable providers to manage bookings and earnings
* Support offline usage with automatic cloud sync
* Secure authentication with role-based access

---

## 🛠️ Features

### 🔐 User Authentication

* Login & Signup (Email/Phone)
* Role-based access: **Customer** & **Service Provider**

### 🏪 Workshop Management

* Providers create and manage workshop profiles
* Upload images, services, and rates
* Customers browse and compare workshops

### 🔧 Services & Spare Parts

* Providers add services and spare parts with images and pricing
* Customers can search, filter, and view details

### 📅 Booking & Appointments

* Book service appointments easily
* Automatic reminders sent **1 hour before booking**

### ⭐ Ratings & Feedback

* Customers rate workshops and leave reviews
* Ratings visible to other users for transparency

### 📜 Service History

* **Customers:** Past services, payments, feedback
* **Providers:** Bookings, completed services, earnings, ratings

### 📡 Offline + Online Sync

* Offline storage using **Room Database (SQLite)**
* Automatic sync with **Firebase Firestore** when online

### 🔔 Notifications

* Booking confirmations
* Appointment reminders
* Status updates via **Firebase Cloud Messaging (FCM)**

---

## 🧰 Tools & Technologies

### 📱 Frontend (Android App)

* Kotlin
* XML Layouts
* Android Studio

### 💾 Local Storage

* Room Database (SQLite)

### ☁️ Cloud & Backend

* Firebase Authentication
* Firebase Firestore
* Firebase Storage

### 🔔 Notifications

* Firebase Cloud Messaging (FCM)

### 🖼️ Image Handling

* Glide / Coil
* Firebase Storage

### 🧑‍💻 Backend APIs

* PHP (developed using VS Code)

---

## 📂 Project Structure

```
Auto-Care-MobileApp/
│── app/                    # Android application source code
│── semester_api/           # PHP backend APIs
│── semester_project_db.sql # Database schema
│── gradle/                 # Gradle configuration
│── README.md               # Project documentation
```

---

## 🚀 Expected Outcomes

* Fully functional Android bike service booking app
* Transparent workshop comparison system
* Smooth booking and notification flow
* Reliable offline + online data synchronization
* Secure authentication and role-based access
* Improved user experience with ratings & feedback

---

## 📌 Conclusion

AutoCare Hub modernizes bike maintenance management by creating a transparent and efficient marketplace for bike services. With features like workshop comparison, offline support, real-time notifications, and service history tracking, the app ensures convenience and reliability for both customers and service providers.

---

## 🎥 Video Tutorial

🎬 Watch the full AutoCare Hub walkthrough:

🔗 **[▶️ Watch on Google Drive](https://drive.google.com/file/d/1lWX7Fo93SGEl0LE5V1O_tPtCSmBj30jp/preview)**

---

## 📄 License

This project is developed for **academic purposes** as part of a Mobile Application Development course.

---

⭐ *If you find this project useful, consider giving the repository a star!*
