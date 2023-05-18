package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ModuleLoader;
import space.sadfox.owlook.utils.Nullable;

public class ActionEntityDao {

	private ActionEntity actionEntity;
	private TableData tableData;

	public ActionEntityDao(ActionEntity actionEntity, TableData tableData) {
		this.actionEntity = actionEntity;
		this.tableData = tableData;
	}
	
	public ActionEntityDao(ActionEntity actionEntity) {
		this.actionEntity = actionEntity;
		this.tableData = null;
	}

	public Action createAction() {
		for (ActionProvider ap : getActionProviders()) {
			if (ap.getIdentifier().equals(actionEntity.getActionProvider())) {
				try {
					return ap.createAction(actionEntity, getTableData());
				} catch (Nullable e) {
					return ap.createAction(actionEntity);
				}
			}
		}
		return null;
	}

	public static ActionEntity createActionEntity(ActionProvider provider) throws JAXBException, IOException {
		ActionEntity actionEntity = EntityLoader.INSTANCE.createEntity(ActionEntity.class);
		actionEntity.setActionProvider(provider.getIdentifier());
		return actionEntity;
	}

	public static List<ActionProvider> getActionProviders() {
		return ModuleLoader.INSTANCE.loadModuleExtension(ActionProvider.class, m -> m instanceof ActionProvider);

//		return ModuleLoader.INSTANCE.loadModuleExtension().stream()
//				.filter(me -> me instanceof ActionProvider)
//				.map(aa -> (ActionProvider) aa)
//				.collect(Collectors.toList());

	}

	public static ActionEntity loadActionEntity(String fileName) throws IOException, JAXBException {
		return EntityLoader.INSTANCE.loadEntity(fileName, ActionEntity.class);
	}

	public static boolean deleteActionEntity(ActionEntity actionEntity) {
		return EntityLoader.INSTANCE.deleteEntity(actionEntity);
	}
	
	public static boolean existActionEntity(String fileName) {
		return EntityLoader.INSTANCE.entityExist(fileName, ActionEntity.class);
	}
	
	private TableData getTableData() throws Nullable {
		if (tableData == null) throw new Nullable();
		return tableData;
	}

}
