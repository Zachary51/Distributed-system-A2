package dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import database.MySQLDBUtil;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

public class C3poDataSource {
  private static ComboPooledDataSource cpds = new ComboPooledDataSource();
  private static final String HOST_NAME = MySQLDBUtil.DB_URL;
  private static final String PORT = "3306";
  private static final String DATABASE = "dsbs";
  private static final String USERNAME = "admin";
  private static final String PASSWORD = "criminal51";

  static {
    try{
      String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
      cpds.setDriverClass("com.mysql.cj.jdbc.Driver");
      cpds.setJdbcUrl(url);
      cpds.setUser(USERNAME);
      cpds.setPassword(PASSWORD);
      cpds.setMaxPoolSize(256);
      cpds.setMaxStatementsPerConnection(100);

    } catch (PropertyVetoException e){
      e.printStackTrace();
    }
  }

  public static Connection getConnection() {
    Connection connection = null;
    int count = 0;
    while(connection == null && count < 5){
      try {
        connection = cpds.getConnection();
      } catch (SQLException e){
        count++;
        System.err.println("Connection error" + e);
      }
    }
    return connection;
  }

  private C3poDataSource(){

  }
}
