package space.sadfox.dataccess.view;

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
import space.sadfox.owlook.jaxb.JAXBEntity;

@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableDataView extends JAXBEntity {
	
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
	public String getExtension() {
		return ".tdataview";
	}

	@Override
	public void initialize() {
		
	}
	
	@Override
	public boolean validate() {
		return true;
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
	
	


}
