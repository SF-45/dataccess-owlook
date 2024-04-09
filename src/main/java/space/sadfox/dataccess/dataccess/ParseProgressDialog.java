package space.sadfox.dataccess.dataccess;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.codicons.Codicons;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker.State;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import space.sadfox.owlook.base.owl.Owl;

public class ParseProgressDialog {
  private enum ProgressIcons {
    DONE(Codicons.CHECK, Color.GREEN), ERROR(Codicons.ERROR, Color.RED);

    private Ikon icon;
    private Paint color;

    private ProgressIcons(Ikon icon, Paint color) {
      this.icon = icon;
      this.color = color;
    }

    public Ikon getIkon() {
      return icon;
    }

    public Paint getPaint() {
      return color;
    }
  }

  private final Owl<TableData> targetTableData;

  private final DialogPane dialogPane = new DialogPane();
  private final Dialog<ButtonType> dialog = new Dialog<>();

  private final TableView<ParserTaskDecorator> parserTaskTableView = new TableView<>();
  private final TableView<DataEntity> dataEntityTableView = new TableView<>();

  private final Button saveButton;
  private final Button cancelButton;

  public static final ButtonType SAVE = new ButtonType("Save", ButtonData.OK_DONE);
  public static final ButtonType CANCEL = ButtonType.CANCEL;

  private final IntegerProperty tasksFinish = new SimpleIntegerProperty();

  public ParseProgressDialog(Owl<TableData> tableData, ParserTask... tasks) {
    this.targetTableData = tableData;
    initTaskTableView();
    initDataTable();
    dialog.setDialogPane(dialogPane);
    dialogPane.getButtonTypes().addAll(SAVE, CANCEL);
    saveButton = (Button) dialogPane.lookupButton(SAVE);
    cancelButton = (Button) dialogPane.lookupButton(CANCEL);

    saveButton.disableProperty().bind((Bindings.createBooleanBinding(() -> {
      return parserTaskTableView.getItems().size() > tasksFinish.get();
    }, tasksFinish, parserTaskTableView.getItems())));

    cancelButton.setOnAction(event -> {
      for (ParserTask parserTask : tasks) {
        parserTask.cancel();
      }
    });

    SplitPane splitPane = new SplitPane();
    dialogPane.setContent(splitPane);
    splitPane.setOrientation(Orientation.HORIZONTAL);
    splitPane.getItems().add(parserTaskTableView);

    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    splitPane.getItems().add(tabPane);

    Tab logTab = new Tab("Log");
    tabPane.getTabs().add(logTab);

    TextArea logTextArea = new TextArea();
    logTextArea.setEditable(false);
    parserTaskTableView.getSelectionModel().selectedItemProperty()
        .addListener((property, oldValue, newValue) -> {
          if (newValue != null) {
            logTextArea.textProperty().bind(newValue.log);
          } else {
            logTextArea.textProperty().unbind();
            logTextArea.setText("");
          }
        });

    logTab.setContent(logTextArea);

    Tab dataEntityTab = new Tab();
    tabPane.getTabs().add(dataEntityTab);
    IntegerProperty dataSize = new SimpleIntegerProperty(0);
    dataEntityTableView.getItems().addListener((InvalidationListener) change -> {
      dataSize.set(dataEntityTableView.getItems().size());
    });
    dataEntityTab.textProperty().bind(Bindings.concat("Data (", dataSize, ")"));
    dataEntityTab.setContent(dataEntityTableView);

    for (ParserTask parserTask : tasks) {
      addTask(parserTask);
    }
  }

  public void addTask(ParserTask parserTask) {
    parserTask.stateProperty().addListener((property, oldValue, newValue) -> {
      if (parserTask.isDone()) {
        tasksFinish.set(tasksFinish.get() + 1);
        List<DataEntity> rezult = parserTask.getValue();
        if (rezult != null) {
          dataEntityTableView.getItems().addAll(rezult);
        }
      }
    });
    parserTaskTableView.getItems().add(new ParserTaskDecorator(parserTask));
  }

  public void addAllTasks(ParserTask... tasks) {
    for (ParserTask task : tasks) {
      addTask(task);
    }
  }

  public void addAllTasks(Collection<ParserTask> tasks) {
    tasks.forEach(this::addTask);
  }

  public Optional<ButtonType> showAndWait() {
    return dialog.showAndWait();
  }

  public StringProperty titleProperty() {
    return dialog.titleProperty();
  }

