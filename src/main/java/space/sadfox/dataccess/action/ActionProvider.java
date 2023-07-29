package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;

public interface ActionProvider extends OwlookModuleComponent {
	Action createAction(ActionEntity actionEntity, TableData target);
	Action createAction(ActionEntity actionEntity);

}
