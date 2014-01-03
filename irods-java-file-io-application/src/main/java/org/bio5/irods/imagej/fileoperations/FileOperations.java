package org.bio5.irods.imagej.fileoperations;

import java.util.Iterator;
import java.util.List;

import org.bio5.irods.imagej.connection.IrodsConnection;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.connection.JargonProperties;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class FileOperations {

	private static String HOME_DIR ="/iplant/home/";

	private IRODSFileFactory iRODSFileFactory;
	private static IRODSFileSystem irodsFileSystem;
	private static IRODSFile iRodsFile ;

	public IRODSFileSystem getIrodsFileSystem() {
		return irodsFileSystem;
	}

	public void setIrodsFileSystem(IRODSFileSystem irodsFileSystem) {
		this.irodsFileSystem = irodsFileSystem;
	}

	public IRODSFileFactory getiRODSFileFactory() {
		return iRODSFileFactory;
	}

	public void setiRODSFileFactory(IRODSFileFactory iRODSFileFactory) {
		this.iRODSFileFactory = iRODSFileFactory;
	}

	public static List<String> getDirectoryContents(
			IRODSAccount iRODSAccount) throws JargonException, FileNotFoundException {


		/*Getting default iRods Session*/
		IRODSSession iRODSSession =IrodsConnection.createDefaultiRodsSession();
		
		/*Setting jargon properties*/
		SettableJargonProperties jp= new SettableJargonProperties();
		System.out.println(" Threads before updating -" +jp.getMaxParallelThreads());
		jp.setMaxParallelThreads(10);
		System.out.println(" Threads after updating-" +jp.getMaxParallelThreads());
		
		/*
		 * Irods File Factory*/
		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);

		String parentFileName  =iRODSAccount.getUserName();
		/*irods file */
		iRodsFile =iRODSFileFactory.instanceIRODSFile(HOME_DIR +parentFileName);

		/*
		 * Directory List*/
		IRODSFileSystemAOImpl IRODSFileSystemAOImpl  =new IRODSFileSystemAOImpl(iRODSSession, iRODSAccount);
		System.out.println("irods file path" +iRodsFile);
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

	public static IRODSFile getiRodsFile() {
		return iRodsFile;
	}

	public static void setiRodsFile(IRODSFile iRodsFile) {
		FileOperations.iRodsFile = iRodsFile;
	}

	/**
	 * @param iRODSAccount
	 * @return
	 * @throws JargonException
	 */
	public static IRODSFileFactory getIrodsAccountFileFactory(
			IRODSAccount iRODSAccount) throws JargonException {
		
		irodsFileSystem = IRODSFileSystem.instance();
		IRODSFileFactory iRODSFileFactory =irodsFileSystem.getIRODSFileFactory(iRODSAccount);
		return iRODSFileFactory;
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
