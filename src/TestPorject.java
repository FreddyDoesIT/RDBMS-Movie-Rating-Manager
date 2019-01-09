import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;

/**
 * This class includes all tests for this iRate database.
 */
public class TestPorject {
  static Connection conn;
  static Statement stmt;
  static String driver = "com.mysql.jdbc.Driver";
  static String url = "jdbc:mysql://localhost/irate";
  static String user = "root";
  static String password = "";

  /**
   * This method try to insert data from external txt files into our iRate database.
   * We deliberately includes some invalid data in our txt files. So some warning message may
   * appear during inserting.
   */
  public static void insertData() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();

      String[] dbTables = {"Movie", "Customer", "Attendance", "Review", "Endorsement"};
      String[] fileNames = {"Movie.txt", "Customer.txt", "Attendance.txt", "Review.txt", "Endorsement.txt"};

      PreparedStatement insertRow_Movie = conn.prepareStatement("insert into Movie(Title, MovieID) values(?, ?)");
      PreparedStatement insertRow_Customer = conn.prepareStatement("insert into Customer(CustomerID, Date, EmailAddress, CustomerName) values(?, ?, ?, ?)");
      PreparedStatement insertRow_Attendance = conn.prepareStatement("insert into Attendance(CustomerID, MovieID, AttendanceDate) values(?, ?, ?)");
      PreparedStatement insertRow_Review = conn.prepareStatement("insert into Review(CustomerID, MovieID, ReviewDate, Rating, Review, ReviewID) values(?, ?, ?, ?, ?, ?)");
      PreparedStatement insertRow_Endorsement = conn.prepareStatement("insert into Endorsement(ReviewID, EndorsementDate, CustomerID) values(?, ?, ?)");

      for (String tb1 : dbTables) {
        stmt.executeUpdate("delete from " + tb1);
        System.out.println("Truncated table " + tb1);
      }

      //Some warning message may appear due to invalid data
      System.out.println("*************************************************************************\n"
              + "Warning information followed is prompted\nas some wrong info trying to be inserted.\n");
      ResultSet rs = null;
      BufferedReader br = new BufferedReader(new FileReader(new File(fileNames[0])));
      String line;

