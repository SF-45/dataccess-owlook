package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;
import space.sadfox.owlook.utils.ModuleLoader;

public class ActionEntityDao {
	
	private ActionEntity actionEntity;
	private TableData tableData;
	private static EntityLoader loader;
	
	static {
		loader = new EntityLoader();
	}

	public ActionEntityDao(ActionEntity actionEntity, TableData tableData) {
		this.actionEntity = actionEntity;
		this.tableData = tableData;
	}
	
	public Action createAction() {
		for (ActionProvider ap : getActionProviders()) {
			if (ap.getIdentifier().equals(actionEntity.getActionProvider())) {
				return ap.createAction(actionEntity, tableData);
			}
		}
		return null;
	}
	
	public static ActionEntity createActionEntity(String fileName, ActionProvider provider) {
		try {
			ActionEntity actionEntity = loader.createEntity(fileName, ActionEntity.class);
			actionEntity.setActionProvider(provider.getIdentifier());
			return actionEntity;
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}
	
	public static List<ActionProvider> getActionProviders() {
		return ModuleLoader.INSTANCE.loadModuleExtension(ActionProvider.class, m -> m instanceof ActionProvider);
		
//		return ModuleLoader.INSTANCE.loadModuleExtension().stream()
//				.filter(me -> me instanceof ActionProvider)
//				.map(aa -> (ActionProvider) aa)
//				.collect(Collectors.toList());
		
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
