package org.bio5.irods.iplugin.fileoperations;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageWindow;
import ij.io.Opener;

import java.io.File;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.bean.TapasCoreFunctions;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.DataNotFoundException;
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
	private IPlugin iPlugin;
	private DataObjectAO dataObjectAO;
	private TransferControlBlock transferControlBlock;
	IRODSFile sourceIrodsFilePath = null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(GetFileFromIrodsSwingWorker.class
			.getName());

	/* Get files from iRODS Server */
	public GetFileFromIrodsSwingWorker(IRODSFileFactory iRODSFileFactory,
			String treePath, IPlugin irodsImagej, JProgressBar progressbar) {
		this.iRODSFileFactory = iRODSFileFactory;
		this.treePath = treePath;
		this.iPlugin = irodsImagej;

	}

	/*
	 * Using SwingWorker-doInBackGround() function to do processing in
	 * background
	 */
	@Override
	public Void doInBackground() throws JargonException {

		log.info("finalTreePath:" + treePath);

		if (null != iPlugin) {
			transferControlBlock = iPlugin.getTransferControlBlock();
			if (null != transferControlBlock) {
				iPlugin.setTransferOptions(transferControlBlock
						.getTransferOptions());
			} else {
				log.error("transferControlBlock is null");
			}
			// iPlugin.getTransferOptions().setMaxThreads(10);
			dataTransferOperationsAO = iPlugin.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataTransferOperations(iPlugin.getIrodsAccount());
			/*
			 * Check if user requires all files under home directory - this has
			 * performance degradation.
			 */
			if (iPlugin.isHomeDirectoryTheRootNode()) {
				sourceIrodsFilePath = iRODSFileFactory
						.instanceIRODSFile(TapasCoreFunctions
								.getRootDirectoryPath(iPlugin) + treePath);
				log.info("sourceIrodsFilePath" + sourceIrodsFilePath);

				/*
				 * iRODSFileFactory
				 * .instanceIRODSFile(IrodsUtilities.getPathSeperator() +
				 * iPlugin.getIrodsAccount().getZone() + treePath);
				 */
			} else {
				sourceIrodsFilePath = iRODSFileFactory
						.instanceIRODSFile(TapasCoreFunctions
								.getHomeDirectoryPath(iPlugin) + treePath);
				/*
				 * iRODSFileFactory
				 * .instanceIRODSFile(IrodsUtilities.getPathSeperator() +
				 * iPlugin.getIrodsAccount().getZone() +
				 * IrodsUtilities.getPathSeperator() + Constants.HOME_STRING +
				 * treePath);
				 */
				log.info("sourceIrodsFilePath" + sourceIrodsFilePath);

			}

			dataObjectAO = iPlugin.getIrodsFileSystem()
					.getIRODSAccessObjectFactory()
					.getDataObjectAO(iPlugin.getIrodsAccount());

			/* Getting MD5 checksum of the current file from iRODS */
			String md5ChecksumLocalFile = null;
			String md5ChecksumServerFile = null;
			try {
				md5ChecksumServerFile = dataObjectAO
						.computeMD5ChecksumOnDataObject(sourceIrodsFilePath);
			} catch (Exception e) {
				log.info("Error while reading MD5 checksum of md5ChecksumServerFile"
						+ e.getMessage());
				JOptionPane
						.showMessageDialog(
								null,
								"Error while reading MD5 checksum of md5ChecksumServerFile!",
								"Error", JOptionPane.ERROR_MESSAGE);
			}
			File destinationLocalFilePath = new File(
					iPlugin.getImageJCacheFolder());
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
									"Local cache directory have files with same name but MD5 checksum is different!",
									"Information",
									JOptionPane.INFORMATION_MESSAGE);
			}
			try {
				if (null != sourceIrodsFilePath) {
					if (null != iPlugin) {

						log.error("Defaulting ErrorWhileUsingGetOperation value to :"
								+ "False");
						iPlugin.setErrorWhileUsingGetOperation(false);

						log.info("Transfer Options in IntraFileStatusCallBack status: "
								+ transferControlBlock.getTransferOptions());
						dataTransferOperationsAO
								.getOperation(
										sourceIrodsFilePath,
										destinationLocalFilePath,
										iPlugin.getIrodsTransferStatusCallbackListener(),
										transferControlBlock);

						if (!iPlugin.isErrorWhileUsingGetOperation()) {
							log.info("Executing openImageUsingImageJ method");
							openImageUsingImageJ();
						} else {
							log.error("Error while transfering files");
							JOptionPane.showMessageDialog(null,
									"Error while transfering files!", "Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			} catch (OverwriteException overwriteException) {
				log.error("File with same name already exist in local directory! "
						+ overwriteException.getMessage());
				JOptionPane
						.showMessageDialog(
								null,
								"File with same name already exist in local directory!",
								"Information", JOptionPane.INFORMATION_MESSAGE);

				/* Getting MD5 checksum of local file, if exists */
				File fileInLocal = new File(
						destinationLocalFilePath.getAbsolutePath()
								+ IrodsUtilities.getPathSeperator()
								+ sourceIrodsFilePath.getName());
				md5ChecksumLocalFile = IrodsUtilities
						.calculateMD5CheckSum(fileInLocal);
				log.info("MD5checksum of local file: " + md5ChecksumLocalFile);

				log.info("MD5 checksum compared - Similar files:"
						+ md5ChecksumLocalFile.equals(md5ChecksumServerFile));

				if (!md5ChecksumLocalFile.equals(md5ChecksumServerFile))
					JOptionPane
							.showMessageDialog(null,
									"File names are same but MD5 checksum is different!");

				overwriteException.printStackTrace();
			} catch (DataNotFoundException dataNotFoundException) {
				JOptionPane.showMessageDialog(null, "dataNotFoundException!",
						"Error", JOptionPane.ERROR_MESSAGE);
				log.info("Error while pulling files!"
						+ dataNotFoundException.getMessage());
			} catch (JargonException jargonException) {
				JOptionPane.showMessageDialog(null,
						"Error while pulling files!", "Error",
						JOptionPane.ERROR_MESSAGE);
				log.info("Error while pulling files!"
						+ jargonException.getMessage());
			}

		}
		return null;
	}

	@Override
	public void done() {
		/*
		 * Source code in done() method is shifted to openImageUsingImageJ()
		 * method
		 */
	}

	private void openImageUsingImageJ() {

		/* Opening the selected ImageJ */
		Opener imagejOpener = new Opener();
		ImagePlus imagePlusInstanceOfCurrentActiveImage = null;
		if (null != iPlugin.getImageJCacheFolder()) {
			String imageFilePath = iPlugin.getImageJCacheFolder()
					+ IrodsUtilities.getPathSeperator()
					+ sourceIrodsFilePath.getName();
			log.info("Current file opened by user: " + imageFilePath);
			ImagePlus imagePlus = imagejOpener.openImage(imageFilePath);
			// ImagePlus imagePlus = IJ.openImage(imageFilePath);
			

			if (null != imagePlus) {
				iPlugin.setImagePlus(imagePlus);
				log.info("ImagePlus instance is not null and before calling show() function of ImagePlus class");
				imagePlus.show();
				imagePlusInstanceOfCurrentActiveImage = IJ.getImage();

				log.info("ImagePlus instance of current image from IJ"
						+ imagePlusInstanceOfCurrentActiveImage);

				/*
				 * Functionality - pending - how to check if Images are opened
				 * in imagej already
				 */
				if (null != imagePlusInstanceOfCurrentActiveImage) {
					log.info("Current Image from IJ"
							+ imagePlusInstanceOfCurrentActiveImage.getImage());
					log.error("image windows are open");
					iPlugin.setImageOpened(true);
				} else {
					log.error("No image windows are open");
					iPlugin.setImageOpened(false);
				}
				log.info("irodsImagej.isImageOpened is set to true");
			} else {
				log.error("ImagePlus instance in GetFileFromIrodsSwingWorker is null and irodsImagej.isImageOpened is false");
				JOptionPane.showMessageDialog(null,
						"File format is not supported by ImageJ!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			IJ.showMessage("ImageJ is not able to open requested file!");
			IJ.showStatus("ImageJ is not able to open requested file!");
			log.error("ImagePlus instance is null and opening file Failed.");
			JOptionPane.showMessageDialog(null,
					"ImagePlus instance is null and opening file Failed!",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
