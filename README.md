# hka-infm-letmecook

![Repository Banner](https://i.imgflip.com/74bvex.png)

Repository for the seminar paper on "Monitoring of Applications with Micrometer" supervised by Professor Vogelsang at Karlsruhe University of Applied Sciences.
The goal of this repository is to provide a sample application for Micrometer in combination with Prometheus.

## Required Installation

- Java 17 or higher
- Gradle
- Docker
- Docker-compose

## Getting Started
The following steps are required for a deployment via docker containers.
1. Navigate to the applications folder
2. `./gradlew build`
3. `docker-compose up`

To stop the application write `docker-compose down`.
