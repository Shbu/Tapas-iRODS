package org.bio5.irods.iplugin.bean;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.exception.IpluginException;
import org.bio5.irods.iplugin.services.IPluginConfigurationServiceImpl;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public final class TapasCoreFunctions {
	static Logger log = Logger.getLogger(TapasCoreFunctions.class.getName());

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

	public static void closeIRODSConnections(IPlugin iplugin)
			throws JargonException {
		if (iplugin.getIrodsFileSystem() != null) {
			iplugin.getIrodsFileSystem().closeAndEatExceptions(
					iplugin.getIrodsAccount());

			iplugin.getIrodsFileSystem().close();
		} else {
			log.error("IrodsFileSystem is empty");
		}
	}

	public static IRODSFileFactory getIrodsAccountFileFactory(IPlugin iplugin)
			throws JargonException {
		IRODSFileSystem irodsFileSystem = null;
		irodsFileSystem = IRODSFileSystem.instance();
		return irodsFileSystem.getIRODSFileFactory(iplugin.getIrodsAccount());
	}

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

	public static String getZoneDirectoryPath(IPlugin iplugin) {
		String zoneDirectoryPath = null;
		if (null != iplugin.getIrodsAccount()) {
			zoneDirectoryPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone();
		} else {
			log.error("iplugin is null");
		}
		return zoneDirectoryPath;
	}

	public static String getCustomDirectoryPath(IPlugin iplugin,
			String customPath) {
		String customDirectoryPath = null;
		if ((customPath.contains(IrodsUtilities.getPathSeperator()))
				|| (customPath.contains("\\")) || (customPath.contains("/"))) {
			String[] subPath = null;
			if (customPath.contains("\\")) {
				subPath = customPath.split("\\");
			}
			if (customPath.contains("/")) {
				subPath = customPath.split("/");
			}
			for (int i = 0; i < subPath.length; i++) {
				if ((!subPath[i].equals("")) && (customDirectoryPath == null)) {
					customDirectoryPath = IrodsUtilities.getPathSeperator()
							+ subPath[i];
				}
			}
		} else {
			customDirectoryPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone()
					+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
					+ IrodsUtilities.getPathSeperator() + customPath;
		}
		return customDirectoryPath;
	}
}
