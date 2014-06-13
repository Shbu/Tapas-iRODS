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
		super();
		this.iplugin = irodsImageJ;
	}

	/* Logger instantiation */
	static Logger log = Logger.getLogger(ObjectDetailsSwingWorker.class
			.getName());

	@Override
	protected Void doInBackground() throws Exception {

		if (null != iplugin.getiRODSFileSystemAOImpl()) {
			filePathToGetObjStat = iplugin.getObjSelectedUsingSingleClick();
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
			ObjStat objStatForGivenAbsolutePath = null;
			if (null != iRODSFileSystemAOImpl && null != filePathToGetObjStat) {
				log.info("filePathToGetObjStat: " + filePathToGetObjStat);
				objStatForGivenAbsolutePath = iRODSFileSystemAOImpl
						.getObjStat(filePathToGetObjStat);
				if (null != objStatForGivenAbsolutePath) {
					iplugin.setObjStatForGivenAbsolutePath(objStatForGivenAbsolutePath);
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

	@Override
	public void done() {
		DirectoryContentsWindow dcp = iplugin.getDirectoryContentsPane();
		dcp.setFileInformationFromObjStat(iplugin
				.getObjStatForGivenAbsolutePath());
	}

}
