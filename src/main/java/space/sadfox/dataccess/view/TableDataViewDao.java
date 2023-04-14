package space.sadfox.dataccess.view;

import space.sadfox.dataccess.dataccess.Field;

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

}
