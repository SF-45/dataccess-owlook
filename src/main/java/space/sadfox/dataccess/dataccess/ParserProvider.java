package space.sadfox.dataccess.dataccess;

import java.util.List;

import space.sadfox.owlook.moduleapi.ModuleExtension;
import space.sadfox.owlook.ui.base.Controller;

public interface ParserProvider extends ModuleExtension {
	
	List<DataEntity> parse(TableData tableData);
	boolean validate(TableData tableData);
	Controller getConfigController(TableData tableData);

}
