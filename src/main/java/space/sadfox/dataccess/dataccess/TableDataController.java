package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import jakarta.xml.bind.JAXBException;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntityInitializeException;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlLoader.DeleteFlag;
import space.sadfox.owlook.ui.base.ControllerException;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.owlook.ui.tools.MessageBox;
import space.sadfox.owlook.utils.Owlook;

public class TableDataController extends FXMLController {
  private class Actions {

    public void createParserEntity(ParserProvider parserProvider) {
      Owl<ParserEntity> parserEntity;
      try {
        parserEntity = OwlLoader.INSTANCE.createHiddenOwl(ParserEntity.class);
        parserEntity.entity().setParserProvider(parserProvider);
        getTableData().entity().getParsers().add(parserEntity);
      } catch (OwlEntityInitializeException e) {
        Owlook.registerException(e);
        MessageBox mBox = new MessageBox(AlertType.ERROR);
        mBox.setTitle("Create Error");
        mBox.setHeaderText("Owl Create Error");
        mBox.setContentText("An error occurred while initializing Owl");
        mBox.showAndWait();
      } catch (IOException | JAXBException | ReflectiveOperationException e) {
        Owlook.registerException(e);
      }
    }

    public void editParserEntity(Owl<ParserEntity> parserEntity) {
      Optional<ParserProvider> oParserProvider = parserEntity.entity().getParserProviderSafe();
      if (oParserProvider.isPresent()) {
        try {
          oParserProvider.get().createController(parserEntity, tableData).show();
        } catch (ControllerException e) {
          Owlook.registerException(e);
        }
      } else {
        MessageBox mBox = new MessageBox(AlertType.ERROR);
        mBox.setTitle("Parser Provider not found");
        mBox.setHeaderText("Parser Provider not found");
      }
    }

    public void deleteParserEntity(Owl<ParserEntity> parserEntity) {
      try {
        OwlLoader.INSTANCE.deleteOwl(parserEntity, Arrays.asList(getTableData()),
            DeleteFlag.NO_DEPENDENCIES);
        getTableData().entity().getParsers().remove(parserEntity);
      } catch (IOException e) {
        Owlook.registerException(e);
      }
    }

    public void duplicateParserEntity(Owl<ParserEntity> parserEntity) {
      try {
        Owl<ParserEntity> newParserEntity = OwlLoader.INSTANCE.duplicateOwl(parserEntity);
        newParserEntity.head().setTitle(parserEntity.head().getTitle() + "(copy)");
        getTableData().entity().getParsers().add(newParserEntity);
      } catch (IOException | JAXBException | ReflectiveOperationException e) {
        Owlook.registerException(e);
      } catch (OwlEntityInitializeException e) {
        Owlook.registerException(e);
        MessageBox mBox = new MessageBox(AlertType.ERROR);
        mBox.setTitle("Create Error");
        mBox.setHeaderText("Owl Create Error");
        mBox.setContentText("An error occurred while initializing Owl");
        mBox.showAndWait();
      }

    }

    private void removeField(List<Field> fields) {
      fields.forEach(i -> getTableData().entity().getFields().remove(i));
    }

    private void removeParserFilter(List<ParserFilter> parserFilters) {
      parserFilters.forEach(
          i -> fieldsTable.getSelectionModel().getSelectedItem().getParserFilters().remove(i));
    }
  }

  @FXML
  private TableView<Owl<ParserEntity>> parsersTableView;

  @FXML
  private Button editParserButton;

  @FXML
  private MenuButton createParserMenuButton;

  @FXML
  private Button deleteParserButton;

  @FXML
  private TableView<Field> fieldsTable;

  @FXML
  private Button loadDataButton;

  @FXML
  private TableView<ParserFilter> parserFilterTable;

  @FXML
  private TextField titleTextField;

  private final Actions ACTIONS = new Actions();
  private final Owl<TableData> tableData;
  private TableDataDao tableDataDao;

  public TableDataController(Owl<TableData> owl) throws ControllerException {
    super(ResourceTarget.class.getResource("fxml/edit-tabledata.fxml"));

    this.tableData = owl;

    init();
    initParserTable();
    initFieldsTable();
    initParserFilterTable();

    fieldsTable.setItems(getTableData().entity().fieldsProperty());
    parsersTableView.setItems(getTableData().entity().getParsers());
    Parsers.getParserProviders().forEach(parserProvider -> {
      MenuItem createParser = new MenuItem(parserProvider.getComponentName());
      createParser.setOnAction(event -> ACTIONS.createParserEntity(parserProvider));
      createParserMenuButton.getItems().add(createParser);
    });
  }

