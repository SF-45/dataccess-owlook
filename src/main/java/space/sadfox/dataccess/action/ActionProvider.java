package space.sadfox.dataccess.action;

import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;
import space.sadfox.owlook.base.owl.Owl;

public interface ActionProvider extends OwlookModuleComponent {
  Action createAction(Owl<ActionEntity> actionEntityOwl, Owl<TableData> tableDataOwl);

  Action createAction(Owl<ActionEntity> actionEntityOwl);
}
