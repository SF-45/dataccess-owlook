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
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.base.owl.OwlEntityHasNoContainingOwls;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class ActionEntity extends OwlEntity implements Controllable {

  private StringProperty description = new SimpleStringProperty("");
  private StringProperty actionProvider = new SimpleStringProperty("");
  private ObservableMap<String, StringProperty> actionProperties =
      FXCollections.observableHashMap();

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
    return Arrays.asList(actionProvider, actionProperties, description);
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
  public String toString() {
    StringBuilder builder = new StringBuilder("Action: " + getOwl().head().getTitle() + "\n");
    builder.append("Action Provider: " + getActionProvider() + "\n\n");

    builder.append("Description: " + getDescription() + "\n\n");

    builder.append("Properties:\n");

    getActionProperties().forEach((key, value) -> {
      builder.append("\t" + key + " = [" + value.get() + "]\n");
    });

    return builder.toString();
  }

  @Override
  public Controller getController() throws IOException {
    return new ActionEntityController((Owl<ActionEntity>) getOwl());
  }

  public Controller getController(Owl<TableData> tableDataOwl) throws IOException {
    return new ActionEntityController((Owl<ActionEntity>) getOwl(), tableDataOwl);
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof ActionEntity)) {
      return;
    }

    ActionEntity ac = (ActionEntity) entity;

    setActionProvider(ac.getActionProvider());
    getActionProperties().clear();
    ac.getActionProperties().forEach(actionPropertiesProperty()::put);
  }

  @Override
  public List<Owl<?>> getChildrenOwls() throws OwlEntityHasNoContainingOwls {
    throw new OwlEntityHasNoContainingOwls();
  }



}
