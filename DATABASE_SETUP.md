# Database Setup and Automatic Updates Guide

## Overview
This project uses PostgreSQL with Flyway migrations for automatic database schema updates. When you pull new code from Git, the database will automatically update to match the latest schema.

## Prerequisites
1. **PostgreSQL 16.1** installed and running
2. **Database named `socialdb`** created
3. **User `postgres` with password `1234`** (or update `application.properties` with your credentials)

## How It Works for Team Development

### 1. Automatic Database Updates
- **Flyway migrations** in `src/main/resources/db/migration/` automatically run when you start the application
- The application will check for new migration files and apply them in order
- Your database will always be up-to-date with the latest schema changes

### 2. Migration Files
Current migrations:
- `V2__fix_foreign_key_violations.sql` - Fixes initial database issues
- `V4__ensure_username_uniqueness.sql` - Adds unique constraints for usernames

### 3. For New Team Members
When you clone this repository:
1. Install PostgreSQL 16.1
2. Create database: `CREATE DATABASE socialdb;`
3. Update `src/main/resources/application.properties` if needed
4. Run: `./mvnw.cmd spring-boot:run` (Windows) or `./mvnw spring-boot:run` (Mac/Linux)
5. Database tables will be created automatically

### 4. For Existing Team Members
When you pull new code:
1. Simply run: `./mvnw.cmd spring-boot:run`
2. New migrations will apply automatically
3. No manual database changes needed

## Database Configuration

### Current Settings (application.properties)
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/socialdb
spring.datasource.username=postgres
spring.datasource.password=1234
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=false
spring.flyway.out-of-order=true
```

### User Entity Constraints
- **Email**: Unique constraint (prevents duplicate emails)
- **Username**: Unique constraint (auto-generated from email, prevents duplicates)
- **Password**: Encrypted using BCrypt

## Features Added in Latest Update

### Enhanced Profile Management
1. **Dynamic Profile Updates**: Profile changes update UI immediately without page refresh
2. **Email Validation**: Prevents duplicate email addresses
3. **Username Sync**: Username automatically updates when email changes
4. **AJAX Endpoints**: Smooth user experience with real-time updates

### New API Endpoints
- `GET /profile/current` - Get current user data
- `POST /profile/update-ajax` - Update profile via AJAX

### Frontend Improvements
- Modal pre-populates with current user data
- Real-time UI updates after profile changes
- Success/error notifications
- Responsive design improvements

## Troubleshooting

### If Migrations Fail
1. Check PostgreSQL is running
2. Verify database connection settings
3. Ensure database `socialdb` exists
4. Check PostgreSQL logs for detailed errors

### If Database Schema Issues
The application uses these fallback strategies:
- `spring.flyway.baseline-on-migrate=true` - Creates baseline for existing databases
- `spring.flyway.validate-on-migrate=false` - Allows checksum mismatches
- `spring.flyway.out-of-order=true` - Applies migrations flexibly

### Manual Database Reset (if needed)
```sql
DROP DATABASE socialdb;
CREATE DATABASE socialdb;
```
Then restart the application.

## Development Workflow
1. **Pull latest code**: `git pull origin main`
2. **Start application**: `./mvnw.cmd spring-boot:run`
3. **Database updates automatically**
4. **Access application**: http://localhost:8080
5. **Make changes and commit**: Database migrations included

## Support
If you encounter issues:
1. Check this README
2. Verify PostgreSQL connection
3. Check application logs
4. Contact team lead for assistance
