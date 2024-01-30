package space.sadfox.dataccess.action;

import java.io.IOException;

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
import space.sadfox.owlook.utils.Nullable;

public class ActionEntityController extends FXMLController {

	@FXML
	private TextArea descriptionTextArea;

	@FXML
	private BorderPane rootBorderPane;

	@FXML
	private TextField titleTextBox;

	private Owl<ActionEntity> actionOwl;

	private Owl<TableData> dataOwl;

	public ActionEntityController(Owl<ActionEntity> actionOwl) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

		this.actionOwl = actionOwl;

		init();
	}

	public ActionEntityController(Owl<ActionEntity> actionOwl, Owl<TableData> dataOwl) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));

		this.actionOwl = actionOwl;
		this.dataOwl = dataOwl;

		init();
	}

	private void init() {
		titleTextBox.setText(getActionOwl().head().getTitle());
		titleTextBox.textProperty().bindBidirectional(getActionOwl().head().titleProperty());
		
		stageTitle.bind(Bindings.concat("Edit Action [", getActionOwl().head().titleProperty(), "]"));

		descriptionTextArea.setText(getActionOwl().entity().getDescription());
		descriptionTextArea.textProperty().bindBidirectional(getActionOwl().entity().descriptionProperty());

		
		try {
			Parent parent;
			try {
				parent = ActionEntities.createAction(getActionOwl(), getTableDataOwl()).getConfigController()
						.getParent();
			} catch (Nullable e) {
				parent = ActionEntities.createAction(getActionOwl()).getConfigController().getParent();
			}
			BorderPane.setMargin(parent, new Insets(5, 5, 5, 5));
			rootBorderPane.setCenter(parent);
		} catch (ActionProviderNotFound e) {
			Label actionProviderNotFoundLabel = 
					new Label("Action Provider not found [" + getActionOwl().entity().getActionProvider() + "]");
			rootBorderPane.setCenter(actionProviderNotFoundLabel);
			
		}

	}

	private Owl<ActionEntity> getActionOwl() {
		return actionOwl;
	}
	
	private Owl<TableData> getTableDataOwl() throws Nullable {
		if (dataOwl == null) {
			throw new Nullable();
		}
		return dataOwl;
	}
}
