package org.bio5.irods.imagej.fileoperations;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJ;
import org.bio5.irods.imagej.connection.IrodsConnection;
import org.bio5.irods.imagej.utilities.Constants;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.connection.IRODSSession;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;

public class FileOperations {

	private IRODSFileFactory iRODSFileFactory;
	private static IRODSFileSystem irodsFileSystem;
	private static IRODSFile iRodsFile;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(FileOperations.class.getName());

	public static List<String> getDirectoryContents(IRODSAccount iRODSAccount,String pathForInternalFiles, IrodsImageJ irodsImagej)
			throws JargonException, FileNotFoundException {

		/* Getting default iRods Session */
		IRODSSession iRODSSession = IrodsConnection.createDefaultiRodsSession();
		/* Setting jargon properties */
		SettableJargonProperties jp = new SettableJargonProperties();
		log.info("Default threads : " + jp.getMaxParallelThreads());
		jp.setMaxParallelThreads(10);
		log.info("Threads upgraded to : " + jp.getMaxParallelThreads());

		/*
		 * Irods File Factory
		 */
		IRODSFileFactory iRODSFileFactory = getIrodsAccountFileFactory(iRODSAccount);

		String parentFileName = iRODSAccount.getUserName();
		
		/* irods file */
		if(null!= pathForInternalFiles){
			iRodsFile = iRODSFileFactory.instanceIRODSFile(pathForInternalFiles);
		}
		else{
		iRodsFile = iRODSFileFactory.instanceIRODSFile(Constants.HOME_DIR_IPLANT_HOME
				+ parentFileName);
		
		}
		irodsImagej.setiRodsFile(iRodsFile);
		
		/*
		 * Directory List
		 */
		IRODSFileSystemAOImpl IRODSFileSystemAOImpl = new IRODSFileSystemAOImpl(
				iRODSSession, iRODSAccount);
		log.info("irods file path" + iRodsFile);
        
		List<String> listInDir = IRODSFileSystemAOImpl.getListInDir(iRodsFile);

		Iterator<String> listInDirectory = listInDir.iterator();
		int count = 1;
		while (listInDirectory.hasNext()) {
			log.info("Files in Dir:" +count +" " +listInDirectory.next());
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

		irodsFileSystem = IRODSFileSystem.instance();
		IRODSFileFactory iRODSFileFactory = irodsFileSystem
				.getIRODSFileFactory(iRODSAccount);
		return iRODSFileFactory;
	}

}
