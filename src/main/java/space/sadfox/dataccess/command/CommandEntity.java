package space.sadfox.dataccess.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import space.sadfox.dataccess.command.hmadapter.HashMapAdapter;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class CommandEntity implements ChangeHistoryKeeping {
	
	private StringProperty name = new SimpleStringProperty();
	private ObjectProperty<CommandTypes> commandType = new SimpleObjectProperty<>(CommandTypes.EXEC);
	private ObservableMap<String, StringProperty> commandProperties = FXCollections.observableHashMap();
	
	
	@XmlAttribute(name = "name")
	public String getName() {
		return name.get();
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public StringProperty nameProperty() {
		return name;
	}
	
	@XmlAttribute(name = "type")
	public CommandTypes getCommandType() {
		return commandType.get();
	}
	public void setCommandType(CommandTypes commandType) {
		this.commandType.set(commandType);
	}
	public ObjectProperty<CommandTypes> commandTypeProperty() {
		return commandType;
	}
	
	@XmlElement(name = "properties")
    @XmlJavaTypeAdapter(HashMapAdapter.class)
	public HashMap<String, StringProperty> getCommandProperties() {
		return new HashMap<>(commandProperties);
	}
	public void setCommandProperties(HashMap<String, StringProperty> commandProperties) {
		this.commandProperties = FXCollections.observableMap(commandProperties);
	}
	public ObservableMap<String, StringProperty> commandPropertiesProperty() {
		return commandProperties;
	}
	
	@Override
	public List<Object> getProperties() {
		return Arrays.asList(name, commandType, commandProperties);
	}

	
	public StringProperty getCommandProperty(String key, String defaultValue) {
		if (commandProperties.containsKey(key)) {
			return commandProperties.get(key);
		} else {
			StringProperty defaultValueProperty = new SimpleStringProperty(defaultValue);
			commandProperties.put(key, defaultValueProperty);
			return defaultValueProperty;
		}
	}
	
	
	
	

	

}
