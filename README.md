## Overview
System where you can add,update and retrieve Employee details from MongoDB
<br>
Used -SpringBoot,mongodb,redis
<br>
Used Spring MVC architecture to build it
<br>
Used Rest Controller concept to hit the API from Postman
<br>
Used redis to temporary store the data in cache.
## API endpoints
Add Employee-
URL: /employee/,
Method: POST,
Request Body: JSON
<br>
Update Employee-
URL: /employee/{id},
Method: PUT,
Request Body: JSON
<br>
Retrieve All Employees-
URL: /employee/,
Method: GET
<br>
Retrieve Employees by Criteria-
URL: /employee/search,
Method: GET,
Query Parameters: id, designation, city, name

## Structure
```
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── demo
│   │   │               ├── controller
│   │   │               │   └── MyController.java
│   │   │               ├── models
│   │   │               │   ├── Employee.java
│   │   │               │   ├── QA.java
│   │   │               │   ├── Development.java
│   │   │               │   └── Manager.java
│   │   │               └── service
│   │   │                   └── CacheService.java
│   │   └── resources
│   │       └── application.properties
└── pom.xml
```
