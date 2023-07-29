package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.utils.EntityLoader;
import space.sadfox.owlook.utils.ModuleLoader;

public class ActionEntities {
	public static Action createAction(ActionEntity actionEntity) throws ActionProviderNotFound {
		for (ActionProvider ap : getActionProviders()) {
			if (ap.getIdentifier().equals(actionEntity.getActionProvider())) {
				return ap.createAction(actionEntity);
			}
		}
		throw new ActionProviderNotFound();
	}

	public static Action createAction(ActionEntity actionEntity, TableData tableData) throws ActionProviderNotFound {
		for (ActionProvider ap : getActionProviders()) {
			if (ap.getIdentifier().equals(actionEntity.getActionProvider())) {
				return ap.createAction(actionEntity, tableData);
			}
		}
		throw new ActionProviderNotFound();
	}

	public static ActionEntity createActionEntity(ActionProvider provider) throws JAXBException, IOException {
		ActionEntity actionEntity = EntityLoader.INSTANCE.createEntity(ActionEntity.class);
		actionEntity.setActionProvider(provider.getIdentifier());
		return actionEntity;
	}

	public static List<ActionProvider> getActionProviders() {
		return ModuleLoader.INSTANCE.loadModuleComponents(ActionProvider.class, m -> m instanceof ActionProvider);

	}

	public static boolean deleteActionEntity(ActionEntity actionEntity) {
		return EntityLoader.INSTANCE.deleteEntity(actionEntity);
	}
}
