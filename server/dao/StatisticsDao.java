package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Stats;

public class StatisticsDao {
  private static StatisticsDao instance = null;

  public static StatisticsDao getInstance(){
    if(instance == null){
      instance = new StatisticsDao();
    }
    return instance;
  }




  public long getRequestValue(String requestType) throws SQLException{
    String selectStats = "SELECT requestValue FROM statistics WHERE requestType=?;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement selectStmt = null;
    ResultSet resultSet = null;
    int requestValue = 0;
    try{
      selectStmt = connection.prepareStatement(selectStats);
      selectStmt.setString(1, requestType);
      resultSet = selectStmt.executeQuery();
      if(resultSet.next()){
        requestValue = resultSet.getInt(1);
      }
      return requestValue;

    }catch (SQLException e){
      e.printStackTrace();
    } finally {
      if(connection != null){
        connection.close();
      }
      if(selectStmt != null){
        selectStmt.close();
      }
    }
    return 0L;
  }

  public void UpdateStatsTable(String Url, String requestMethod, long mean, long max) throws SQLException{
    String selectStats = "SELECT COUNT(*), AVG(mean), MAX(max) FROM statistics WHERE (url=?) and (requestType=?);";
    String deleteStats = "DELETE FROM statistics WHERE (url=?) and (requestType=?);";
    String insertStats = "INSERT INTO statistics(url, requestType, mean, max) VALUES(?,?,?,?);";
    PreparedStatement updateStmt = null;
    Connection connection = C3poDataSource.getConnection();
    ResultSet results = null;
    try{
      long count = 0L;
      long curMean = 0L;
      long curMax = 0L;
      updateStmt = connection.prepareStatement(selectStats);
      updateStmt.setString(1, Url);
      updateStmt.setString(2, requestMethod);
      results = updateStmt.executeQuery();
      if(results.next()){
        count = results.getLong(1);
        curMean = results.getLong(2);
        curMax = results.getLong(3);
      }

      if(count > 1000){
        updateStmt = connection.prepareStatement(deleteStats);
        updateStmt.setString(1, Url);
        updateStmt.setString(2, requestMethod);
        updateStmt.executeUpdate();
        mean = (curMean * count + mean) / (count + 1);
        max = Math.max(curMax, max);
      }

      updateStmt = connection.prepareStatement(insertStats);
      updateStmt.setString(1, Url);
      updateStmt.setString(2, requestMethod);
      updateStmt.setLong(3, mean);
      updateStmt.setLong(4, max);
      updateStmt.executeUpdate();

    } catch (SQLException e){
      e.printStackTrace();
    }finally {
      if (connection != null) {
        connection.close();
      }
      if (updateStmt != null) {
        updateStmt.close();
      }
      if(results != null){
        results.close();
      }
    }
  }

  public List<Stats> getStats() throws SQLException{
    String selectStmt = "SELECT url, requestType, AVG(mean), MAX(max) FROM statistics GROUP BY url,requestType;";
    Connection connection = C3poDataSource.getConnection();
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    List<Stats> list = new ArrayList<>();
    try{
      preparedStatement = connection.prepareStatement(selectStmt);
      resultSet = preparedStatement.executeQuery();
      while(resultSet.next()){
        list.add(new Stats(resultSet.getString(1), resultSet.getString(2), resultSet.getLong(3), resultSet.getLong(4)));
      }
    } catch (SQLException e){
      e.printStackTrace();
      throw e;
    } finally {
      if(connection != null){
        connection.close();
      }
      if(preparedStatement != null){
        preparedStatement.close();
      }
      if(resultSet != null){
        resultSet.close();
      }
    }
    return list;
  }
}
