# Spring Boot WebFlux with Reactive MongoDB Integration

This is a learning project that demonstrates how to integrate **Spring Boot WebFlux** with **Reactive MongoDB** using Spring Data MongoDB. The project is designed to be reactive and non-blocking, making use of Spring WebFlux for asynchronous processing and MongoDB's reactive API.

## Features

- **Spring Boot** 3.x (with WebFlux)
- **Reactive MongoDB** Integration
- **Asynchronous Data Access**
- Simple CRUD operations (Create, Read, Update, Delete)
- Use of **Mono** and **Flux** for reactive programming

## Technologies Used

- **Spring Boot 3.x**
- **Spring WebFlux**
- **Spring Data MongoDB Reactive**
- **MongoDB 4.x or later**
- **Project Reactor** (Mono & Flux)
- **Lombok** (for boilerplate code reduction)

## Prerequisites

To run this project, you need to have the following:

- **Java 17** or higher
- **MongoDB** instance running locally or remotely (you can also use MongoDB Atlas)
- **Maven**

## Project Setup

### 1. Clone the repository

```bash
git clone https://github.com/khansohail425/reactive-spring-mongodb.git
cd spring-boot-webflux-reactive-mongodb
```

## 2. Configure MongoDB Connection

In your application.properties (or application.yml), configure the MongoDB connection settings. For a local instance, it might look something like this:
````
spring.data.mongodb.uri=mongodb://localhost:27017/yourdb
````

## 3. Build the Project

````bash
mvn clean install
````

