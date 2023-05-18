package space.sadfox.dataccess.view;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.Field;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;

public class TableDataViewDao {

	private TableDataView tableDataView;
	
	public TableDataViewDao(TableDataView tableDataView) {
		this.tableDataView = tableDataView;

	}
	
	public FieldView addNewField(String dataField, String frendlyFieldName) {
		FieldView fieldView = new FieldView();
		fieldView.setFieldName(dataField);
		fieldView.setFriendlyFieldName(frendlyFieldName);
		tableDataView.getFieldViews().add(fieldView);
		return fieldView;
	}

	public static TableDataView createTableDataView() {
		try {
			return EntityLoader.INSTANCE.createEntity(TableDataView.class);
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	public static boolean deleteTableDataView(TableDataView tableDataView) {
		return EntityLoader.INSTANCE.deleteEntity(tableDataView);
	}
	
	public static TableDataView loadTableDataView(String fileName) throws IOException, JAXBException {
		return EntityLoader.INSTANCE.loadEntity(fileName, TableDataView.class);
	}
}
