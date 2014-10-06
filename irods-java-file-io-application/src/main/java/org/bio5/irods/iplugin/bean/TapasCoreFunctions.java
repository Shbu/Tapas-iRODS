package org.bio5.irods.iplugin.bean;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.exception.IpluginException;
import org.bio5.irods.iplugin.services.IPluginConfigurationServiceImpl;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

/**
 * @author Sharan
 * 
 */
public final class TapasCoreFunctions {

	/* Logger instantiation */
	static Logger log = Logger.getLogger(TapasCoreFunctions.class.getName());

	/**
	 * This method returns directoryPath (root path) for given account. Ex:
	 * /<zone>/
	 * 
	 * @param iplugin
	 * @return
	 */
	public static String getRootDirectoryPath(IPlugin iplugin) {

		String rootPath = null;
		if (null != iplugin.getIrodsAccount()) {
			rootPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone();
		} else {
			log.error("iplugin is null");
		}
		return rootPath;
	}

	/**
	 * This method returns home directory path for given account Ex:
	 * /<zone>/home
	 * 
	 * @param iplugin
	 * @return
	 */
	public static String getHomeDirectoryPath(IPlugin iplugin) {

		String homeDirectoryPath = null;
		if (null != iplugin.getIrodsAccount()) {
			homeDirectoryPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone()
					+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING;
		} else {
			log.error("iplugin is null");
		}
		return homeDirectoryPath;
	}

	/**
	 * This method will return AccountDirectoryPath for given user. Ex:
	 * /<Zone>/home/<username>
	 * 
	 * @param iplugin
	 * @return
	 */
	public static String getAccountDirectoryPath(IPlugin iplugin) {
		String accountDirectoryPath = null;
		if (null != iplugin.getIrodsAccount()) {
			accountDirectoryPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone()
					+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
					+ IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getUserName();
		} else {
			log.error("iplugin is null");
		}

		return accountDirectoryPath;
	}

	/**
	 * This method will close all active iRODS Connections
	 * 
	 * @param iplugin
	 */
	public static void closeIRODSConnections(IPlugin iplugin) {
		if (iplugin.getIrodsFileSystem() != null) {
			iplugin.getIrodsFileSystem().closeAndEatExceptions(
					iplugin.getIrodsAccount());
		} else {
			log.error("IrodsFileSystem is empty");
		}

	}

	/**
	 * Returns IrodsAcccountFileFactory object.
	 * 
	 * @param iplugin
	 * @return
	 * @throws JargonException
	 */
	public static IRODSFileFactory getIrodsAccountFileFactory(IPlugin iplugin)
			throws JargonException {
		IRODSFileSystem irodsFileSystem = null;
		irodsFileSystem = IRODSFileSystem.instance();
		return irodsFileSystem.getIRODSFileFactory(iplugin.getIrodsAccount());
	}

	/**
	 * Create ConfigurationService for Tapa Transactions
	 * 
	 * @param iplugin
	 * @throws IpluginException
	 */
	public static IPluginConfigurationServiceImpl createConfigurationServiceForTapasTransactions(
			IPlugin iplugin) throws IpluginException {
		if (null != iplugin) {
			IPluginConfigurationServiceImpl iPluginConfigurationService = null;
			iPluginConfigurationService = new IPluginConfigurationServiceImpl(
					iplugin);
			return iPluginConfigurationService;
		}
		return null;
	}
	
	/*Code realted to Jargon Conveyor*/

	/**
	 * Validate PassPhrase in Tear off mode
	 * 
	 * @param iplugin
	 */
	/*public static void validatePassPhraseInTearOffMode(IPlugin iplugin) {
		if (null != iplugin.getIrodsAccount()) {
			try {
				GridAccountService gridAccountService = iplugin
						.getConveyorService().getGridAccountService();
				//gridAccountService.validatePassPhrase("abc1d");
				try {
					iplugin.getConveyorService()
							.validatePassPhraseInTearOffMode(
									iplugin.getIrodsAccount());
				} catch (AuthenticationException authenticationException) {
					log.error("AuthenticationException while validatePassPhraseInTearOffMode"
							+ authenticationException.getMessage());

				} catch (JargonException jargonException) {
					log.error("JargonException while validatePassPhraseInTearOffMode"
							+ jargonException.getMessage());
				}
				if (null != gridAccountService) {
					gridAccountService
							.addOrUpdateGridAccountBasedOnIRODSAccount(iplugin
									.getIrodsAccount());
					GridAccount gridAccount = gridAccountService
							.findGridAccountByIRODSAccount(iplugin
									.getIrodsAccount());
					if (null != gridAccount) {
						log.info("gaccount details" + gridAccount.getUserName());
						iplugin.setGridAccount(gridAccount);
					}
				}

			} catch (ConveyorExecutionException conveyorExecutionException) {
				log.error("Conveyor execution exception while Authenticating the user"
						+ conveyorExecutionException.getMessage());
			} catch (PassPhraseInvalidException passPhraseInvalidException) {
				log.error("PassPhraseInvalidException while Authenticating the user"
						+ passPhraseInvalidException.getMessage());
			}
		} else {
			log.error("iRODS account object in iPlugin is null!");
		}
	}*/
}
