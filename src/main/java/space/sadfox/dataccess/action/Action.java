package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.ui.base.Controller;

public interface Action {
	void execCommand(DataEntity ...dataEntities);
	Controller getConfigController();
	ActionEntity getActionEntity();
}
