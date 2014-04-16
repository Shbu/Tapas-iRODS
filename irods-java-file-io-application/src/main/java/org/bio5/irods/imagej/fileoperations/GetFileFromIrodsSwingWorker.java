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
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
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

	}

	/*
	 * Using SwingWorker-doInBackGround() function to do processing in
	 * background
	 */
	@SuppressWarnings("deprecation")
	@Override
	public Void doInBackground() throws Exception {

		log.info("finalTreePath:" + treePath);

		if (null != irodsImagej) {
			transferControlBlock = irodsImagej.getTransferControlBlock();
			irodsImagej.setTransferControlBlock(transferControlBlock);
			irodsImagej.setTransferOptions(transferControlBlock
					.getTransferOptions());
			irodsImagej.getTransferOptions().setMaxThreads(10);
			dataTransferOperationsAO = irodsImagej.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataTransferOperations(irodsImagej.getIrodsAccount());
			/*
			 * Check if user requires all files under home directory - this has
			 * performance degradation.
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
			} catch (Exception e) {
				log.info("Error while reading MD5 checksum of md5ChecksumServerFile"
						+ e.getMessage());
			}
			File destinationLocalFilePath = new File(
					irodsImagej.getImageJCacheFolder());
			log.info("sourceIrodsFilePath before inserting file"
					+ sourceIrodsFilePath);
			log.info("destinationLocalFilePath before inserting file"
					+ destinationLocalFilePath);

			/* Getting MD5 checksum of local file, if exists */
			File localFile = new File(
					destinationLocalFilePath.getAbsolutePath()
							+ IrodsUtilities.getPathSeperator()
							+ sourceIrodsFilePath.getName());
			md5ChecksumLocalFile = IrodsUtilities
					.calculateMD5CheckSum(localFile);
			log.info("MD5checksum of iRODS server file: "
					+ md5ChecksumServerFile);
			log.info("MD5checksum of local file: " + md5ChecksumLocalFile);

			if (null != md5ChecksumLocalFile && null != md5ChecksumServerFile
					&& "" != md5ChecksumLocalFile
					&& "" != md5ChecksumServerFile) {
				log.info("MD5 checksum compared - are they Similar files ?"
						+ md5ChecksumLocalFile.equals(md5ChecksumServerFile));

				if (!md5ChecksumLocalFile.equals(md5ChecksumServerFile))
					JOptionPane
							.showMessageDialog(
									null,
									"Local cache directory have files with same name but MD5 checksum is different!");
			}
			try {
				if (null != sourceIrodsFilePath) {
					if (null != irodsImagej) {
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
					}
				}
			} catch (OverwriteException oe) {
				log.error("File with same name already exist in local directory! "
						+ oe.getMessage());
				JOptionPane
						.showMessageDialog(null,
								"File with same name already exist in local directory!");

				/* Getting MD5 checksum of local file, if exists */
				File localFile2 = new File(
						destinationLocalFilePath.getAbsolutePath()
								+ IrodsUtilities.getPathSeperator()
								+ sourceIrodsFilePath.getName());
				md5ChecksumLocalFile = IrodsUtilities
						.calculateMD5CheckSum(localFile2);
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

	@Override
	public void done() {
		/* Opening the selected ImageJ */
		Opener imagejOpener = new Opener();
		String imageFilePath = irodsImagej.getImageJCacheFolder()
				+ IrodsUtilities.getPathSeperator()
				+ sourceIrodsFilePath.getName();
		log.info("Current file opened by user: " + imageFilePath);
		ImagePlus imagePlus = imagejOpener.openImage(imageFilePath);
		// ImagePlus imagePlus = IJ.openImage(imageFilePath);

		if (imagePlus != null) {
			log.info("ImagePlus instance is not null and before calling show() function of ImagePlus class");
			imagePlus.show();
		} else {
			IJ.showMessage("ImageJ is not able to open requested file!");
			IJ.showStatus("ImageJ is not able to open requested file!");
			log.error("ImagePlus instance is null and opening file Failed.");
		}
	}
}
