package space.sadfox.dataccess.filter;

import space.sadfox.owlook.owlery.OwlAdapter;

public class TableDataFilterAdapter extends OwlAdapter<TableDataFilter> {

	@Override
	protected Class<TableDataFilter> getTarget() {
		return TableDataFilter.class;
	}

}
