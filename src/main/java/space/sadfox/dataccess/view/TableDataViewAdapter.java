package space.sadfox.dataccess.view;

import space.sadfox.owlook.jaxb.adapters.JAXBEntityAdapter;

public class TableDataViewAdapter extends JAXBEntityAdapter<TableDataView> {

	@Override
	protected Class<TableDataView> getTarget() {
		return TableDataView.class;
	}

}
