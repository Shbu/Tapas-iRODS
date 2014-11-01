package org.bio5.irods.iplugin.swingworkers;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.pub.IRODSFileSystemAOImpl;
import org.irods.jargon.core.pub.domain.ObjStat;

public class ObjectDetailsLite {
	private IPlugin iplugin;
	private String filePathToGetObjStat;
	private IRODSFileSystemAOImpl iRODSFileSystemAOImpl;

	public ObjectDetailsLite(IPlugin iplugin) {
		this.iplugin = iplugin;
	}

	static Logger log = Logger.getLogger(ObjectDetailsLite.class.getName());

	public ObjStat getObjStatValueOfObj() throws Exception {
		ObjStat objStatLiteForGivenAbsolutePath = null;
		if (null != this.iplugin.getiRODSFileSystemAOImpl()) {
			this.filePathToGetObjStat = this.iplugin
					.getSelectedNodeInTreeForSingleClick();

			log.info("filePathToGetObjStat before getting objStat: "
					+ this.filePathToGetObjStat);
			if ((this.iplugin.getCustomPath() != null)
					&& (null != this.filePathToGetObjStat)) {
				String customPathForObjStat = this.iplugin.getCustomPath();
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
			} else if (this.iplugin.isHomeDirectoryTheRootNode()) {
				this.filePathToGetObjStat = (TapasCoreFunctions
						.getRootDirectoryPath(this.iplugin) + this.filePathToGetObjStat);
			} else {
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
			if ((null != this.iRODSFileSystemAOImpl)
					&& (null != this.filePathToGetObjStat)) {
				log.info("filePathToGetObjStat: " + this.filePathToGetObjStat);
				objStatLiteForGivenAbsolutePath = this.iRODSFileSystemAOImpl
						.getObjStat(this.filePathToGetObjStat);
			} else {
				log.error("iRODSFileSystemAOImpl is null");
			}
		} else {
			log.error("iRODSFileSystemAOImpl or ObjSelectedUsingSingleClick() is empty");
		}
		return objStatLiteForGivenAbsolutePath;
	}
}
