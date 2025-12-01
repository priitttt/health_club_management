# FitZone - Health and Fitness Club Management System

FitZone is a desktop application for managing a health and fitness club. It provides separate dashboards for Members, Trainers, and Administrators to manage memberships, classes, personal training sessions, health metrics, and billing.

## Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Key Features](#key-features)
- [Usage Guide](#usage-guide)
- [Troubleshooting](#troubleshooting)

## Features

### For Members

- **User Registration & Authentication** - Secure registration and login
- **Dashboard Overview** - View latest health metrics, active goals, upcoming sessions, and registered classes
- **Health Metrics Tracking** - Log and track Weight, Heart Rate, Blood Pressure, Body Fat %, and Steps with automatic unit labels (kg, bpm, mmHg, %, steps)
- **Fitness Goals Management** - Set and track fitness goals with automatic unit labels
- **PT Session Booking** - Book personal training sessions with real-time trainer availability display. Double-click time slots to auto-fill booking form
- **Group Class Registration** - Browse and register for group fitness classes
- **Profile Management** - Update personal information
- **Billing View** - View payment history and bills

### For Trainers

- **Availability Management** - Set and manage availability slots
- **Member Lookup** - View member profiles and information
- **Schedule View** - View upcoming sessions and classes
- **Dashboard** - Overview of sessions, classes, and availability

### For Administrators

- **Room Management** - Manage gym rooms and their availability
- **Class Management** - Create and manage group fitness classes, set schedules, and track capacity
- **Billing & Payments** - Handle member billing and process payments
- **Member & Trainer Management** - Oversee all members and trainers
- **System Overview** - Comprehensive dashboard for club operations

## Technology Stack

- **Language**: Java 20
- **Build Tool**: Maven 3.11.0
- **Database**: PostgreSQL 42.7.8
- **ORM**: Hibernate 7.1.10.Final
- **UI Framework**: Java Swing
- **UI Theme**: FlatLaf 3.3
- **Date Picker**: JCalendar 1.4

## Prerequisites

Before running the application, ensure you have:

1. **Java Development Kit (JDK) 20 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify: `java -version`

2. **Maven 3.11.0 or higher**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Verify: `mvn -version`

3. **PostgreSQL Database**
   - Download from [PostgreSQL Official Site](https://www.postgresql.org/download/)
   - pgAdmin 4 recommended for database management
   - Ensure PostgreSQL server is running

4. **IDE** (Recommended: IntelliJ IDEA)
   - Download from [JetBrains](https://www.jetbrains.com/idea/)

## Installation

### Step 1: Clone the Repository

```bash
git clone https://github.com/priitttt/health_club_management.git
cd health_club_management
```

### Step 2: Create the Database

1. Open **pgAdmin 4**
2. Create a new database named: `health_club_management`
3. Tables will be automatically created by Hibernate on first run

### Step 3: Configure Database Connection

Edit `health_club_management/src/main/resources/hibernate.cfg.xml` and update:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/health_club_management</property>
<property name="hibernate.connection.username">your_username</property>
<property name="hibernate.connection.password">your_password</property>
```

### Step 4: Build the Project

**Using Maven:**
```bash
cd health_club_management
mvn clean install
```

**Using IntelliJ IDEA:**
- Right-click on `pom.xml` → Maven → Reload Project
- Build → Build Project

## Running the Application

### From IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Navigate to `src/main/java/Main.java`
3. Right-click on `Main.java` → Run 'Main.main()'

### From Command Line

```bash
cd health_club_management
mvn compile exec:java -Dexec.mainClass="Main"
```

## Key Features

### Health Metrics with Automatic Unit Labels

The system automatically displays appropriate units for health metrics:
- **Weight**: kg
- **Heart Rate**: bpm
- **Blood Pressure**: mmHg
- **Body Fat %**: %
- **Steps**: steps

Units appear next to input fields, in history tables, and on the dashboard.

### Trainer Availability Display

- Real-time table showing available time slots for selected trainers
- Filters out already-booked sessions
- Shows availability for the next 14 days
- Double-click a slot to auto-fill the booking form
- Validates trainer availability before allowing bookings

### Booking Validation

- Trainers must have availability slots marked as "Available"
- Prevents double-booking of trainers or rooms
- Automatically detects and prevents time slot conflicts
- Clear error messages guide users

## Usage Guide

### First Time Setup

1. **Start PostgreSQL Server** - Ensure PostgreSQL is running
2. **Create Database** - Create `health_club_management` database in pgAdmin 4
3. **Run Application** - Execute `Main.java` (tables will be created automatically)
4. **Create Admin Account** - Register with "Admin" role selected

### For Members

1. Register or login to access your dashboard
2. Log health metrics with automatic unit labels
3. Set fitness goals with appropriate units
4. Book PT sessions by viewing trainer availability and double-clicking time slots
5. Register for group fitness classes
6. Update profile information

### For Trainers

1. Register/login to access trainer dashboard
2. Set availability slots (members can only book when you're marked available)
3. View schedule of upcoming sessions and classes
4. Look up member information

### For Administrators

1. Login to access admin dashboard
2. Manage rooms, classes, billing, and system operations
3. Oversee all members and trainers

## Troubleshooting

### Database Connection Issues

- Verify PostgreSQL is running: `pg_isready`
- Check database credentials in `hibernate.cfg.xml`
- Ensure database `health_club_management` exists
- Check PostgreSQL port (default: 5432)

### Build Issues

- Verify Java 20: `java -version`
- Verify Maven: `mvn -version`
- Clean and rebuild: `mvn clean install`
- Ensure IDE is using correct JDK version

### Runtime Issues

- Check console for error messages
- Verify dependencies are downloaded (run `mvn clean install`)
- Ensure database is accessible from pgAdmin 4

## Contributing

Contributions are welcome! Please:

1. Fork the repository
2. Create a feature branch
3. Make your changes with proper comments
4. Commit with clear messages
5. Push and open a Pull Request

## Support

For issues or questions, open an issue on the GitHub repository.

---

**Important Note**: Create a local database named `health_club_management` in pgAdmin 4 before running the application. The system uses a local database by default.
