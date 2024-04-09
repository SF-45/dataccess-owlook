package space.sadfox.dataccess.dataccess;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import space.sadfox.owlook.utils.MessageLevel;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.OwlookMessage;

public class ParserProviderAdapter extends XmlAdapter<String, ParserProvider> {

  @Override
  public ParserProvider unmarshal(String v) throws Exception {
    try {
      return Parsers.getParserProvider(v);
    } catch (ParserProviderNotFound e) {
      OwlookMessage message = new OwlookMessage(MessageLevel.WARNING);
      message.setName("Parser Provider Not Found");
      message.setMessage("[" + v + "]");
      Owlook.registerMessage(message);
      return null;
    }
  }

  @Override
  public String marshal(ParserProvider v) throws Exception {
    if (v == null)
      return null;
    return v.getIdentifier();
  }

}
