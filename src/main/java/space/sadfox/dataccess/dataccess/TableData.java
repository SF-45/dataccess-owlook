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
import space.sadfox.owlook.owlery.OwleryCreatable;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Owlook;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableData extends OwlEntity implements Controllable, OwleryCreatable {

  @FunctionalInterface
  public static interface DataUpdateListener {
    void update();
  }

  private final ObservableList<Owl<ParserEntity>> parsers = FXCollections.observableArrayList();

  private ObservableList<Field> fields;

  private final List<DataUpdateListener> dataUpdateListeners = new ArrayList<>();

  @XmlElement
  @XmlJavaTypeAdapter(ParserEntityAdapter.class)
  public List<Owl<ParserEntity>> getParsers() {
    return parsers;
  }

  public ObservableList<Owl<ParserEntity>> parsersProperty() {
    return parsers;
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
    return Arrays.asList(fieldsProperty(), parsersProperty());
  }

  @Override
  public void initialize() {}

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
  public Controller getController() throws IOException {
    return new TableDataController((Owl<TableData>) getOwl());
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
        Owlook.registerException(1, e);
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

  @Override
  public List<Owl<?>> getChildrenOwls() {
    return new ArrayList<>(getParsers());
  }

}
