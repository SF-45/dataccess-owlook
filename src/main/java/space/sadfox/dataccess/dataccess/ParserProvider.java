package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;
import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.Controller;

public interface ParserProvider extends OwlookModuleComponent {

  List<DataEntity> parse(Owl<TableData> tableData);

  boolean validate(Owl<TableData> tableData);

  Controller getConfigController(Owl<TableData> tableData) throws IOException;

}
