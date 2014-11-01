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
	private IPlugin iplugin;
	private static IRODSFile iRodsFile = null;
	List<CollectionAndDataObjectListingEntry> collectionsUnderGivenAbsolutePath = null;

	public RetrieveInternalNodesSwingWorker(
			String singleClickPathForInternalFilesWithSizeCheck,
			Object[] pathForInternalFilesAsElements, IPlugin iplugin) {
		this.singleClickPathForInternalFiles = singleClickPathForInternalFilesWithSizeCheck;
		this.pathForInternalFiles = pathForInternalFilesAsElements;
		this.iplugin = iplugin;
	}

	static Logger log = Logger.getLogger(RetrieveInternalNodesSwingWorker.class
			.getName());

	public Void doInBackground() throws Exception {
		if (null != this.iplugin) {
			SettableJargonProperties jargonProperties = null;
			jargonProperties = new SettableJargonProperties();
			log.info("Default threads : "
					+ jargonProperties.getMaxParallelThreads());

			IRODSFileFactory iRODSFileFactory = this.iplugin
					.getiRODSFileFactory();

			String finalpathToGetIrodsFileInstance = this.iplugin
					.getPathTillHome();

			String loopPath = "";
			log.info("ChildPath before adding path seperator internalFolders: "
					+ finalpathToGetIrodsFileInstance);

			String pathToPrefix = null;
			String filePathToGetObjStat = this.iplugin
					.getSelectedNodeInTreeForSingleClick();
			if (null != this.iplugin.getCustomPath()) {
				String customPathForObjStat = this.iplugin.getCustomPath();
				log.info("Custom Path in RetrieveInternalNodesSwingWorker: "
						+ customPathForObjStat);

				String[] Stringtokens = IrodsUtilities
						.getStringTokensForGivenURI(customPathForObjStat);

				log.info("filePathToGetObjStat path: " + filePathToGetObjStat);

				String newCustomPathAfterTokenizing = "";
				if (Stringtokens.length > 2) {
					for (int i = 2; i < Stringtokens.length; i++) {
						newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
								+ IrodsUtilities.getPathSeperator()
								+ Stringtokens[i];
					}
				} else {
					log.error("length of String tokens is less that 2");
				}
				log.info("newCustomPathAfterTokenizing: "
						+ newCustomPathAfterTokenizing);

				newCustomPathAfterTokenizing = IrodsUtilities
						.replaceBackSlashWithForwardSlash_ViceVersa(newCustomPathAfterTokenizing);

				log.info("newCustomPathAfterTokenizing after adding changing fileseperator: "
						+ newCustomPathAfterTokenizing);

				customPathForObjStat = customPathForObjStat
						+ newCustomPathAfterTokenizing;
				log.info("final path to get ObjStat details for CustomPath: "
						+ customPathForObjStat);

				pathToPrefix = customPathForObjStat;
			}
			if ((this.iplugin.isHomeDirectoryTheRootNode())
					&& (null == this.iplugin.getCustomPath())) {
				pathToPrefix = TapasCoreFunctions
						.getRootDirectoryPath(this.iplugin);

				log.info("Path till root: " + pathToPrefix);
			}
			if ((!this.iplugin.isHomeDirectoryTheRootNode())
					&& (null == this.iplugin.getCustomPath())) {
				pathToPrefix = TapasCoreFunctions
						.getHomeDirectoryPath(this.iplugin);

				log.info("Path till home: " + pathToPrefix);
			}
			loopPath = this.singleClickPathForInternalFiles;
			log.info("looppath after adding singleClickpath:" + loopPath);
			if ((null == loopPath) || ("" == loopPath)) {
				if (null != this.pathForInternalFiles) {
					log.info("length of pathForInternalFiles.length: "
							+ this.pathForInternalFiles.length);
					for (int i = 0; i < this.pathForInternalFiles.length; i++) {
						log.info("pathForInternalFiles files: "
								+ this.pathForInternalFiles[i]);

						loopPath = loopPath + IrodsUtilities.getPathSeperator()
								+ this.pathForInternalFiles[i].toString();
					}
				} else {
					log.error("pathForInternalFiles is null");
				}
				log.info("loop Path after adding pathForInternalFiles: "
						+ loopPath);
			} else {
				log.error("loopPath is neither null nor empty" + loopPath);
			}
			if (null != this.iplugin.getCustomPath()) {
				finalpathToGetIrodsFileInstance = pathToPrefix;
				log.info("finalpathToGetIrodsFileInstance when customPath is not null: "
						+ finalpathToGetIrodsFileInstance);
			} else {
				finalpathToGetIrodsFileInstance = pathToPrefix + loopPath;
				log.info("finalpathToGetIrodsFileInstance is null: "
						+ finalpathToGetIrodsFileInstance);
			}
			if (null != this.iplugin.getCustomPath()) {
				finalpathToGetIrodsFileInstance = this.iplugin.getCustomPath();
				
				String newPathAfterTokenizingLoopPath="";
				if(null!=loopPath){
					
					String[] stringtokensLoopPath = IrodsUtilities
							.getStringTokensForGivenURI(loopPath);
					
					
					if (stringtokensLoopPath.length > 2) {
						for (int i = 2; i < stringtokensLoopPath.length; i++) {
							newPathAfterTokenizingLoopPath = newPathAfterTokenizingLoopPath
									+ IrodsUtilities.getPathSeperator()
									+ stringtokensLoopPath[i];
						}
						log.info("newPathAfterTokenizingLoopPath :" +newPathAfterTokenizingLoopPath);
					} else {
						log.error("length of String tokens is less that 2");
					}
				}
				
				
				
				finalpathToGetIrodsFileInstance = finalpathToGetIrodsFileInstance
						+ newPathAfterTokenizingLoopPath;
				log.info("Final loop path after getting customePath: "
						+ finalpathToGetIrodsFileInstance);
				finalpathToGetIrodsFileInstance = IrodsUtilities
						.replaceBackSlashWithForwardSlash_ViceVersa(finalpathToGetIrodsFileInstance);
			}
			log.info("finalpath before getting irodsFile instance to pull files: "
					+ finalpathToGetIrodsFileInstance);
			if ((null != iRODSFileFactory)
					&& (null != finalpathToGetIrodsFileInstance)
					&& ("" != finalpathToGetIrodsFileInstance)) {
				iRodsFile = iRODSFileFactory
						.instanceIRODSFile(finalpathToGetIrodsFileInstance);

				log.info("Got instance of iRODSFile for finalpathToGetIrodsFileInstance ");
			}
			this.collectionsUnderGivenAbsolutePath = FileOperations
					.retrieveCollectionsUnderGivenPath(iRodsFile, this.iplugin);

			log.info("After executing retrieveCollectionsUnderGivenPath: "
					+ this.collectionsUnderGivenAbsolutePath);
		}
		if (null != iRodsFile) {
			this.iplugin.setiRodsFile(iRodsFile);
		}
		parseAndSetChildNodesToIplugin();
		return null;
	}

	public void done() {
	}

	public void parseAndSetChildNodesToIplugin() {
		log.info("Inside parseAndSetChildNodesToIplugin method");
		List<DefaultMutableTreeNode> childNodesListAfterLazyLoading = null;
		if (null != this.collectionsUnderGivenAbsolutePath) {
			childNodesListAfterLazyLoading = parseDirectoryContentsUsingList(this.collectionsUnderGivenAbsolutePath);
		} else {
			log.error("collectionsUnderGivenAbsolutePath is null");
		}
		if (null != this.iplugin.getChildNodesListAfterLazyLoading()) {
			this.iplugin.getChildNodesListAfterLazyLoading().clear();
		} else {
			log.error("irodsImageJ.getChildNodesListAfterLazyLoading() is null");
		}
		if (null != childNodesListAfterLazyLoading) {
			if (childNodesListAfterLazyLoading.size() > 0) {
				log.info("Clearing and Setting childNodesListAfterLazyLoading into iplugin");
				this.iplugin
						.setChildNodesListAfterLazyLoading(childNodesListAfterLazyLoading);
			} else {
				log.error("Child Nodes doesn't exist, empty folder!");
				JOptionPane
						.showMessageDialog(null, "Empty Folder!", "Error", 0);

				log.info("Removing childNodesListAfterLazyLoading from irodsImageJ");
				this.iplugin.getChildNodesListAfterLazyLoading().clear();
				log.info("size of irodsImageJ.getChildNodesListAfterLazyLoading() "
						+ this.iplugin.getChildNodesListAfterLazyLoading()
								.size());
			}
		} else {
			log.error("childNodesListAfterLazyLoading is null");
		}
	}

	private List<DefaultMutableTreeNode> parseDirectoryContentsUsingList(
			List<CollectionAndDataObjectListingEntry> listOfCollectionsUnderGivenAbsolutePath) {
		List<DefaultMutableTreeNode> listOfNodes = new ArrayList();
		CollectionAndDataObjectListingEntry collectionAndDataObjectListingEntryForInternalNode = null;
		for (int i = 0; i < listOfCollectionsUnderGivenAbsolutePath.size(); i++) {
			collectionAndDataObjectListingEntryForInternalNode = (CollectionAndDataObjectListingEntry) listOfCollectionsUnderGivenAbsolutePath
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
