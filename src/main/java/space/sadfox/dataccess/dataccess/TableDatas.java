package space.sadfox.dataccess.dataccess;

import java.io.IOException;
import java.util.List;

import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.jaxb.EntityLoader;
import space.sadfox.owlook.utils.ModuleLoader;
import space.sadfox.owlook.utils.OwlLogger;

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
	
	public static TableData createTableData() {
		try {
			TableData tableData = EntityLoader.INSTANCE.createEntity(TableData.class);
			tableData.setTitle("New Table Data");
			return tableData;
		} catch (JAXBException | IOException e) {
			OwlLogger.registerException(1, e);
		}
		return null;
	}
	
	public static List<TableData> loadAllTableDatas() throws IOException {
		List<TableData> tableDatas = EntityLoader.INSTANCE.loadAllEntities(TableData.class);
		return tableDatas;

	}
	
	public static boolean deleteTableData(TableData tableData) {
		return EntityLoader.INSTANCE.deleteEntity(tableData);
	}
}
