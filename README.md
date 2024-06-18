# Hotel Management System

The Hotel Management System is designed to streamline operations for both hotel employees and guests. It offers functionalities for the customers, i.e., searching the available rooms, reservations, customer check-ins and check-outs, and invoice generation.

## Authors

- Omar Albatran - 1221344 [@albatranomar](https://www.github.com/albatranomar)
- Baha Al-khateeb - 1221131 [@bahafn](https://www.github.com/bahafn)


## ERD
![erd](https://github.com/albatranomar/project2-hotel-management-system/blob/main/hotel-management-system-erd.png?raw=true)


## Resources

- **User** represents an account of an Admin or Customer in the system. It has attributes to identify the account holder such as email, first name, last name, phone number, and the role (Admin or Customer) of the account in the system.

- **Room** represents a room on the hotel. It has the following attributes: room's number, type, status (Available or Reserved), capacity, and cost.

- **Room Feature** represents a feature that can be in a room. Other than the ID, it only holds the name of the feature

- **Room Facility** represents a facility that can be in a room. Other than the ID, it only holds the name of the facility.

- **Booking** represents a reservation of a room by a customer. It has attributes about the reservation details such as check in and out date, number of adults and children the rooms are reserved for, and the status of the reservation. It also has relationshup attributes such as the customer's id and the payment's id.

- **Payment** represents a payment for a specific booking. It has attributes such as payment status (Pending or Paid) and the bill which is the total cost of the booking.

- **Employee** represents a hotel worker. It has attributes such as first name, last name, date of birth, geneder, phone number, email, and salary.

- **HouseKeeping** represents a task done or to be done in a room by an employee. It has attributes to describe the task such as the task (describtion), the date the task was done or to be done, and status of the task.

## Run Locally

Clone the project

```bash
  git clone https://github.com/albatranomar/project2-hotel-management-system.git
```

Go to the project directory

```bash
  cd project2-hotel-management-system/application
```

Start (Docker)

```bash
  docker-compose up
```

 Update your `application.yml` to match this

 ```yml
version: "3.7"
name: hotel-management-system
services:
  mysqldb:
    image: "mysql:8.0"
    restart: always
    ports:
      - "3306:3306"
    networks:
      - hotelsystem-net
    environment:
      MYSQL_DATABASE: hotelDB
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
      MYSQL_ROOT_PASSWORD: root
  api_service:
    build: .
    restart: always
    ports:
      - "8080:8080"
    networks:
      - hotelsystem-net
    environment:
      - spring.datasource.url=jdbc:mysql://mysqldb:3306/hotelDB?allowPublicKeyRetrieval=true
      - spring.datasource.password=admin
      - spring.datasource.username=admin
    depends_on:
      - mysqldb

    volumes:
      - .m2:/root/.m2
networks:
  hotelsystem-net:
```

You are ready to go!
## Deployment (Docker Hub)

In the application directory

```bash
  docker-compose build
```

Add tag

```bash
docker tag hotel-management-system-api_service {OWNER}/{REPOSITORY}
```

Push to docker hub
```bash
docker push {OWNER}/{REPOSITORY}
```


After this anyone wants to run the app can make just an `docker-compose.yml`

```yml
version: "3.7"
name: hotel-management-system
services:
  mysqldb:
    image: "mysql:8.0"
    restart: always
    ports:
      - "3306:3306"
    networks:
      - hotelsystem-net
    environment:
      MYSQL_DATABASE: hotelDB
      MYSQL_USER: admin
      MYSQL_PASSWORD: admin
      MYSQL_ROOT_PASSWORD: root
  api_service:
    image: {OWNER}/{REPOSITORY}
    restart: always
    ports:
      - "8080:8080"
    networks:
      - hotelsystem-net
    environment:
      - spring.datasource.url=jdbc:mysql://mysqldb:3306/hotelDB?allowPublicKeyRetrieval=true
      - spring.datasource.password=admin
      - spring.datasource.username=admin
    depends_on:
      - mysqldb

    volumes:
      - .m2:/root/.m2
networks:
  hotelsystem-net:
```

[The docker repository](https://hub.docker.com/r/omaralbatran1221344/project2-hotel-management-system) 
## What we learned

During this project we learned many fundamental concepts in creating Web Services and APIs. These concepts include the most important steps of a development cycle, including: development, testing, and deployment. Since we needed a working backend that meets the need of a simple hotel managment, we needed to go through the entire process of creating this system which we used the Code First approach for. We also then needed to test the API where we learned to create complete testing scenarios for the APIs. And we finally created docker files to deploy our app which we uploaded to docker hub. This forced us to learn how to use docker and debug its errors and problems that came along.
