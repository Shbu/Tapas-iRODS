package org.bio5.irods.imagej.fileoperations;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJBean;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsPropertiesConstruction;
import org.bio5.irods.imagej.utilities.IrodsTransferStatusCallbackListener;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class GetFileFromIrodsSwingWorker extends SwingWorker<Void, Integer> {

	private IRODSFileFactory iRODSFileFactory;
	private String treePath;
	private DataTransferOperations dataTransferOperationsAO;
	private IrodsImageJBean irodsImagej;
	private DataObjectAO dataObjectAO;
	private JProgressBar jprogressbar;
	private TransferControlBlock transferControlBlock;
	IRODSFile sourceIrodsFilePath = null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(DirectoryContentsPane.class.getName());

	/* Get files from iRODS Server */
	public GetFileFromIrodsSwingWorker(IRODSFileFactory iRODSFileFactory,
			String treePath, IrodsImageJBean irodsImagej,
			JProgressBar progressbar) {
		this.iRODSFileFactory = iRODSFileFactory;
		this.treePath = treePath;
		this.irodsImagej = irodsImagej;
		this.jprogressbar = irodsImagej.getJprogressbar();

	}

	/*
	 * Using SwingWorker-doInBackGround() function to do processing in
	 * background
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Void doInBackground() throws Exception {

		// getImageFile(iRODSFileFactory,treePath,irodsAccount );
		log.info("finalTreePath:" + treePath);

		/* Recheck irodsAccounZone for all accounts */
		// IRODSFileInputStream irodsfileistream =
		// iRODSFileFactory.instanceIRODSFileInputStream(IrodsUtilities.pathSeperator()
		// +irodsAccount.getZone() +treePath);

		if (null != irodsImagej) {
			transferControlBlock = irodsImagej.getTransferControlBlock();
			irodsImagej.setTransferControlBlock(transferControlBlock);
			irodsImagej.setTransferOptions(transferControlBlock
					.getTransferOptions());
			irodsImagej.getTransferOptions().setMaxThreads(10);
			// Get file to local directory using getDataTransferOperations ---
			// Need to check benchmarks
			dataTransferOperationsAO = irodsImagej.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataTransferOperations(irodsImagej.getIrodsAccount());
			/*
			 * Check if user requires all files under home directory - this has
			 * performance hit
			 */
			if (irodsImagej.isHomeDirectoryTheRootNode()) {
				sourceIrodsFilePath = iRODSFileFactory
						.instanceIRODSFile(IrodsUtilities.getPathSeperator()
								+ irodsImagej.getIrodsAccount().getZone()
								+ treePath);
			} else {
				sourceIrodsFilePath = iRODSFileFactory
						.instanceIRODSFile(IrodsUtilities.getPathSeperator()
								+ irodsImagej.getIrodsAccount().getZone()
								+ IrodsUtilities.getPathSeperator()
								+ Constants.HOME + treePath);

			}

			dataObjectAO = irodsImagej.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataObjectAO(irodsImagej.getIrodsAccount());
			/* Getting MD5 checksum of the current file from iRODS */
			String md5ChecksumLocalFile = null;
			String md5ChecksumServerFile = null;
			try {
				md5ChecksumServerFile = dataObjectAO
						.computeMD5ChecksumOnDataObject(sourceIrodsFilePath);
				log.info("MD5checksum of iRODS server file: "
						+ md5ChecksumServerFile);
			} catch (Exception e) {
				log.info("Error while reading MD5 checksum" + e.getMessage());
			}
			File destinationLocalFilePath = new File(
					Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);
			log.info("sourceIrodsFilePath before inserting file"
					+ sourceIrodsFilePath);
			log.info("destinationLocalFilePath before inserting file"
					+ destinationLocalFilePath);
			try {
				if (null != sourceIrodsFilePath) {
					if (null != irodsImagej) {
						/*
						 * IrodsTransferStatusCallbackListener
						 * irodsTransferStatusCallbackListener = new
						 * IrodsTransferStatusCallbackListener(
						 * iRODSFileFactory, null, irodsImagej, jprogressbar);
						 * irodsImagej .setIrodsTransferStatusCallbackListener(
						 * irodsTransferStatusCallbackListener);
						 */
						log.info("IntraFileStatusCallBack: "
								+ transferControlBlock.getTransferOptions()
										.isIntraFileStatusCallbacks());
						dataTransferOperationsAO
								.getOperation(
										sourceIrodsFilePath,
										destinationLocalFilePath,
										irodsImagej
												.getIrodsTransferStatusCallbackListener(),
										transferControlBlock);

						/*
						 * Getting CollectionAndDataObjectListAndSearchAO -
						 * Experiment CollectionAndDataObjectListAndSearchAO
						 * collectionAO = irodsImagej.getIrodsFileSystem().
						 * getIRODSAccessObjectFactory
						 * ().getCollectionAndDataObjectListAndSearchAO
						 * (irodsImagej.getIrodsAccount());
						 * List<CollectionAndDataObjectListingEntry> childCache
						 * =
						 * collectionAO.listDataObjectsAndCollectionsUnderPath(
						 * destinationLocalFilePath.toString());
						 */

					}
				}
			} catch (OverwriteException oe) {
				log.error("File with same name already exist in local directory! "
						+ oe.getMessage());
				JOptionPane
						.showMessageDialog(null,
								"File with same name already exist in local directory!");

				/* Getting MD5 checksum of local file, if exists */
				File localFile = new File(
						destinationLocalFilePath.getAbsolutePath()
								+ IrodsUtilities.getPathSeperator()
								+ sourceIrodsFilePath.getName());
				md5ChecksumLocalFile = IrodsUtilities
						.calculateMD5CheckSum(localFile);
				log.info("MD5checksum of local file: " + md5ChecksumLocalFile);

				log.info("MD5 checksum compared - Similar files:"
						+ md5ChecksumLocalFile.equals(md5ChecksumServerFile));

				if (!md5ChecksumLocalFile.equals(md5ChecksumServerFile))
					JOptionPane
							.showMessageDialog(null,
									"File names are same but MD5 checksum is different!");

				oe.printStackTrace();
			} catch (JargonException je) {
				JOptionPane.showMessageDialog(null,
						"Error while pulling files!");
				log.info("Error while pulling files!");
			}
		}
		return null;
	}

	/*
	 * @Override public void process(List<Integer> chunks) { for(int i : chunks)
	 * { jprogressbar.setValue(i); } }
	 */

	@Override
	public void done() {
		/* Opening the selected ImageJ */
		Opener imageOpener = new Opener();
		String imageFilePath = Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY
				+ IrodsUtilities.getPathSeperator()
				+ sourceIrodsFilePath.getName();
		log.info("Current file opened by user: " + imageFilePath);
		ImagePlus imp = imageOpener.openImage(imageFilePath);
		// ImagePlus imp = IJ.openImage(imageFilePath);

		if (imp != null) {
			log.info("ImagePlus is not null and before calling show() function of ImagePlus class");
			imp.show();
		} else {
			IJ.showMessage("Opening file Failed.");
			IJ.showStatus("Opening file Failed.");
			log.error("ImagePlus instance is null and opening file Failed.");
		}
	}
}
