import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This program creates a database for iRate movie application.
 * There are tables for Customer, Movie, Review, Attendance and Endorsement.
 */
public class Creation {
  static Connection conn; // connect to the database
  static Statement stmt; // statement is channel for sending commands through connection
  static String driver = "com.mysql.jdbc.Driver";
  static String url = "jdbc:mysql://localhost/irate";
  static String user = "root";
  static String password = "";

  /**
   * This method creates the database of iRate.
   */
  public static void createDatabase() {
    // Once you have created a database,
    // you do not need to run it every time you run the program.
    try {
      conn = DriverManager.getConnection("jdbc:mysql://localhost/", user, password);
      stmt = conn.createStatement();
      String dropSql = "DROP database irate";
      try {
        stmt.executeUpdate(dropSql);
        System.out.println(dropSql);
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
      String createSql = "CREATE database irate";
      try {
        stmt.executeUpdate(createSql);
        System.out.println(createSql);
      } catch (SQLException ex) {
        System.out.println(ex.getMessage());
      }
      stmt.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method creates all tables in the project.
   */
  public static void createTables() {

    // Tables created by this program
    String[] dbTables = {"Attendance", "Endorsement", "Review", "Customer", "Movie"};

    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();

      //drop the database tables and recreate them below
      for (String tb1 : dbTables) {
        try {
          stmt.executeUpdate("DROP TABLE " + tb1);
          System.out.println("Dropped table " + tb1);
        } catch (SQLException ex) {
          System.out.println("Did not drop table " + tb1);
        }
      }

      //create the Customer table
      String createTable_Customer =
              "CREATE TABLE IF NOT EXISTS irate.Customer (" +
                      "  CustomerID INT NOT NULL AUTO_INCREMENT," +
                      "  Date DATE NOT NULL," +
                      "  EmailAddress VARCHAR(45) NOT NULL," +
                      "  CustomerName VARCHAR(45) NOT NULL," +
                      "  PRIMARY KEY (CustomerID))";
      stmt.executeUpdate(createTable_Customer);
      System.out.println("Created entity table Customer");

      //create the Movie table
      String createTable_Movie =
              "CREATE TABLE IF NOT EXISTS irate.Movie (" +
                      "  Title VARCHAR(64) NOT NULL," +
                      "  MovieID INT NOT NULL AUTO_INCREMENT," +
                      "  PRIMARY KEY (MovieID))";
      stmt.executeUpdate(createTable_Movie);
      System.out.println("Created entity table Movie");

      //create the Attendance table
      String createTable_Attendance =
              "CREATE TABLE IF NOT EXISTS irate.Attendance (" +
                      "  AttendanceID INT NOT NULL AUTO_INCREMENT," +
                      "  MovieID INT NOT NULL," +
                      "  AttendanceDate DATE NOT NULL," + //this date must smaller than CustomerDate
                      "  CustomerID INT NOT NULL," +
                      "  PRIMARY KEY (AttendanceID)," +
                      "  FOREIGN KEY (CustomerID) " +
                      "  REFERENCES irate.Customer(CustomerID) ON DELETE CASCADE," +
                      "  FOREIGN KEY (MovieID) " +
                      "  REFERENCES irate.Movie(MovieID) ON DELETE CASCADE)";
      stmt.executeUpdate(createTable_Attendance);
      System.out.println("Created entity table Attendance");

      //create the Review table
      String createTable_Review =
              "CREATE TABLE IF NOT EXISTS irate.Review (" +
                      "  CustomerID INT NOT NULL," +
                      "  MovieID INT NOT NULL," +
                      "  ReviewDate DATE NOT NULL," +
                      "  Rating INT NOT NULL," +
                      "  Review TEXT NOT NULL," +
                      "  ReviewID INT NOT NULL AUTO_INCREMENT," +
                      "  PRIMARY KEY (ReviewID)," +
                      "  FOREIGN KEY (MovieID) " +
                      "  REFERENCES irate.Movie(MovieID) ON DELETE CASCADE," +
                      "  FOREIGN KEY (CustomerID) " +
                      "  REFERENCES irate.Customer(CustomerID) ON DELETE CASCADE)";
      stmt.executeUpdate(createTable_Review);
      System.out.println("Created entity table Review");

      //create the Endorsement table
      String createTable_Endorsement =
              "CREATE TABLE IF NOT EXISTS irate.Endorsement (" +
                      "  ReviewID INT NOT NULL," +
                      "  EndorsementDate DATE NOT NULL," +
                      "  CustomerID INT NOT NULL," +
                      "  PRIMARY KEY (EndorsementDate, CustomerID, ReviewID)," +
                      "  FOREIGN KEY (ReviewID) REFERENCES irate.Review(ReviewID) ON DELETE CASCADE," +
                      "  FOREIGN KEY (CustomerID) REFERENCES irate.Customer(CustomerID) ON DELETE CASCADE)";
      stmt.executeUpdate(createTable_Endorsement);
      System.out.println("Created entity table Endorsement");

      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method creates all triggers.
   */
  public static void createTriggers() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
    } catch (SQLException e) {
      e.printStackTrace();
    }

    // Triggers created by this program
    String[] dbTriggers = {"EmailChecking", "ReviewChecking", "EndorsementChecking"};
    for (String tgr : dbTriggers) {
      try {
        stmt.executeUpdate("DROP trigger " + tgr);
        System.out.println("Dropped trigger " + tgr);
      } catch (SQLException e) {
        System.out.println("Did not drop " + tgr);
      }
    }

    try {
      // Creates trigger for Customer
      // This trigger will prompt information if invalid Email inserted
      String createTrigger_CustomerEmailChecking = "" +
              " CREATE trigger EmailChecking before INSERT" +
              " ON Customer FOR each row" +
              " BEGIN" +
              " DECLARE msg VARCHAR (200);" +
              " if (new.EmailAddress NOT REGEXP '^[[:alnum:]._%-\\\\+]+@[[:alnum:].-]+[.][[:alnum:]]{2,4}$')" +
              "   THEN" +
              "   SET msg = \"The email address is not valid. Input again.\";" +
              "    signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
              " END if;" +
              " END;";
      stmt.executeUpdate(createTrigger_CustomerEmailChecking);
      System.out.println("Created trigger for CustomerEmailChecking");

      // Creates trigger for Review
      // This trigger will promPt information when invalid review is inserted
      String createTrigger_ReviewChecking = "" +
              " CREATE TRIGGER ReviewChecking BEFORE INSERT" +
              " ON Review for each row" +
              " BEGIN" +
              " DECLARE msg VARCHAR(200);" +
              // A review of a particular movie should be written by a customer
              // who attended the movie within the last week.
              " if (0 = (SELECT COUNT(*) AS dup FROM Attendance " +
              " WHERE AttendanceDate <= new.ReviewDate " +
              " AND (AttendanceDate >= date_sub(new.ReviewDate, INTERVAL 7 DAY)) " +
              " AND CustomerID = new.CustomerID AND MovieID = new.MovieID)) " +
              " THEN" +
              "   SET msg = \"You have to review a movie within a week of its most recent attendance date.\";" +
              "   signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
              " END if;" +
              // There can only be one movie review per customer.
              " if (0 != (SELECT COUNT(*) AS dup FROM Review WHERE CustomerID = new.CustomerID AND MovieID = new.MovieID)) then" +
              "   SET msg = \"You are not allowed to review a movie twice.\";" +
              "   signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
              " END if;" +
              " END;";

      stmt.executeUpdate(createTrigger_ReviewChecking);
      System.out.println("Created trigger for ReviewChecking");

      // Creates trigger for Endorsement
      String createTrigger_EndorsementChecking =
              " CREATE TRIGGER EndorsementChecking BEFORE INSERT" +
                      " ON Endorsement for each row" +
                      " BEGIN" +
                      " DECLARE msg VARCHAR(200);" +
                      " DECLARE cusID INT;" +
                      " DECLARE movID INT;" +
                      " SET cusID = (SELECT CustomerID FROM Review WHERE ReviewID = new.ReviewID);" +
                      " SET movID = (SELECT MovieID FROM Review WHERE ReviewID = new.ReviewID);" +
                      // The user who wants to endorse a review must have written a review
                      " if (SELECT COUNT(*) AS dup FROM Review WHERE ReviewID = new.ReviewID "
                      // ReviewDate should come before EndorsementDate
                      + "AND (ReviewDate <= new.EndorsementDate) "
                      // EndorsementDate should be within three days of the day user writes a review
                      + "AND (ReviewDate > date_sub(new.EndorsementDate, INTERVAL 3 DAY)) = 0) "
                      + "THEN" +
                      "   SET msg = \"You cannot endorse a review that was written three days ago.\";" +
                      "   signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
                      " END if;" +
                      // user cannot endorsement his/her own review
                      " if (new.CustomerID = cusID) THEN" +
                      "   SET msg = \"You cannot endorse your own review.\";" +
                      "   signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
                      " END if;" +

                      " if (0 != (SELECT COUNT(CustomerID) AS dup FROM Endorsement "
                      + "WHERE CustomerID = new.CustomerID "
                      + "AND EndorsementDate = new.EndorsementDate)) "
                      + "THEN" +
                      "   if (0 != (SELECT COUNT(MovieId) AS duplicate "
                      + "FROM Review JOIN Endorsement "
                      + "ON Review.ReviewID = Endorsement.ReviewID "
                      + "AND Review.MovieID = movID)) "
                      + "THEN" +
                      "     SET msg = \"You've already endorsed a review of this movie today.\";" +
                      "     signal SQLSTATE 'HY000' SET MESSAGE_TEXT = msg;" +
                      "   END if;" +
                      " END if;" +
                      " END;";

      stmt.executeUpdate(createTrigger_EndorsementChecking);
      System.out.println("Created trigger for EndorsementChecking");

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method creates all procedures
   */
  public static void createProcedure() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();

      String[] dbStoredProcedures = {
              "freeTicketWinner",
              "freeGift",
              "howManyRegisteredCustomers"
      };

      for (String procedure : dbStoredProcedures) {
        try {
          stmt.executeUpdate("DROP PROCEDURE " + procedure);
          System.out.println("Dropped procedure " + procedure);
        } catch (SQLException e) {
          System.out.println("Did not drop " + procedure);
        }
      }

      // Argument: date, top-rated review
      String createStrPrcdr_freeTicketWinner = ""
              + "CREATE PROCEDURE freeTicketWinner(IN date Date)"
              + " BEGIN"
              + " SELECT Customer.CustomerName, COUNT(*) AS COUNT"
              + " FROM (Review JOIN Endorsement ON Review.ReviewID = Endorsement.ReviewID "
              + "JOIN Customer ON Review.CustomerID = Customer.CustomerID)"
              + "WHERE ReviewDate >= date_sub(DATE, INTERVAL 3 DAY) " // three days earlier means 4 days
              + "AND ReviewDate <= date "
              + "GROUP BY Endorsement.ReviewID ORDER BY COUNT DESC "
              + "limit 1;"
              + " END;";
      stmt.executeUpdate(createStrPrcdr_freeTicketWinner);
      System.out.println("Created stored procedure: " + dbStoredProcedures[0]);

      // Argument: date, find Someone who voted one or more movie reviews as "helpful" on a given day
      String createStrPrcdr_freeGift = ""
              + " CREATE PROCEDURE freeGift(IN date Date)"
              + " BEGIN"
              + " SELECT Customer.CustomerName"
              + " FROM (Customer JOIN Endorsement ON Customer.CustomerId = Endorsement.CustomerID) WHERE EndorsementDate = date;"
              + "END;";
      stmt.executeUpdate(createStrPrcdr_freeGift);
      System.out.println("Created stored procedure: " + dbStoredProcedures[1]);

      // query data of how many registered customers of the iRate app
      String createStrdPrcdr_howManyRegisteredCustomers = ""
              + "CREATE procedure howManyRegisteredCustomers()" +
              " BEGIN" +
              "  SELECT COUNT(*) AS num FROM Customer;" +
              " END;";
      stmt.executeUpdate(createStrdPrcdr_howManyRegisteredCustomers);
      System.out.println("Created stored procedure: " + dbStoredProcedures[2]);


    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
