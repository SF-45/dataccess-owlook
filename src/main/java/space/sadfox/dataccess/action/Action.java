package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.Controller;

public interface Action {
	void run(DataEntity ...dataEntities);
	Controller getConfigController();
	Owl<ActionEntity> getActionEntityOwl();
	ActionProvider getActionProvider();
}
