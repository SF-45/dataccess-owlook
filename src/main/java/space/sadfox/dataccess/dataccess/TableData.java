package space.sadfox.dataccess.dataccess;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import space.sadfox.owlook.jaxb.JAXBEntity;


@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class TableData extends JAXBEntity {
	
	private StringProperty title = new SimpleStringProperty();

    private StringProperty pathToData = new SimpleStringProperty("");
    
    private BooleanProperty autoUpdate = new SimpleBooleanProperty(false);

    private ObservableList<Field> fields = FXCollections.observableArrayList();
    
    private ObservableList<ParserFilter> prefilters = FXCollections.observableArrayList();

	@Override
	@XmlAttribute
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String tableName) {
		this.title.set(tableName);
	}
	
	public StringProperty titleProperty() {
		return title;
	}

	@XmlElementWrapper(name = "Prefilters")
	@XmlElement(name = "Prefilter")
	public List<ParserFilter> getPrefilters() {
		return prefilters;
	}
	
	public ObservableList<ParserFilter> prefiltersProperty() {
		return prefilters;
	}
	
	@XmlAttribute(name = "PathToData")
	public String getPathToData() {
		return pathToData.get();
	}

	public void setPathToData(String pathToData) {
		this.pathToData.set(pathToData);
	}
	
	public StringProperty pathToDataProperty() {
		return pathToData;
	}
	
	@XmlAttribute(name = "AutoUpdate")
	public Boolean getAutoUpdate() {
		return autoUpdate.get();
	}

	public void setAutoUpdate(Boolean autoUpdate) {
		this.autoUpdate.set(autoUpdate);
	}
	
	public BooleanProperty autoUpdateProperty() {
		return autoUpdate;
	}

	@XmlElementWrapper(name = "Fields")
	@XmlElement(name = "Field")
	public List<Field> getFields() {
		return fields;
	}
	
	public ObservableList<Field> fieldsProperty() {
		return fields;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, pathToData, autoUpdate, fields, prefilters);
	}

	@Override
	public String getExtension() {
		return ".tdata";
	}

//	@Override
//	public PreLoadAction getPreLoadAction() {
//		return (entity) -> {
//			if (entity instanceof TableData) {
//				TableData tData = (TableData) entity;
//				if (tData.getAutoUpdate()) {
//					new TableDataDao(tData).loadData();
//				}
//			}
//		};
//	} TODO: Удалить потом

	@Override
	public void initialize() {
		if (getAutoUpdate()) {
			new TableDataDao(this).loadData();
		}
		
	}
	
	@Override
	public boolean validate() {
		return true;
	}

	
	
	
	
    
    
    
    

}
