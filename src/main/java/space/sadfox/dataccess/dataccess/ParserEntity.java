package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import space.sadfox.owlook.base.owl.LazyOwlEntity;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;

@XmlSeeAlso(LazyOwlEntity.class)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ParserEntity extends LazyOwlEntity implements Controllable {

  private final ObjectProperty<ParserProvider> parserProvider = new SimpleObjectProperty<>();
  private final BooleanProperty enable = new SimpleBooleanProperty(false);

  public ObjectProperty<ParserProvider> parserProviderProperty() {
    return parserProvider;
  }

  @XmlJavaTypeAdapter(ParserProviderAdapter.class)
  public ParserProvider getParserProvider() {
    return parserProvider.get();
  }

  public Optional<ParserProvider> getParserProviderSafe() {
    if (getParserProvider() == null) {
      return Optional.empty();
    } else {
      return Optional.of(getParserProvider());
    }
  }

  public void setParserProvider(ParserProvider parserProvider) {
    this.parserProvider.set(parserProvider);
  }

  public BooleanProperty enableProperty() {
    return enable;
  }

  @XmlAttribute
  public Boolean isEnable() {
    return enable.get();
  }

  public void setEnable(Boolean enable) {
    this.enable.set(enable);
  }

  @Override
  public List<Object> getProperties() {
    List<Object> props = super.getProperties();
    props.add(parserProvider);
    props.add(enable);
    return props;
  }

  @Override
  public void syncWith(OwlEntity entity) {
    super.syncWith(entity);
    if (!(entity instanceof ParserEntity)) {
      return;
    }
    ParserEntity pe = (ParserEntity) entity;

    setParserProvider(pe.getParserProvider());
    setEnable(pe.isEnable());
  }

  @Override
  public String getEntityName() {
    return "TDParser";
  }

  @Override
  public Controller getController() throws IOException, ParserProviderNotFound {
    if (getParserProviderSafe().isPresent()) {
      return getParserProvider().createController((Owl<ParserEntity>) getOwl());
    } else {
      throw new ParserProviderNotFound("ParserProviderNotFound");
    }
  }

}
