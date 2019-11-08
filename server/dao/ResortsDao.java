package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Resorts;

public class ResortsDao {
  private static ResortsDao instance = null;

  public static ResortsDao getInstance(){
    if(instance == null){
      instance = new ResortsDao();
    }
    return instance;
  }

  public Resorts create(Resorts resort) throws SQLException{
    String insertResort = "INSERT INTO resorts(resortId, resortName) VALUES(?,?);";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement insertStmt = null;
    try{
      insertStmt = connection.prepareStatement(insertResort);
      insertStmt.setInt(1, resort.getResortId());
      insertStmt.setString(2, resort.getResortName());
      insertStmt.executeUpdate();
      return resort;

    } catch (SQLException e){
      e.printStackTrace();
    } finally {
      if(connection != null){
        connection.close();
      }
      if(insertStmt != null){
        insertStmt.close();
      }
    }
    return null;
  }

  public List<Resorts> getAllResorts() throws SQLException{
    String getResorts = " SELECT * FROM resorts;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    List<Resorts> resorts = new ArrayList<>();
    ResultSet results  =null;
    try{
      selectStmt = connection.prepareStatement(getResorts);
      results = selectStmt.executeQuery(getResorts);
      while(results.next()){
        int resortId = results.getInt("resortId");
        String resortName = results.getString("resortName");
        Resorts resort = new Resorts(resortId, resortName);
        resorts.add(resort);
      }
      return resorts;
    } catch (SQLException e){
      e.printStackTrace();
      throw e;
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(selectStmt != null) {
        selectStmt.close();
      }
      if(results != null) {
        results.close();
      }
    }
  }

  public Resorts getResortById(int resortId) throws SQLException{
    String selectResort = "SELECT * FROM resorts WHERE resorts.resortId = ?;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    ResultSet results = null;
    try{
      selectStmt = connection.prepareStatement(selectResort);
      selectStmt.setInt(1, resortId);
      results = selectStmt.executeQuery();
      if(results.next()){
        int resultResortId = results.getInt("resortId");
        String resortName = results.getString("resortName");
        Resorts resort = new Resorts(resultResortId, resortName);
        return resort;
      }
    } catch (SQLException e){
      e.printStackTrace();
      throw e;
    } finally {
      if(connection != null) {
        connection.close();
      }
      if(selectStmt != null) {
        selectStmt.close();
      }
      if(results != null) {
        results.close();
      }
    }
    return null;
  }
}
