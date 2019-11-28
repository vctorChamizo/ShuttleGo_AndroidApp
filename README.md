# ShuttleGo-Android

Most of the traffic between european airports and hotels is caused by
private vehicles, it usually causes important traffic jams between these two.
Providing a low cost and efficient access to airports has become a fundamental
mobility issue for cities, so some minority transport methods are being promoted.

Shuttle bus is one of these new initiatives, which has become popular
during the last years, making companies offer this new type of service as an
alternative.

The goal of this bachelor thesis is showing a smartphone app that solves
the user’s problem when booking these kind of services, giving portability,
simplicity and quickness to the experience, properties that cannot be found in other
similar apps. This application works for both ways, passengers and drivers.
Although the project started as an idea only for airports, it’s scalable and can be
used to solve another transport situations.

## Content
 - Project report.
 - Source code server.
 - Source code client.
 - APK application.
 
## Authors: 
 - Carlos Castellanos Mateo.
 - Víctor Chamizo Rodriguez.
 
 # Software Engineering - Point Of Sale


## Introduction

This project aims to create an application that helps to understand and apply the main problems that software engineering tries to solve by applying the necessary patterns and algorithms to successfully achieve that goal.

Software engineering definition:

*"The application of a systematic,disciplined,quantifiable approach to the development,operation and maintenance of software; that is, the application of engineering to software"* — **IEEE Systems** 

The project applies and satisfies the following features:

   - Requirements engineering.
   - Software maintenance and evolution
   - Advanced multitier architecture design patterns
   - Domain store pattern in use: JPA
   - Advanced UML
   - Object-oriented design patterns
   - Metamodels, UML profiles and model-driven development


## Documentation

The objective of the application that manages the use of a Point of Sale Terminal in an easy and simple way for the user, through a database that contains the information of both the clients that will access the services of the application as of the products that are available for sale.

The application will have several roles that will have different permits. These roles are: administrator, client and employee.

The functionality common to all roles is as follows: products may be asked questions such as the price of the item, if it is in stock, the quantity of products purchased, make a return of the item ...

The administrator role will have the ability to administer the product system, being able to add, delete and modify them. You can also manage customer data registered in the POS.

In addition, the administrator will have access to the data of the employees registered in the system, manage the associated departments and manage their schedules.


![Point Of Sale](https://i.pinimg.com/originals/48/13/76/4813768a889df6c6182df49fe7476cd5.gif)


## Projects

Two projects have been developed with identical functionality but with differences in their architecture with the aim of implementing two different patterns. These patterns are the use of Data-Access-Object (DAO) and Java-Persistence-API (JPA) in the services and integration layer of the application.

On the other hand, the presentation layer of the application has been developed with the Model-View-Controller (MVC) pattern.

Next, the differences between the architectures are flashed.

### DAO-Project
- Data Access Object (DAO): Allows access to the data layer.
data, providing representations oriented to
objects. Each module contains a DAO.

### JPA-Project
   - JPA (Java Persistence API): Manage entities through
   of the entity manager, which is the persistence manager of the
   JPA

   - Business Objects (BO): Contains a set of attributes, values, operations and relationships with other objects
   of the business, whose objective is to shape the behavior of the business layer. Each module has an object class
   of business.

### Setup
   - The DAO prohecto is into the directory: /src/dao_project
   - The JPA prohecto is into the directory: /src/jpa_project


## Technologies

The implementation of this project was carried out using:

   - **Java** to implement the application's logic and integration sections.
   - **SQL** to implement the data base logic.
   

## Author

The project has been carried out by [Victor Chamizo](https://github.com/vctorChamizo).

Happy coding! 💻

