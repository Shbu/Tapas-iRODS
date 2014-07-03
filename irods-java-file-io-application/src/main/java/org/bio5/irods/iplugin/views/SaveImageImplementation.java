package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.ImagePlus;

import java.awt.Image;
import java.io.File;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;

public class SaveImageImplementation {

	/**
	 * 
	 */
	private IPlugin iplugin;
	private PutFileToIrodsSwingWorker putFile;
	private String destinationFilePath = null;
	private String savePathWithFileName = null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(SaveImageImplementation.class
			.getName());

	public SaveImageImplementation(IPlugin ipluginInstance) {
		this.iplugin = ipluginInstance;

		/* Assign variables to fields */
		assignVariablesToFields();
		saveCurrentEditedImageFileToLocal();
		saveCurrentEditedFileToIrodsByPluginOption();
	}

	private void assignVariablesToFields() {

		/* Setting user selected path as destination path to save files */
		if (null != iplugin.getSelectedNodeInTreeForSingleClick()
				&& iplugin.getSelectedNodeInTreeForSingleClick() != "") {
			destinationFilePath = setDestinationPathWithGivenString(iplugin
					.getSelectedNodeInTreeForSingleClick());
		} else if (null != iplugin.getSelectedNodeInTreeForDoubleClick()
				&& iplugin.getSelectedNodeInTreeForDoubleClick() != "") {
			log.info("Inside assignVariablesToFields() and SelectedNodeInTreeForDoubleClick is not null"
					+ iplugin.getSelectedNodeInTreeForDoubleClick());

			destinationFilePath = setDestinationPathWithGivenString(iplugin
					.getSelectedNodeInTreeForDoubleClick());
		}
	}

	private void saveCurrentEditedImageFileToLocal() {

		/* ImagePlus imp = iplugin.getImagePlus(); */
		ImagePlus imp = IJ.getImage();

		if (null != imp) {
			/*
			 * if (null != iplugin.getImageJCacheFolder() && null !=
			 * iplugin.getSelectedNodeInTreeForDoubleClick())
			 */
			if (null != iplugin.getImageJCacheFolder())

			{
				/*
				 * String fileName = IrodsUtilities
				 * .getFileNameFromDirectoryPath(iplugin
				 * .getSelectedNodeInTreeForDoubleClick());
				 */

				String fileName = imp.getTitle();
				if (null != fileName) {
					savePathWithFileName = iplugin.getImageJCacheFolder()
							+ IrodsUtilities.getPathSeperator() + fileName;
					log.info("savePathWithFileName : " + savePathWithFileName);

					/* Getting current active image */

					IJ.save(imp, savePathWithFileName);
					log.info("File saved to local machine : " + fileName);
					JOptionPane.showMessageDialog(null,
							"File saved to local machine with filename and extention : "
									+ fileName);
				}

			} else {
				log.error("ImageJCacheFolder or ImagePlus is null!");
				JOptionPane.showMessageDialog(null,
						"ImageJCacheFolder or ImagePlus is null!", "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			log.error("Error while getting iplugin-imagePlus instance");
			JOptionPane.showMessageDialog(null,
					"Error while getting iplugin-imagePlus instance!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void saveCurrentEditedFileToIrodsByPluginOption() {
		try {
			String targetResourceName = "";

			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;

			if (null != iplugin && null != iplugin.getIrodsAccount()
					&& null != iplugin.getiRODSFileFactory()) {
				targetResourceName = iplugin.getIrodsAccount()
						.getDefaultStorageResource();
				if (savePathWithFileName != null) {
					sourceLocalfile = new File(savePathWithFileName);
					log.info("savePathWithFileName is set into sourceLocalfile: "
							+ sourceLocalfile);
				} else {
					log.error("savePathWithFileName is null");
					JOptionPane.showMessageDialog(null,
							"savePathWithFileName is null!", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (null != destinationFilePath && destinationFilePath != ""
						&& null != sourceLocalfile
						&& sourceLocalfile.getAbsolutePath() != "") {

					destinationIrodsFile = iplugin.getiRODSFileFactory()
							.instanceIRODSFile(destinationFilePath);
					log.info("destinaitonIrodsFile absolutepath: "
							+ destinationIrodsFile.getAbsoluteFile());

					try {
						if (null != sourceLocalfile
								&& null != destinationIrodsFile
								&& null != targetResourceName) {
							log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
							putFile = new PutFileToIrodsSwingWorker(iplugin,
									sourceLocalfile, destinationIrodsFile,
									targetResourceName);
							putFile.execute();
							log.info("Executed PutFile method!");
						} else {
							log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName is null");
							JOptionPane
									.showMessageDialog(
											null,
											"sourceLocalfile or destinaitonIrodsFile or targetResourceName is null!",
											"Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					} catch (Exception execeptionWhileExecutingPutFile) {
						log.error(execeptionWhileExecutingPutFile.getMessage());
						JOptionPane
								.showMessageDialog(null,
										execeptionWhileExecutingPutFile
												.getStackTrace(), "Error",
										JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					log.error("sourceLocalfile or destinationFilePath is null");
					JOptionPane.showMessageDialog(null,
							"sourceLocalfile or destinationFilePath is null!",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				log.error("iplugin instance is null");
			}
		}

		catch (JargonException ExceptionInExecutingSaveButton) {
			log.error(ExceptionInExecutingSaveButton.getMessage());
			ExceptionInExecutingSaveButton.printStackTrace();
			JOptionPane.showMessageDialog(null,
					ExceptionInExecutingSaveButton.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private String setDestinationPathWithGivenString(String destinationPath) {
		String destinationFilePath;
		destinationFilePath = IrodsUtilities.getPathSeperator()
				+ iplugin.getIrodsAccount().getZone()
				+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
				+ IrodsUtilities.getPathSeperator() + destinationPath;
		return destinationFilePath;
	}
}
