package space.sadfox.dataccess.view;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.Stack;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyEvent;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.base.jaxb.ChangeHistoryListener;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.StageFactory;

public class TableViewForTableData extends TableView<DataEntity> {
	
	public class FindActionDelay {
		
		private Thread timer;
		private final Runnable action;
		private final LongProperty time = new SimpleLongProperty(0);
		private final LongProperty delay = new SimpleLongProperty(0);
		
		private FindActionDelay() {
			action = () -> {
				getItems().removeListener(searchChangeListener);
				if (currentSearchText.get() == "" || currentSearchText.get() == null) {
					getItems().clear();
					getItems().addAll(currentItems);
				} else {
					getItems().clear();
					getItems().addAll(currentItems.filtered(dataEntity -> dataEntity.findLike(currentSearchText.get())));
				}
				getItems().addListener(searchChangeListener);
				time.set(0);
				refresh();
				System.out.println(currentSearchText.get());
			};
		}
		
		private void findAction() {
			time.set(0);
			if (timer == null || !timer.isAlive()) {
				timer= new Thread(() -> {
					for (;time.get() <= delay.get(); time.set(time.get() + 1)) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							Owlook.registerException(2, e);
						}
					}
					Platform.runLater(action);
				});
				timer.start();
				
			}
		}
		
		public double getTime() {
			return time.get();
		}
		
		public ReadOnlyLongProperty timeProperty() {
			return time;
		}
		
		public double getDelay() {
			return delay.get();
		}
		
		public void setDelay(long millis) {
			this.delay.set(millis);
		}
		
		public LongProperty delayProperty() {
			return delay;
		}
		
		public DoubleBinding progressProperty() {
			return Bindings.createDoubleBinding(() -> {
				return delay.get() <= 0 ? 0d : time.doubleValue() / delay.doubleValue();
			}, time, delay);
		}
	}

	private final Stack<ObservableList<DataEntity>> historyFind = new Stack<>();
	private ObservableList<DataEntity> currentItems = FXCollections.observableArrayList();
	private ListChangeListener<DataEntity> searchChangeListener;
	private StringProperty currentSearchText = new SimpleStringProperty("");
	private StringProperty searchTextHistory = new SimpleStringProperty("");
	private ObservableList<String> searchTextHistoryList = FXCollections.observableArrayList();
	private final String SEARCH_HISTORY_DELIMMER = " -> ";
	private final FindActionDelay findActionDelay = new FindActionDelay();

	private ContextMenu contextMenu = new ContextMenu();
	private MenuItem undo = new MenuItem("Undo");
	private IntegerProperty indProperty = new SimpleIntegerProperty();

	private Owl<TableDataView> currentViewOwl;
	private ChangeHistoryListener<OwlEntity> changeListener;

	public TableViewForTableData() {

		setContextMenu(contextMenu);
		setEditable(true);
		getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
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
		
		
		initContextMenu();
		initSearch();
	}
	
	
	private void initContextMenu() {
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

			currentItems = historyFind.pop();
			
			if (searchTextHistoryList.size() != 0) {
				setCurrentSearchText(searchTextHistoryList.remove(searchTextHistoryList.size() - 1));
			} else {
				setCurrentSearchText("");
			}

			if (historyFind.size() == 0) {
				undo.setVisible(false);
			} else {

			}
		});
		getContextMenu().getItems().add(undo);
	}


	private void initSearch() {
		searchChangeListener = change -> {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved()) {
					itemsChangeAction();
				}
			}
		};
		
		getItems().addListener(searchChangeListener);
		itemsProperty().addListener((property, oldValue, newValue) -> {
			oldValue.removeListener(searchChangeListener);
			newValue.addListener(searchChangeListener);
			itemsChangeAction();
			
		});
		searchTextHistoryList.addListener((InvalidationListener) change -> {
			searchTextHistory.set(String.join(SEARCH_HISTORY_DELIMMER, searchTextHistoryList));
		});
		
		currentSearchText.addListener((property, oldValue, newValue) -> {
			if (oldValue != null && oldValue.equals(newValue)) return;
			findActionDelay.findAction();
		});
		
	}

	private void updateDataView() {
		getColumns().clear();
		currentViewOwl.entity().getFieldViews().forEach(this::createColumn);
	}

	private void createColumn(FieldView field) {
		TableColumn<DataEntity, String> column = new TableColumn<>();
		column.setVisible(field.getVisible());
		
		if (field.getFriendlyFieldName() == null || field.getFriendlyFieldName().equals("")) {
			column.setText(field.getFieldName());
		} else {
			column.setText(field.getFriendlyFieldName());
		}
		

		// column.visibleProperty().bind(field.visibleProperty());
		// column.textProperty().bind(field.friendlyFieldNameProperty());

		column.setCellValueFactory(
				dataEntity -> new SimpleStringProperty(dataEntity.getValue().getValue(field.getFieldName())));
		column.setCellFactory(teTableColumn -> new TextFieldTableCell<>());

		getColumns().add(column);
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
	
	private void itemsChangeAction() {
		historyFind.clear();
		currentItems.clear();
		currentItems.addAll(getItems());
		searchTextHistoryList.clear();
		currentSearchText.set("");
	}
	
	public void nextFind() {
		if (currentSearchText.get() == "") return;
		historyFind.push(FXCollections.observableArrayList(currentItems));
		currentItems.clear();
		currentItems.addAll(getItems());
		undo.setVisible(true);
		searchTextHistoryList.add(currentSearchText.get());
	}


	public void setTableDataView(Owl<TableDataView> tableDataViewOwl) {
		if (currentViewOwl != null) {
			currentViewOwl.entity().getChangeHistory().removeListener(changeListener);
		}
		currentViewOwl = tableDataViewOwl;
		changeListener = change -> {
			if (change.wasModify()) {
				updateDataView();
			}
		};
		tableDataViewOwl.entity().getChangeHistory().addListener(changeListener);
		updateDataView();
	}


	public String getCurrentSearchText() {
		return currentSearchText.get();
	}
	
	public void setCurrentSearchText(String findable) {
		currentSearchText.set(findable);
	}
	
	public StringProperty currentSearchTextProperty() {
		return currentSearchText;
	}

	public String getSearchTextHistory() {
		return searchTextHistory.get();
	}
	public ReadOnlyStringProperty searchTextHistoryProperty() {
		return searchTextHistory;
	}
	
	public FindActionDelay getFindActionDelay() {
		return findActionDelay;
	}

}
