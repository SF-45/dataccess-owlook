package space.sadfox.dataccess.filter;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.dataccess.dataccess.Comparison;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
public class Filter implements ChangeHistoryKeeping {
	private StringProperty field = new SimpleStringProperty();
	private ObjectProperty<Comparison> comparision = new SimpleObjectProperty<>();
	private StringProperty value = new SimpleStringProperty();
	private ObjectProperty<NextComp> next = new SimpleObjectProperty<>();

	@XmlAttribute
	public String getField() {
		return field.get();
	}

	public void setField(String field) {
		this.field.set(field);
	}

	public StringProperty fieldProperty() {
		return field;
	}

	@XmlAttribute
	public Comparison getComparision() {
		return comparision.get();
	}

	public void setComparision(Comparison comparision) {
		this.comparision.set(comparision);
	}

	public ObjectProperty<Comparison> comparisionProperty() {
		return comparision;
	}

	@XmlAttribute
	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value.set(value);
	}

	public StringProperty valueProperty() {
		return value;
	}
	
	@XmlAttribute(name = "next")
	public NextComp getNext() {
		return next.get();
	}

	public void setNext(NextComp next) {
		this.next.set(next);
	}
	
	public ObjectProperty<NextComp> nextProperty() {
		return next;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(field, comparision, value);
	}


	@Override
	public String toString() {
		return "Filter [field=" + field.get() + ", comparision=" + comparision.get() + ", value=" + value.get() + "]";
	}
	

}
