package space.sadfox.dataccess.view;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.dataccess.dataccess.Field;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
public class FieldView implements ChangeHistoryKeeping {
	
	private StringProperty fieldName = new SimpleStringProperty();
	private StringProperty friendlyFieldName = new SimpleStringProperty();
	private BooleanProperty visible = new SimpleBooleanProperty(true);
	
	public FieldView() {}
	
	public FieldView(String fieldName) {
		setFieldName(fieldName);
	}
	
	public FieldView(String fieldName, String friendlyFieldName) {
		setFieldName(fieldName);
		setFriendlyFieldName(friendlyFieldName);
	}
	
	public FieldView(Field field) {
		setFieldName(field.getFieldName());
	}
	
	@XmlAttribute(name = "FieldName")
	public String getFieldName() {
		return fieldName.get();
	}
	public void setFieldName(String filedName) {
		this.fieldName.set(filedName);
	}
	public StringProperty fieldNameProperty() {
		return fieldName;
	}
	
	@XmlAttribute(name = "FriendlyFieldName")
	public String getFriendlyFieldName() {
		return friendlyFieldName.get();
	}
	public void setFriendlyFieldName(String friendlyFieldName) {
		this.friendlyFieldName.set(friendlyFieldName);
	}
	public StringProperty friendlyFieldNameProperty() {
		return friendlyFieldName;
	}
	
	@XmlAttribute(name = "Visible")
	public boolean getVisible() {
		return visible.get();
	}
	public void setVisible(boolean visible) {
		this.visible.set(visible);
	}
	public BooleanProperty visibleProperty() {
		return visible;
	}
	@Override
	public List<Object> getProperties() {
		return Arrays.asList(fieldName, friendlyFieldName, visible);
	}
	
	

}
