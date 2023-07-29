package space.sadfox.dataccess.view;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.owlook.base.jaxb.JAXBEntity;
import space.sadfox.owlook.ui.base.Controllable;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.utils.Nullable;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableDataView extends JAXBEntity implements Controllable {
	
	private StringProperty title = new SimpleStringProperty();
	private ObservableList<FieldView> fieldViews = FXCollections.observableArrayList();
	
	@Override
	@XmlAttribute(name = "title")
	public String getTitle() {
		return title.get();
	}
	
	public void setTitle(String name) {
		this.title.set(name);
	}
	
	public StringProperty titleProperty() {
		return title;
	}
	
	@XmlElementWrapper(name = "FieldViews")
	@XmlElement(name = "FieldView")
	public List<FieldView> getFieldViews() {
		return fieldViews;
	}
	public ObservableList<FieldView> fieldViewsProperty() {
		return fieldViews;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, fieldViews);
	}

	@Override
	public void initialize() {
		
	}
	
	@Override
	public void validate() {
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("TableDataView: " + getTitle() + "\n\n");
		builder.append("FieldViews:\n");
		
		for (FieldView view : getFieldViews()) {
			builder.append("\t" + view.getFieldName() +" | "+ view.getFriendlyFieldName() + " | " + view.getVisible() + "\n");
		}
		return builder.toString();
	}

	@Override
	public Controller getConfigController() throws IOException{
		return new TableDataViewController(this);
	}
	
	public Controller getConfigController(TableData tableData) throws IOException, Nullable {
		return new TableDataViewController(this, tableData);
	}

	@Override
	public void syncWith(JAXBEntity entity) {
		if (!(entity instanceof TableDataView)) {
			return;
		}
		
		TableDataView targetView = (TableDataView) entity;
		
		setTitle(targetView.getTitle());
		
		getFieldViews().clear();
		targetView.getFieldViews().forEach(targetFieldView -> {
			FieldView newFieldView = new FieldView();
			newFieldView.setFieldName(targetFieldView.getFieldName());
			newFieldView.setFriendlyFieldName(targetFieldView.getFriendlyFieldName());
			newFieldView.setVisible(targetFieldView.getVisible());
			
			getFieldViews().add(newFieldView);
		});
	}
	
	


}
