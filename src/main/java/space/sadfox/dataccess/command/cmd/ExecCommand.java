package space.sadfox.dataccess.command.cmd;

import java.io.IOException;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.dataccess.command.Command;
import space.sadfox.dataccess.command.CommandEntity;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.ErrorLogger;

public class ExecCommand implements Command {

	private CommandEntity commandEntity;
	StringProperty execCommand;
	ObjectProperty<ExecMode> execMode = new SimpleObjectProperty<>();
	ObjectProperty<ExecShell> shell = new SimpleObjectProperty<>();

	public ExecCommand(CommandEntity commandEntity) {
		this.commandEntity = commandEntity;

		execCommand = commandEntity.getCommandProperty(ExecProperties.EXEC_COMMAND.name(), "");
		// -----------------ExecMode-------------------
		StringProperty execModeProp = commandEntity.getCommandProperty(ExecProperties.EXEC_MODE.name(),
				ExecMode.SINGLE.name());
		execModeProp.addListener((property, oldValue, newValue) -> {
			execMode.set(ExecMode.valueOf(newValue));
		});
		execMode.set(ExecMode.valueOf(execModeProp.get()));
		execMode.addListener((property, oldValue, newValue) -> {
			execModeProp.set(newValue.name());
		});
		// -----------------ExecShell-------------------
		StringProperty shellProp = commandEntity.getCommandProperty(ExecProperties.EXEC_SHELL.name(),
				ExecShell.CMD.name());
		shellProp.addListener((property, oldValue, newValue) -> {
			shell.set(ExecShell.valueOf(newValue));
		});
		shell.set(ExecShell.valueOf(shellProp.get()));
		shell.addListener((property, oldValue, newValue) -> {
			shellProp.set(newValue.name());
		});

	}

	@Override
	public void execCommand(DataEntity... dataEntities) {
		// TODO Auto-generated method stub

	}

	@Override
	public Controller getConfigController() {
		try {
			ExecCommandEditNode editNode = new ExecCommandEditNode();
			execMode.addListener((property, oldValue, newValue) -> {
				if (newValue.equals(ExecMode.SINGLE)) {
					editNode.singleExec.setSelected(true);
				} else {
					editNode.multiExec.setSelected(true);
				}
			});
			shell.addListener((property, oldValue, newValue) -> {
				if (newValue.equals(ExecShell.CMD)) {
					editNode.cmdShell.setSelected(true);
				} else {
					editNode.powershellShell.setSelected(true);
				}
			});
			editNode.commandTextArea.textProperty().bindBidirectional(execCommand);
			editNode.title.textProperty().bindBidirectional(commandEntity.nameProperty());
			editNode.singleExec.setOnAction(event -> execMode.set(ExecMode.SINGLE));
			editNode.multiExec.setOnAction(event -> execMode.set(ExecMode.MULTI));
			if (execMode.get().equals(ExecMode.SINGLE)) editNode.singleExec.setSelected(true);
			else editNode.multiExec.setSelected(true);
			
			editNode.cmdShell.setOnAction(event -> shell.set(ExecShell.CMD));
			editNode.powershellShell.setOnAction(event -> shell.set(ExecShell.POWERSHELL));
			if (shell.get().equals(ExecShell.CMD)) editNode.cmdShell.setSelected(true);
			else editNode.powershellShell.setSelected(true);
			
			return editNode;
		} catch (IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	@Override
	public CommandEntity getCommandEntity() {
		return commandEntity;
	}

}
