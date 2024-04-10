package space.sadfox.dataccess.dataccess;

import java.util.List;
import space.sadfox.owlook.moduleloader.ModuleLoader;

public class Parsers {
  public static ParserProvider getParserProvider(String identifier) throws ParserProviderNotFound {
    for (ParserProvider p : getParserProviders()) {
      if (identifier.equals(p.getIdentifier())) {
        return p;
      }
    }
    throw new ParserProviderNotFound();
  }

  public static List<ParserProvider> getParserProviders() {
    return ModuleLoader.INSTANCE.loadModuleComponents(ParserProvider.class,
        m -> m instanceof ParserProvider);
  }
}
