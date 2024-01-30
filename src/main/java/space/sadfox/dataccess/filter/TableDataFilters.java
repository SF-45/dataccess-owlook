package space.sadfox.dataccess.filter;

import java.util.Iterator;
import jakarta.xml.bind.JAXBException;
import space.sadfox.dataccess.dataccess.DataEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.dataccess.TableDataDao;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Logger;

public class TableDataFilters {
  public static Owl<TableDataFilter> createTableDataFilter() {
    try {
      return OwlLoader.INSTANCE.createOwl(TableDataFilter.class);
    } catch (Exception e) {
      Logger.registerException(1, e);
      return null;
    }
  }

  public static boolean deleteTableDataFilter(Owl<TableDataFilter> owl) {
    try {
      OwlLoader.INSTANCE.deleteOwl(owl);
      return true;
    } catch (Exception e) {
      Logger.registerException(1, e);
      return false;
    }
  }

  public static DataEntity[] getDataEntities(Owl<TableDataFilter> filterOwl, Owl<TableData> dataOwl)
      throws JAXBException {
    StringBuilder sqlBulder = new StringBuilder();

    Iterator<Filter> iterator = filterOwl.entity().getFilters().iterator();

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
          sqlBulder.append(filter.getField()).append(" LIKE ")
              .append("'%" + filter.getValue() + "%'");
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

    return new TableDataDao(dataOwl).selectAllWhere(sqlBulder.toString());

  }
}
