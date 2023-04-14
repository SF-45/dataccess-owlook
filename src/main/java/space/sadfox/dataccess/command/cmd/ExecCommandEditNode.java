package space.sadfox.dataccess.command.cmd;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import space.sadfox.dataccess.command.CommandEntity;
import space.sadfox.owlook.ui.base.Controller;

public class ExecCommandEditNode extends Controller {
	
	@FXML
    TextArea commandTextArea;

    @FXML
    Label head;

    @FXML
    RadioButton multiExec;

    @FXML
    TextField title;

    @FXML
    RadioButton singleExec;
    
    @FXML
    RadioButton powershellShell;
    
    @FXML
    RadioButton cmdShell;
    
    CommandEntity commandEntity;
    

	ExecCommandEditNode() throws IOException {
		super(ExecCommandEditNode.class.getResource("cmd-config.fxml"));
	}

}
