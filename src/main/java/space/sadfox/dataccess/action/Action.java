package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.ui.base.Controller;

public interface Action {
	void run(DataEntity ...dataEntities);
	Controller getConfigController();
	ActionEntity getActionEntity();
	ActionProvider getActionProvider();
}
