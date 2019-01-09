#  iRate for Managing Customer Ratings

## Project Overview
iRate is a social media application that encourages theater customers to rate a movie that they saw at the theater in the past 7 days and write a short review. This project provides database for an application like iRate.

#### Extended Description
 Theater customers after watching a movie can write a short review for people who wants information about it. Other customers can vote one review of a particular movie as "helpful" each day. The writer of the top-rated review of a movie written 3 days earlier receives a free movie ticket, and "like" is closed for all reviews of the movie written 3 days ago. Someone who voted one or more movie reviews as "helpful" on a given day will be chosen to receive a free concession item. This database provides all features of this application.
 
#### Team 
* **Freddy Lyu** 
* **Joe He** 

#### Technologies Built With

* [MySQL](https://www.mysql.com/) - Used to develop database
* [InteliJ](https://www.jetbrains.com/idea/) - Used to develop Java part of the project
* [MySQLWorkbench](https://www.mysql.com/products/workbench/) - Used to generate ER Diagram


## Project Design
We have designed Customer, Movie, Review, Attendance, Endorsement five tables in total to build the database.

In each table, there are both primary keys and foreign keys serving as constraint check to meet the application's requirement.
* In Customer table, we have set CustomerID as the primary key.
* In Movie table, similarly its MovieID acts as the primary key.
* In Review table, the ReviewID serves as the primary key. 

    CustomerID and MovieID are foreign keys with **ON DELETE CASCADE**. They make it happen that if a customer is deleted, all of his or her reviews are deleted.
    
* In Attendance table, we have added a new column of AttendanceID to play the role as primary key. 
    
    With MovieID and CustomerID being the foreign key with **ON DELETE CASCADE**, it can implement the feature that a customer's attendance log will be deleted once he/she is deleted.
* In Endorsement table, all three ReviewID, CustomerID, and EndorsementDate together have became the primary key. 

    ReviewID and CustomerID again are foreign keys with **ON DELETE CASCADE**. They allow the endorsement to be deleted if a customer is deleted.
    
    **Below please refer to the schema to find more specific design information.**

#### Schema for iRate Database![image](https://github.ccs.neu.edu/2018FACS5200SV/project-1-lxheqiao/blob/master/ER%20Diagram.png)

##### Customer
* CustomerID: primary key, an auto incremented integer
* Date: a Date type data representing the registration date of the application
* EmailAddress: a string of up to 45 characters
* CustomerName: a string of up to 45 characters

A trigger **BEF INSERT EmailChecking** has been created for this table to check the input email address is valid.
- It checks email such as missing "@" such as 123gmail.com

##### Movie
* MovieID: primary key, an auto incremented integer
* Title: a string of up to 64 characters


##### Attendance
* AttendanceID: primary key, an auto incremented integer
* AttendanceDate: a Date type data representing when the customer will watch the movie he/she bought the ticket for
* MovieID: foreign key referencing to the Movie table
* CustomerID: foreign key referencing to the Customer table

##### Review
* ReviewID: primary key, an auto incremented integer
* ReviewDate: a Date type data representing the time a customer writes a review
* Rating: an integer referring to how satisfactory a customer is to the movie he/she has watched
* Review: a text data, the content a customer writes for a movie
* MovieID: foreign key referencing to the Movie table
* CustomerID: foreign key referencing to the Customer table

A trigger **BEF INSERT ReviewChecking** has been created for this table to check:

**1>** there is **only one** movie review per customer, and **2>** the date of the review must be **within 7 days** of the most recent attendance of the movie. 



##### Endorsement
* EndorsementDate: primary key, a Date type data representing when a customer endorses some review
* ReviewID: primary key, and foreign key referencing to the Review table
* CustomerID: primary key, and foreign key referencing to the Customer table

A trigger **BEF INSERT EndorsementChecking** has been created for this table to check:

**1>** a customer cannot endorse his or her own review, **2>** a customer's current endorsement of a review for a movie must be at least one day after the customer's endorsement of a review for the same movie, and **3>** voting is closed for all reviews of the movie written three days ago.


## Running the tests
* #### Setup
**1. Download iRate project to a local folder, import it to InteliJ after unzipping it.**

**2. Make sure MySQL and Java works well on your laptop. If not, then read the following part.**

1> Download **MySQL** at [here](https://dev.mysql.com/downloads/mysql/), scrolling down to choose the fittest part for your machine. 

2> Download **MySQL Connectors** at [here](https://dev.mysql.com/downloads/connector/j/). Version **Platform Independent** would be fine or you can make it as your wish. After doing this, add this jar to the project library. Please remember the user and password. Current password for the program is empty.

**3. Navigate to Run.java and run the main method.** Please remember to input your own password at each file in src folder. Those places are an empty string for now. If you do not set up a password, then the program should be able to run directly. The code snippet looks like the following one.
```java
static String password = "";
```


* #### Running Tests and Interpret the Output

All results will be printed to console with clear segmentation and detailed prompt information. Technically, there are two parts of our program: 

1> Create the database, triggers, and stored procedures

2> Tests for the iRate database 

* #### Testing strategy
In general, we have made 5 steps to ensure that our program works as expected.

**STEP1:** Successfully createing tables, trigger, and procedures.

**STEP2:** Able to insert all valid data into created database.

**STEP3:** Calling procedures to query specific data.

**STEP4:** Testing if invalid data can be inserted.
 
**STEP5:** Checking once some data is deleted, whether or not other         related data could be deleted too.

* #### Output Interpretation
Interpert the printed result(test result)
Messages from different part will be sepereted by 
"**************************************************************"

1> For the first part of printed result， we try to build our database, tables, triggers and procedures.
If they are built successfully, a message like "Created entity table Customer" will be printed.

2> For the second part, we try to insert some data into our database. We deliberately designed some invalid data in our input. So some warning messages will be printed on the screen.
Finally, every table and their contents will be showed on the screen.

3> For the third part, we try to do some query:
 * The free ticket winner is: 
The writer of the top-rated review of a movie written 3 days earlier.

 * The free gift winner is:
Someone who voted one or more movie reviews as "helpful" on a given day.

 * The registered customer is:
How many customers exist in out database.
This part tested our stored procedures.

4> For the fourth part, we try to insert some invalid data into our database.
Every invalid data and the reason why it is not allowed to be insetted into the database is printed.
This test tested our triggers, and foriegn key constraints.

5> For the fifth part, we try to delete some data in our database.
We print the tables before and after delete operation for your convenience to see the difference. 
So in this way, we can know that all its related data is being deleted.
## Major Design Decisions, Limitations, and Future Work
#### Major Design Decisions
##### How data would be read in?
We have written a function to parse data from files(.txt) into our created tables.

##### How to implement our database?
* Five tables to be made up of the basic content of the iRate database.
* Foreign constraints used to realize features such as one piece of data deleted so that other related data would be deleted either.
* Triggers to implement more features such as a customer can only endorse his/her review.

#### Limitations
##### Security
* At present only a piece of DML would even drop the database. 
* Database can only be backed up mannually.
* Data reading in is exposable to users.
##### Convenience
* User interface is not that user-friendly.

#### Future Work
Upon time constraint, data sets are still limited, so the efficiency would need to be improved if needed. For the **limitations** aforementioned, we need to apply into more knowledge and efforts to upgrade current version.



  


