# iWaitLess 

Software system aiming to enhance the entire process of ordering, service, and communication in restaurants and entertainment venues. Customers can place orders directly from their table in just a few steps using only a mobile device.

## Demo Video
To view a demonstration of the application running in production mode, please refer to the video mode below:
- administrative module - https://youtu.be/cVU-Kjianc0
- client module - https://youtu.be/wU9TSH-0HlQ

## Features

- *Seamless Ordering:* Customers can place orders quickly from their mobile devices.
- *Efficient Communication:* Enhances communication between customers and restaurant staff.
- *User-Friendly Interface:* Developed using Vaadin and CSS for an intuitive experience.
- *Robust Backend:* Built using Java 21 and Spring Boot for high performance and reliability.
- *Secure and Scalable Database:* MySQL is used to store and manage data efficiently.

## Technologies Used

- *Backend:* Java 21 with Spring Boot framework
- *Frontend:* Vaadin and CSS
- *Database:* MySQL

## Installation & Setup

### Prerequisites
- Java 21 installed
- MySQL Server installed and configured
- Maven installed

### Steps to Run the Application
1. Clone the repository
```
git clone <repository-url>
cd <project-folder>
```
2. Set up the database 

Create a MySQL database and configure connection settings in **application.properties**. All needed data is stored in **create_schema.sql** file.
3. Build the project
```
mvn clean install
```
4. Run the application
```
mvn spring-boot:run
```
5. Access the application
- open a browser and navigate to http://localhost:8080 to access the administration module. There are three roles and different access rights for each. Use the following credentials:
  - admin/password
  - waiter/password
  - kitchen/password
- open a browser and navigate to http://localhost:8080/menu-preview?table=1 to access the client's module

### Configuration

Edit the application.properties file to configure database connection settings:
```
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Edit the directories to which files will be sent:
```
iwaitless.constant.qr.directory=D:/iwaitless/QRCodes/
iwaitless.constant.images.directory=D:/iwaitless/menu-items/
```