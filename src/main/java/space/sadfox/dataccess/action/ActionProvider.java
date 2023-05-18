package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.moduleapi.ModuleExtension;

public interface ActionProvider extends ModuleExtension {
	Action createAction(ActionEntity actionEntity, TableData target);
	Action createAction(ActionEntity actionEntity);

}
