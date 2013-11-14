package org.bio5.irods.imagej.fileoperations;

import java.util.Iterator;
import java.util.List;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class FileOperations {

	private static String HOME_DIR ="/iplant/home/";


	public static List<String> getDirectoryContents(
			IRODSAccount iRODSAccount,String parentFileName ) throws JargonException, FileNotFoundException {


		/*Getting default iRods Session*/
		IRODSSession iRODSSession =IrodsConnection.createDefaultiRodsSession();

		/*
		 * Irods File Factory*/
		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);

		/*irods file */
		IRODSFile iRodsFile =iRODSFileFactory.instanceIRODSFile(HOME_DIR +parentFileName);

		/*
		 * Directory List*/
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

	/**
	 * @param iRODSAccount
	 * @return
	 * @throws JargonException
	 */
	public static IRODSFileFactory getIrodsAccountFileFactory(
			IRODSAccount iRODSAccount) throws JargonException {
		IRODSFileSystem irodsFileSystem;
		irodsFileSystem = IRODSFileSystem.instance();
		IRODSFileFactory iRODSFileFactory =irodsFileSystem.getIRODSFileFactory(iRODSAccount);
		return iRODSFileFactory;
	}

	public static IRODSFile getIrodsAccountFile(IRODSAccount iRODSAccount) throws JargonException{

		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);
		IRODSFile iRodsFile =iRODSFileFactory.instanceIRODSFile(HOME_DIR +iRODSAccount.getUserName());
		return iRodsFile;

	}
	
	public void readImageFile(IRODSAccount iRODSAccount) throws JargonException
	{
		/*
		 * Irods File Factory*/
		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);
		
		
		
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
