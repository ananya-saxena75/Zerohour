# 🚀 ZeroHour – AI Deadline Guardian

ZeroHour is an AI-powered productivity companion that helps students and professionals manage deadlines intelligently. It prioritizes tasks using AI, syncs events with Google Calendar, and provides a centralized dashboard to prevent missed deadlines.

## 🌟 Features

- ✅ AI-powered task prioritization using Gemini AI
- 📅 Google Calendar integration
- ⏰ Deadline tracking with overdue alerts
- 📊 Interactive productivity dashboard
- 📝 Task management (Create, Update, Delete)
- 🎯 Priority-based organization
- 🔄 Real-time backend API integration
- 💾 MySQL database support

---

## 🛠 Tech Stack

### Frontend
- React.js
- HTML5
- CSS3
- JavaScript

### Backend
- Java
- Spring Boot
- Spring Data JPA
- REST APIs

### Database
- MySQL

### APIs
- Google Calendar API
- Gemini AI API

### Tools
- Git
- GitHub
- Vercel
- Maven
- Postman

---

## 📂 Project Structure

```
ZeroHour/
│
├── frontend/          # React Frontend
│
├── backend/           # Spring Boot Backend
│
└── README.md
```

---

## ⚙️ Installation

### Clone Repository

```bash
git clone https://github.com/ananya-saxena75/Zerohour.git

cd Zerohour
```

---

### Backend Setup

```bash
cd backend
```

Configure `application.properties`

```properties
spring.datasource.url=YOUR_DATABASE_URL
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD

gemini.api.key=YOUR_GEMINI_KEY

google.calendar.client-id=YOUR_CLIENT_ID
google.calendar.client-secret=YOUR_CLIENT_SECRET
```

Run

```bash
./mvnw spring-boot:run
```

Backend runs on

```
http://localhost:8080
```

---

### Frontend Setup

```bash
cd frontend

npm install

npm start
```

Frontend runs on

```
http://localhost:3000
```

---

## 📸 Screenshots

> Add screenshots of:

- Dashboard
- AI Prioritization
- Task Management
- Google Calendar Integration

---

## 🎥 Demo

Live Frontend

https://zerohour-coral.vercel.app/

Demo Video



---

## 💡 Future Enhancements

- Team collaboration
- Email reminders
- Mobile application
- Analytics dashboard
- AI productivity reports
- Push notifications

---

## 👩‍💻 Developer

**Ananya Saxena**

B.Tech Artificial Intelligence & Data Science

GitHub

https://github.com/ananya-saxena75

---

## 📄 License

Developed as a Hackathon Project.
