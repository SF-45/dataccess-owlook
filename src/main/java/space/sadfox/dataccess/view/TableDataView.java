package space.sadfox.dataccess.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntity;
import space.sadfox.owlook.base.owl.OwlEntityHasNoContainingOwls;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableDataView extends OwlEntity implements Controllable {

  private ObservableList<FieldView> fieldViews = FXCollections.observableArrayList();

  @XmlElementWrapper(name = "fieldViews")
  @XmlElement(name = "fieldView")
  public List<FieldView> getFieldViews() {
    return fieldViews;
  }

  public ObservableList<FieldView> fieldViewsProperty() {
    return fieldViews;
  }

  @Override
  public List<Object> getProperties() {
    return Arrays.asList(fieldViews);
  }

  @Override
  public void initialize() {

  }

  @Override
  public String toString() {
    StringBuilder builder =
        new StringBuilder("TableDataView: " + getOwl().head().getTitle() + "\n\n");
    builder.append("FieldViews:\n");

    for (FieldView view : getFieldViews()) {
      builder.append("\t" + view.getFieldName() + " | " + view.getFriendlyFieldName() + " | "
          + view.getVisible() + "\n");
    }
    return builder.toString();
  }

  @Override
  public Controller getController() throws IOException {
    return new TableDataViewController((Owl<TableDataView>) getOwl());
  }

  public Controller getController(Owl<TableData> dataOwl) throws IOException, Nullable {
    return new TableDataViewController((Owl<TableDataView>) getOwl(), dataOwl);
  }

  @Override
  public void syncWith(OwlEntity entity) {
    if (!(entity instanceof TableDataView)) {
      return;
    }

    TableDataView targetView = (TableDataView) entity;

    getFieldViews().clear();
    targetView.getFieldViews().forEach(targetFieldView -> {
      FieldView newFieldView = new FieldView();
      newFieldView.setFieldName(targetFieldView.getFieldName());
      newFieldView.setFriendlyFieldName(targetFieldView.getFriendlyFieldName());
      newFieldView.setVisible(targetFieldView.getVisible());

      getFieldViews().add(newFieldView);
    });
  }

  @Override
  public List<Owl<?>> getChildrenOwls() throws OwlEntityHasNoContainingOwls {
    throw new OwlEntityHasNoContainingOwls();
  }



}
