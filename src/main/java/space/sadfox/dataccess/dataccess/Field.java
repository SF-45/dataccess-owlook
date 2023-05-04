package space.sadfox.dataccess.dataccess;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class Field implements ChangeHistoryKeeping {
    private StringProperty fieldName = new SimpleStringProperty();
    private StringProperty parseAssociation = new SimpleStringProperty();


    public Field(String fieldName, String parseAssociation) {
        this.fieldName.set(fieldName);
        this.parseAssociation.set(parseAssociation);
    }

    public Field() {
    }

    @XmlAttribute(name = "FieldName")
    public String getFieldName() {
        return fieldName.get();
    }

    @XmlAttribute(name = "ParseAssociation")
    public String getParseAssociation() {
        return parseAssociation.get();
    }

    public void setFieldName(String fieldName) {
        this.fieldName.set(fieldName);
    }

    public void setParseAssociation(String parseAssociation) {
        this.parseAssociation.set(parseAssociation);
    }
    
	public StringProperty fieldNameProperty() {
		return fieldName;
	}
    
	public StringProperty parseAssociationProperty() {
		return parseAssociation;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Field) {
			Field eq = (Field) obj;
			return getFieldName().equals(eq.getFieldName());
		} else {
			return false;
		}
		
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(fieldName, parseAssociation);
	}
    
    
}
