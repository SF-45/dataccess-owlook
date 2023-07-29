package space.sadfox.dataccess.dataccess;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.OwlLogger;
import space.sadfox.owlook.utils.StageFactory;

public class TableDataController extends Controller {

	@FXML
	private CheckBox autoUpdate;

	@FXML
	private Button configParser;

	@FXML
	private TableView<Field> fieldsTable;

	@FXML
	private ChoiceBox<ParserProvider> parserChoiseBox;

	@FXML
	private TableView<ParserFilter> parserFilterTable;

	@FXML
	private TextField pathToDataTextField;

	@FXML
	private TextField titleTextField;

	@FXML
	private Button selectPath;

	private TableData tableData;
	private TableDataDao tableDataDao;

	public TableDataController(TableData tableData) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-tabledata.fxml"));

		this.tableData = tableData;

		init();
		initFieldsTable();
		initParserFilterTable();

		fieldsTable.setItems(getTableData().fieldsProperty());
		parserChoiseBox.setItems(FXCollections.observableList(TableDatas.getParserProviders()));
		try {
			ParserProvider selectProvider = getTableData().getParserSafe();
			for (ParserProvider provider : parserChoiseBox.getItems()) {
				if (provider.getComponentName().equals(selectProvider.getComponentName())) {
					parserChoiseBox.getSelectionModel().select(provider);
					break;
				}
			}
		} catch (ParserProviderNotFound e) {
		}

	}

	private void init() {

		titleTextField.setText(getTableData().getTitle());
		titleTextField.textProperty().bindBidirectional(getTableData().titleProperty());

		autoUpdate.setSelected(getTableData().getAutoUpdate());
		autoUpdate.selectedProperty().bindBidirectional(getTableData().autoUpdateProperty());

		pathToDataTextField.setText(getTableData().getPathToData());
		pathToDataTextField.textProperty().bindBidirectional(getTableData().pathToDataProperty());

		parserChoiseBox.setConverter(new StringConverter<ParserProvider>() {

			@Override
			public String toString(ParserProvider object) {
				if (object == null)
					return "Not found";
				return object.getComponentName();
			}

			@Override
			public ParserProvider fromString(String string) {
				// TODO Auto-generated method stub
				return null;
			}

		});
		parserChoiseBox.getSelectionModel().selectedItemProperty().addListener((property, oldValue, newValue) -> {
			if (oldValue == newValue)
				return;
			getTableData().setParser(newValue);
		});

		configParser.setOnAction(event -> {
			try {
				parserChoiseBox.getSelectionModel().getSelectedItem()
						.getConfigController(getTableData()).show();
			} catch (IOException e) {
				OwlLogger.registerException(1, e);
			}
		});

		selectPath.setOnAction(event -> {
			FileChooser chooser = new FileChooser();
			if (pathToDataTextField.getText() != null && pathToDataTextField.getText() != "") {
				Path pathToData = Path.of(pathToDataTextField.getText());
				if (pathToData.getParent() != null && Files.exists(pathToData.getParent())) {
					chooser.setInitialDirectory(pathToData.getParent().toFile());
				}
			}
			File chooseFile = chooser.showOpenDialog(StageFactory.INSTANCE.getCurrentStage());
			if (chooseFile == null)
				return;
			pathToDataTextField.setText(chooseFile.getPath());
		});

	}

	private void initFieldsTable() {
		fieldsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		TableColumn<Field, String> fieldName = new TableColumn<>("Field");
		fieldName.setEditable(true);
		fieldName.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		fieldName.setCellFactory(TextFieldTableCell.forTableColumn());
		fieldName.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFieldName(editEvent.getNewValue());
		});
		fieldsTable.getColumns().add(fieldName);

		TableColumn<Field, String> friendlyFieldName = new TableColumn<>("Friendly Name");
		friendlyFieldName.setEditable(true);
		friendlyFieldName.setCellValueFactory(new PropertyValueFactory<>("friendlyFieldName"));
		friendlyFieldName.setCellFactory(TextFieldTableCell.forTableColumn());
		friendlyFieldName.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFriendlyFieldName(editEvent.getNewValue());
		});
		fieldsTable.getColumns().add(friendlyFieldName);

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

		fieldsTable.getSelectionModel().selectedItemProperty().addListener((property, oldValue, newValue) -> {
			parserFilterTable.setItems(newValue.parserFiltersProperty());
		});

		// ==================================================================
		// Context Menu
		// ==================================================================

		ContextMenu fieldTableContextMenu = new ContextMenu();
		fieldsTable.setContextMenu(fieldTableContextMenu);

		MenuItem addField = new MenuItem("New Field");
		addField.setOnAction(event -> {
			Field newField = getTableDataDao().addNewField();
			newField.setFieldName("newField");
		});
		fieldTableContextMenu.getItems().add(addField);

		MenuItem removeField = new MenuItem("Remove");
		fieldsTable.getSelectionModel().getSelectedItems().addListener((InvalidationListener) change -> {
			removeField.setVisible(!fieldsTable.getSelectionModel().isEmpty());
		});

		removeField.setOnAction(event -> {
			removeField(fieldsTable.getSelectionModel().getSelectedItems());
		});
		fieldTableContextMenu.getItems().add(removeField);

		// ==================================================================
		// Key bindings
		// ==================================================================

		fieldsTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = fieldsTable.getSelectionModel();
				if (!selection.isEmpty()) {
					// var item = selection.getSelectedItem();
					// tableData.getFields().remove(item);
					// selection.getSelectedItems().forEach(i ->
					// getTableData().getFields().remove(i));
					removeField(selection.getSelectedItems());
				}
				break;
			default:
				break;
			}
		});
	}

	private void initParserFilterTable() {
		TableColumn<ParserFilter, Comparison> comparison = new TableColumn<>("Comparison");
		comparison.setEditable(true);
		comparison.setCellValueFactory(new PropertyValueFactory<>("comparison"));
		comparison.setCellFactory(ComboBoxTableCell.forTableColumn(Comparison.values()));
		comparison.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setComparison(editEvent.getNewValue());
		});
		parserFilterTable.getColumns().add(comparison);

		TableColumn<ParserFilter, String> value = new TableColumn<>("Value");
		value.setEditable(true);
		value.setCellValueFactory(new PropertyValueFactory<>("value"));
		value.setCellFactory(TextFieldTableCell.forTableColumn());
		value.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setValue(editEvent.getNewValue());
		});
		parserFilterTable.getColumns().add(value);

		// ==================================================================
		// Context Menu
		// ==================================================================

		ContextMenu prefiltersContextMenu = new ContextMenu();
		parserFilterTable.setContextMenu(prefiltersContextMenu);

		MenuItem create = new MenuItem("New Parser Filter");
		create.setOnAction(event -> {
			fieldsTable.getSelectionModel().getSelectedItem().getParserFilters().add(new ParserFilter());
		});
		prefiltersContextMenu.getItems().add(create);

		MenuItem removePrefilter = new MenuItem("Remove");
		parserFilterTable.getSelectionModel().getSelectedItems().addListener((InvalidationListener) change -> {
			removePrefilter.setVisible(!parserFilterTable.getSelectionModel().isEmpty());
		});
		removePrefilter.setOnAction(event -> {
			removeParserFilter(parserFilterTable.getSelectionModel().getSelectedItems());
		});
		prefiltersContextMenu.getItems().add(removePrefilter);

		// ==================================================================
		// Key bindings
		// ==================================================================

		parserFilterTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				removeParserFilter(parserFilterTable.getSelectionModel().getSelectedItems());
				break;
			default:
				break;
			}
		});
	}

	private void removeField(List<Field> fields) {
		fields.forEach(i -> getTableData().getFields().remove(i));
	}

	private void removeParserFilter(List<ParserFilter> parserFilters) {
		parserFilters.forEach(i -> fieldsTable.getSelectionModel().getSelectedItem().getParserFilters().remove(i));
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
