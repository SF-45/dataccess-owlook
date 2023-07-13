package space.sadfox.dataccess.dataccess;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import space.sadfox.dataccess.dataccess.core.DBHandler;
import space.sadfox.owlook.logger.LogLevel;
import space.sadfox.owlook.utils.LoggerMessage;
import space.sadfox.owlook.utils.OwlLogger;

public class TableDataDao {

	private TableData tableData;
	private Path dataBasePath;

	String tableName;

	public TableDataDao(TableData tableData) {
		this.tableData = tableData;

		tableName = "TABLEDATA";
		dataBasePath = tableData.getResourcesPath().resolve("TableDataDB");
	}

	public DataEntity createDataEntity() {
		return new DataEntity(getTableData().getFields());
	}

	public void loadData() {
		try (DBHandler handler = new DBHandler(dataBasePath)) {
			Statement statement = handler.getStatement();
			createNewTable(statement);
			ParserProvider parser = getTableData().getParserSafe();
			List<DataEntity> data = parser.parse(getTableData());

			for (DataEntity entity : data) {
				try {
					insertDataEntity(entity, statement);
				} catch (SQLException e) {
					OwlLogger.registerException(1, e);
				}

			}

			getTableData().notifyTableDataChangeListeners(new TableData.Change() {

				@Override
				public boolean wasRemoved() {
					return false;
				}

				@Override
				public boolean wasModify() {
					return false;
				}

				@Override
				public boolean wasDataUpdate() {
					return true;
				}
			});
		} catch (SQLException | ParserProviderNotFound e) {
			OwlLogger.registerException(1, e);
		}

	}

	private void insertDataEntity(DataEntity dataEntity, Statement statement) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName);
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();
		getTableData().getFields().forEach(field -> {
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

		String dataType = " VARCHAR (100)";
		List<String> fields = new ArrayList<>();
		getTableData().getFields().forEach(field -> {
			fields.add(field.getFieldName());
		});

		sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(ID INTEGER NOT NULL AUTO_INCREMENT");

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
		getTableData().getFields().add(field);
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
			OwlLogger.registerException(2, e);
		}
		return false;

	}

	public DataEntity[] selectAllWhere(String sql) {
		try (DBHandler handler = new DBHandler(dataBasePath)) {
			
			if (!existData(handler.getConnection())) {
				LoggerMessage message = new LoggerMessage(LogLevel.INFO);
				message.setName("Try select not load data");
				message.setMessage("sql = [" + sql + "]");
				OwlLogger.registerMessage(message);
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
			OwlLogger.registerException(1, e);
		}
		return new DataEntity[0];
	}

	public TableData getTableData() {
		return tableData;
	}

}
