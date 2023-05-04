package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.dataccess.filter.Filter;
import space.sadfox.owlook.ui.base.Controller;

public class TableDataController extends Controller {

	@FXML
	private CheckBox autoUpdate;

	@FXML
	private TableView<Field> fieldsTable;

	@FXML
	private TextField pathToDataTextField;

	@FXML
	private TableView<StringProperty> prefilterValues;

	@FXML
	private TableView<ParserFilter> prefiltersTable;

	@FXML
	private Button selectPath;

	private TableData tableData;
	private TableDataDao tableDataDao;

	public TableDataController(TableData tableData) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-tabledata.fxml"));

		this.tableData = tableData;

		autoUpdate.setSelected(tableData.getAutoUpdate());
		autoUpdate.selectedProperty().bindBidirectional(tableData.autoUpdateProperty());

		pathToDataTextField.setText(tableData.getPathToData());
		pathToDataTextField.textProperty().bindBidirectional(tableData.pathToDataProperty());

		// ==================================================================
		// Init TableView for fields
		// ==================================================================

		fieldsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		TableColumn<Field, String> fieldName = new TableColumn<>("Field");
		fieldName.setEditable(true);
		fieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		fieldName.setCellFactory(TextFieldTableCell.forTableColumn());
		fieldName.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFieldName(editEvent.getNewValue());
		});
		fieldsTable.getColumns().add(fieldName);

		TableColumn<Field, String> parseAssociation = new TableColumn<>("Parse Association");
		parseAssociation.setEditable(true);
		parseAssociation.setCellValueFactory(new PropertyValueFactory<>("parseAssociation"));
		parseAssociation.setCellFactory(TextFieldTableCell.forTableColumn());
		parseAssociation.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setParseAssociation(editEvent.getNewValue());
		});
		fieldsTable.getColumns().add(parseAssociation);

		ObjectProperty<Field> draggedField = new SimpleObjectProperty<>();
		IntegerProperty draggedInd = new SimpleIntegerProperty();

		fieldsTable.setRowFactory(call -> {
			TableRow<Field> row = new TableRow<>();

			row.setOnDragDetected(dragEvent -> {
				draggedField.set(row.getItem());
				draggedInd.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedInd.get() == row.getIndex() || draggedField.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				fieldsTable.getItems().remove(draggedField.get());
				fieldsTable.getItems().add(tempInd, draggedField.get());
				fieldsTable.getSelectionModel().clearSelection();
				fieldsTable.getSelectionModel().select(tempInd);
				draggedInd.set(tempInd);
				dragEvent.consume();
			});

			return row;
		});

		fieldsTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = fieldsTable.getSelectionModel();
				if (!selection.isEmpty()) {
					// var item = selection.getSelectedItem();
					// tableData.getFields().remove(item);
					selection.getSelectedItems().forEach(i -> tableData.getFields().remove(i));
					removeAction(selection.getSelectedItems());
				}
				break;
			}
		});

		// ==================================================================
		// Context Menu
		// ==================================================================

		// ============ Field Table =================
		ContextMenu fieldTableContextMenu = new ContextMenu();
		fieldsTable.setContextMenu(fieldTableContextMenu);

		MenuItem addField = new MenuItem("New Field");
		addField.setOnAction(event -> {
			Field newField = getTableDataDao().addNewField();
			newField.setFieldName("fieldName");
			newField.setParseAssociation("Assoc");
		});
		fieldTableContextMenu.getItems().add(addField);
		
		MenuItem removeField = new MenuItem("Remove");
		fieldsTable.getSelectionModel().getSelectedItems().addListener((InvalidationListener) change -> {
			removeField.setVisible(!fieldsTable.getSelectionModel().isEmpty());
		});
		
		removeField.setOnAction(event -> {
			removeAction(fieldsTable.getSelectionModel().getSelectedItems());
		});
		fieldTableContextMenu.getItems().add(removeField);

		// ============ Prefilter Table =================

		ContextMenu prefiltersContextMenu = new ContextMenu();
		prefiltersTable.setContextMenu(prefiltersContextMenu);

		MenuItem create = new MenuItem("New Prefilter");
		create.setOnAction(event -> {
			getTableDataDao().addNewPreFilter().setComparison(Comparison.EQUAL);
		});
		prefiltersContextMenu.getItems().add(create);
		
		MenuItem removePrefilter = new MenuItem("Remove");
		prefiltersTable.getSelectionModel().getSelectedItems().addListener((InvalidationListener) change -> {
			removePrefilter.setVisible(!prefiltersTable.getSelectionModel().isEmpty());
		});
		removePrefilter.setOnAction(event -> {
			removeAction(prefiltersTable.getSelectionModel().getSelectedItem());
		});
		prefiltersContextMenu.getItems().add(removePrefilter);
				
		// ============ Prefilter Value Table =================

		ContextMenu prefilterValuesContextMenu = new ContextMenu();
		prefilterValues.setContextMenu(prefilterValuesContextMenu);

		MenuItem createValue = new MenuItem("New Value");
		createValue.setOnAction(event -> {
			prefiltersTable.getSelectionModel().getSelectedItem().getValue().add(new SimpleStringProperty("val"));
		});
		prefilterValuesContextMenu.getItems().add(createValue);
		
		

		// ==================================================================
		// Init TreeTableView for prefilters
		// ==================================================================

		TableColumn<ParserFilter, String> attr = new TableColumn<>("Attribute");
		attr.setEditable(true);
		attr.setCellValueFactory(new PropertyValueFactory<>("attr"));
		attr.setCellFactory(TextFieldTableCell.forTableColumn());
		attr.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setAttr(editEvent.getNewValue());
		});
		prefiltersTable.getColumns().add(attr);

		TableColumn<ParserFilter, Comparison> comparison = new TableColumn<>("Comparison");
		comparison.setEditable(true);
		comparison.setCellValueFactory(new PropertyValueFactory<>("comparison"));
		comparison.setCellFactory(ComboBoxTableCell.forTableColumn(Comparison.values()));
		comparison.setOnEditCommit(event -> {
			event.getRowValue().setComparison(event.getNewValue());
		});
		prefiltersTable.getColumns().add(comparison);

		ObjectProperty<ParserFilter> draggedPrefill = new SimpleObjectProperty<>();
		IntegerProperty draggedIndPrefill = new SimpleIntegerProperty();

		prefiltersTable.setRowFactory(call -> {
			TableRow<ParserFilter> row = new TableRow<>();

			row.setOnDragDetected(dragEvent -> {
				draggedPrefill.set(row.getItem());
				draggedIndPrefill.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedIndPrefill.get() == row.getIndex() || draggedPrefill.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				prefiltersTable.getItems().remove(draggedPrefill.get());
				prefiltersTable.getItems().add(tempInd, draggedPrefill.get());
				prefiltersTable.getSelectionModel().clearSelection();
				prefiltersTable.getSelectionModel().select(tempInd);
				draggedIndPrefill.set(tempInd);
				dragEvent.consume();
			});

			return row;
		});

		prefiltersTable.getSelectionModel().selectedItemProperty().addListener((property, oldValue, newValue) -> {
			prefilterValues.setItems(newValue.valueProperty());
		});

		prefiltersTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = prefiltersTable.getSelectionModel();
				if (!selection.isEmpty()) {
					removeAction(selection.getSelectedItem());
				}
				break;
			}
		});

		// ========================== Prefilters Value ===========================

		TableColumn<StringProperty, String> value = new TableColumn<>("Value");
		value.setEditable(true);
		value.setCellFactory(TextFieldTableCell.forTableColumn());
		value.setCellValueFactory(s -> s.getValue());

		prefilterValues.getColumns().add(value);

		// ==================================================================
		// Set Items
		// ==================================================================

		fieldsTable.setItems(tableData.fieldsProperty());
		prefiltersTable.setItems(tableData.prefiltersProperty());

	}
	

	private void removeAction(List<Field> fields) {
		fields.forEach(i -> getTableData().getFields().remove(i));
	}

	private void removeAction(ParserFilter parserFilter) {
		tableData.getPrefilters().remove(parserFilter);
	}

	private TableData getTableData() {
		return tableData;
	}

	private TableDataDao getTableDataDao() {
		if (tableDataDao == null) {
			tableDataDao = new TableDataDao(getTableData());
		}

		return tableDataDao;
	}

}
