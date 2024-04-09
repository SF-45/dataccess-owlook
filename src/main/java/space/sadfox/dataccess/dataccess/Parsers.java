package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntityInitializeException;
import space.sadfox.owlook.moduleloader.ModuleLoader;
import space.sadfox.owlook.owlery.OwlLoader;

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

  public static Owl<ParserEntity> createParserEntity(ParserProvider parserProvider)
      throws IOException, JAXBException, ReflectiveOperationException,
      OwlEntityInitializeException {
    Owl<ParserEntity> parserEntity = OwlLoader.INSTANCE.createOwl(ParserEntity.class);
    parserEntity.entity().setParserProvider(parserProvider);
    parserEntity.head().setHidden(true);
    return parserEntity;
  }
}
