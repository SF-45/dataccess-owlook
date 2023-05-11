package space.sadfox.dataccess.filter;

import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.Comparison;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;

public class TableDataFilterDao {

	private TableDataFilter filter;
	private TableData tData;
	private TableDataDao tableDataDao;

	public TableDataFilterDao(TableDataFilter filter, TableData tableData) {
		this.filter = filter;
		this.tData = tableData;
		tableDataDao = new TableDataDao(tData);
	}


	public Filter addNewFilter(String field, Comparison comparison, String value, NextComp next) {
		Filter filter = new Filter();
		filter.setField(field);
		filter.setComparision(comparison);
		filter.setValue(value);
		filter.setNext(next);
		this.filter.getFilters().add(filter);
		return filter;
	}

	public DataEntity[] getDataEntities() throws JAXBException {

		if (tableDataDao == null)
			throw new JAXBException("Table Data is not connected");
		StringBuilder sqlBulder = new StringBuilder();

		Iterator<Filter> iterator = filter.getFilters().iterator();

		while (iterator.hasNext()) {
			Filter filter = iterator.next();
			switch (filter.getComparision()) {
			case EQUAL:
				sqlBulder.append(filter.getField()).append(" = ").append("'" + filter.getValue() + "'");
				break;
			case NOT_EQUAL:
				sqlBulder.append(filter.getField()).append(" != ").append("'" + filter.getValue() + "'");
				break;
			case LIKE:
				sqlBulder.append(filter.getField()).append(" LIKE ").append("'%" + filter.getValue() + "%'");
				break;
			default:
				break;
			}
			if (iterator.hasNext())
				sqlBulder.append(" " + filter.getNext() + " ");
		}

		return tableDataDao.selectAllWhere(sqlBulder.toString());

	}

	public TableDataFilter getFilter() {
		return filter;
	}
	

}
