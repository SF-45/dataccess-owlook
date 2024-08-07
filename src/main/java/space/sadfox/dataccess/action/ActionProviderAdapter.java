package space.sadfox.dataccess.action;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import space.sadfox.owlook.utils.MessageLevel;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.OwlookMessage;

public class ActionProviderAdapter extends XmlAdapter<String, ActionProvider> {

  @Override
  public ActionProvider unmarshal(String v) throws Exception {
    try {
      return Actions.getActionProvider(v);
    } catch (ActionProviderNotFound e) {
      OwlookMessage message = new OwlookMessage(MessageLevel.ERROR);
      message.setName("Owl Load Error");
      message.setMessage("While loading Owl, the provider was not found");
      Owlook.notificate(message);
      Owlook.registerException(e);
      throw e;
    }
  }

  @Override
  public String marshal(ActionProvider v) throws Exception {
    return v.getIdentifier();
  }


}
