package org.bio5.irods.iplugin.views;

import ij.IJ;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;
import org.bio5.irods.iplugin.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.iplugin.utilities.Constants;
import org.bio5.irods.iplugin.utilities.IrodsUtilities;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.io.IRODSFile;

public class SaveImagePanelImplementation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private HashMap<String, Object> saveDetails;
	private JTextField textField_LocalFileName;
	private IPlugin iplugin;
	private JTextField textField_irodsDestinationPath;
	private JFileChooser fileChooser;
	private PutFileToIrodsSwingWorker putFile;
	private JLabel lbl_localFileName;
	private JLabel lbl_irodsDestinationPath;
	private String destinationFilePath = null;
	private String savePathWithFileName = null;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(SaveImagePanelImplementation.class
			.getName());

	/**
	 * Create the frame.
	 * 
	 * @param saveDetails
	 */
	public SaveImagePanelImplementation(IPlugin ipluginInstance) {
		setTitle("iRODS ImageJ - Save Image");
		this.iplugin = ipluginInstance;

		/* Screen Design */
		init();

		/* Assign variables to fields */
		assignVariablesToFields();
		saveCurrentEditedImageFileToLocal();

		JButton btn_saveButton = new JButton("Save");
		btn_saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Save to iRODS Server - Button Clicked");
				// saveCurrentEditedImageFileToLocal();
				// saveCurrentEditedFileToIrods();
			}
		});

		JButton btn_Cancel = new JButton("Cancel");
		btn_Cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/* Edit functionality - pending */
				if (null != iplugin.getSaveImagePanelImplementation()) {
					iplugin.getSaveImagePanelImplementation().dispose();
				}
			}
		});

		JButton btn_select_local_file = new JButton("Browse File");
		btn_select_local_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseFileFromLocalMachine();
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
		.setHorizontalGroup(gl_contentPane
				.createParallelGroup(Alignment.TRAILING)
				.addGroup(
						gl_contentPane
						.createSequentialGroup()
						.addGap(38)
						.addGroup(
								gl_contentPane
								.createParallelGroup(
										Alignment.LEADING)
										.addComponent(
												lbl_irodsDestinationPath)
												.addComponent(
														lbl_localFileName))
														.addGap(18)
														.addGroup(
																gl_contentPane
																.createParallelGroup(
																		Alignment.LEADING,
																		false)
																		.addGroup(
																				gl_contentPane
																				.createSequentialGroup()
																				.addComponent(
																						btn_saveButton)
																						.addGap(37)
																						.addComponent(
																								btn_Cancel))
																								.addGroup(
																										gl_contentPane
																										.createSequentialGroup()
																										.addComponent(
																												textField_LocalFileName,
																												GroupLayout.PREFERRED_SIZE,
																												181,
																												GroupLayout.PREFERRED_SIZE)
																												.addGap(18)
																												.addComponent(
																														btn_select_local_file))
																														.addComponent(
																																textField_irodsDestinationPath))
																																.addGap(67)));
		gl_contentPane
		.setVerticalGroup(gl_contentPane
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_contentPane
						.createSequentialGroup()
						.addGap(73)
						.addGroup(
								gl_contentPane
								.createParallelGroup(
										Alignment.BASELINE)
										.addComponent(
												textField_LocalFileName,
												GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE,
												GroupLayout.PREFERRED_SIZE)
												.addComponent(
														lbl_localFileName)
														.addComponent(
																btn_select_local_file))
																.addGap(27)
																.addGroup(
																		gl_contentPane
																		.createParallelGroup(
																				Alignment.BASELINE)
																				.addComponent(
																						textField_irodsDestinationPath,
																						GroupLayout.PREFERRED_SIZE,
																						GroupLayout.DEFAULT_SIZE,
																						GroupLayout.PREFERRED_SIZE)
																						.addComponent(
																								lbl_irodsDestinationPath))
																								.addGap(39)
																								.addGroup(
																										gl_contentPane
																										.createParallelGroup(
																												Alignment.BASELINE)
																												.addComponent(
																														btn_saveButton)
																														.addComponent(
																																btn_Cancel))
																																.addContainerGap(81, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[] {
				contentPane, lbl_localFileName, textField_LocalFileName,
				btn_select_local_file, lbl_irodsDestinationPath,
				textField_irodsDestinationPath, btn_saveButton, btn_Cancel }));
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 526, 332);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textField_LocalFileName = new JTextField();
		textField_LocalFileName.setColumns(10);

		lbl_localFileName = new JLabel("Local File Name : ");

		lbl_irodsDestinationPath = new JLabel("iRODS Destination Path :");

		textField_irodsDestinationPath = new JTextField();
		textField_irodsDestinationPath.setColumns(10);

		fileChooser = new JFileChooser();

	}

	private void assignVariablesToFields() {

		/* Setting user selected path as destination path to save files */
		if (null != iplugin.getSelectedNodeInTreeForSingleClick()
				&& iplugin.getSelectedNodeInTreeForSingleClick() != "") {
			/*
			 * textField_irodsDestinationPath.setText(iplugin
			 * .getSelectedNodeInTreeForSingleClick());
			 */

			destinationFilePath = setDestinationPathWithGivenString(iplugin
					.getSelectedNodeInTreeForSingleClick());
		} else if (null != iplugin.getSelectedNodeInTreeForDoubleClick()
				&& iplugin.getSelectedNodeInTreeForDoubleClick() != "") {
			log.info("Inside assignVariablesToFields() and SelectedNodeInTreeForDoubleClick is not null"
					+ iplugin.getSelectedNodeInTreeForDoubleClick());
			/*
			 * textField_irodsDestinationPath.setText(iplugin
			 * .getSelectedNodeInTreeForDoubleClick());
			 */

			destinationFilePath = setDestinationPathWithGivenString(iplugin
					.getSelectedNodeInTreeForDoubleClick());
		}

		/* Setting current edited file */

	}

	private void saveCurrentEditedImageFileToLocal() {
		try {
			if (null != iplugin.getImagePlus()) {
				if (null != iplugin.getImageJCacheFolder()
						&& null != iplugin
						.getSelectedNodeInTreeForDoubleClick()) {
					String fileName = IrodsUtilities
							.getFileNameFromDirectoryPath(iplugin
									.getSelectedNodeInTreeForDoubleClick());
					if (null != fileName) {
						savePathWithFileName = iplugin.getImageJCacheFolder()
								+ IrodsUtilities.getPathSeperator() + fileName;
						log.info("savePathWithFileName" + savePathWithFileName);
						IJ.save(iplugin.getImagePlus(), savePathWithFileName);
						/*
						 * IJ.saveAs(iplugin.getImagePlus(), "tiff",
						 * savePathWithFileName);
						 */
						log.info("File saved to local machine with same name and extention"
								+ fileName);
						JOptionPane.showMessageDialog(null,
								"File saved to local machine with filename and extention: "
										+ fileName);
						textField_LocalFileName.setText(savePathWithFileName);
						log.info("textField_LocalFileName set to savePathWithFileName"
								+ textField_LocalFileName.getText());

						saveCurrentEditedFileToIrodsByPluginOption();
					} else {
						log.error("File name is null in saveCurrentEditedImageFileToLocal method!");
					}

				} else {
					log.error("ImageJCacheFolder or ImagePlus is null");
				}

			} else {
				log.error("iplugin imageJ imagePlus instance is null!");
				JOptionPane.showMessageDialog(null,
						"iplugin imageJ imagePlus instance is null!", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception exception) {
			log.error("Error while saving file to local!"
					+ exception.getMessage());
		}

	}

	private void saveCurrentEditedFileToIrodsByPluginOption() {
		try {
			String targetResourceName = "";
			targetResourceName = iplugin.getIrodsAccount()
					.getDefaultStorageResource();
			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;

			if (savePathWithFileName != null) {
				sourceLocalfile = new File(savePathWithFileName);
				log.info("savePathWithFileName is set into sourceLocalfile: "
						+ sourceLocalfile);
			} else {
				log.error("savePathWithFileName is null");
			}

			if (null != destinationFilePath && destinationFilePath != ""
					&& null != sourceLocalfile
					&& sourceLocalfile.getAbsolutePath() != "") {

				destinationIrodsFile = iplugin.getiRODSFileFactory()
						.instanceIRODSFile(destinationFilePath);
				log.info("destinaitonIrodsFile absolutepath: "
						+ destinationIrodsFile.getAbsoluteFile());

				try {
					if (null != iplugin && null != sourceLocalfile
							&& null != destinationIrodsFile
							&& null != targetResourceName) {
						log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
						putFile = new PutFileToIrodsSwingWorker(iplugin,
								sourceLocalfile, destinationIrodsFile,
								targetResourceName);
						putFile.execute();
						log.info("Executed PutFile method!");
						JOptionPane.showMessageDialog(null,
								"Uploading file completed!");
					} else {
						log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName is null");
					}
				} catch (Exception execeptionWhileExecutingPutFile) {
					log.error(execeptionWhileExecutingPutFile.getMessage());
					JOptionPane.showMessageDialog(null,
							execeptionWhileExecutingPutFile.getMessage());
				}
			} else {
				log.error("sourceLocalfile or textField_irodsDestinationPath is null");
			}
		}

		catch (JargonException ExceptionInExecutingSaveButton) {
			log.error(ExceptionInExecutingSaveButton.getMessage());
			ExceptionInExecutingSaveButton.printStackTrace();
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

	private void saveCurrentEditedFileToIrods() {
		try {

			String sourceFilePath = null;
			String destinationFilePath = null;
			String targetResourceName = "";
			targetResourceName = iplugin.getIrodsAccount()
					.getDefaultStorageResource();
			File sourceLocalfile = null;
			IRODSFile destinationIrodsFile = null;
			sourceLocalfile = new File(sourceFilePath);

			if (textField_LocalFileName != null) {
				sourceFilePath = textField_LocalFileName.getText();

				log.info("sourceLocalfile is not null and setting to textField_LocalFileName: "
						+ sourceLocalfile);
			} else {
				log.error("textField_LocalFileName is null");
			}

			if (fileChooser.getSelectedFile().getAbsolutePath() != null
					&& fileChooser.getSelectedFile().getAbsolutePath() != "") {
				sourceFilePath = fileChooser.getSelectedFile()
						.getAbsolutePath();
				log.info("sourceFilePath of fileChoose: " + sourceFilePath);
				log.info("sourceLocalfile after choosing file: "
						+ sourceLocalfile);
			} else {
				log.error("fileChooser.getSelectedFile().getAbsolutePath() is null or empty");
			}

			/*
			 * SingleClick is not set properly--- below code is not working --
			 * fix it..chck logger for execution
			 */

			log.info("Just beffore checking if SelectedNodeInTreeForSingleClick is null");

			if (null != textField_irodsDestinationPath
					&& textField_irodsDestinationPath.getText() != ""
					&& null != sourceLocalfile
					&& sourceLocalfile.getAbsolutePath() != "") {

				destinationFilePath = IrodsUtilities.getPathSeperator()
						+ iplugin.getIrodsAccount().getZone()
						+ IrodsUtilities.getPathSeperator() + Constants.HOME_STRING
						+ IrodsUtilities.getPathSeperator()
						+ textField_irodsDestinationPath.getText();
				log.info("Final destinationFilePath: " + destinationFilePath);
				destinationIrodsFile = iplugin.getiRODSFileFactory()
						.instanceIRODSFile(destinationFilePath);
				log.info("destinaitonIrodsFile absolutepath: "
						+ destinationIrodsFile.getAbsoluteFile());

				try {
					if (null != iplugin && null != sourceLocalfile
							&& null != destinationIrodsFile
							&& null != targetResourceName) {
						log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
						putFile = new PutFileToIrodsSwingWorker(iplugin,
								sourceLocalfile, destinationIrodsFile,
								targetResourceName);
						putFile.execute();
						log.info("Executed PutFile method!");
					} else {
						log.error("sourceLocalfile or destinaitonIrodsFile or targetResourceName are null");
					}
				} catch (Exception excetionPutFile) {
					log.error(excetionPutFile.getMessage());
					JOptionPane.showMessageDialog(null,
							excetionPutFile.getMessage());
				}

			} else {
				log.error("sourceLocalfile or textField_irodsDestinationPath is null");
			}
		}

		catch (JargonException ExceptionInSaveButtonClicked) {
			log.error(ExceptionInSaveButtonClicked.getMessage());
			ExceptionInSaveButtonClicked.printStackTrace();
		}

	}

	private void browseFileFromLocalMachine() {

		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int option = fileChooser
				.showOpenDialog(SaveImagePanelImplementation.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			textField_LocalFileName
			.setText(((fileChooser.getSelectedFile() != null) ? fileChooser
					.getSelectedFile().getAbsolutePath()
					: "nothing is selected"));
		} else {
			textField_LocalFileName.setName("File selection canceled !");
		}
	}

}
