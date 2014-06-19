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
		super();
		this.iplugin = iplugin;
	}

	/* Logger instantiation */
	static Logger log = Logger.getLogger(ObjectDetailsLite.class.getName());

	public ObjStat getObjStatValueOfObj() throws Exception {
		ObjStat objStatLiteForGivenAbsolutePath = null;

		if (null != iplugin.getiRODSFileSystemAOImpl()) {
			filePathToGetObjStat = iplugin.getSelectedNodeInTreeForSingleClick();
			if (iplugin.isHomeDirectoryTheRootNode()) {
				/*
				 * filePathToGetObjStat = IrodsUtilities.getPathSeperator() +
				 * irodsImageJ.getIrodsAccount().getZone() +
				 * filePathToGetObjStat;
				 */
				filePathToGetObjStat = TapasCoreFunctions
						.getRootDirectoryPath(iplugin) + filePathToGetObjStat;
			} else {
				/*
				 * filePathToGetObjStat = IrodsUtilities.getPathSeperator() +
				 * iplugin.getIrodsAccount().getZone() +
				 * IrodsUtilities.getPathSeperator() + Constants.HOME_STRING +
				 * filePathToGetObjStat;
				 */
				filePathToGetObjStat = TapasCoreFunctions
						.getHomeDirectoryPath(iplugin) + filePathToGetObjStat;
			}
			filePathToGetObjStat = filePathToGetObjStat.replace('\\', '/');
			log.info("filePathToGetObjStat: " + filePathToGetObjStat);

			filePathToGetObjStat = IrodsUtilities
					.refactorSlashInFilePaths(filePathToGetObjStat);
			iRODSFileSystemAOImpl = iplugin.getiRODSFileSystemAOImpl();

			if (null != iRODSFileSystemAOImpl && null != filePathToGetObjStat) {
				log.info("filePathToGetObjStat: " + filePathToGetObjStat);
				objStatLiteForGivenAbsolutePath = iRODSFileSystemAOImpl
						.getObjStat(filePathToGetObjStat);

			} else {
				log.error("iRODSFileSystemAOImpl is null");
			}

		} else {
			log.error("iRODSFileSystemAOImpl or ObjSelectedUsingSingleClick() is empty");
		}
		return objStatLiteForGivenAbsolutePath;
	}

}
