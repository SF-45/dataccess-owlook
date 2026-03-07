package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.base.owl.OwlEntityInitializeException;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.owlery.OwlReferenceList;
import space.sadfox.owlook.owlery.OwlReferenceListAdapter;
import space.sadfox.owlook.owlery.OwleryCreatable;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.base.ControllerException;
import space.sadfox.owlook.utils.Owlook;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableData extends OwlEntity implements Controllable, OwleryCreatable {

  @FunctionalInterface
  public static interface DataUpdateListener {
    void update();
  }

  private OwlReferenceList<ParserEntity> parsers = new OwlReferenceList<>(ParserEntity.class);

  private ObservableList<Field> fields;

  private final List<DataUpdateListener> dataUpdateListeners = new ArrayList<>();

  @XmlElement
  @XmlJavaTypeAdapter(OwlReferenceListAdapter.class)
  public OwlReferenceList<ParserEntity> getParsers() {
    return parsers;
  }

  @SuppressWarnings("unused")
  private void setParsers(OwlReferenceList<ParserEntity> parsers) {
    this.parsers = parsers;
  }

  @XmlElementWrapper(name = "fields")
  @XmlElement(name = "field")
  public List<Field> getFields() {
    return fieldsProperty();
  }

  public ObservableList<Field> fieldsProperty() {
    if (fields == null) {
      fields = FXCollections.observableArrayList();
    }
    return fields;
  }

  @Override
  public List<Object> getProperties() {
    return Arrays.asList(fieldsProperty(), getParsers());
  }

  @Override
  public void initialize() throws OwlEntityInitializeException {
    boolean pP = parsers.setParent(thisOwl());
    if (!pP) {
      throw new OwlEntityInitializeException("Parent is not set");
    }
  }

  public void addDataUpdateListener(DataUpdateListener dataUpdateListener) {
    dataUpdateListeners.add(dataUpdateListener);
  }

  public void removeDataUpdateListener(DataUpdateListener dataUpdateListener) {
    dataUpdateListeners.remove(dataUpdateListener);
  }

  void notifyDataUpdateListeners() {
    dataUpdateListeners.forEach(DataUpdateListener::update);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Controller getController() throws ControllerException {
    return new TableDataController((Owl<TableData>) thisOwl());
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof TableData)) {
      return;
    }

    TableData newTableData = (TableData) entity;

    parsers.clear();
    newTableData.getParsers().forEach(parserEntity -> {
      try {
        parsers.add(OwlLoader.INSTANCE.duplicateOwl(parserEntity));
      } catch (IOException | JAXBException | ReflectiveOperationException
          | OwlEntityInitializeException e) {
        Owlook.registerException(e);
      }
    });

    getFields().clear();
    newTableData.getFields().forEach(field -> {
      Field newField = new Field();

      newField.setFieldName(field.getFieldName());
      newField.setFriendlyFieldName(field.getFriendlyFieldName());

      field.getParserFilters().forEach(parserFilter -> {
        ParserFilter newParserFilter = new ParserFilter();

        newParserFilter.setComparison(parserFilter.getComparison());
        newParserFilter.setValue(parserFilter.getValue());
        newField.getParserFilters().add(newParserFilter);
      });

      getFields().add(newField);
    });

  }
}
