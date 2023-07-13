package space.sadfox.dataccess;

import java.util.Arrays;
import java.util.List;

import space.sadfox.dataccess.action.ActionEntity;
import space.sadfox.dataccess.dataccess.TableData;
import space.sadfox.dataccess.filter.TableDataFilter;
import space.sadfox.dataccess.view.TableDataView;
import space.sadfox.owlook.jaxb.JAXBEntity;
import space.sadfox.owlook.moduleapi.ModuleHasNoConfiguration;
import space.sadfox.owlook.moduleapi.OwlookModule;
import space.sadfox.owlook.utils.Nullable;

public class ModuleProvider implements OwlookModule {

	@Override
	public String getShortModuleDescription() {
		return "Loading, accessing and searching a data set in tabular form";
	}

	@Override
	public String getModuleDescription() {
		return "Loading, accessing and searching a data set in tabular form";
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

	@Override
	public void initModule() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<? extends JAXBEntity> getConfigTarget() throws ModuleHasNoConfiguration {
		throw new ModuleHasNoConfiguration();
	}

}
