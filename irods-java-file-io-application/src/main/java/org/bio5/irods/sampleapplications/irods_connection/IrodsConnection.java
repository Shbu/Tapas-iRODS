package org.bio5.irods.sampleapplications.irods_connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.pub.IRODSFileSystem;


public class IrodsConnection {


	private static final String AUTHENTICATION_SCHEME = "AuthenticationScheme";
	private static final String HOME_DIRECTORY = "homeDirectory";
	public static String HOME_DIR ="/iplant";
	public static String ZONE="iplant";
	public static final String DEFAULT_STORAGE_RESOURCE = null;


	public static IRODSAccount irodsConnection(String userName, String password, String zone, String host, int port) {

		IRODSAccount iRODSAccount = new IRODSAccount ( 
				host, port, userName, password, HOME_DIR, zone, DEFAULT_STORAGE_RESOURCE );
		return iRODSAccount;
	}

	public static HashMap<String, Object> accountDetails(IRODSAccount iRODSAccount) {
		/*
		 * Account Details*/
		HashMap<String, Object> irodsAccount_Details_Map= new HashMap<String, Object>();
		irodsAccount_Details_Map.put(HOME_DIRECTORY, iRODSAccount.getHomeDirectory());
		irodsAccount_Details_Map.put(AUTHENTICATION_SCHEME, iRODSAccount.getAuthenticationScheme());
		
		return irodsAccount_Details_Map;
		
	}
	
	public static IRODSSession createDefaultiRodsSession()
		{
		IRODSFileSystem irodsFileSystem;
		IRODSSession iRODSSession = null;
		try{
		irodsFileSystem= IRODSFileSystem.instance();
		iRODSSession =irodsFileSystem.getIrodsSession();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return iRODSSession;
		
	}
}
