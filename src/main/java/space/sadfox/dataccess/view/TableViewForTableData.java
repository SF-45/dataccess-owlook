package space.sadfox.dataccess.view;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Stack;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.jaxb.ChangeListener;
import space.sadfox.owlook.utils.StageFactory;

public class TableViewForTableData extends TableView<DataEntity> {
	
	private final Stack<ObservableList<DataEntity>> historyFind = new Stack<>();
    private ContextMenu contextMenu = new ContextMenu();
    private MenuItem undo = new MenuItem("Undo");
    private Menu showHideMenu = new Menu("Show/Hide");
    private SimpleStringProperty historyFindText = new SimpleStringProperty("");
    private SimpleStringProperty prefHistoryText = new SimpleStringProperty();
    private SimpleStringProperty fullFindText = new SimpleStringProperty();
    private SimpleIntegerProperty indProperty = new SimpleIntegerProperty();
    
    private TableDataView currentView;
    private ChangeListener changeListener;
     
    
    private Text selected = new Text("0");
    private Text entityCount = new Text("0");
    private TextFlow textFlow = new TextFlow();

	public TableViewForTableData() {
		setContextMenu(contextMenu);
        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        //TODO: начал делать счет элементов TableData
        //textFlow.getChildren().addAll(selected, new Text("0"), entityCount);
        //selected.textProperty().bind(this.getSelectionModel().selectedItemProperty());
        
        MenuItem copy = new MenuItem("Copy");
        copy.setOnAction(e -> copyAction());
        contextMenu.getItems().add(copy);

        MenuItem copyAll = new MenuItem("CopyAll");
        copyAll.setOnAction(e -> {
            getSelectionModel().selectAll();
            copyAction();
        });
        contextMenu.getItems().add(copyAll);
        
        MenuItem openInWindow = new MenuItem("Open in window");
        openInWindow.setOnAction(e -> {
            if (getSelectionModel().getSelectedItems().size() > 0) {
                getSelectionModel().getSelectedItems().forEach(de -> {
                    DataEntityViewer viewer = new DataEntityViewer(de);
                    StageFactory.INSTANCE.registerStage(viewer);
                    viewer.show();
                });
            }
        });
        contextMenu.getItems().add(openInWindow);
        
        undo.setVisible(false);
        undo.setOnAction(e -> {
            if (historyFind.size() == 0) return;
            setItems(historyFind.pop());
            if (historyFind.size() == 0) {
                historyFindText.set("");
                undo.setVisible(false);
            } else {
                String tabText = historyFindText.get();
                historyFindText.set(tabText.substring(0, tabText.lastIndexOf(" -> ")));
            }
        });
        getContextMenu().getItems().add(undo);
        
        InvalidationListener changeListener = prop -> {
            fullFindText.set(prefHistoryText.get() + historyFindText.get());
        };
        historyFindText.addListener(changeListener);
        prefHistoryText.addListener(changeListener);
        
        contextMenu.getItems().add(showHideMenu);
        

        this.setRowFactory(dataEntityTableView -> {
            TableRow<DataEntity> row = new TableRow<>();

            row.setOnDragDetected(event -> {
                if (!row.isEmpty() && event.isPrimaryButtonDown()) {
                    indProperty.set(row.getIndex());
                    row.startFullDrag();
                }
            });
            row.setOnMouseDragOver(event -> {
                getSelectionModel().clearSelection();

                int firstInd = indProperty.get();
                int lastInd = row.getIndex();
                TableView.TableViewSelectionModel<DataEntity> selection = getSelectionModel();

                if (firstInd < lastInd) {
                    selection.selectRange(firstInd, lastInd + 1);
                } else if (firstInd > lastInd) {
                    selection.selectRange(firstInd, lastInd - 1);
                } else {
                    selection.select(firstInd);
                }

            });
            return row;
        });
        
        addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            switch (keyEvent.getCode()) {
                case C:
                    if (keyEvent.isControlDown()) {
                        copyAction();
                    }
			default:
				break;
            }
        });
        
	}
	
	public void setTableDataView(TableDataView tableDataView) {
		if (currentView != null) {
			currentView.getChangeHistory().removeChangeListener(changeListener);
		}
		currentView = tableDataView;
		changeListener = () -> {
			updateDataView();
		};
		tableDataView.getChangeHistory().addChangeListener(changeListener);
		updateDataView();
	}
	
	private void updateDataView() {
		getColumns().clear();
		currentView.getFieldViews().forEach(this::createColumn);
	}
	
	private void createColumn(FieldView field) {
        TableColumn<DataEntity, String> column = new TableColumn<>();
        column.setVisible(field.getVisible());
        column.setText(field.getFriendlyFieldName());
        
        //column.visibleProperty().bind(field.visibleProperty());
        //column.textProperty().bind(field.friendlyFieldNameProperty());

        column.setCellValueFactory(dataEntity -> new SimpleStringProperty(dataEntity.getValue().getValue(field.getFieldName())));
        column.setCellFactory(teTableColumn -> new TextFieldTableCell<>());

        getColumns().add(column);
    }
	
	public void findAction(String findable) {
        if (getItems() == null && getItems().size() == 0) return;
        historyFind.push(getItems());
        historyFindText.set(historyFindText.get() + " -> \'" + findable + "\'");
        setItems(getItems().filtered(dataEntity -> dataEntity.findLike(findable)));
        undo.setVisible(true);
        refresh();
    }
	
	public String getPrefHistoryText() {
        return prefHistoryText.get();
    }
	
	public void setPrefHistoryText(String prefHistoryText) {
        this.prefHistoryText.set(prefHistoryText);
    }

    public StringProperty prefHistoryTextProperty() {
        return prefHistoryText;
    }
    
    public String getFullFindText() {
        return fullFindText.get();
    }

    public StringProperty fullFindTextProperty() {
        return fullFindText;
    }

	private void copyAction() {
	    StringBuilder builder = new StringBuilder();
	    TableView.TableViewSelectionModel<DataEntity> selection = this.getSelectionModel();
	    if (selection.getSelectedItems().size() == this.getItems().size()) {
	        selection.getSelectedItem().getFields().forEach(field -> {
	            builder.append(field.getFieldName() + '\t');
	        });
	        builder.append('\n');
	    }
	    selection.getSelectedItems().forEach(dataEntity -> {
	        dataEntity.getFields().forEach(field -> builder.append(dataEntity.getValue(field) + '\t'));
	        builder.append('\n');
	    });
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(builder.toString()), null);
	
	}
	
	

}
