# Purchase Approval Application

## Overview
This is a simple full-stack web application for processing purchase approvals. It consists of:

- **Backend**: A Spring Boot API that handles purchase approval logic.
- **Frontend**: A React application using Bootstrap for styling, allowing users to submit purchase requests.

---

## Features
- Users can submit a purchase request with **Personal ID, Requested Amount, and Payment Period**.
- The system evaluates the request and returns an **approval decision**.
- The UI displays the approval result with a **Bootstrap-styled form**.

---

## Technologies Used
### Backend
- Java 17
- Spring Boot 3
- Spring Web
- Lombok
- Gradle
- PostgreSQL
- H2 (for testing)

### Frontend
- React 18
- Bootstrap 5
- Axios (for API requests)

---

## Installation & Setup
### Backend Setup
1. Navigate to the backend project directory:
   ```sh
   cd backend
   ```
2. Build and run the application:
   ```sh
   ./gradlew bootRun
   ```
3. The backend API will be available at:
   ```
   http://localhost:8080/api/purchase/apply
   ```

### Frontend Setup
1. Navigate to the frontend directory:
   ```sh
   cd src/frontend
   ```
2. Install dependencies:
   ```sh
   npm install
   ```
3. Start the React application:
   ```sh
   npm start
   ```
4. The frontend will run at:
   ```
   http://localhost:3000
   ```

---

## API Endpoint
### `POST /api/purchase/apply`
#### Request Body (JSON):
```json
{
  "personalId": "12345678934",
  "requestedAmount": 5000,
  "paymentPeriodMonths": 12
}
```
#### Response (JSON):
```json
{
  "approved": true,
  "approvedAmount": 5000
}
```

---

## Folder Structure
```
PurchaseApproval/
│── backend/               # Spring Boot backend
│   ├── src/main/java/...  # Java source code
│   ├── build.gradle       # Gradle dependencies
│   ├── settings.gradle    # Gradle settings
│   └── ...
│
│── frontend/              # React frontend
│   ├── src/
│   │   ├── components/    # React components
│   │   ├── App.js         # Main React app
│   │   ├── index.js       # Entry point
│   │   └── ...
│   ├── public/
│   ├── package.json       # Frontend dependencies
│   └── ...
│
└── README.md              # Project documentation
```

---

## Troubleshooting

### If `axios` Fails
Ensure `axios` is installed in your frontend:
```sh
npm install axios
```

---

## Possible future Enhancements
### 1. Persistent Storage with ORM
- Introduce a data model with entities such as Customer, Purchase, and PurchaseApplication.
- Store purchase application results using an ORM framework to track customer purchases and applications.
- Implement database migrations and data consistency checks.

### 2. Security and Authentication
- Implement user authentication and client registration.
- Restrict access based on user roles (e.g., customers vs. operators).

### 3. Internal Back Office for Operators
- Develop an admin panel where operators can review purchase applications.
- Implement role-based access control.
- Add approval and rejection workflows for manual operator review.

### 4. Frontend Features
- Improve validation for invalid input fields.
- Improve error handling for backend failures.
- Enhance UI with user-friendly feedback and interactive components.

---

## Author
Artūrs Ļebedjko 


