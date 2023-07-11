package space.sadfox.dataccess.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableDataFilter extends JAXBEntity implements ChangeHistoryKeeping {

	private StringProperty title = new SimpleStringProperty("");
	private ObservableList<Filter> filters = FXCollections.observableArrayList();

	@XmlElementWrapper(name = "Filters")
	@XmlElement(name = "Filter")
	public List<Filter> getFilters() {
		return filters;
	}

	public ObservableList<Filter> filtersProperty() {
		return filters;
	}

	@XmlAttribute(name = "title")
	@Override
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String name) {
		this.title.set(name);
	}

	public StringProperty titleProperty() {
		return title;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(filters, title);
	}

	@Override
	public void initialize() {

	}
	
	@Override
	public void validate() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TableDataFilter: " + getTitle() + "\n\n");
		builder.append("Filters:\n");
		for (Filter f : getFilters()) {
			builder.append("\t" + f.getField() + " | " + f.getComparision()+ " | " + f.getValue() + "\n");
		}
		return builder.toString();
	}

	@Override
	public Controller getConfigController() throws IOException {
		return new TableDataFilterController(this);
	}
	
	public Controller getConfigController(TableData tableData) throws IOException, Nullable {
		return new TableDataFilterController(this, tableData);
	}

	@Override
	public void syncWith(JAXBEntity entity) {
		if (!(entity instanceof TableDataFilter)) {
			return;
		}
		
		TableDataFilter targetFilter = (TableDataFilter) entity;
		
		setTitle(targetFilter.getTitle());
		
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
