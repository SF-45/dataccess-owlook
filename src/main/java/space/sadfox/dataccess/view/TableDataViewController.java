package space.sadfox.dataccess.view;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

public class TableDataViewController extends Controller {

	@FXML
	private ChoiceBox<String> TDField;

	@FXML
	private Button add;

	@FXML
	private TextField friendlyName;

	@FXML
	private TableView<FieldView> viewTable;

	@FXML
	private TextField title;

	private TableData tableData;
	private TableDataView view;
	private TableDataViewDao viewDao;

	public TableDataViewController(TableDataView view, TableData tableData) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-view.fxml"));

		this.tableData = tableData;
		this.view = view;
		
		init();
		initViewTable();
		initKeyBind();

	}
	
	public TableDataViewController(TableDataView view) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-view.fxml"));

		this.tableData = null;
		this.view = view;
		
		init();
		initViewTable();
		initKeyBind();

	}

	private void init() {
		getStage().titleProperty().bind(Bindings.concat("Edit View [", getTableDataView().titleProperty(), "]"));

		title.textProperty().bindBidirectional(getTableDataView().titleProperty());

		try {
			TDField.getItems().addAll(getTableDataFields());
			if (TDField.getItems().size() > 0) {
				TDField.getSelectionModel().select(0);
			}
		} catch (Nullable e) {}
		

		add.setOnAction(event -> {
			getTableDataViewDao().addNewField(TDField.getValue(), friendlyName.getText());
		});
	}

	@SuppressWarnings("unchecked")
	private void initViewTable() {
		TableColumn<FieldView, String> field = new TableColumn<>("Field");
		field.setCellValueFactory(new PropertyValueFactory<>("fieldName"));
		try {
			field.setCellFactory(ComboBoxTableCell.forTableColumn(getTableDataFields()));
		} catch (Nullable e) {
			field.setCellFactory(TextFieldTableCell.forTableColumn());
		}
		field.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFieldName(editEvent.getNewValue());
		});

		TableColumn<FieldView, String> friendlyName = new TableColumn<>("Friendly Name");
		friendlyName.setCellValueFactory(new PropertyValueFactory<>("friendlyFieldName"));
		friendlyName.setCellFactory(TextFieldTableCell.forTableColumn());
		friendlyName.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setFriendlyFieldName(editEvent.getNewValue());
		});

		TableColumn<FieldView, Boolean> visible = new TableColumn<>("Visible");
		visible.setCellValueFactory(new PropertyValueFactory<>("visible"));
		visible.setCellFactory(call -> {
			CheckBoxTableCell<FieldView, Boolean> cell = new CheckBoxTableCell<>();
			return cell;
		});
		// visible.setCellFactory(CheckBoxTableCell.forTableColumn(visible));

		viewTable.getColumns().addAll(field, friendlyName, visible);
		viewTable.setItems(getTableDataView().fieldViewsProperty());

		ObjectProperty<FieldView> draggedView = new SimpleObjectProperty<>();
		IntegerProperty draggedInd = new SimpleIntegerProperty();

		viewTable.setRowFactory(call -> {
			TableRow<FieldView> row = new TableRow<>();

			row.setOnDragDetected(dragEvent -> {
				draggedView.set(row.getItem());
				draggedInd.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedInd.get() == row.getIndex() || draggedView.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				viewTable.getItems().remove(draggedView.get());
				viewTable.getItems().add(tempInd, draggedView.get());
				viewTable.getSelectionModel().select(tempInd);
				draggedInd.set(tempInd);
				dragEvent.consume();
			});

			return row;
		});
	}

	private void initKeyBind() {
		getParent().addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case Z:
				if (keyEvent.isControlDown()) {
					getTableDataView().getChangeHistory().back();
				}
				break;
			default:
				break;
			}
		});

		viewTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = viewTable.getSelectionModel();
				if (!selection.isEmpty()) {
					var item = selection.getSelectedItem();
					getTableDataView().getFieldViews().remove(item);
				}
				break;
			default:
				break;
			}
		});
	}

	private TableData getTableData() throws Nullable {
		if (tableData == null)
			throw new Nullable();
		return tableData;
	}

	private TableDataView getTableDataView() {
		return view;
	}

	private TableDataViewDao getTableDataViewDao() {
		if (viewDao == null) {
			viewDao = new TableDataViewDao(getTableDataView());
		}
		return viewDao;
	}

	private ObservableList<String> getTableDataFields() throws Nullable {
		List<String> fields = getTableData().getFields().stream().map(f -> f.getFieldName()).collect(Collectors.toList());
		return FXCollections.observableList(fields);
	}

}
