package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.ModuleLoader;

public class ActionEntityDao {
	
	private ActionEntity actionEntity;
	private static EntityLoader loader;
	
	static {
		loader = new EntityLoader();
	}

	public ActionEntityDao(ActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}
	
	public Action createAction() {
		for (ActionApi ap : getActionProviders()) {
			if (ap.getIdentifier().equals(actionEntity.getActionProvider())) {
				return ap.createAction(actionEntity);
			}
		}
		return null;
	}
	
	public static ActionEntity createActionEntity(String fileName, ActionApi provider) {
		try {
			ActionEntity actionEntity = loader.createEntity(fileName, ActionEntity.class);
			actionEntity.setActionProvider(provider.getIdentifier());
			return actionEntity;
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	public static List<ActionApi> getActionProviders() {
		return ModuleLoader.INSTANCE.loadModuleExtension().stream()
				.filter(me -> me instanceof ActionApi)
				.map(aa -> (ActionApi) aa)
				.collect(Collectors.toList());
		
	}
	
	public static ActionEntity loadActionEntity(String fileName) {
		try {
			return loader.loadEntity(fileName, ActionEntity.class);
		} catch (IOException | JAXBException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	

}
