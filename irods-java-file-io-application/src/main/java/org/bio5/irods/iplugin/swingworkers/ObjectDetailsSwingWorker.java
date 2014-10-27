package org.bio5.irods.iplugin.swingworkers;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.bio5.irods.iplugin.views.DirectoryContentsWindow;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.domain.ObjStat;

public class ObjectDetailsSwingWorker extends SwingWorker<Void, Integer> {
	private IPlugin iplugin;
	private String filePathToGetObjStat;
	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;

	public ObjectDetailsSwingWorker(IPlugin irodsImageJ) {
		this.iplugin = irodsImageJ;
	}

	static Logger log = Logger.getLogger(ObjectDetailsSwingWorker.class
			.getName());

	protected Void doInBackground() throws Exception {
		if (null != this.iplugin.getiRODSFileSystemAOImpl()) {
			this.filePathToGetObjStat = this.iplugin
					.getObjSelectedUsingSingleClick();
			if (null != this.iplugin.getCustomPath()) {
				String customPathForObjStat = this.iplugin.getCustomPath();
				log.info("Custom Path in ObjectDetailsSwingWorker: "
						+ customPathForObjStat);

				String[] Stringtokens = IrodsUtilities
						.getStringTokensForGivenURI(this.filePathToGetObjStat);

				String newCustomPathAfterTokenizing = "";
				for (int i = 2; i < Stringtokens.length; i++) {
					newCustomPathAfterTokenizing = newCustomPathAfterTokenizing
							+ IrodsUtilities.getPathSeperator()
							+ Stringtokens[i];
				}
				log.info("newCustomPathAfterTokenizing: "
						+ newCustomPathAfterTokenizing);

				newCustomPathAfterTokenizing = IrodsUtilities
						.replaceBackSlashWithForwardSlash_ViceVersa(newCustomPathAfterTokenizing);

				customPathForObjStat = customPathForObjStat
						+ newCustomPathAfterTokenizing;
				log.info("final path to get ObjStat details for CustomPath: "
						+ customPathForObjStat);

				this.filePathToGetObjStat = customPathForObjStat;
			} else if ((this.iplugin.isHomeDirectoryTheRootNode())
					&& (null == this.iplugin.getCustomPath())) {
				this.filePathToGetObjStat = (TapasCoreFunctions
						.getRootDirectoryPath(this.iplugin) + this.filePathToGetObjStat);
			} else if ((!this.iplugin.isHomeDirectoryTheRootNode())
					&& (null == this.iplugin.getCustomPath())) {
				this.filePathToGetObjStat = (TapasCoreFunctions
						.getHomeDirectoryPath(this.iplugin) + this.filePathToGetObjStat);
			}
			this.filePathToGetObjStat = this.filePathToGetObjStat.replace('\\',
					'/');
			log.info("filePathToGetObjStat: " + this.filePathToGetObjStat);

			this.filePathToGetObjStat = IrodsUtilities
					.refactorSlashInFilePaths(this.filePathToGetObjStat);

			this.iRODSFileSystemAOImpl = this.iplugin
					.getiRODSFileSystemAOImpl();
			ObjStat objStatForGivenAbsolutePath = null;
			if ((null != this.iRODSFileSystemAOImpl)
					&& (null != this.filePathToGetObjStat)) {
				log.info("filePathToGetObjStat: " + this.filePathToGetObjStat);
				objStatForGivenAbsolutePath = this.iRODSFileSystemAOImpl
						.getObjStat(this.filePathToGetObjStat);
				if (null != objStatForGivenAbsolutePath) {
					this.iplugin
							.setObjStatForGivenAbsolutePath(objStatForGivenAbsolutePath);
				} else {
					log.error("objStatForGivenAbsolutePath is null");
				}
			} else {
				log.error("iRODSFileSystemAOImpl is null");
			}
		} else {
			log.error("iRODSFileSystemAOImpl or ObjSelectedUsingSingleClick() is empty");
		}
		return null;
	}

	public void done() {
		DirectoryContentsWindow dcp = this.iplugin.getDirectoryContentsPane();
		dcp.setFileInformationFromObjStat(this.iplugin
				.getObjStatForGivenAbsolutePath());
	}
}
