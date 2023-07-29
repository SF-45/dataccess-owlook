package space.sadfox.dataccess.view;

import java.io.IOException;

import jakarta.xml.bind.JAXBException;
import space.sadfox.owlook.utils.EntityLoader;
import space.sadfox.owlook.utils.OwlLogger;

public class TableDataViews {
	public static TableDataView createTableDataView() {
		try {
			return EntityLoader.INSTANCE.createEntity(TableDataView.class);
		} catch (JAXBException | IOException e) {
			OwlLogger.registerException(1, e);
		}
		return null;
	}
	
	public static boolean deleteTableDataView(TableDataView tableDataView) {
		return EntityLoader.INSTANCE.deleteEntity(tableDataView);
	}
}
