package space.sadfox.dataccess.dataccess;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Field implements ChangeHistoryKeeping {
	private StringProperty fieldName;
	private ObservableList<ParserFilter> parserFilters;

	@XmlAttribute(name = "name")
	public String getFieldName() {
		return fieldNameProperty().get();
	}

	public void setFieldName(String fieldName) {
		fieldNameProperty().set(fieldName);
	}
	
	public StringProperty fieldNameProperty() {
		if (fieldName == null) {
			fieldName = new SimpleStringProperty();
		}
		return fieldName;
	}
	
	@XmlElement(name = "filter")
	public List<ParserFilter> getParserFilters() {
		return parserFiltersProperty();
	}
	
	public ObservableList<ParserFilter> parserFiltersProperty() {
		if (parserFilters == null) {
			parserFilters = FXCollections.observableArrayList();
		}
		return parserFilters;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return getFieldName().equals(obj);
		}
		return super.equals(obj);
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(fieldNameProperty(), parserFiltersProperty());
	}

}
