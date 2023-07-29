package space.sadfox.dataccess.action;

import space.sadfox.owlook.utils.JAXBEntityAdapter;

public class ActionEntityAdapter extends JAXBEntityAdapter<ActionEntity>{

	@Override
	protected Class<ActionEntity> getTarget() {
		return ActionEntity.class;
	}


}
