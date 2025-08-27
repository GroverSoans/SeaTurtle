# Docker Commands

## Run the Application

```bash
# Build and start all services
docker-compose up --build

# Start services in background
docker-compose up -d --build
```

## Stop the Application

```bash
# Stop all services
docker-compose down

# Stop and remove containers, networks, and volumes
docker-compose down --volumes --remove-orphans
```

