package space.sadfox.dataccess.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.owlery.OwleryCreatable;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.base.ControllerException;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableDataFilter extends OwlEntity implements Controllable, OwleryCreatable {

  private ObservableList<Filter> filters = FXCollections.observableArrayList();

  @XmlElementWrapper(name = "filters")
  @XmlElement(name = "filter")
  public List<Filter> getFilters() {
    return filters;
  }

  public ObservableList<Filter> filtersProperty() {
    return filters;
  }

  @Override
  public List<Object> getProperties() {
    return Arrays.asList(filters);
  }

  @Override
  public void initialize() {

  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("TableDataFilter: " + thisOwl().head().getTitle() + "\n\n");
    builder.append("Filters:\n");
    for (Filter f : getFilters()) {
      builder
          .append("\t" + f.getField() + " | " + f.getComparision() + " | " + f.getValue() + "\n");
    }
    return builder.toString();
  }

  @Override
  public Controller getController() throws ControllerException {
    return new TableDataFilterController((Owl<TableDataFilter>) thisOwl());
  }

  public Controller getController(Owl<TableData> tableDataOwl) throws ControllerException {
    return new TableDataFilterController((Owl<TableDataFilter>) thisOwl(), tableDataOwl);
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof TableDataFilter)) {
      return;
    }

    TableDataFilter targetFilter = (TableDataFilter) entity;

    getFilters().clear();
    targetFilter.getFilters().forEach(targerF -> {
      Filter newFilter = new Filter();
      newFilter.setComparision(targerF.getComparision());
      newFilter.setField(targerF.getField());
      newFilter.setNext(targerF.getNext());
      newFilter.setValue(targerF.getValue());

      getFilters().add(newFilter);
    });
  }
}
