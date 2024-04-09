package space.sadfox.dataccess.action;

import java.io.IOException;
import java.util.List;
import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.base.owl.OwlEntityInitializeException;
import space.sadfox.owlook.moduleloader.ModuleLoader;
import space.sadfox.owlook.owlery.OwlLoader;

public class Actions {
  public static Owl<ActionEntity> createActionEntity(ActionProvider provider) throws IOException,
      JAXBException, ReflectiveOperationException, OwlEntityInitializeException {
    Owl<ActionEntity> actionEntityOwl = OwlLoader.INSTANCE.createOwl(ActionEntity.class);
    actionEntityOwl.entity().setActionProvider(provider);
    return actionEntityOwl;
  }

  public static List<ActionProvider> getActionProviders() {
    return ModuleLoader.INSTANCE.loadModuleComponents(ActionProvider.class,
        m -> m instanceof ActionProvider);

  }

  public static ActionProvider getActionProvider(String identifier) throws ActionProviderNotFound {
    for (ActionProvider a : getActionProviders()) {
      if (identifier.equals(a.getIdentifier())) {
        return a;
      }
    }
    throw new ActionProviderNotFound(identifier);

  }
}
