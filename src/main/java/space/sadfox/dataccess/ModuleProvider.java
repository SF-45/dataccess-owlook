package space.sadfox.dataccess;

import java.util.Arrays;
import java.util.List;

import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.Module;
import space.sadfox.owlook.utils.Nullable;

public class ModuleProvider implements Module {

	@Override
	public String getModuleName() {
		// TODO Auto-generated method stub
		return "dataccess";
	}

	@Override
	public String getModuleDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getModuleVersion() {
		// TODO Auto-generated method stub
		return "0.6.0";
	}

	@Override
	public List<Class<? extends JAXBEntity>> getJaxbEntities() throws Nullable {
		return Arrays.asList(ActionEntity.class, TableData.class, TableDataFilter.class, TableDataView.class);
	}

}
