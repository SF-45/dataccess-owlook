package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import space.sadfox.owlook.base.moduleapi.OwlookModuleComponent;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.ui.base.Controller;

public interface ParserProvider extends OwlookModuleComponent {

  ParserTask createParser(Owl<ParserEntity> parserEntity, Owl<TableData> tableData);

  Controller createController(Owl<ParserEntity> parserEntity, Owl<TableData> tableData)
      throws IOException;

  Controller createController(Owl<ParserEntity> parserEntity) throws IOException;

}
