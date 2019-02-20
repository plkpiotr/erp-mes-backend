# <a name="0">ERP-MES (Back-end)</a>

- [Overview](#1)
- [Back-end technologies](#2)
- [ERP features](#3)
  - [Staff management](#3.1)
  - [Delivery and warehouse management](#3.2)
  - [Finance management and reporting](#3.3)
  - [Production planning](#3.4)
  - [Mailbox](#3.5)
- [MES features](#4)
  - [Key Performance Indicators](#4.1)
  - [Instant Messenger](#4.2)
  - [Kaizen Teian](#4.3)
  - [Kanban Board](#4.4)
  - [Task scheduling algorithm](#4.5)
- [Security](#5)
- [Project structure](#6)
- [Example local configuration](#7)
- [Front-end repository](#8)
- [Heroku platform](#9)


## <a name="1">Overview</a> [&#8250;&#8250;&#8250;](#0)

Back-end layer of RESTful web service created as engineering thesis by [@patsaf](https://github.com/patsaf) and [@plkpiotr](https://github.com/plkpiotr).
 
ERP-MES is intended for management of a forwarding company.

The application was equipped with features typical of Enterprise Resource Planning and Manufacturing Execution System.

## <a name="2">Back-end technologies</a> [&#8250;&#8250;&#8250;](#0)

- Java 8
- Spring Framework (MVC, Security, Data JPA)
- Spring Boot (Mail, Websocket)
- JSON Web Token
- JUnit
- Lombok
- Hibernate
- PostgreSQL 9
- Maven 4
- Heroku

## <a name="3">ERP features</a> [&#8250;&#8250;&#8250;](#0)

### <a name="3.1">Staff management</a> [&#8250;](#3)

The company staff is divided into managers and employees, each of whom is the application's user. In their own profile, each user can, for example, submit holiday requests. Managers can approve such requests for their subordinates. Basic contact information may be found in a user's profile. ![profill](https://user-images.githubusercontent.com/18569675/53121758-35a4dd00-3555-11e9-9c48-90f9d862d370.PNG)

### <a name="3.2">Delivery and warehouse management</a> [&#8250;](#3)

Keeping track of the items stored in the warehouse. Automatic generation of recommended deliveries, based on customer demand and lean management principles. All online store operations (orders, complaints, returns) are immediately reflected on the warehouse state.

### <a name="3.3">Finance management and reporting</a> [&#8250;](#3)

Storing, updating and analysing all the company's financial operations. Automatic generation of monthly reports. Financial estimates for a given period of time (by default: month), which are calculated based on data gatheres by previous reports. ![report](https://user-images.githubusercontent.com/18569675/53122608-43f3f880-3557-11e9-9616-6117540c2ecf.PNG)

### <a name="3.4">Production planning</a> [&#8250;](#3)

Monitoring if the amount of work planned for a given day does not exceed the assumed daily plan and if so - notifying the person responsible. Possibility to introduce special production plans. Making sure all orders, complaints and returns are resolved without delays. ![plan](https://user-images.githubusercontent.com/18569675/53122888-0e034400-3558-11e9-9ff7-9cc5790fe72b.PNG) 

### <a name="3.5">Mailbox</a> [&#8250;](#3)

Automatic generation of first login password and sending it via e-mail when registering a new user. Automatic notifications of order/complaint/return status change sent to customers. Possibility of e-mail communication between the company and its customers.

![mailbox](https://user-images.githubusercontent.com/18569675/53123037-6e928100-3558-11e9-8b30-827bdf7d9eda.PNG)

## <a name="4">MES features</a> [&#8250;&#8250;&#8250;](#0)

### <a name="4.1">Key Performance Indicators</a> [&#8250;](#4)

Evaluation of the work-in-progress expressed in mean times, number of tasks and suggestions by category - for one employee and the whole team: ![kpi](https://user-images.githubusercontent.com/21959354/52904777-f7e04580-3230-11e9-85f4-cde2b4736bb7.png)

### <a name="4.2">Instant Messenger</a> [&#8250;](#4)

Providing communication between employees in real time and documenting time and author's initials: ![chat](https://user-images.githubusercontent.com/21959354/52904775-f747af00-3230-11e9-9019-f2dc7d7730cf.png)

### <a name="4.3">Kaizen Teian</a> [&#8250;](#4)

Employee suggestion system with possibility of searching records:![suggestions](https://user-images.githubusercontent.com/21959354/52904778-f7e04580-3230-11e9-8096-c36101a698ff.png)

### <a name="4.4">Kanban Board</a> [&#8250;](#4)

Visualization of tasks created in the last four weeks for on person: ![kanban](https://user-images.githubusercontent.com/21959354/52904776-f7e04580-3230-11e9-9d3d-affa11a61646.PNG)

### <a name="4.5">Task scheduling algorithm</a> [&#8250;](#4)

Planning and reduction of total time allowed for tasks through scheduling algorithm: ![task](https://user-images.githubusercontent.com/21959354/52904779-f7e04580-3230-11e9-8328-4e4e3657c149.png)

## <a name="5">Security</a> [&#8250;&#8250;&#8250;](#0)

- Only registered users can acces the application.
- Access is granted based on the JWT token sent as request header.
- Upon first login attempt, a first login password is used and the user needs to set their own password.
- Each request is filtered by Spring Security mechanisms and access to given resources is granted based on the user's role in the company.

## <a name="6">Project structure</a> [&#8250;&#8250;&#8250;](#0)

```
└────src
    ├───main
    │   ├───java
    │   │   └───com
    │   │       └───herokuapp
    │   │           └───erpmesbackend
    │   │               └───erpmesbackend
    │   │                   ├───communication
    │   │                   │   ├───config
    │   │                   │   ├───controller
    │   │                   │   ├───dto
    │   │                   │   ├───factory
    │   │                   │   ├───model
    │   │                   │   ├───repository
    │   │                   │   ├───request
    │   │                   │   └───service
    │   │                   ├───exceptions
    │   │                   ├───production
    │   │                   │   ├───controller
    │   │                   │   ├───dto
    │   │                   │   ├───factory
    │   │                   │   ├───model
    │   │                   │   ├───repository
    │   │                   │   ├───request
    │   │                   │   └───service
    │   │                   ├───security
    │   │                   ├───shop
    │   │                   │   ├───controller
    │   │                   │   ├───factory
    │   │                   │   ├───model
    │   │                   │   ├───repository
    │   │                   │   ├───request
    │   │                   │   └───service
    │   │                   └───staff
    │   │                       ├───controller
    │   │                       ├───dto
    │   │                       ├───factory
    │   │                       ├───model
    │   │                       ├───repository
    │   │                       ├───request
    │   │                       └───service
    │   └───resources
    │       └───db
    │           └───changelog
    └───test
        └───java
            └───com
                └───herokuapp
                    └───erpmesbackend
                        └───erpmesbackend
                            └───erpmesbackend
                                └───controllers
```

## <a name="7">Example local configuration</a> [&#8250;&#8250;&#8250;](#0)

File application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/erpmes
spring.datasource.username=postgres
spring.datasource.password=postgres1
spring.jpa.hibernate.ddl-auto=create
```

## <a name="8">Front-end repository</a> [&#8250;&#8250;&#8250;](#0)

Find front-end repository on Github: [plkpiotr/erp-mes-frontend](https://github.com/plkpiotr/erp-mes-frontend)

## <a name="9">Heroku platform</a> [&#8250;&#8250;&#8250;](#0)

Check out ERP-MES: [erp-mes-backend.herokuapp.com](https://erp-mes-backend.herokuapp.com/) and [erp-mes-frontend.herokuapp.com](https://erp-mes-frontend.herokuapp.com/).

Visit both of the links. First of them launches back-end layer and the database (if it is inactive) while the second contains front-end layer with login form.

Then use one of the following data:

Email: `patrycja@erp-mes.pl`

Password: `haslo123`

or

Email: `piotr@erp-mes.pl`

Password: `haslo123`
