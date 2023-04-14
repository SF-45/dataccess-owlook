package space.sadfox.dataccess.command;

import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.ui.base.Controller;

public interface Command {
	void execCommand(DataEntity ...dataEntities);
	Controller getConfigController();
	CommandEntity getCommandEntity();

}
