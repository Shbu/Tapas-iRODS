package org.bio5.irods.iplugin.fileoperations;

import java.util.List;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TasselCoreFunctions;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class FileOperations {

	private static IRODSFile iRodsFile;
	private static IRODSFileFactory iRODSFileFactory;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(FileOperations.class.getName());

	/**
	 * @param iRODSAccount
	 * @return
	 * @throws JargonException
	 */
	

	public static List<CollectionAndDataObjectListingEntry> setIrodsFile(
			String pathForInternalFiles, IPlugin iPlugin,
			boolean isHomeDirectoryFlagOn) throws JargonException {

		/* Setting jargon properties */
		SettableJargonProperties jp = new SettableJargonProperties();
		log.info("Default threads : " + jp.getMaxParallelThreads());
		jp.setMaxParallelThreads(10);
		log.info("Threads upgraded to : " + jp.getMaxParallelThreads());

		iRODSFileFactory = iPlugin.getiRODSFileFactory();
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;

		if (null != pathForInternalFiles) {

			iRodsFile = iRODSFileFactory
					.instanceIRODSFile(pathForInternalFiles);
			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		} else if (pathForInternalFiles == null && isHomeDirectoryFlagOn) {

			pathForInternalFiles = TasselCoreFunctions
					.getHomeDirectoryPath(iPlugin);
			log.info("pathForInternalFiles till home directory: " + pathForInternalFiles);

			/*
			 * IrodsUtilities.getPathSeperator() +
			 * irodsImagej.getIrodsAccount().getZone() +
			 * IrodsUtilities.getPathSeperator() + Constants.HOME_STRING;
			 */

			/*
			 * pathForInternalFiles = IrodsUtilities.getPathSeperator() +
			 * irodsImagej.getIrodsAccount().getZone();
			 */

			iPlugin.setPathTillHome(pathForInternalFiles);
			log.info("irods file path if pathForInternalFiles is null and isHomeDirectoryFlagOn is true"
					+ iPlugin.getPathTillHome());

			iRodsFile = iRODSFileFactory.instanceIRODSFile(iPlugin
					.getPathTillHome());
			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		} else if (pathForInternalFiles == null && !isHomeDirectoryFlagOn) {

			pathForInternalFiles = TasselCoreFunctions
					.getAccountDirectoryPath(iPlugin);
			log.info("pathForInternalFiles till Account directory: "+pathForInternalFiles);
			/*
			 * pathForInternalFiles = IrodsUtilities.getPathSeperator() +
			 * iPlugin.getIrodsAccount().getZone() +
			 * IrodsUtilities.getPathSeperator() + Constants.HOME_STRING +
			 * IrodsUtilities.getPathSeperator() +
			 * iPlugin.getIrodsAccount().getUserName();
			 */
			iPlugin.setPathTillHome(pathForInternalFiles);
			log.info("irods file path if pathForInternalFiles is null and isHomeDirectoryFlagOn is false:"
					+ iPlugin.getPathTillHome());

			iRodsFile = iRODSFileFactory.instanceIRODSFile(iPlugin
					.getPathTillHome());
			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		}
		iPlugin.setiRodsFile(iRodsFile);

		return collectionsUnderGivenAbsolutePath;
	}

	public static List<CollectionAndDataObjectListingEntry> retrieveCollectionsUnderGivenPath(
			IRODSFile irodsFileForAbsolutePath, IPlugin irodsImagej) {

		CollectionAndDataObjectListAndSearchAO CollectionAndDataObjectListAndSearchAO;
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;
		try {
			CollectionAndDataObjectListAndSearchAO = irodsImagej
					.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getCollectionAndDataObjectListAndSearchAO(
							irodsImagej.getIrodsAccount());
			log.info("internal node path"
					+ irodsFileForAbsolutePath.getAbsolutePath());
			// collectionsUnderGivenAbsolutePath =
			// collectionAO.listDataObjectsAndCollectionsUnderPath(path);
			collectionsUnderGivenAbsolutePath = CollectionAndDataObjectListAndSearchAO
					.listDataObjectsAndCollectionsUnderPath(irodsFileForAbsolutePath
							.getAbsolutePath());
			if(null==collectionsUnderGivenAbsolutePath){
				log.error("No files retrieved from given path: " +irodsFileForAbsolutePath
						.getAbsolutePath());
			}

		} catch (JargonException e) {
			log.error("Error while retrieving collectionsUnderGivenAbsolutePath: "
					+ e.getMessage());
			/*
			 * JOptionPane.showMessageDialog(null,
			 * "Error while retrieving collectionsUnderGivenAbsolutePath!",
			 * "Error", JOptionPane.ERROR_MESSAGE);
			 */
		}
		return collectionsUnderGivenAbsolutePath;
	}
}
