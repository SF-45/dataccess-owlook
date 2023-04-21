package space.sadfox.dataccess.action;

import space.sadfox.owlook.moduleapi.ModuleExtension;

public interface ActionApi extends ModuleExtension {
	Action createAction(ActionEntity actionEntity);

}
