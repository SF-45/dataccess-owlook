package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;

import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;
import space.sadfox.owlook.ui.base.Controller;

public interface ParserProvider extends OwlookModuleComponent {
	
	List<DataEntity> parse(TableData tableData);
	boolean validate(TableData tableData);
	Controller getConfigController(TableData tableData) throws IOException;

}
