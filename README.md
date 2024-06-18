# Hotel Management System

The Hotel Management System is designed to streamline operations for both hotel employees and guests. It offers functionalities for the customers, i.e., searching the available rooms, reservations, customer check-ins and check-outs, and invoice generation.

## Authors

- Omar Albatran - 1221344 [@albatranomar](https://www.github.com/albatranomar)
- Baha Al-khateeb - 1221131 [@bahafn](https://www.github.com/bahafn)


## ERD
![erd](https://github.com/albatranomar/project2-hotel-management-system/blob/main/hotel-management-system-erd.png?raw=true)


## Resources

- **Stock** represents the stock in the inventory. It has attributes such as stock ID, name, description, category, cost, selling price, and quantity.

- **Warehouse** represents the locations where inventory stocks are stored or managed. It have attributes such as warehouse ID, name, address, description, capacity

- **Manager** represents the person that manages a single warehouse.

- **Provider** information about the providers(suppliers) whom products are purchased. It has attributes such as provider ID, name, contact information.

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
## Feedback

- 

