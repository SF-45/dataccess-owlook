package space.sadfox.dataccess.dataccess;

import space.sadfox.owlook.owlery.OwlAdapter;

public class TableDataAdapter extends OwlAdapter<TableData> {

	@Override
	protected Class<TableData> getTarget() {
		return TableData.class;
	}

}
