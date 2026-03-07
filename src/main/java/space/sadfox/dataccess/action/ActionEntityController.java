package space.sadfox.dataccess.action;

import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.FXMLController;
import space.sadfox.owlook.ui.base.FXMLControllerException;

public class ActionEntityController extends FXMLController {

  @FXML
  private TextArea descriptionTextArea;

  @FXML
  private BorderPane rootBorderPane;

  @FXML
  private TextField titleTextBox;

  private final Owl<ActionEntity> actionOwl;

  private final Optional<Owl<TableData>> oDataOwl;

  public ActionEntityController(Owl<ActionEntity> actionOwl) throws FXMLControllerException {
    super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

    this.actionOwl = actionOwl;
    oDataOwl = Optional.empty();

    init();
  }

  public ActionEntityController(Owl<ActionEntity> actionOwl, Owl<TableData> dataOwl)
      throws FXMLControllerException {
    super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

    this.actionOwl = actionOwl;
    this.oDataOwl = Optional.of(dataOwl);

    init();
  }

  private void init() {
    titleTextBox.setText(actionOwl.head().getTitle());
    titleTextBox.textProperty().bindBidirectional(actionOwl.head().titleProperty());

    stageTitle.bind(Bindings.concat("Edit Action [", actionOwl.head().titleProperty(), "]"));

    descriptionTextArea.setText(actionOwl.entity().getDescription());
    descriptionTextArea.textProperty().bindBidirectional(actionOwl.entity().descriptionProperty());

    Optional<ActionProvider> oProvider = actionOwl.entity().getActionProviderSafe();
    if (oProvider.isPresent()) {
      ActionProvider provider = oProvider.get();
      Action action = null;
      Parent parent;
      if (oDataOwl.isPresent()) {
        action = provider.createAction(actionOwl, oDataOwl.get());
      } else {
        action = provider.createAction(actionOwl);
      }
      parent = action.getConfigController().getParent();
      BorderPane.setMargin(parent, new Insets(5, 5, 5, 5));
      rootBorderPane.setCenter(parent);
    } else {
      Label actionProviderNotFoundLabel = new Label("Action Provider not found ");
      rootBorderPane.setCenter(actionProviderNotFoundLabel);
    }
  }
}
