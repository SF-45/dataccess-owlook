package space.sadfox.dataccess.dataccess;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.jaxb.adapters.StringPropertyAdapter;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

public class ParserFilter implements ChangeHistoryKeeping {

    private StringProperty attr = new SimpleStringProperty();
    private ObjectProperty<Comparison> comparison = new SimpleObjectProperty<>();
    private ObservableList<StringProperty> value = FXCollections.observableArrayList();

    public ParserFilter(String attr, Comparison comparison, String ... value) {
        this.attr.set(attr);
        this.comparison.set(comparison);
        Arrays.stream(value).forEach(v -> this.value.add(new SimpleStringProperty(v)));
    }

    public ParserFilter() {
    }

    @XmlAttribute(name = "Attribute")
    public String getAttr() {
        return attr.get();
    }

    public void setAttr(String attr) {
        this.attr.set(attr);
    }
    
    public StringProperty attrProperty() {
		return attr;
	}

    @XmlAttribute(name = "Comparison")
    public Comparison getComparison() {
        return comparison.get();
    }

    public void setComparison(Comparison comparison) {
        this.comparison.set(comparison);
    }
    
    public ObjectProperty<Comparison> comparisonProperty() {
		return comparison;
	}

    @XmlJavaTypeAdapter(StringPropertyAdapter.class)
    @XmlElement(name = "Value")
    public List<StringProperty> getValue() {
        return value;
    }
    
    public ObservableList<StringProperty> valueProperty() {
    	return value;
    }

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(attr, comparison, value);
	}

}
