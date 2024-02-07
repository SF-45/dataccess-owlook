package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.base.owl.OwlEntityHasNoContainingOwls;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableData extends OwlEntity implements Controllable {

  @FunctionalInterface
  public static interface DataUpdateListener {
    void update();
  }

  private StringProperty pathToData;

  private BooleanProperty autoUpdate;

  private final ObjectProperty<ParserProvider> parser = new SimpleObjectProperty<>();

  private ObservableList<Field> fields;

  private final List<DataUpdateListener> dataUpdateListeners = new ArrayList<>();

  @XmlAttribute(name = "parser")
  @XmlJavaTypeAdapter(ParserProviderAdapter.class)
  public ParserProvider getParser() {
    return parser.get();
  }

  public ParserProvider getParserSafe() throws ParserProviderNotFound {
    if (parser.get() == null) {
      throw new ParserProviderNotFound();
    }
    return parser.get();
  }

  public void setParser(ParserProvider parser) {
    this.parser.set(parser);
  }

  public ObjectProperty<ParserProvider> parserProperty() {
    return parser;
  }

  @XmlAttribute(name = "pathToData")
  public String getPathToData() {
    return pathToDataProperty().get();
  }

  public void setPathToData(String pathToData) {
    pathToDataProperty().set(pathToData);
  }

  public StringProperty pathToDataProperty() {
    if (pathToData == null) {
      pathToData = new SimpleStringProperty();
    }
    return pathToData;
  }

  @XmlAttribute(name = "autoUpdate")
  public Boolean getAutoUpdate() {
    return autoUpdateProperty().get();
  }

  public void setAutoUpdate(Boolean autoUpdate) {
    autoUpdateProperty().set(autoUpdate);
  }

  public BooleanProperty autoUpdateProperty() {
    if (autoUpdate == null) {
      autoUpdate = new SimpleBooleanProperty(false);
    }
    return autoUpdate;
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
    return Arrays.asList(pathToDataProperty(), autoUpdateProperty(), fieldsProperty());
  }

  @Override
  public void initialize() {
    if (getAutoUpdate()) {
      new TableDataDao((Owl<TableData>) getOwl()).loadData();
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
  public String toString() {
    StringBuilder builder = new StringBuilder("TableData: " + getOwl().head().getTitle() + "\n");
    try {
      builder.append("Parser: " + getParserSafe().getIdentifier() + "\n");
    } catch (ParserProviderNotFound e) {
      builder.append("Parser: Indefined\n");
    }
    builder.append("Path To Data: [" + getPathToData() + "]\n");
    builder.append("Auto Update: " + getAutoUpdate() + "\n");
    builder.append("Fields:\n");

    for (Field field : getFields()) {
      builder.append("\t" + field.getFieldName() + " | " + field.getFriendlyFieldName() + "\n");

      for (ParserFilter parserFilter : field.getParserFilters()) {
        builder
            .append("\t\t" + parserFilter.getComparison() + " " + parserFilter.getValue() + "\n");
      }
    }

    return builder.toString();
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

    setPathToData(newTableData.getPathToData());
    setAutoUpdate(newTableData.getAutoUpdate());

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
  public List<Owl<?>> getChildrenOwls() throws OwlEntityHasNoContainingOwls {
    throw new OwlEntityHasNoContainingOwls();
  }

}
