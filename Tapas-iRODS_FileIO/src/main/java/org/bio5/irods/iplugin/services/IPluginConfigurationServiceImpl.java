package org.bio5.irods.iplugin.services;

import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.exception.IpluginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IPluginConfigurationServiceImpl {
	public IPlugin iplugin = null;
	private static final Logger log = LoggerFactory
			.getLogger(IPluginConfigurationServiceImpl.class);

	public IPluginConfigurationServiceImpl(IPlugin iplugin)
			throws IpluginException {
		if (null != iplugin) {
			this.iplugin = iplugin;
		} else {
			log.info("iplugin is empty!");
		}
	}
}
