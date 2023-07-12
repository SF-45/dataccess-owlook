package space.sadfox.dataccess.filter;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.dataccess.dataccess.Comparison;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

public class TableDataFilterController extends Controller {

	@FXML
	private SplitMenuButton addto;

	@FXML
	private ComboBox<Comparison> compareComboBox;

	@FXML
	private ComboBox<String> dateFieldComboBox;

	@FXML
	private TableView<Filter> filtersTableView;

	@FXML
	private TextField titleTextBox;

	@FXML
	private TextField valueTextBox;

	private TableDataFilter filter;
	private TableData tableData;
	private ObjectProperty<NextComp> currectComp = new SimpleObjectProperty<>(NextComp.AND);

	public TableDataFilterController(TableDataFilter filter, TableData tableData) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-filter.fxml"));
		this.filter = filter;
		this.tableData = tableData;
		
		init();
		initFiltersTableView();
		initKeyBind();

	}
	
	public TableDataFilterController(TableDataFilter filter) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-filter.fxml"));
		this.filter = filter;
		this.tableData = null;
		
		init();
		initFiltersTableView();
		initKeyBind();

	}

	private void init() {
		stageTitle.bind(Bindings.concat("Edit Filter [", getTableDataFilter().titleProperty(), "]"));
		
		titleTextBox.textProperty().bindBidirectional(getTableDataFilter().titleProperty());

		try {
			dateFieldComboBox.getItems().addAll(getTableDataFields());
			if (dateFieldComboBox.getItems().size() > 0) {
				dateFieldComboBox.getSelectionModel().select(0);
			}
		} catch (Nullable e) {}
		
		compareComboBox.getItems().addAll(Comparison.values());
		compareComboBox.getSelectionModel().select(0);

		currectComp.addListener((property, oldValue, newValue) -> {
			addto.setText(newValue.toString());
		});

		MenuItem and = new MenuItem("AND");
		and.setOnAction(event -> currectComp.set(NextComp.AND));
		MenuItem or = new MenuItem("OR");
		or.setOnAction(event -> currectComp.set(NextComp.OR));
		addto.getItems().addAll(and, or);
		addto.setText(currectComp.get().toString());
		addto.setOnAction(event -> {
			Filter newFilter = new Filter();
			newFilter.setField(dateFieldComboBox.getValue());
			newFilter.setComparision(compareComboBox.getValue());
			newFilter.setValue(valueTextBox.getText());
			newFilter.setNext(currectComp.get());
			getTableDataFilter().getFilters().add(newFilter);
		});
	}

	@SuppressWarnings("unchecked")
	private void initFiltersTableView() {
		TableColumn<Filter, String> field = new TableColumn<>("Field");
		field.setEditable(true);
		field.setSortable(false);
		field.setCellValueFactory(new PropertyValueFactory<>("field"));
		try {
			field.setCellFactory(ComboBoxTableCell.forTableColumn(getTableDataFields()));
		} catch (Nullable e) {
			field.setCellFactory(TextFieldTableCell.forTableColumn());
		}
		field.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setField(editEvent.getNewValue());
		});

		TableColumn<Filter, Comparison> comparison = new TableColumn<>("Comparison");
		comparison.setEditable(true);
		comparison.setSortable(false);
		comparison.setCellValueFactory(new PropertyValueFactory<>("comparision"));
		comparison.setCellFactory(ComboBoxTableCell.forTableColumn(Comparison.values()));
		comparison.setOnEditCommit(event -> {
			event.getRowValue().setComparision(event.getNewValue());
		});

		TableColumn<Filter, String> value = new TableColumn<>("Value");
		value.setEditable(true);
		value.setSortable(false);
		value.setCellValueFactory(new PropertyValueFactory<>("value"));
		value.setCellFactory(TextFieldTableCell.forTableColumn());
		value.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setValue(editEvent.getNewValue());
		});

		TableColumn<Filter, NextComp> next = new TableColumn<>("Next");
		next.setEditable(true);
		next.setSortable(false);
		next.setCellValueFactory(new PropertyValueFactory<>("next"));
		next.setCellFactory(ComboBoxTableCell.forTableColumn(NextComp.values()));
		next.setOnEditCommit(editEvent -> {
			editEvent.getRowValue().setNext(editEvent.getNewValue());
		});

		filtersTableView.getColumns().addAll(field, comparison, value, next);
		filtersTableView.setItems(getTableDataFilter().filtersProperty());

		ObjectProperty<Filter> draggedFilter = new SimpleObjectProperty<>();
		IntegerProperty draggedInd = new SimpleIntegerProperty();

		filtersTableView.setRowFactory(call -> {
			TableRow<Filter> row = new TableRow<>();

			row.setOnDragDetected(dragEvent -> {
				draggedFilter.set(row.getItem());
				draggedInd.set(row.getIndex());
				row.startFullDrag();
				dragEvent.consume();
			});
			row.setOnMouseDragOver(dragEvent -> {
				if (draggedInd.get() == row.getIndex() || draggedFilter.get() == null || row.getItem() == null)
					return;
				int tempInd = row.getIndex();
				filtersTableView.getItems().remove(draggedFilter.get());
				filtersTableView.getItems().add(tempInd, draggedFilter.get());
				filtersTableView.getSelectionModel().select(tempInd);
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
					getTableDataFilter().getChangeHistory().back();
				}
				break;
			default:
				break;
			}
		});

		filtersTableView.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
			switch (keyEvent.getCode()) {
			case DELETE:
				var selection = filtersTableView.getSelectionModel();
				if (!selection.isEmpty()) {
					var item = selection.getSelectedItem();
					getTableDataFilter().getFilters().remove(item);
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

	private TableDataFilter getTableDataFilter() {
		return filter;
	}

	private ObservableList<String> getTableDataFields() throws Nullable {
		List<String> fields = getTableData().getFields().stream().map(f -> f.getFieldName())
				.collect(Collectors.toList());
		return FXCollections.observableList(fields);

	}

}
