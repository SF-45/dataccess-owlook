package space.sadfox.dataccess.action;

import space.sadfox.owlook.jaxb.adapters.JAXBEntityAdapter;

public class ActionEntityAdapter extends JAXBEntityAdapter<ActionEntity>{

	@Override
	protected Class<ActionEntity> getTarget() {
		return ActionEntity.class;
	}


}
