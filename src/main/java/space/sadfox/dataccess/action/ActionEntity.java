package space.sadfox.dataccess.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import space.sadfox.dataccess.command.hmadapter.HashMapAdapter;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.jaxb.PreLoadAction;
import space.sadfox.owlook.moduleapi.ChangeHistoryKeeping;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class ActionEntity extends JAXBEntity {
	
	private StringProperty title = new SimpleStringProperty();
	private StringProperty actionProvider = new SimpleStringProperty();
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
		return Arrays.asList(title, actionProvider, actionProperties);
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
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Node getSimpleConfigNode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PreLoadAction getPreLoadAction() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	

	

}