      //Insert data into Movie
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length != 2) continue;

        int movieID = Integer.valueOf(data[0]);
        String title = data[1];

        insertRow_Movie.setString(1, title);
        insertRow_Movie.setInt(2, movieID);
        try {
          insertRow_Movie.execute();
        } catch (SQLException ex) {
          System.out.println("Input data to Movie table:");
          System.out.println("(MovieID: " + movieID + "Title: " + title + ")");
          System.out.println("is invalid due to: " + ex.getMessage() + "\n");
        }
      }

      //Insert data into Customer
      br = new BufferedReader(new FileReader(new File(fileNames[1])));
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length != 4) continue;

        int customerID = Integer.valueOf(data[0]);
        String registrationDate = data[1];
        String emailAddress = data[2];
        String customerName = data[3];

        insertRow_Customer.setInt(1, customerID);
        insertRow_Customer.setString(2, registrationDate);
        insertRow_Customer.setString(3, emailAddress);
        insertRow_Customer.setString(4, customerName);
        try {
          insertRow_Customer.execute();
        } catch (SQLException ex) {
          System.out.println("Input data to Customer table:");
          System.out.println("(CustomerID: " + customerID + " registrationDate: " + registrationDate
                  + " emailAddress:" + emailAddress + " customerName: " + customerName + ")");
          System.out.println("is invalid due to: " + ex.getMessage() + "\n");
        }
      }

      //Insert data into attendance
      br = new BufferedReader(new FileReader(new File(fileNames[2])));
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length != 3) continue;

        int customerID = Integer.valueOf(data[0]);
        int movieID = Integer.valueOf(data[1]);
        String attendanceDate = data[2];
        insertRow_Attendance.setInt(1, customerID);
        insertRow_Attendance.setInt(2, movieID);
        insertRow_Attendance.setString(3, attendanceDate);
        try {
          insertRow_Attendance.execute();
        } catch (SQLException ex) {
          System.out.println("Input data to Attendance table:");
          System.out.println("(CustomerID: " + customerID + " MovieID: " + movieID +
                  " Attendance Date: " + attendanceDate + ")");
          System.out.println("is invalid due to: " + ex.getMessage() + "\n");
        }
      }

      //insert data into Review
      br = new BufferedReader(new FileReader(new File(fileNames[3])));
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length != 6) continue;

        int customerID = Integer.valueOf(data[0]);
        int movieID = Integer.valueOf(data[1]);
        String reviewDate = data[2];
        String rating = data[3];
        String review = data[4];
        int reviewID = Integer.valueOf(data[5]);

        insertRow_Review.setInt(1, customerID);
        insertRow_Review.setInt(2, movieID);
        insertRow_Review.setString(3, reviewDate);
        insertRow_Review.setString(4, rating);
        insertRow_Review.setString(5, review);
        insertRow_Review.setInt(6, reviewID);
        try {
          insertRow_Review.execute();
        } catch (SQLException ex) {
          System.out.println("Input data to Review table:");
          System.out.println("(CustomerID: " + customerID + " MovieID: " + movieID + " ReviewDate: " +
                  reviewDate + " Rating: " + rating + " Review: " + review + " ReviewID: " + reviewID + ")");
          System.out.println("is invalid due to: " + ex.getMessage() + "\n");
        }
      }

      //Insert data into Endorsement
      br = new BufferedReader(new FileReader(new File(fileNames[4])));
      while ((line = br.readLine()) != null) {
        String[] data = line.split(",");
        if (data.length != 3) continue;

        int reviewID = Integer.valueOf(data[0]);
        String endorsementDate = data[1];
        int customerID = Integer.valueOf(data[2]);

        insertRow_Endorsement.setInt(1, reviewID);
        insertRow_Endorsement.setString(2, endorsementDate);
        insertRow_Endorsement.setInt(3, customerID);
        try {
          insertRow_Endorsement.execute();
        } catch (SQLException ex) {
          System.out.println("Input data to Endorsement table:");
          System.out.println("(ReviewID: " + reviewID + " EndorsementDate: " + endorsementDate
                  + " CustomerID: " + customerID + ")");
          System.out.println("is invalid due to: " + ex.getMessage() + "\n");
        }
      }

      System.out.println("Customer Table:");
      showCustomerTable();
      System.out.println("\nMovie Table:");
      showMovieTable();
      System.out.println("\nAttendance Table");
      showAttendanceTable();
      System.out.println("\nReview Table");
      showReviewTable();
      System.out.println("\nEndorsement Table");
      showEndorsementTable();
      stmt.close();
      conn.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * This method test:
   * 1. If a customer is deleted, all of his or her reviews and endorsements are deleted.
   * 2. If a movie is deleted, all of its attendances are deleted.
   * 3. If a movie is deleted, all of its reviews are also deleted.
   * 4. If a review is deleted, all endorsements are also deleted.
   */
  public static void testDelete() {
    try {

      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      //If a customer is deleted, all of his or her reviews and endorsements are deleted
      System.out.println("If a customer is deleted, all of his or her reviews and endorsements are deleted.\n");
      System.out.println("Before Delete operation: Customer table");
      showCustomerTable();
      System.out.println("Before Delete operation: Review table");
      showReviewTable();
      System.out.println("Before Delete operation: Endorsement table");
      showEndorsementTable();

      System.out.println("delete from Customer where CustomerID = 1\n");
      stmt.executeUpdate("DELETE FROM Customer WHERE CustomerID = 1");

      System.out.println("After delete operation: Customer table");
      showCustomerTable();
      System.out.println("After delete operation: Rreview table");
      showReviewTable();
      System.out.println("After delete operation: Endorsement table");
      showEndorsementTable();


      //If a movie is deleted, all of its attendances and reviews are deleted.
      System.out.println("If a movie is deleted, all of its attendances and reviews are deleted.");
      System.out.println("Before Delete operation: movie");
      showMovieTable();
      System.out.println("Before Delete operation: Attendance");
      showAttendanceTable();
      System.out.println("Before Delete operation: Review");
      showReviewTable();

      System.out.println("delete from Movie where MovieID = 2\n");
      stmt.executeUpdate("DELETE FROM Movie WHERE MovieID = 2");

      System.out.println("After delete: movie");
      showMovieTable();
      System.out.println("After delete: Attendance");
      showAttendanceTable();
      System.out.println("After delete: Review");
      showReviewTable();

      // If a review is deleted, all endorsements are also deleted.
      System.out.println("If a review is deleted, all endorsements are also deleted.");
      System.out.println("Before Delete operation: review");
      showReviewTable();
      System.out.println("Before Delete operation: endorsement");
      showEndorsementTable();

      System.out.println("delete from Review where ReviewID = 8\n");
      stmt.executeUpdate("DELETE FROM Review WHERE ReviewID = 8");

      System.out.println("After delete: review");
      showReviewTable();
      System.out.println("After delete: endorsement");
      showEndorsementTable();

    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method prints all contents in Customer table.
   */
  private static void showCustomerTable() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      cstmt = conn.prepareCall("SELECT * FROM Customer");
      rs = cstmt.executeQuery();
      System.out.format("%-12s%-15s%-25s%-12s", "CustomerID", "Date", "EmailAddress", "CustomerName");
      System.out.println();
      while (rs.next()) {
        int customerID = rs.getInt("CustomerID");
        String date = rs.getString("Date");
        String emailAddress = rs.getString("EmailAddress");
        String customerName = rs.getString("CustomerName");
        System.out.format("%-12d%-15s%-25s%-12s", customerID, date, emailAddress, customerName);
        System.out.println();
      }
      System.out.println();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method prints all content in Movie table.
   */
  private static void showMovieTable() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      cstmt = conn.prepareCall("SELECT * FROM Movie");
      rs = cstmt.executeQuery();
      System.out.format("%-20s%-15s", "Title", "MovieID");
      System.out.println();
      while (rs.next()) {
        int movieID = rs.getInt("MovieID");
        String title = rs.getString("Title");
        System.out.format("%-20s%-15d", title, movieID);
        System.out.println();
      }
      System.out.println();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method print all contents in attendance table.
   */
  private static void showAttendanceTable() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      cstmt = conn.prepareCall("SELECT * FROM Attendance");
      rs = cstmt.executeQuery();
      System.out.format("%-12s%-20s%-12s%-12s", "MovieID", "AttendanceDate", "CustomerID", "AttendanceID");
      System.out.println();
      while (rs.next()) {
        int customerID = rs.getInt("CustomerID");
        String date = rs.getString("AttendanceDate");
        int movieID = rs.getInt("MovieID");
        int attendanceID = rs.getInt("AttendanceID");
        System.out.format("%-12d%-20s%-12d%-12d", movieID, date, customerID, attendanceID);
        System.out.println();
      }
      System.out.println();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method prints all contents in Review table.
   */
  private static void showReviewTable() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      cstmt = conn.prepareCall("SELECT * FROM Review");
      rs = cstmt.executeQuery();
      System.out.format("%-12s%-12s%-20s%-12s%-45s%-12s", "CustomerID", "MovieID", "ReviewDate", "Rating", "ReviewContent", "ReviewID");
      System.out.println();
      while (rs.next()) {
        int customerID = rs.getInt("CustomerID");
        int movieID = rs.getInt("MovieID");
        String date = rs.getString("ReviewDate");
        int rating = rs.getInt("Rating");
        String reviewContent = rs.getString("Review");
        int reviewID = rs.getInt("ReviewID");
        System.out.format("%-12d%-12d%-20s%-12d%-45s%-12d", customerID, movieID, date, rating, reviewContent, reviewID);
        System.out.println();
      }
      System.out.println();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method prints all contents in endorsement table.
   */
  private static void showEndorsementTable() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();
      CallableStatement cstmt = null;
      ResultSet rs = null;

      cstmt = conn.prepareCall("SELECT * FROM Endorsement");
      rs = cstmt.executeQuery();
      System.out.format("%-12s%-20s%-12s", "ReviewID", "EndorsementDate", "CustomerID");
      System.out.println();
      while (rs.next()) {
        int customerID = rs.getInt("CustomerID");
        int reviewID = rs.getInt("ReviewID");
        String date = rs.getString("EndorsementDate");
        System.out.format("%-12d%-20s%-12d", reviewID, date, customerID);
        System.out.println();
      }
      System.out.println();
    } catch (SQLException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * This method tests all invalid insertion. The warning information would be printed out to
   * console.
   */
  public static void insertInvalid() {
    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();

      System.out.println("*************************************************************************\n"
              + "The following information are all testing results of invalid insertion.\n");

      // This is the insert statement to test
      // that invalid customer information(Email address) can be checked.
      System.out.println("The following information inserted into Customer table\n"
              + "contains invalid email and should be checked.\n"
              + "'2018-06-03','123gmail.com','BBking'\n");
      String insertIntoCustomerWithInvalidEmailAddress = ""
              + "INSERT INTO Customer(Date,EmailAddress,CustomerName) VALUES('2018-06-03','123gmail.com','BBking');";
      try {
        stmt.executeUpdate(insertIntoCustomerWithInvalidEmailAddress);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }

      // This following is the insert statement checking that a customer cannot review a movie twice.
      System.out.println("The following information inserted into Review table\n"
              + "contains invalid information and should be checked.\n"
              + "1,1,'2018-01-01',4,'Good+invalid',2\n");
      String insertIntoReviewWithDupliReview = ""
              + "INSERT INTO Review VALUES(1,1,'2018-01-01',4,'Good+invalid',2);";
      try {
        stmt.executeUpdate(insertIntoReviewWithDupliReview);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }

      // Tests that a customer cannot review a movie
      // a week or over later than its most recent attendance date.
      System.out.println("The following information inserted into Review table\n"
              + "contains invalid information and should be checked.\n"
              + "1,2,'2018-01-10',4,'not bad+invalid',4\n");
      String insertIntoReview = ""
              + "INSERT INTO Review VALUES(1,2,'2018-01-10',4,'not bad+invalid',4);";
      try {
        stmt.executeUpdate(insertIntoReview);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }

      // Tests that a customer cannot endorse his/her own review.
      System.out.println("The following information inserted into Endorsement table\n"
              + "contains invalid information and should be checked.\n"
              + "1,'2018-01-01',1\n");
      String insertIntoEndorsement = ""
              + "INSERT INTO Endorsement VALUES(1,'2018-01-01',1);";
      try {
        stmt.executeUpdate(insertIntoEndorsement);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }

      // Tests a customer cannot endorse a review that was writted three days ago.
      System.out.println("The following information inserted into Endorsement table\n"
              + "contains invalid information and should be checked.\n"
              + "8,'2018-05-07',1\n");
      String insertIntoEndorsement2 = ""
              + "INSERT INTO Endorsement VALUES(8,'2018-05-07',1);";
      try {
        stmt.executeUpdate(insertIntoEndorsement2);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }

      // Testing that a customer can only endorse a review of a movie only once.
      System.out.println("The following information inserted into Endorsement table\n"
              + "contains invalid information and should be checked.\n"
              + "1,'2018-01-02',2\n");
      String insertIntoEndorsement3 = ""
              + "INSERT INTO Endorsement VALUES(1,'2018-01-02',2);";
      try {
        stmt.executeUpdate(insertIntoEndorsement3);
      } catch (SQLException e) {
        System.out.println(e.getMessage() + "\n");
      }
      System.out.println("*************************************************************************\n");
      stmt.close();
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