  public String getTitle() {
    return dialog.getTitle();
  }

  public void setTitle(String title) {
    dialog.setTitle(title);
  }

  private void initTaskTableView() {
    TableColumn<ParserTaskDecorator, Node> iconColumn = new TableColumn<>();
    parserTaskTableView.getColumns().add(iconColumn);
    iconColumn.setCellValueFactory(call -> call.getValue().icon);
    iconColumn.setCellFactory(call -> {
      TableCell<ParserTaskDecorator, Node> cell = new TableCell<>() {
        @Override
        protected void updateItem(Node icon, boolean empty) {
          super.updateItem(icon, empty);

          if (icon == null || empty) {
            setGraphic(null);
          } else {
            setGraphic(icon);
          }
        }
      };
      cell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
      return cell;
    });

    TableColumn<ParserTaskDecorator, String> parserProviderColumn = new TableColumn<>("Provider");
    parserTaskTableView.getColumns().add(parserProviderColumn);
    parserProviderColumn.setCellValueFactory(call -> {
      String providerName;
      var oParserProvider = call.getValue().T.getParserProvider();
      if (oParserProvider.isPresent()) {
        providerName = oParserProvider.get().getComponentName();
      } else {
        providerName = "N/A";
      }
      return new SimpleStringProperty(providerName);
    });

    TableColumn<ParserTaskDecorator, String> titleColumn = new TableColumn<>("Title");
    parserTaskTableView.getColumns().add(titleColumn);
    titleColumn
        .setCellValueFactory(call -> call.getValue().T.getParserEntity().head().titleProperty());

    TableColumn<ParserTaskDecorator, Double> progressColumn = new TableColumn<>("Progress");
    parserTaskTableView.getColumns().add(progressColumn);
    progressColumn.setCellValueFactory(call -> call.getValue().T.progressProperty().asObject());
    progressColumn.setCellFactory(ProgressBarTableCell.forTableColumn());

    TableColumn<ParserTaskDecorator, String> messageColumn = new TableColumn<>("Message");
    parserTaskTableView.getColumns().add(messageColumn);
    messageColumn.setCellValueFactory(call -> call.getValue().message);


  }

  private void initDataTable() {
    targetTableData.entity().getFields().forEach(field -> {
      String columnName;
      if (field.getFriendlyFieldName().equals("")) {
        columnName = field.getFieldName();
      } else {
        columnName = field.getFriendlyFieldName();
      }
      TableColumn<DataEntity, String> fieldColumn = new TableColumn<>(columnName);
      fieldColumn
          .setCellValueFactory(call -> new SimpleStringProperty(call.getValue().getValue(field)));
      dataEntityTableView.getColumns().add(fieldColumn);
    });
  }

  private class ParserTaskDecorator {
    final ObjectProperty<Node> icon = new SimpleObjectProperty<>();
    final ParserTask T;
    final StringProperty message = new SimpleStringProperty();
    final StringProperty log = new SimpleStringProperty();
    final StringBuilder logBuilder = new StringBuilder();

    public ParserTaskDecorator(ParserTask t) {
      T = t;

      icon.set(createIcon(t.getState()));
      t.stateProperty().addListener((property, oldValue, newValue) -> {
        icon.set(createIcon(newValue));
      });


      t.messageProperty().addListener((property, oldValue, newValue) -> {
        message.set(newValue);
        logBuilder.append(newValue).append('\n');
        log.set(logBuilder.toString());
      });

      t.exceptionProperty().addListener((property, oldValue, newValue) -> {
        if (newValue != null) {
          message.set(newValue.getClass().getSimpleName() + ": " + newValue.getMessage());
          logBuilder.append(message.get());
          log.set(logBuilder.toString());
        }
      });
    }

    private Node createIcon(State state) {
      ProgressIcons progressIcon;
      // Через switch работает неправильно
      if (state.equals(State.SUCCEEDED)) {
        progressIcon = ProgressIcons.DONE;
      } else if (state.equals(State.FAILED)) {
        progressIcon = ProgressIcons.ERROR;
      } else {
        ProgressIndicator indicator = new ProgressIndicator();
        indicator.setPrefSize(20d, 20d);
        return indicator;
      }
      FontIcon icon = FontIcon.of(progressIcon.getIkon());
      icon.setFill(progressIcon.getPaint());
      icon.setIconSize(20);
      return icon;
    }
  }
}
