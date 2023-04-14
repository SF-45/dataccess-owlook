package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.core.DBHandler;
import space.sadfox.dataccess.dataccess.core.XMLParser;

public class TableDataDao {

	private TableData tData;
	private Path dataBasePath;

	String tableName;

	public TableDataDao(TableData tableData) {
		this.tData = tableData;
		Path tPath = tableData.getPath();
		tableName = tPath.getFileName().toString().replace(".", "");
		dataBasePath = tPath.getParent().resolve(tPath.getFileName().toString());
	}

	public DataEntity createDataEntity() {
		return new DataEntity(tData.getFields());
	}

	public void loadData() {
		try (DBHandler handler = new DBHandler(dataBasePath)) {
			Statement statement = handler.getStatement();
			createNewTable(statement);
			XMLParser parser = new XMLParser(Path.of(tData.getPathToData()));
			List<DataEntity> data = parser.parse(tData);

			for (DataEntity entity : data) {
				insertDataEntity(entity, statement);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

	}

	private void insertDataEntity(DataEntity dataEntity, Statement statement) throws SQLException {
		StringBuilder sql = new StringBuilder("INSERT INTO " + tableName);
		List<String> fields = new ArrayList<>();
		List<String> values = new ArrayList<>();
		tData.getFields().forEach(field -> {
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

		String dataType = " VARCHAR (50)";
		List<String> fields = new ArrayList<>();
		tData.getFields().forEach(field -> {
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
		tData.getFields().add(field);
		return field;
	}

	public ParserFilter addNewPreFilter() {
		ParserFilter filter = new ParserFilter();
		tData.getPrefilters().add(filter);
		return filter;
	}

	public DataEntity[] selectAll() {
		return selectAllWhere("");
	}

	public DataEntity[] selectAllWhere(String sql) {
		try (DBHandler handler = new DBHandler(dataBasePath)) {

			if (sql == null)
				sql = "";
			if (!sql.equals("")) {
				sql = " WHERE(" + sql + ")";
			}

			Statement statement = handler.getStatement();
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
			e.printStackTrace();
		}
		return new DataEntity[0];
	}

}
