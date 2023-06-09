**The Asset Management Digital Challenge**
==========================================
*Transfer Money Between Two Accounts*

This is a simple REST API service which handles money transactions between two accounts. Project is build with gradle based on SpringBoot, Spring MVC, Lombok. JUnit 4 and AssertJ frameworks are used for testing.

# Components of the System

1. Account Service serves Account based functions to create and read accounts and perform credit/debit operations.

*Parameters of Account object:*

* accountID: Unique ID of account
* balance: Available account funds

*Classes of Account service:*

* Account model
* AccountsRepositoryInMemory in memory repository holds Account objects
* AccountsService service component
* AccountController REST Controller component

2. Transfer Service serves money transfer between two account.

*Parameters of transfer object:*

* accountFrom: Source account
* accountTo : Destination account
* amount : amount to transfer from accountFrom to accountTo.

*Classes of Transfer service:*

* Transfer model
* TransfersRepositoryInMemory in memory repository holds Transfer objects per Account.
* TransferService-service component
* TransferController-REST Controller component
* EmailNotificationService-sends notification mails both accounts if the transaction committed.

# Tech Stack Used:

* Java 8
* Gradle
* Spring Boot
* Spring MVC
* JUnit 4 is used for testing (Over 90% coverage).

# Data Storage:

* Accounts in transfer operation are synchronized in their Id order to avoid deadlock.
* ConcurrentHashMap is used to support thread-safe storage for Accounts.

# How to Run Tests:

1. Commandline:

* gradle(w) test

2. IDE (IntelliJ or Eclipse)

* Open project
* Run Tests in DWSTest (IntelliJ)
* Run As > JUnit Test (Eclipse)
