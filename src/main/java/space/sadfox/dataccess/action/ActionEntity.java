package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.LazyOwlEntity;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;

@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(LazyOwlEntity.class)
@XmlRootElement
public class ActionEntity extends LazyOwlEntity implements Controllable {

  private final StringProperty description = new SimpleStringProperty("");
  private ActionProvider actionProvider;

  @XmlElement
  public String getDescription() {
    return descriptionProperty().get();
  }

  public void setDescription(String description) {
    descriptionProperty().set(description);
  }

  public StringProperty descriptionProperty() {
    return description;
  }

  @XmlElement
  @XmlJavaTypeAdapter(ActionProviderAdapter.class)
  public ActionProvider getActionProvider() {
    return actionProvider;
  }

  public Optional<ActionProvider> getActionProviderSafe() {
    if (getActionProvider() == null) {
      return Optional.empty();
    } else {
      return Optional.of(getActionProvider());
    }
  }

  void setActionProvider(ActionProvider actionProvider) {
    this.actionProvider = actionProvider;
  }

  @Override
  public List<Object> getProperties() {
    List<Object> props = super.getProperties();
    props.addAll(Arrays.asList(actionProvider, description));
    return props;
  }

  @Override
  @SuppressWarnings("unchecked")
  public Controller getController() throws IOException {
    return new ActionEntityController((Owl<ActionEntity>) getOwl());
  }

  @SuppressWarnings("unchecked")
  public Controller getController(Owl<TableData> tableDataOwl) throws IOException {
    return new ActionEntityController((Owl<ActionEntity>) getOwl(), tableDataOwl);
  }

  @Override
  public String getEntityName() {
    return "TDAction";
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof ActionEntity)) {
      return;
    }
    super.syncWith(entity);

    ActionEntity ac = (ActionEntity) entity;

    setActionProvider(ac.getActionProvider());
    setDescription(ac.getDescription());
  }
}
