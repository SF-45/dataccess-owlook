package space.sadfox.dataccess.action;

import java.util.List;
import java.util.stream.Collectors;

import space.sadfox.owlook.utils.ModuleLoader;

public class ActionEntityDao {
	
	private ActionEntity actionEntity;

	public ActionEntityDao(ActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}
	
	public static Action createAction(ActionEntity actionEntity) {
		for (ActionUtility au : getActionUtilities()) {
			if (au.getIdentifier().equals(actionEntity.getActionProvider())) {
				return au.createAction(actionEntity);
			}
		}
		return null;
	}
	
	private static List<ActionUtility> getActionUtilities() {
		return ModuleLoader.INSTANCE.loadUtilities().stream()
				.filter(u -> u instanceof ActionUtility)
				.map(au -> (ActionUtility) au)
				.collect(Collectors.toList());
		
	}
	
	

}
