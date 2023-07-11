package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.base.Controller;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class ActionEntity extends JAXBEntity {

	private StringProperty title = new SimpleStringProperty("");
	private StringProperty description = new SimpleStringProperty("");
	private StringProperty actionProvider = new SimpleStringProperty("");
	private ObservableMap<String, StringProperty> actionProperties = FXCollections.observableHashMap();

	@Override
	@XmlAttribute(name = "title")
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public StringProperty titleProperty() {
		return title;
	}
	
	@XmlElement(name = "description")
	public String getDescription() {
		return descriptionProperty().get();
	}

	public void setDescription(String description) {
		descriptionProperty().set(description);
	}

	public StringProperty descriptionProperty() {
		return description;
	}

	@XmlAttribute(name = "actionProvider")
	public String getActionProvider() {
		return actionProvider.get();
	}

	public void setActionProvider(String actionProvider) {
		this.actionProvider.set(actionProvider);
	}

	public StringProperty actionProviderProperty() {
		return actionProvider;
	}

	@XmlElement(name = "properties")
	@XmlJavaTypeAdapter(HashMapAdapter.class)
	public HashMap<String, StringProperty> getActionProperties() {
		return new HashMap<>(actionProperties);
	}

	public void setActionProperties(HashMap<String, StringProperty> commandProperties) {
		this.actionProperties = FXCollections.observableMap(commandProperties);
	}

	public ObservableMap<String, StringProperty> actionPropertiesProperty() {
		return actionProperties;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, actionProvider, actionProperties, description);
	}

	public StringProperty getActionProperty(String key, String defaultValue) {
		if (actionProperties.containsKey(key)) {
			return actionProperties.get(key);
		} else {
			StringProperty defaultValueProperty = new SimpleStringProperty(defaultValue);
			actionProperties.put(key, defaultValueProperty);
			return defaultValueProperty;
		}
	}

	@Override
	public void initialize() {

	}
	
	@Override
	public void validate() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Action: " + getTitle() + "\n");
		builder.append("Action Provider: " + getActionProvider() + "\n\n");
		
		builder.append("Description: " + getDescription() + "\n\n");
		
		builder.append("Properties:\n");
		
		getActionProperties().forEach((key, value) -> {
			builder.append("\t" + key + " = [" + value.get() + "]\n");
		});
		
		return builder.toString();
	}

	@Override
	public Controller getConfigController() throws IOException {
		return new ActionEntityController(this);
	}
	
	public Controller getConfigController(TableData tableData) throws IOException {
		return new ActionEntityController(this, tableData);
	}

	@Override
	public void syncWith(JAXBEntity entity) {
		if (!(entity instanceof ActionEntity)) {
			return;
		}
		
		ActionEntity ac = (ActionEntity) entity;
		
		setTitle(ac.getTitle());
		setActionProvider(ac.getActionProvider());
		getActionProperties().clear();
		ac.getActionProperties().forEach(actionPropertiesProperty()::put);
	}
	
	


}
