package org.bio5.irods.iplugin.bean;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public final class TasselCoreFunctions {

	/* Logger instantiation */
	static Logger log = Logger.getLogger(TasselCoreFunctions.class.getName());

	public static String getRootDirectoryPath(IPlugin iplugin) {

		String rootPath = null;
		if (null != iplugin.getIrodsAccount()) {
			rootPath = IrodsUtilities.getPathSeperator()
					+ iplugin.getIrodsAccount().getZone()
					+ IrodsUtilities.getPathSeperator();
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

	public void closeIRODSConnections(IPlugin iplugin) {
		if (iplugin.getIrodsFileSystem() != null) {
			iplugin.getIrodsFileSystem().closeAndEatExceptions(
					iplugin.getIrodsAccount());
		} else {
			log.error("IrodsFileSystem is empty");
		}

	}

	public static IRODSFileFactory getIrodsAccountFileFactory(IPlugin iplugin)
			throws JargonException {
		IRODSFileSystem irodsFileSystem=null;
			irodsFileSystem = IRODSFileSystem.instance();
			return irodsFileSystem.getIRODSFileFactory(iplugin
					.getIrodsAccount());
	}

}
