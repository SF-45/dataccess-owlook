package space.sadfox.dataccess;

import space.sadfox.owlook.base.jaxb.ObservedJAXBEntity;
import space.sadfox.owlook.base.moduleapi.ModuleHasNoConfiguration;
import space.sadfox.owlook.base.moduleapi.OwlookModule;

public class ModuleProvider implements OwlookModule {

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
	public void initModule() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<? extends ObservedJAXBEntity> getConfigTarget() throws ModuleHasNoConfiguration {
		throw new ModuleHasNoConfiguration();
	}

}
