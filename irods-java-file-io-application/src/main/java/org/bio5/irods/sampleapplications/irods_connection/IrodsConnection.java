package org.bio5.irods.sampleapplications.irods_connection;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSFileSystem;


public class IrodsConnection {


	public static final String DATA_IPLANTCOLLABORATIVE_ORG = "data.iplantcollaborative.org";
	public static String HOME_DIR ="/iplant/home/";
	public static String ZONE="iplant";
	public static final String DEFAULT_STORAGE_RESOURCE = null;




	public static IRODSAccount irodsConnection(String userName, String password) {
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));

		IRODSAccount iRODSAccount = new IRODSAccount ( 
				DATA_IPLANTCOLLABORATIVE_ORG, 1247, userName, password, HOME_DIR, ZONE, DEFAULT_STORAGE_RESOURCE );
		IRODSFileSystem irodsFileSystem = null;
		return iRODSAccount;
	}

	private static void accountDetails(IRODSAccount iRODSAccount) {
		/*
		 * Account Details*/
		System.out.println("Welcome "+ iRODSAccount.getUserName() +"..!");
	}
}
