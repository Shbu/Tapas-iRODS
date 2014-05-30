package org.bio5.irods.iplugin.swingworkers;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TasselCoreFunctions;
import org.bio5.irods.iplugin.utilities.Constants;
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
				/*filePathToGetObjStat = IrodsUtilities.getPathSeperator()
						+ irodsImageJ.getIrodsAccount().getZone()
						+ filePathToGetObjStat;*/
				filePathToGetObjStat = TasselCoreFunctions.getRootDirectoryPath(iplugin)+filePathToGetObjStat;
			} else {
				/*filePathToGetObjStat = IrodsUtilities.getPathSeperator()
						+ iplugin.getIrodsAccount().getZone()
						+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
						+ filePathToGetObjStat;*/
				filePathToGetObjStat = TasselCoreFunctions.getHomeDirectoryPath(iplugin) +filePathToGetObjStat;
			}
			filePathToGetObjStat = filePathToGetObjStat.replace('\\', '/');
			log.info("filePathToGetObjStat: " + filePathToGetObjStat);
			iRODSFileSystemAOImpl = iplugin.getiRODSFileSystemAOImpl();
			ObjStat objStatForGivenAbsolutePath = iRODSFileSystemAOImpl
					.getObjStat(filePathToGetObjStat);
			iplugin
					.setObjStatForGivenAbsolutePath(objStatForGivenAbsolutePath);

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
