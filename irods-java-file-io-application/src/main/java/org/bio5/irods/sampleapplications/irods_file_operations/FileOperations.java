package org.bio5.irods.sampleapplications.irods_file_operations;

import java.util.Iterator;
import java.util.List;

import org.bio5.irods.sampleapplications.irods_connection.IrodsConnection;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class FileOperations {
	
	public static String HOME_DIR ="/iplant/home/";

	
	public static List<String> DirectoryContentsList(
			IRODSAccount iRODSAccount) throws JargonException, FileNotFoundException {
		
		
		/*Getting default iRods Session*/
		IRODSSession iRODSSession =IrodsConnection.createDefaultiRodsSession();
		
		
		/*
		 * Irods File Factory*/
		IRODSFileSystem irodsFileSystem;
		irodsFileSystem = IRODSFileSystem.instance();
		IRODSFileFactory iRODSFileFactory =irodsFileSystem.getIRODSFileFactory(iRODSAccount);
		
		/*irods file */
		IRODSFile iRodsFile =iRODSFileFactory.instanceIRODSFile(HOME_DIR +iRODSAccount.getUserName());
		
		
		/*
		 * File Operations*/
		IRODSFileSystemAOImpl IRODSFileSystemAOImpl  =new IRODSFileSystemAOImpl(iRODSSession, iRODSAccount);
		List<String> listInDir  = IRODSFileSystemAOImpl.getListInDir(iRodsFile);

		Iterator<String> listInDirectory =listInDir.iterator();
		int count = 1;
		while(listInDirectory.hasNext())
		{
			System.out.println("Files in Dir:" +count +" " +listInDirectory.next());
			count++;
		}
		return listInDir;
	}
	
/*	public static IRODSFileFactory getIrodsFileFactory(IRODSAccount iRODSAccount)
	{
		try{
		IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();
		IRODSFileFactory iRODSFileFactory =irodsFileSystem.getIRODSFileFactory(iRODSAccount);
		}
		catch()
		return iRODSFileFactory;
	}*/

}
