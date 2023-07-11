package space.sadfox.dataccess.filter;

import java.io.IOException;
import java.util.Iterator;

import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.OwlLogger;

public class TableDataFilters {
	public static TableDataFilter createTableDataFilter() {
		try {
			return EntityLoader.INSTANCE.createEntity(TableDataFilter.class);
		} catch (JAXBException | IOException e) {
			OwlLogger.registerException(1, e);
		}
		// TODO: Убрать от сюда null
		return null;
	}

	public static boolean deleteTableDataFiter(TableDataFilter tableDataFilter) {
		return EntityLoader.INSTANCE.deleteEntity(tableDataFilter);
	}
	
	public static DataEntity[] getDataEntities(TableDataFilter tableDataFilter, TableData tableData) throws JAXBException {
		StringBuilder sqlBulder = new StringBuilder();

		Iterator<Filter> iterator = tableDataFilter.getFilters().iterator();

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

		return new TableDataDao(tableData).selectAllWhere(sqlBulder.toString());

	}
}
