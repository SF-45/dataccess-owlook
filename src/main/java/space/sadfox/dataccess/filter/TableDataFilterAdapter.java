package space.sadfox.dataccess.filter;

import space.sadfox.owlook.utils.JAXBEntityAdapter;

public class TableDataFilterAdapter extends JAXBEntityAdapter<TableDataFilter> {

	@Override
	protected Class<TableDataFilter> getTarget() {
		return TableDataFilter.class;
	}

}
