package space.sadfox.dataccess.dataccess;

import space.sadfox.owlook.jaxb.adapters.JAXBEntityAdapter;

public class TableDataAdapter extends JAXBEntityAdapter<TableData> {

	@Override
	protected Class<TableData> getTarget() {
		return TableData.class;
	}

}
