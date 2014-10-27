package org.bio5.irods.iplugin.fileoperations;

import java.net.SocketTimeoutException;
import java.util.List;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.CollectionAndDataObjectListAndSearchAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class FileOperations {
	private static IRODSFile iRodsFile;
	private static IRODSFileFactory iRODSFileFactory;
	static Logger log = Logger.getLogger(FileOperations.class.getName());

	public static List<CollectionAndDataObjectListingEntry> setIrodsFile(
			String pathForInternalFiles, IPlugin iPlugin,
			boolean isHomeDirectoryFlagOn) throws JargonException,
			SocketTimeoutException {
		SettableJargonProperties jp = new SettableJargonProperties();
		log.info("Default threads : " + jp.getMaxParallelThreads());
		iRODSFileFactory = iPlugin.getiRODSFileFactory();
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;
		if (null != pathForInternalFiles) {
			iRodsFile = iRODSFileFactory
					.instanceIRODSFile(pathForInternalFiles);

			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		} else if ((pathForInternalFiles == null) && (isHomeDirectoryFlagOn)) {
			pathForInternalFiles = TapasCoreFunctions
					.getHomeDirectoryPath(iPlugin);

			log.info("pathForInternalFiles till home directory: "
					+ pathForInternalFiles);

			iPlugin.setPathTillHome(pathForInternalFiles);
			log.info("irods file path if pathForInternalFiles is null and isHomeDirectoryFlagOn is true"
					+ iPlugin.getPathTillHome());

			iRodsFile = iRODSFileFactory.instanceIRODSFile(iPlugin
					.getPathTillHome());

			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		} else if ((pathForInternalFiles == null) && (!isHomeDirectoryFlagOn)) {
			pathForInternalFiles = TapasCoreFunctions
					.getAccountDirectoryPath(iPlugin);

			log.info("pathForInternalFiles till Account directory: "
					+ pathForInternalFiles);

			iPlugin.setPathTillHome(pathForInternalFiles);
			log.info("irods file path if pathForInternalFiles is null and isHomeDirectoryFlagOn is false:"
					+ iPlugin.getPathTillHome());

			iRodsFile = iRODSFileFactory.instanceIRODSFile(iPlugin
					.getPathTillHome());

			collectionsUnderGivenAbsolutePath = retrieveCollectionsUnderGivenPath(
					iRodsFile, iPlugin);
		}
		if (null != iRodsFile) {
			iPlugin.setiRodsFile(iRodsFile);
		}
		return collectionsUnderGivenAbsolutePath;
	}

	public static List<CollectionAndDataObjectListingEntry> retrieveCollectionsUnderGivenPath(
			IRODSFile irodsFileForAbsolutePath, IPlugin irodsImagej)
			throws SocketTimeoutException {
		List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;
		try {
			CollectionAndDataObjectListAndSearchAO CollectionAndDataObjectListAndSearchAO = irodsImagej
					.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getCollectionAndDataObjectListAndSearchAO(
							irodsImagej.getIrodsAccount());

			log.info("internal node path: "
					+ irodsFileForAbsolutePath.getAbsolutePath());

			collectionsUnderGivenAbsolutePath = CollectionAndDataObjectListAndSearchAO
					.listDataObjectsAndCollectionsUnderPath(irodsFileForAbsolutePath
							.getAbsolutePath());
			if (null == collectionsUnderGivenAbsolutePath) {
				log.error("No files retrieved from given path: "
						+ irodsFileForAbsolutePath.getAbsolutePath());
			}
		} catch (JargonException e) {
			log.error("Error while retrieving collectionsUnderGivenAbsolutePath: "
					+ e.getMessage());
		}
		return collectionsUnderGivenAbsolutePath;
	}
}
