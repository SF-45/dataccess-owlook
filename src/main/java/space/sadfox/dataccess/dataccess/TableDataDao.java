package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import space.sadfox.dataccess.dataccess.core.DBHandler;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlResource;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.MessageLevel;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.OwlookMessage;
import space.sadfox.owlook.utils.ProjectPath;

public class TableDataDao {

  private final Owl<TableData> tableData;

  private final String EXTENSION = ".mv.db";
  private final String tableName = "TABLEDATA";
  private final String dbName = "TableDataDB";

  public TableDataDao(Owl<TableData> owl) {
    this.tableData = owl;
  }

  public DataEntity createDataEntity() {
    return new DataEntity(tableData.entity().getFields());
  }

  public void loadData() {
    List<ParserTask> tasks = new ArrayList<>();
    ParseProgressDialog progressDialog = new ParseProgressDialog(tableData);
    tableData.entity().getParsers().forEach(parserEntity -> {
      if (parserEntity.entity().isEnable()) {
        var oParserProvider = parserEntity.entity().getParserProviderSafe();
        if (oParserProvider.isPresent()) {
          ParserProvider parserProvider = oParserProvider.get();
          ParserTask task = parserProvider.createParser(parserEntity, tableData);
          task.exceptionProperty().addListener((property, oldValue, newValue) -> {
            if (newValue != null) {
              Owlook.registerException(2, newValue);
            }
          });
          progressDialog.addTask(task);
          Thread th = new Thread(task);
          th.setDaemon(true);
          th.start();
          tasks.add(task);
        } else {
          OwlookMessage m = new OwlookMessage(MessageLevel.WARNING);
          m.setName("Parser Provider not found");
          m.setMessage("Parser provider not found for " + parserEntity.info().owlName() + ": "
              + parserEntity.head().getTitle());
          Owlook.notificate(m);
        }
      }
    });

    var oButtonType = progressDialog.showAndWait();

    if (oButtonType.isEmpty() || !oButtonType.get().equals(ParseProgressDialog.SAVE)) {
      return;
    }

    List<DataEntity> parseRezult = new ArrayList<>();
    tasks.forEach(task -> {
      try {
        parseRezult.addAll(task.get());
      } catch (ExecutionException | InterruptedException e) {
        Owlook.registerException(3, e);
      }
    });

    if (parseRezult.size() == 0) {
      MessageBox m = new MessageBox(AlertType.CONFIRMATION);
      String title = "Loaded data is empty";
      m.setTitle(title);
      m.setHeaderText(title);
      m.setContentText("Are you sure you want to save the data? This will delete existing data.");
      m.getButtonTypes().clear();
      m.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
      var oAns = m.showAndWait();
      if (oAns.isEmpty() || oAns.get().equals(ButtonType.NO)) {
        return;
      }

    }

    String tempDBName = tableData.info().id() + dbName;
    Path tempDir = ProjectPath.TEMP.getPath();
    Path tempDBFile = tempDir.resolve(tempDBName + EXTENSION);

    try {
      Files.deleteIfExists(tempDBFile);
    } catch (IOException e) {
      Owlook.registerException(3, e);
    }
    try (DBHandler handler = new DBHandler(tempDir.resolve(tempDBName))) {
      createNewTable(handler.getStatement());

      parseRezult.forEach(dataEntity -> {
        try {
          insertDataEntity(dataEntity, handler.getStatement());
        } catch (SQLException e) {
          Owlook.registerException(3, e);
        }
      });

    } catch (SQLException e) {
      Owlook.registerException(1, e);
    }

    try (OwlResource oRes = tableData.openResource()) {
      try (OutputStream o = oRes.resourceOutputStream(dbName + EXTENSION);
          InputStream i = Files.newInputStream(tempDBFile)) {
        i.transferTo(o);
      }
    } catch (IOException e) {
      Owlook.registerException(1, e);
    }
    tableData.entity().notifyDataUpdateListeners();



    // Task<Void> loadWait = new Task<Void>() {
    // @Override
    // public Void call() {
    // List<DataEntity> parseRezult = new ArrayList<>();
    // tasks.forEach(task -> {
    // try {
    // parseRezult.addAll(task.get());
    // } catch (CancellationException e) {
    // } catch (InterruptedException | ExecutionException e) {
    // Owlook.registerException(1, e);
    // }
    // });
    //
    // try (OwlResource res = tableData.openResource();
    // DBHandler handler = new DBHandler(res.resourcePath(dbName))) {
    //
    // Statement statement = handler.getStatement();
    // createNewTable(statement);
    // for (DataEntity entity : parseRezult) {
    // try {
    // insertDataEntity(entity, statement);
    // } catch (SQLException e) {
    // Owlook.registerException(1, e);
    // }
    // }
    // tableData.entity().notifyDataUpdateListeners();
    // } catch (SQLException | IOException e) {
    // Owlook.registerException(1, e);
    // }
    // OwlookMessage message = new OwlookMessage(MessageLevel.INFO);
    // message.setName("Data loaded");
    // message.setMessage(parseRezult.size() + " values loaded");
    // Owlook.notificate(message);
    // return null;
    // }
    // };
    // Thread th = new Thread(loadWait);
    // th.setDaemon(true);
    // th.start();
  }

  private synchronized void insertDataEntity(DataEntity dataEntity, Statement statement)
      throws SQLException {
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

  private synchronized void createNewTable(Statement statement) throws SQLException {
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
    OwlookMessage m = new OwlookMessage(MessageLevel.INFO);
    m.setName("Create New Table");
    m.setMessage("SQL: " + sql);
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

  public synchronized DataEntity[] selectAllWhere(String sql) {
    String pathToDBInOwl = "";
    try (OwlResource res = tableData.openResource()) {
      if (!res.isResourceExist(dbName + EXTENSION)) {
        // TODO: Оповещение о том что данные не загружены
        return new DataEntity[0];
      }

      pathToDBInOwl = res.resourcePath(dbName).toString();
    } catch (IOException e) {
      Owlook.registerException(1, e);
    }
    try (DBHandler handler = new DBHandler(tableData.location(), pathToDBInOwl)) {
      if (!existData(handler.getConnection())) {
        OwlookMessage message = new OwlookMessage(MessageLevel.INFO);
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
    } catch (SQLException e) {
      Owlook.registerException(1, e);
    }
    return new DataEntity[0];
  }

}
