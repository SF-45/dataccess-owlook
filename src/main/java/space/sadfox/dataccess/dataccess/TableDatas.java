package space.sadfox.dataccess.dataccess;

import java.util.List;

import space.sadfox.owlook.base.owl.Owl;
import space.sadfox.owlook.moduleloader.ModuleLoader;
import space.sadfox.owlook.owlery.OwlLoader;
import space.sadfox.owlook.utils.Logger;

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
		return ModuleLoader.INSTANCE.loadModuleComponents(ParserProvider.class, m -> m instanceof ParserProvider);
	}
	
	public static Owl<TableData> createTableDataOwl() throws Exception {
		return OwlLoader.INSTANCE.createOwl(TableData.class);
	}
	
	public static List<Owl<TableData>> loadAllTableDataOwls() {
		return OwlLoader.INSTANCE.getOwls(TableData.class);

	}
	
	public static boolean deleteTableDataOwl(Owl<TableData> owl) {
		try {
			OwlLoader.INSTANCE.deleteOwl(owl);
			return true;
		} catch (Exception e) {
			Logger.registerException(1, e);
			return false;
		}
	}
}
