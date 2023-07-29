package space.sadfox.dataccess.dataccess;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.owlook.base.jaxb.ChangeHistoryKeeping;

public class ParserFilter implements ChangeHistoryKeeping {

	private ObjectProperty<Comparison> comparison;
    private StringProperty value;
    
    @XmlAttribute(name = "comparison")
	public Comparison getComparison() {
		return comparisonProperty().get();
	}
	
	public void setComparison(Comparison comparison) {
		comparisonProperty().set(comparison);
	}
	
	public ObjectProperty<Comparison> comparisonProperty() {
		if (comparison == null) {
			comparison = new SimpleObjectProperty<>(Comparison.EQUAL);
		}
		return comparison;
	}
	
	@XmlAttribute(name = "value")
	public String getValue() {
		return valueProperty().get();
	}
	
	public void setValue(String value) {
		valueProperty().set(value);
	}
	
	public StringProperty valueProperty() {
		if (value == null) {
			value = new SimpleStringProperty();
		}
		return value;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(comparisonProperty(), valueProperty());
	}
	
	
    
    
}
