package org.bio5.irods.iplugin.fileoperations;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.SettableJargonProperties;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.query.CollectionAndDataObjectListingEntry;

public class RetrieveInternalNodesSwingWorker extends
		SwingWorker<Void, Integer> {

	private Object[] pathForInternalFiles;
	private String singleClickPathForInternalFiles;
	private IPlugin irodsImageJ;
	private static IRODSFile iRodsFile = null;
	List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;

	public RetrieveInternalNodesSwingWorker(
			String singleClickPathForInternalFilesWithSizeCheck,
			Object[] pathForInternalFilesAsElements, IPlugin irodsImageJ) {
		super();
		this.singleClickPathForInternalFiles = singleClickPathForInternalFilesWithSizeCheck;
		this.pathForInternalFiles = pathForInternalFilesAsElements;
		this.irodsImageJ = irodsImageJ;
	}

	/* Logger instantiation */
	static Logger log = Logger.getLogger(RetrieveInternalNodesSwingWorker.class
			.getName());

	@Override
	public Void doInBackground() throws Exception {

		if (null != irodsImageJ) {
			/* Setting jargon properties */
			SettableJargonProperties jargonProperties = null;
			jargonProperties =	new SettableJargonProperties();
			log.info("Default threads : " + jargonProperties.getMaxParallelThreads());
			jargonProperties.setMaxParallelThreads(17);
			log.info("Threads upgraded to : " + jargonProperties.getMaxParallelThreads());
			IRODSFileFactory iRODSFileFactory = irodsImageJ
					.getiRODSFileFactory();
			String finalpathToGetIrodsFileInstance = irodsImageJ
					.getPathTillHome();
			String loopPath = "";
			log.info("ChildPath before adding path seperator"
					+ finalpathToGetIrodsFileInstance);

			String pathToPrefix = null;
			if (irodsImageJ.isHomeDirectoryTheRootNode()) {
				pathToPrefix = TapasCoreFunctions
						.getRootDirectoryPath(irodsImageJ);
				log.info("Path till root: " + pathToPrefix);
			}
			if (!irodsImageJ.isHomeDirectoryTheRootNode()) {
				pathToPrefix = TapasCoreFunctions
						.getHomeDirectoryPath(irodsImageJ);
				log.info("Path till home: " + pathToPrefix);
			}
			loopPath = singleClickPathForInternalFiles;
			log.info("looppath after adding singleClickpath:" + loopPath);

			if (null == loopPath || "" == loopPath) {
				if (null != pathForInternalFiles) {
					log.info("pathForInternalFiles length"
							+ pathForInternalFiles.length);

					for (int i = 0; i < pathForInternalFiles.length; i++) {
						log.info("pathForInternalFiles files: "
								+ pathForInternalFiles[i]);
						loopPath += IrodsUtilities.getPathSeperator()
								+ pathForInternalFiles[i].toString();
					}
				} else {
					log.error("pathForInternalFiles is null");
				}
				log.info("loop Path after adding pathForInternalFiles: "
						+ loopPath);
			} else {
				log.error("loopPath is either null or empty");
			}

			log.info("pathToPrefix: " + pathToPrefix);

			finalpathToGetIrodsFileInstance = pathToPrefix + loopPath;
			log.info("finalpath before getting irodsFile instance to pull files: "
					+ finalpathToGetIrodsFileInstance);

			if (null != iRODSFileFactory
					&& null != finalpathToGetIrodsFileInstance
					&& "" != finalpathToGetIrodsFileInstance) {
				iRodsFile = iRODSFileFactory
						.instanceIRODSFile(finalpathToGetIrodsFileInstance);
			}
			collectionsUnderGivenAbsolutePath = FileOperations
					.retrieveCollectionsUnderGivenPath(iRodsFile, irodsImageJ);

		}
		if (null != iRodsFile) {
			irodsImageJ.setiRodsFile(iRodsFile);
		}
		parseAndSetChildNodesToIplugin();
		return null;
	}

	@Override
	public void done() {

	}

	/*
	 * Change done() to something else - to some other method and wipe code in
	 * done() - it should be empty. Next call execute in invoking function and
	 * not doInbackground() - pending.
	 */
	/**
	 * 
	 */
	public void parseAndSetChildNodesToIplugin() {
		List<DefaultMutableTreeNode> childNodesListAfterLazyLoading = null;
		if (null != collectionsUnderGivenAbsolutePath) {
			childNodesListAfterLazyLoading = parseDirectoryContentsUsingList(collectionsUnderGivenAbsolutePath);
		} else {
			log.error("collectionsUnderGivenAbsolutePath is null");
		}
		if (null != irodsImageJ.getChildNodesListAfterLazyLoading()) {
			/* Clearing nodes before loading new nodes */
			irodsImageJ.getChildNodesListAfterLazyLoading().clear();
		} else {
			log.error("irodsImageJ.getChildNodesListAfterLazyLoading() is null");
		}
		if (null != childNodesListAfterLazyLoading) {
			if (childNodesListAfterLazyLoading.size() > 0) {
				irodsImageJ
						.setChildNodesListAfterLazyLoading(childNodesListAfterLazyLoading);
			} else {
				log.error("Child Nodes doesn't exist, empty folder!");
				JOptionPane.showMessageDialog(null, "Empty Folder!", "Error",
						JOptionPane.ERROR_MESSAGE);
				log.info("Removing childNodesListAfterLazyLoading from irodsImageJ");
				irodsImageJ.getChildNodesListAfterLazyLoading().clear();
				log.info("size of irodsImageJ.getChildNodesListAfterLazyLoading() "
						+ irodsImageJ.getChildNodesListAfterLazyLoading()
								.size());
			}
		} else {
			log.error("childNodesListAfterLazyLoading is null");
		}
	}

	/**
	 * @param listOfCollectionsUnderGivenAbsolutePath
	 * @return
	 */
	private List<DefaultMutableTreeNode> parseDirectoryContentsUsingList(
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath) {
		List<DefaultMutableTreeNode> listOfNodes = new ArrayList<DefaultMutableTreeNode>();
		CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntryForInternalNode = null;
		for (int i = 0; i < listOfCollectionsUnderGivenAbsolutePath.size(); i++) {
			collectionAndDataObjectListingEntryForInternalNode = listOfCollectionsUnderGivenAbsolutePath
					.get(i);
			if (!collectionAndDataObjectListingEntryForInternalNode
					.isCollection()) {
				log.info("File name:"
						+ collectionAndDataObjectListingEntryForInternalNode
								.getNodeLabelDisplayValue()
						+ ":"
						+ collectionAndDataObjectListingEntryForInternalNode
								.getFormattedAbsolutePath());
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						collectionAndDataObjectListingEntryForInternalNode
								.getNodeLabelDisplayValue(),
						false);
				listOfNodes.add(child);
			}

			if (collectionAndDataObjectListingEntryForInternalNode
					.isCollection()) {
				log.info("Direc name:"
						+ collectionAndDataObjectListingEntryForInternalNode
								.getNodeLabelDisplayValue());
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(
						collectionAndDataObjectListingEntryForInternalNode
								.getNodeLabelDisplayValue(),
						true);
				listOfNodes.add(child);
			}
		}
		return listOfNodes;
	}
}
