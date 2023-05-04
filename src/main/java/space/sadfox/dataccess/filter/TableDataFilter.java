package space.sadfox.dataccess.filter;

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
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

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
	public String getExtension() {
		return ".tdatafilter";
	}

	@Override
	public void initialize() {

	}
	
	@Override
	public boolean validate() {
		return true;
	}


}
