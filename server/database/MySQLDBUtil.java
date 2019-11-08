package database;

public class MySQLDBUtil {
  public static final String DB_URL = "database-1.cm6ghr047azj.us-west-2.rds.amazonaws.com";
  private static final String PORT_NUMBER = "3306";
  private static final String DB_NAME = "dsbs";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "criminal51";

  private static final String LOCAL_DB_URL = "localhost";
  private static final String LOCAL_USERNAME = "root";
  public static final String LOCAL_URL = "jdbc:mysql://" + LOCAL_DB_URL + ":" + PORT_NUMBER
      + "/" + DB_NAME
      + "?user=" + LOCAL_USERNAME + "&password=" + PASSWORD
      + "&autoReconnect=true&serverTimezone=UTC&useSSL=true";

}
