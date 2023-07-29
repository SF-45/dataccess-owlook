import space.sadfox.dataccess.ModuleProvider;
import space.sadfox.owlook.base.moduleapi.OwlookModule;

module space.sadfox.dataccess {

	requires transitive space.sadfox.owlook;
	requires com.h2database;
	requires java.sql;
	requires java.desktop;
	
	exports space.sadfox.dataccess.dataccess;
	exports space.sadfox.dataccess.filter;
	exports space.sadfox.dataccess.view;
	exports space.sadfox.dataccess.action;
	
	opens space.sadfox.dataccess.dataccess to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.filter to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.view to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.action to jakarta.xml.bind, javafx.fxml;
	
	
	provides OwlookModule with ModuleProvider;
	
}