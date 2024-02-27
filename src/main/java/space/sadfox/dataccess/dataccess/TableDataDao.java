package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import space.sadfox.dataccess.dataccess.core.DBHandler;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlResource;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.LogLevel;
import space.sadfox.owlook.utils.LogMessage;

public class TableDataDao {

  private final Owl<TableData> tableData;

  private final String tableName = "TABLEDATA";
  private final String dbName = "TableDataDB";


  public TableDataDao(Owl<TableData> owl) {
    this.tableData = owl;
  }

  public DataEntity createDataEntity() {
    return new DataEntity(tableData.entity().getFields());
  }

  public void loadData() {
    try (OwlResource res = tableData.openResource();
        DBHandler handler = new DBHandler(res.resourcePath(dbName))) {

      Statement statement = handler.getStatement();
      createNewTable(statement);
      ParserProvider parser = tableData.entity().getParserSafe();
      List<DataEntity> data = parser.parse(tableData);

      for (DataEntity entity : data) {
        try {
          insertDataEntity(entity, statement);
        } catch (SQLException e) {
          Owlook.registerException(1, e);
        }

      }

      tableData.entity().notifyDataUpdateListeners();
    } catch (SQLException | ParserProviderNotFound | IOException e) {
      Owlook.registerException(1, e);
    }
  }

  private void insertDataEntity(DataEntity dataEntity, Statement statement) throws SQLException {
    StringBuilder sql = new StringBuilder("INSERT INTO " + tableName);
    List<String> fields = new ArrayList<>();
    List<String> values = new ArrayList<>();
    tableData.entity().getFields().forEach(field -> {
      fields.add(field.getFieldName());
      values.add("'" + dataEntity.getValue(field) + "'");
    });

    sql.append("(").append(String.join(", ", fields)).append(")");
    sql.append(" VALUES");
    sql.append("(").append(String.join(", ", values)).append(")");
    // System.out.println(sql);
    statement.executeUpdate(sql.toString());

  }

  private void createNewTable(Statement statement) throws SQLException {
    StringBuilder sql = new StringBuilder();

    String dataType = " VARCHAR (200)";
    List<String> fields = new ArrayList<>();
    tableData.entity().getFields().forEach(field -> {
      fields.add(field.getFieldName());
    });

    sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName)
        .append("(ID INTEGER NOT NULL AUTO_INCREMENT");

    fields.forEach(fieldS -> {
      sql.append(", " + fieldS + dataType);
    });
    sql.append(", PRIMARY KEY (ID))");

    statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
    statement.executeUpdate(sql.toString());
    System.out.println(sql);
  }

  public Field addNewField() {
    Field field = new Field();
    tableData.entity().getFields().add(field);
    return field;
  }

  public DataEntity[] selectAll() {
    return selectAllWhere("");
  }

  private boolean existData(Connection connection) {
    try {
      ResultSet rset = connection.getMetaData().getTables(null, "PUBLIC", tableName, null);
      while (rset.next()) {
        return true;
      }
    } catch (SQLException e) {
      Owlook.registerException(2, e);
    }
    return false;

  }

  public DataEntity[] selectAllWhere(String sql) {
    try (OwlResource res = tableData.openResource();
        DBHandler handler = new DBHandler(res.resourcePath(dbName))) {

      if (!existData(handler.getConnection())) {
        LogMessage message = new LogMessage(LogLevel.INFO);
        message.setName("Try select not load data");
        message.setMessage("sql = [" + sql + "]");
        Owlook.registerMessage(message);
        return new DataEntity[0];
      }

      if (sql == null)
        sql = "";
      if (!sql.equals("")) {
        sql = " WHERE(" + sql + ")";
      }
      Statement statement = handler.getStatement();
      System.out.println("SELECT * FROM " + tableName + sql);
      ResultSet rezult = statement.executeQuery("SELECT * FROM " + tableName + sql);

      List<DataEntity> entities = new ArrayList<>();
      while (rezult.next()) {
        DataEntity entity = createDataEntity();
        for (Field field : entity.getFields()) {
          String rez = rezult.getString(field.getFieldName());
          if (rez != null)
            entity.setValue(field, rez);
        }
        entities.add(entity);
      }
      return entities.toArray(new DataEntity[0]);
    } catch (SQLException | IOException e) {
      Owlook.registerException(1, e);
    }
    return new DataEntity[0];
  }

}
