package space.sadfox.dataccess.dataccess;

import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.Controller;
import space.sadfox.owlook.ui.base.ControllerException;

public interface ParserProvider extends OwlookModuleComponent {

  ParserTask createParser(Owl<ParserEntity> parserEntity, Owl<TableData> tableData);

  Controller createController(Owl<ParserEntity> parserEntity, Owl<TableData> tableData)
      throws ControllerException;

  Controller createController(Owl<ParserEntity> parserEntity) throws ControllerException;

}
