module space.sadfox.dataccess {

	requires transitive space.sadfox.owlook;
	requires com.h2database;
	requires javafx.fxml;
	requires javafx.controls;
	
	exports space.sadfox.dataccess.dataccess;
	exports space.sadfox.dataccess.filter;
	exports space.sadfox.dataccess.view;
	exports space.sadfox.dataccess.command;
	
	exports space.sadfox.dataccess.command.hmadapter to org.glassfish.jaxb.core, org.glassfish.jaxb.runtime;
	
	opens space.sadfox.dataccess.dataccess to jakarta.xml.bind;
	opens space.sadfox.dataccess.filter to jakarta.xml.bind;
	opens space.sadfox.dataccess.view to jakarta.xml.bind;
	opens space.sadfox.dataccess.command to jakarta.xml.bind;
	opens space.sadfox.dataccess.command.hmadapter to jakarta.xml.bind;
	
	opens space.sadfox.dataccess.command.cmd to javafx.fxml;
	
}