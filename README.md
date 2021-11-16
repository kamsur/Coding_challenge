**Dev Challenge (Short)**

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

# Further improvements

* In memory repository will be converted to Spring JPA CRUD repository with a transactional DBMS (such as MySQL).
* More tests for TransferController will be implemented to increase the coverage to 100%.
* JUnit will be upgraded to version 5.8, old Gradle commands will be updated.
* Code coverage and static analysis (SonarQube) will be added.
* Additional scenario based functional tests will be performed with Cucumber.
* Controllers will be configured to respond accordingly with custom error messages.
* EmailNotifications will be saved to a queue as Events which will be consumed by a service subscribed for the event type to remove dependency and reduce transaction time.
* Frontend UI will be provided to test system manually.
* Login with OAuth2 SSO
* Currency support for Accounts
* Time based exchange rates for transfers
* Global Date-time support for Transfer object
* Future transfer with reservation
* Transfer between other bank accounts.
* Warnings and deprecated Gradle features will be removed.