  private void init() {

    stageTitle.bind(Bindings.concat("Edit TableData [", tableData.head().titleProperty(), "]"));

    titleTextField.setText(getTableData().head().getTitle());
    titleTextField.textProperty().bindBidirectional(getTableData().head().titleProperty());

    editParserButton.setOnAction(event -> {
      var selectionModel = parsersTableView.getSelectionModel();
      if (!selectionModel.isEmpty()) {
        var parserEntity = parsersTableView.getSelectionModel().getSelectedItem();
        ACTIONS.editParserEntity(parserEntity);
      }
    });

    deleteParserButton.setOnAction(event -> {
      var selectionModel = parsersTableView.getSelectionModel();
      if (!selectionModel.isEmpty()) {
        var parserEntity = parsersTableView.getSelectionModel().getSelectedItem();
        ACTIONS.deleteParserEntity(parserEntity);
      }
    });

    loadDataButton.setOnAction(event -> {
      getTableDataDao().loadData();
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
        if (draggedInd.get() == row.getIndex() || draggedField.get() == null
            || row.getItem() == null)
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

    fieldsTable.getSelectionModel().selectedItemProperty()
        .addListener((property, oldValue, newValue) -> {
          if (newValue != null) {
            parserFilterTable.setItems(newValue.parserFiltersProperty());
          }
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
    fieldsTable.getSelectionModel().getSelectedItems()
        .addListener((InvalidationListener) change -> {
          removeField.setVisible(!fieldsTable.getSelectionModel().isEmpty());
        });

    removeField.setOnAction(event -> {
      ACTIONS.removeField(fieldsTable.getSelectionModel().getSelectedItems());
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
            ACTIONS.removeField(selection.getSelectedItems());
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
    parserFilterTable.getSelectionModel().getSelectedItems()
        .addListener((InvalidationListener) change -> {
          removePrefilter.setVisible(!parserFilterTable.getSelectionModel().isEmpty());
        });
    removePrefilter.setOnAction(event -> {
      ACTIONS.removeParserFilter(parserFilterTable.getSelectionModel().getSelectedItems());
    });
    prefiltersContextMenu.getItems().add(removePrefilter);

    // ==================================================================
    // Key bindings
    // ==================================================================

    parserFilterTable.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
      switch (keyEvent.getCode()) {
        case DELETE:
          ACTIONS.removeParserFilter(parserFilterTable.getSelectionModel().getSelectedItems());
          break;
        default:
          break;
      }
    });
  }

  private void initParserTable() {
    parsersTableView.setEditable(true);

    TableColumn<Owl<ParserEntity>, Boolean> enableColumn = new TableColumn<>("Enable");
    enableColumn.setEditable(true);
    enableColumn.setCellValueFactory(call -> call.getValue().entity().enableProperty());
    enableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enableColumn));
    parsersTableView.getColumns().add(enableColumn);

    TableColumn<Owl<ParserEntity>, String> providerColumn = new TableColumn<>("Provider");
    providerColumn.setCellValueFactory(callback -> {
      var oParserProvider = callback.getValue().entity().getParserProviderSafe();
      if (oParserProvider.isPresent()) {
        return new SimpleStringProperty(oParserProvider.get().getComponentName());
      } else {
        return new SimpleStringProperty("N/A");
      }
    });
    parsersTableView.getColumns().add(providerColumn);

    TableColumn<Owl<ParserEntity>, String> titleColumn = new TableColumn<>("Title");
    titleColumn.setCellValueFactory(call -> call.getValue().head().titleProperty());
    titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    parsersTableView.getColumns().add(titleColumn);

    parsersTableView.setRowFactory(call -> {
      TableRow<Owl<ParserEntity>> row = new TableRow<>();
      row.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
        if (mouseEvent.isPrimaryButtonDown() && (mouseEvent.getClickCount() == 2)) {
          ACTIONS.editParserEntity(row.getItem());
        }
      });
      ContextMenu rowContextMenu = new ContextMenu();
      row.setContextMenu(rowContextMenu);

      row.setOnContextMenuRequested(event -> {
        if (row.isEmpty()) {
          rowContextMenu.hide();
        }
      });

      MenuItem editParserEntityMenuItem = new MenuItem("Edit");
      editParserEntityMenuItem.setOnAction(event -> {
        ACTIONS.editParserEntity(row.getItem());
      });
      rowContextMenu.getItems().add(editParserEntityMenuItem);

      MenuItem duplicateParserEntityMenuItem = new MenuItem("Duplicate");
      duplicateParserEntityMenuItem.setOnAction(event -> {
        ACTIONS.duplicateParserEntity(row.getItem());
      });
      rowContextMenu.getItems().add(duplicateParserEntityMenuItem);

      MenuItem deleteParserEntityMenuItem = new MenuItem("Delete");
      deleteParserEntityMenuItem.setOnAction(event -> {
        ACTIONS.deleteParserEntity(row.getItem());
      });
      rowContextMenu.getItems().add(deleteParserEntityMenuItem);

      return row;
    });
  }

  private Owl<TableData> getTableData() {
    return tableData;
  }

  private TableDataDao getTableDataDao() {
    if (tableDataDao == null) {
      tableDataDao = new TableDataDao(getTableData());
    }

    return tableDataDao;
  }

}
