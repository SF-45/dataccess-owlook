package space.sadfox.dataccess.action;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import space.sadfox.dataccess.ResourceTarget;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

public class ActionEntityController extends Controller {
	
    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private TextField titleTextBox;
    
    private ActionEntity actionEntity;
    private ActionEntityDao actionEntityDao;
    
    private TableData tableData;

	public ActionEntityController(ActionEntity actionEntity) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));
		
		this.actionEntity = actionEntity;
		
		init();
	}
	
	public ActionEntityController(ActionEntity actionEntity, TableData tableData) throws IOException {
		super(ResourceTarget.class.getResource("fxml/edit-action.fxml"));
		
		this.actionEntity = actionEntity;
		this.tableData = tableData;
		
		init();
	}

	private void init() {
		titleTextBox.setText(getActionEntity().getTitle());
		titleTextBox.textProperty().bindBidirectional(getActionEntity().titleProperty());
		
		descriptionTextArea.setText(getActionEntity().getDescription());
		descriptionTextArea.textProperty().bindBidirectional(getActionEntity().descriptionProperty());
		
		Parent parent = getActionEntityDao().createAction().getConfigController().getParent();
		BorderPane.setMargin(parent, new Insets(5, 5, 5, 5));
		rootBorderPane.setCenter(parent);
		
		
	}

	private ActionEntity getActionEntity() {
		return actionEntity;
	}

	private ActionEntityDao getActionEntityDao() {
		if (actionEntityDao == null) {
			try {
				actionEntityDao = new ActionEntityDao(getActionEntity(), getTableData());
			} catch (Nullable e) {
				actionEntityDao = new ActionEntityDao(getActionEntity());
			}
		}
		return actionEntityDao;
	}
	
	private TableData getTableData() throws Nullable {
		if (tableData == null) {
			throw new Nullable();
		}
		
		return tableData;
	}
	
	

}
