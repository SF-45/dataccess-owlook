package space.sadfox.dataccess.dataccess;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import space.sadfox.owlook.utils.Owlook;
import space.sadfox.owlook.utils.LogLevel;
import space.sadfox.owlook.utils.LogMessage;

public class ParserProviderAdapter extends XmlAdapter<String, ParserProvider> {

	@Override
	public ParserProvider unmarshal(String v) throws Exception {
		try {
			return TableDatas.getParserProvider(v);
		} catch (ParserProviderNotFound e) {
			LogMessage message = new LogMessage(LogLevel.WARNING);
			message.setName("Parser Provider Not Found [" + v + "]");
			message.setMessage("Parser Provider Not Found [" + v + "]");
			Owlook.registerMessage(message);
			return null;
		}
	}

	@Override
	public String marshal(ParserProvider v) throws Exception {
		if (v == null) return null;
		return v.getIdentifier();
	}

}
