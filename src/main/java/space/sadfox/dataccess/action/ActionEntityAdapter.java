package space.sadfox.dataccess.action;

import space.sadfox.owlook.owlery.OwlAdapter;

public class ActionEntityAdapter extends OwlAdapter<ActionEntity>{

	@Override
	protected Class<ActionEntity> getTarget() {
		return ActionEntity.class;
	}


}
