package space.sadfox.dataccess.command;

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
public class CommandEntityList extends JAXBEntity {

	private StringProperty title = new SimpleStringProperty("");
	private ObservableList<CommandEntity> commands = FXCollections.observableArrayList();

	@XmlElementWrapper(name = "Commands")
	@XmlElement(name = "Command")
	public List<CommandEntity> getCommands() {
		return commands;
	}

	public ObservableList<CommandEntity> commandsProperty() {
		return commands;
	}

	@XmlAttribute
	@Override
	public String getTitle() {
		return title.get();
	}

	public void setTitle(String name) {
		this.title.set(name);
	}

	public StringProperty titleProperty() {
		return title;
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getProperties() {
		return Arrays.asList(title, commands);
	}

	@Override
	public void initialize() {
		
	}
	
	@Override
	public boolean validate() {
		return true;
	}


	
	

}
