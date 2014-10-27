package org.bio5.irods.iplugin.connection;

import java.util.HashMap;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.pub.IRODSFileSystem;

public class IrodsConnection {
	private static final String AUTHENTICATION_SCHEME = "AuthenticationScheme";
	private static final String HOME_DIRECTORY = "homeDirectory";
	public static String HOME_DIR = "/iplant";
	public static String ZONE = "iplant";

	public static IRODSAccount irodsConnection(String userName,
			String password, String zone, String host, int port) {
		IRODSAccount iRODSAccount = new IRODSAccount(host, port, userName,
				password, HOME_DIR, zone, "");

		return iRODSAccount;
	}

	public static HashMap<String, Object> accountDetails(
			IRODSAccount iRODSAccount) {
		HashMap<String, Object> irodsAccount_Details_Map = new HashMap();
		irodsAccount_Details_Map.put("homeDirectory",
				iRODSAccount.getHomeDirectory());

		irodsAccount_Details_Map.put("AuthenticationScheme",
				iRODSAccount.getAuthenticationScheme());

		return irodsAccount_Details_Map;
	}

	public static IRODSSession createDefaultiRodsSession() {
		IRODSSession iRODSSession = null;
		try {
			IRODSFileSystem irodsFileSystem = IRODSFileSystem.instance();
			iRODSSession = irodsFileSystem.getIrodsSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return iRODSSession;
	}
}
