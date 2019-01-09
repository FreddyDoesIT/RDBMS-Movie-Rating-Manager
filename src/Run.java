import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is the class contains the main method.
 */
public class Run {
  static Connection conn;
  static String driver = "com.mysql.jdbc.Driver";
  static String url = "jdbc:mysql://localhost/irate";
  static String user = "root";
  static String password = "";
  static CallableStatement cstmt = null;
  static Statement stmt = null;
  static ResultSet rs = null;

  /**
   * Run this method to create database, tables, triggers and procedures and test them.
   *
   */
  public static void main(String[] args) {
    //Create database, tables, triggers and procedures.
    create();

    //Insert data into all tables, test procedures, inserting invalid data, deleting some data.
    test();
  }

  /**
   * This method creates the database, tables, triggers and procedures.
   */
  private static void create() {
    Creation.createDatabase();
    Creation.createTables();
    Creation.createTriggers();
    Creation.createProcedure();
  }

  /**
   *  This method inserts data into all tables.
   *  It tests procedures, both valid and invalid data insertion as well as
   *  data deletion.
   */
  private static void test() {
    // Test if valid data can be inserted as expected.
    TestPorject.insertData();

    System.out.println("************************************************************************\n"
            + "Following is the query to find the free movie ticket winner\n"
            + "and the free concession winner with given date.\n");

    try {
      conn = DriverManager.getConnection(url, user, password);
      stmt = conn.createStatement();

      //Find the writer of the top-rated review of a movie written 3 days earlier.
      cstmt = conn.prepareCall("call freeTicketWinner('2018-05-08')");
      rs = cstmt.executeQuery();
      System.out.println("The free ticket winner is:");
      while (rs.next()) {
        System.out.println(rs.getString("CustomerName"));
      }

      //Find someone who voted one or more movie reviews as "helpful" on a given day.
      cstmt = conn.prepareCall("call freeGift('2018-01-01')");
      rs = cstmt.executeQuery();
      System.out.println("The free gift winner is:");
      while (rs.next()) {
        System.out.println(rs.getString("CustomerName"));
      }

      //Find how many customers this database contains.
      cstmt = conn.prepareCall("call howManyRegisteredCustomers()");
      rs = cstmt.executeQuery();
      System.out.println("The registered customer is:");
      while (rs.next()) {
        System.out.println(rs.getInt("num" ) + "\n");
      }

      //Test if invalid data can insert into the tables.
      TestPorject.insertInvalid();

      //Test if all related data is deleted when one of the customer, movie or review is deleted.
      TestPorject.testDelete();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
