# <a name="0">ERP-MES (Back-end)</a>

- [Overview](#1)
- [Back-end technologies](#2)
- [ERP features](#3)
  - [One of them](#3.1)
- [MES features](#4)
  - [Key Performance Indicators](#4.1)
  - [Instant Messenger](#4.2)
  - [Kaizen Teian](#4.3)
  - [Kanban Board](#4.4)
  - [Task scheduling algorithm](#4.5)
- [Project structure](#5)
- [Example local configuration](#6)
- [Front-end repository](#7)
- [Heroku platform](#8)


## <a name="1">Overview</a> [&#8250;&#8250;&#8250;](#0)

Back-end layer of RESTful web service created as engineering thesis by [@patsaf](https://github.com/patsaf) and [@plkpiotr](https://github.com/plkpiotr). ERP-MES is intended for management of a forwarding company. The application was equipped with features typical of Enterprise Resource Planning and Manufacturing Execution System.

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

### <a name="3.1">One of them</a> [&#8250;](#3)

[one of them]

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

## <a name="5">Project structure</a> [&#8250;&#8250;&#8250;](#0)

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

## <a name="6">Example local configuration</a> [&#8250;&#8250;&#8250;](#0)

File application.properties:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/erpmes
spring.datasource.username=postgres
spring.datasource.password=postgres1
spring.jpa.hibernate.ddl-auto=create
```

## <a name="7">Front-end repository</a> [&#8250;&#8250;&#8250;](#0)

Find front-end repository on Github: [plkpiotr/erp-mes-frontend](https://github.com/plkpiotr/erp-mes-frontend)

## <a name="8">Heroku platform</a> [&#8250;&#8250;&#8250;](#0)

Check out ERP-MES: [erp-mes-backend.herokuapp.com](https://erp-mes-backend.herokuapp.com/) and [erp-mes-frontend.herokuapp.com](https://erp-mes-frontend.herokuapp.com/).

Visit both of the links. First of them launches back-end layer and the database (if is inactive) while the second contains front-end layer with login form.

Then use one of the following data:

Email: `patrycja@erp-mes.pl`

Password: `haslo123`

or

Email: `piotr@erp-mes.pl`

Password: `haslo123`
