package org.bio5.irods.iplugin.services;

import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.exception.IpluginException;
import org.slf4j.LoggerFactory;

public class IPluginConfigurationServiceImpl {

	public IPlugin iplugin = null;

	private static final org.slf4j.Logger log = LoggerFactory
			.getLogger(IPluginConfigurationServiceImpl.class);

	public IPluginConfigurationServiceImpl(final IPlugin iplugin)
			throws IpluginException {

		if (null != iplugin) {
			this.iplugin = iplugin;

			
			/*Code realted to Jargon Conveyor*/
			/*try {
				
				ConveyorBootstrapConfiguration conveyorBootstrapConfiguration = new ConveyorBootstrapConfiguration();
				ConveyorBootstrapper conveyorBootstrapper = new BasicConveyorBootstrapperImpl(
						conveyorBootstrapConfiguration);
				ConveyorService conveyorService = conveyorBootstrapper
						.bootstrap(iplugin.getIrodsFileSystem()
								.getIRODSAccessObjectFactory());
				log.info("Created ConveyorService instance from conveyorBootStrapper");
				iplugin.setConveyorService(conveyorService);
				ConveyorCallbackListener conveyorCallBackListener = (ConveyorCallbackListener) iplugin.getIrodsTransferStatusCallbackListener();
				conveyorService.setConveyorCallbackListener(conveyorCallBackListener);
			}

			catch (Exception exception) {
				Logger.getLogger(
						IPluginConfigurationServiceImpl.class.getName()).log(
						Level.SEVERE, null, exception);

				if (exception.getMessage().indexOf("Failed to start database") != -1) {
					throw new IpluginAlreadyRunningException(
							"iDrop is already running");
				} else {
					throw new IpluginException(exception);
				}

			}*/
		} else {
			log.info("iplugin is empty!");
		}

	}

}