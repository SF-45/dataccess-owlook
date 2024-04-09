package space.sadfox.dataccess.dataccess.core;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBHandler implements AutoCloseable {

  static final String DB_URL = "jdbc:h2:";

  static final String USER = "root";
  static final String PASS = "1qaz2wsx3edc";

  private final Connection connection;
  private Statement statement;
  private PreparedStatement preparedStatement;

  public DBHandler(Path path) throws SQLException {
    DriverManager.registerDriver(new org.h2.Driver());
    this.connection = DriverManager.getConnection(DB_URL + path, USER, PASS);
  }

  public DBHandler(Path pathToZip, String pathInZip) throws SQLException {
    DriverManager.registerDriver(new org.h2.Driver());
    String URL = DB_URL + "zip:" + pathToZip + "!" + pathInZip;
    this.connection = DriverManager.getConnection(URL, USER, PASS);

  }

  public void executeUpdate(String SQL) throws SQLException {
    statement = connection.createStatement();
    statement.executeUpdate(SQL);
  }

  public ResultSet executeQuery(String SQL) throws SQLException {

    statement = connection.createStatement();
    return statement.executeQuery(SQL);
  }

  public Statement getStatement() throws SQLException {
    if (statement == null)
      statement = connection.createStatement();
    return statement;
  }

  public PreparedStatement getPreparedStatement(String sql) throws SQLException {
    if (preparedStatement == null)
      preparedStatement = connection.prepareStatement(sql);
    return preparedStatement;
  }

  public Connection getConnection() {
    return connection;
  }

  @Override
  public void close() throws SQLException {
    if (statement != null)
      statement.close();
    if (preparedStatement != null)
      preparedStatement.close();
    if (connection != null)
      connection.close();

  }
}
