package space.sadfox.dataccess.action;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlAccessorType(XmlAccessType.NONE)
public class HashMapEntry {

    private String key;

    
    private StringProperty value = new SimpleStringProperty();
    
    
    
    public HashMapEntry(String key, StringProperty value) {
		super();
		this.key = key;
		this.value = value;
	}
    
    public HashMapEntry() {}

	@XmlAttribute
    public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@XmlValue
	public String getValue() {
		return value.get();
	}

	public void setValue(String value) {
		this.value.set(value);
	}
	
	public StringProperty valueProperty() {
		return value;
	}


    
}
