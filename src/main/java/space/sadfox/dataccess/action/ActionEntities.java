package space.sadfox.dataccess.action;

import java.util.List;

import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.moduleloader.ModuleLoader;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Logger;

public class ActionEntities {
	public static Action createAction(Owl<ActionEntity> actionEntityOwl) throws ActionProviderNotFound {
		return createAction(actionEntityOwl, null);
	}

	public static Action createAction(Owl<ActionEntity> actionEntityOwl, Owl<TableData> tableDataOwl) throws ActionProviderNotFound {
		String currentActionIdentifier = actionEntityOwl.entity().getActionProvider();
		for (ActionProvider ap : getActionProviders()) {
			if (ap.getIdentifier().equals(currentActionIdentifier)) {
				if (tableDataOwl == null) {
					return ap.createAction(actionEntityOwl);
				} else {
					return ap.createAction(actionEntityOwl, tableDataOwl);
				}
			}
		}
		throw new ActionProviderNotFound();
	}

	public static Owl<ActionEntity> createActionEntity(ActionProvider provider) throws Exception {
		Owl<ActionEntity> actionEntityOwl = OwlLoader.INSTANCE.createOwl(ActionEntity.class);
		actionEntityOwl.entity().setActionProvider(provider.getIdentifier());
		return actionEntityOwl;
	}

	public static List<ActionProvider> getActionProviders() {
		return ModuleLoader.INSTANCE.loadModuleComponents(ActionProvider.class, m -> m instanceof ActionProvider);

	}

	public static boolean deleteActionEntity(Owl<ActionEntity> actionEntityOwl) {
		try {
			OwlLoader.INSTANCE.deleteOwl(actionEntityOwl);
			return true;
		} catch (Exception e) {
			Logger.registerException(1, e);
			return false;
		}
	}
}
