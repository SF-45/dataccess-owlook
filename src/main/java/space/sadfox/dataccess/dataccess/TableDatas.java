package space.sadfox.dataccess.dataccess;

import java.util.List;

import space.sadfox.owlook.utils.ModuleLoader;

public class TableDatas {
	public static ParserProvider getParserProvider(String identifier) throws ParserProviderNotFound {
		for (ParserProvider p : getParserProviders()) {
			if (identifier.equals(p.getIdentifier())) {
				return p;
			}
		}
		throw new ParserProviderNotFound();
	}
	public static List<ParserProvider> getParserProviders() {
		return ModuleLoader.INSTANCE.loadModuleExtension(ParserProvider.class, m -> m instanceof ParserProvider);
	}
}
