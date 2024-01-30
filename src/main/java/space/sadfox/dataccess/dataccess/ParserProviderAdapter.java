package space.sadfox.dataccess.dataccess;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import space.sadfox.owlook.logger.LogLevel;
import space.sadfox.owlook.utils.Logger;
import space.sadfox.owlook.utils.LoggerMessage;

public class ParserProviderAdapter extends XmlAdapter<String, ParserProvider> {

	@Override
	public ParserProvider unmarshal(String v) throws Exception {
		try {
			return TableDatas.getParserProvider(v);
		} catch (ParserProviderNotFound e) {
			LoggerMessage message = new LoggerMessage(LogLevel.WARNING);
			message.setName("Parser Provider Not Found [" + v + "]");
			message.setMessage("Parser Provider Not Found [" + v + "]");
			Logger.registerMessage(message);
			return null;
		}
	}

	@Override
	public String marshal(ParserProvider v) throws Exception {
		if (v == null) return null;
		return v.getIdentifier();
	}

}
