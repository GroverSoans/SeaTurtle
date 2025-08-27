# Docker Setup for Inventory Management System

This document explains how to run the Inventory Management System using Docker and Docker Compose.

## Quick Start

### 1. Build and Run All Services

```bash
# Build and start all services
docker-compose up --build


### 2. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:4567
- **Database**: SQLite file stored in `./challenge.db`

### 3. Stop Services

```bash
# Stop all services
docker-compose down


## Service Details

### Backend Service (`inventory-backend`)
- **Port**: 4567
- **Image**: Custom Java 11 + Maven
- **Database**: SQLite with persistent storage
- **Health Check**: `/version` endpoint

### Frontend Service (`inventory-frontend`)
- **Port**: 3000
- **Image**: Custom Next.js 18
- **Build**: Multi-stage build for optimization
- **Health Check**: Root endpoint

## Docker Commands

### View Running Services
```bash
docker-compose ps
```

### View Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend

# Follow logs in real-time
docker-compose logs -f
```

### Rebuild Specific Service
```bash
# Rebuild and restart backend only
docker-compose up --build backend

# Rebuild and restart frontend only
docker-compose up --build frontend
```

