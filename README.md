# Smart Order Management System (SOMS)

## Project Overview
The **Smart Order Management System (SOMS)** is a distributed system designed for managing product orders in an e-commerce setup. The system demonstrates how services can collaborate using **Eureka** for service discovery, **gRPC** for inter-service communication, and **Kafka** for event-driven processing.

## Architecture

The SOMS system consists of multiple services, each with specific responsibilities. These services communicate with each other using gRPC and Kafka. The gateway API exposes a **REST** interface to interact with clients.

### Services Overview

#### **User Service**
- **Responsibility**: Manages user profiles and authentication.

#### **Product Service**
- **Responsibility**: Manages product inventory and pricing.

#### **Order Service**
- **Responsibility**: Handles order placement and updates.

#### **Notification Service**
- **Responsibility**: Sends notifications (e.g., email or SMS) for order updates.

#### **Analytics Service**
- **Responsibility**: Generates insights based on order data.

## Communication Flow

- **Service Discovery**: All services are registered with **Eureka Server** for dynamic discovery. Services can locate each other via Eureka, making the system resilient to scaling and service restarts.
  
- **gRPC**: Used for synchronous, efficient communication between services like **Order Service**, **User Service**, and **Product Service**.

- **Kafka**: Handles asynchronous communication for event-driven updates (e.g., publishing order events, consuming them for notifications and analytics).

## Tech Stack

- **Backend**: 
  - Java with **Spring Boot** for gRPC services and Eureka integration.
  - **Kafka** for messaging.

- **Database**: 
  - **PostgreSQL** or **MySQL** for relational data storage.

- **Service Discovery**: 
  - **Eureka Server**.

- **Messaging**: 
  - **Kafka** for event streaming.

## Project Structure

### 1. **Eureka Server**
- Set up a centralized **Eureka Server** for service registration and discovery.

### 2. **User Service**
- **Responsibilities**: Manage user profiles and authentication.

### 3. **Product Service**
- **Responsibilities**: Manage product inventory and pricing.

### 4. **Order Service**
- **Responsibilities**: Handle order placement and updates.

### 5. **Notification Service**
- **Responsibilities**: Send notifications (e.g., email, SMS) based on order events.

### 6. **Analytics Service**
- **Responsibilities**: Generate insights based on order data.

### 7. **Kafka Integration**
- **Responsibilities**: Enable event-driven architecture.

### 8. **Gateway Service (Optional)**
- **Responsibilities**: Expose RESTful APIs to interact with the system.
