import space.sadfox.dataccess.ModuleProvider;
import space.sadfox.dataccess.action.ActionTool;
import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.owlook.moduleapi.Tool;

module space.sadfox.dataccess {

	requires transitive space.sadfox.owlook;
	requires com.h2database;
	requires javafx.fxml;
	requires javafx.controls;
	
	exports space.sadfox.dataccess.dataccess;
	exports space.sadfox.dataccess.filter;
	exports space.sadfox.dataccess.view;
	exports space.sadfox.dataccess.action;
	
	opens space.sadfox.dataccess.dataccess to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.filter to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.view to jakarta.xml.bind, javafx.fxml;
	opens space.sadfox.dataccess.action to jakarta.xml.bind, javafx.fxml;
	
	
	provides Module with ModuleProvider;
	provides Tool with ActionTool;
	
}