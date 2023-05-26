package space.sadfox.dataccess.filter;

import java.io.IOException;
import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.Comparison;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ErrorLogger;

public class TableDataFilterDao {

	private TableDataFilter filter;
	private TableData tData;

	public TableDataFilterDao(TableDataFilter filter, TableData tableData) {
		this.filter = filter;
		this.tData = tableData;
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

		if (getTableData() == null)
			throw new JAXBException("Table Data is not connected");
		StringBuilder sqlBulder = new StringBuilder();

		Iterator<Filter> iterator = filter.getFilters().iterator();

		boolean open = false;
		while (iterator.hasNext()) {
			Filter filter = iterator.next();

			if (iterator.hasNext() && filter.getNext().equals(NextComp.OR) && !open) {
				sqlBulder.append("(");
				open = true;
			}

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
			if (iterator.hasNext()) {
				if (filter.getNext().equals(NextComp.AND) && open) {
					sqlBulder.append(")");
					open = false;
				}

				sqlBulder.append(" " + filter.getNext() + " ");
			} else if (open) {
				sqlBulder.append(")");
				open = false;
			}

		}

		return getTableDataDao().selectAllWhere(sqlBulder.toString());

	}

	public static TableDataFilter createTableDataFilter() {
		try {
			return EntityLoader.INSTANCE.createEntity(TableDataFilter.class);
		} catch (JAXBException | IOException e) {
			ErrorLogger.registerException(e);
		}
		return null;
	}

	public static boolean deleteTableDataFiter(TableDataFilter tableDataFilter) {
		return EntityLoader.INSTANCE.deleteEntity(tableDataFilter);
	}

	public static TableDataFilter loadTableDataFilter(String fileName) throws IOException, JAXBException {
		return EntityLoader.INSTANCE.loadEntity(fileName, TableDataFilter.class);
	}

	public TableDataFilter getFilter() {
		return filter;
	}

	private TableData getTableData() {
		return tData;
	}

	private TableDataDao getTableDataDao() {
		return new TableDataDao(getTableData());
	}

}
