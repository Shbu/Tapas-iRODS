package org.bio5.irods.imagej.views;

import ij.IJ;

import java.awt.Component;
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
import org.bio5.irods.imagej.bean.IPlugin;
import org.bio5.irods.imagej.fileoperations.PutFileToIrodsSwingWorker;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
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
	private JTextField textField_FileName;
	private IPlugin iplugin;
	private JTextField textField_irodsDestinationPath;
	private JFileChooser fileChooser;
	private PutFileToIrodsSwingWorker putFile;
	private JLabel lbl_localFileName;
	private JLabel lbl_irodsDestinationPath;

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

		JButton btn_saveButton = new JButton("Save");
		btn_saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log.info("Save to iRODS Server - Button Clicked");
				saveCurrentEditedImageFileToLocal();
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
																				textField_FileName,
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
																textField_FileName,
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
				contentPane, lbl_localFileName, textField_FileName,
				btn_select_local_file, lbl_irodsDestinationPath,
				textField_irodsDestinationPath, btn_saveButton, btn_Cancel }));
	}

	private void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 526, 332);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		textField_FileName = new JTextField();
		textField_FileName.setColumns(10);

		lbl_localFileName = new JLabel("Local File Name : ");

		lbl_irodsDestinationPath = new JLabel("iRODS Destination Path :");

		textField_irodsDestinationPath = new JTextField();
		textField_irodsDestinationPath.setColumns(10);

	}

	private void assignVariablesToFields() {

		/* Setting user selected path as destination path to save files */
		if (null != iplugin.getSelectedNodeInTreeForSingleClick()
				&& iplugin.getSelectedNodeInTreeForSingleClick() != "") {
			textField_irodsDestinationPath.setText(iplugin
					.getSelectedNodeInTreeForSingleClick());
		} else if (null != iplugin.getSelectedNodeInTreeForDoubleClick()
				&& iplugin.getSelectedNodeInTreeForDoubleClick() != "") {
			log.info("Inside assignVariablesToFields() and SelectedNodeInTreeForDoubleClick is not null"
					+ iplugin.getSelectedNodeInTreeForDoubleClick());
			textField_irodsDestinationPath.setText(iplugin
					.getSelectedNodeInTreeForDoubleClick());
		}

		/* Setting current edited file */

	}

	private void saveCurrentEditedImageFileToLocal() {
		if (null != iplugin.getImagePlus()) {
			// FileSaver fileSaver = new FileSaver(iplugin.getImagePlus());
			// fileSaver.save();
			if (null != iplugin.getImageJCacheFolder()
					&& null != iplugin.getSelectedNodeInTreeForDoubleClick()) {
				String fileName = IrodsUtilities
						.getFileNameFromDirectoryPath(iplugin
								.getSelectedNodeInTreeForDoubleClick());
				if (null != fileName) {
					String savePathWithFileName = iplugin
							.getImageJCacheFolder()
							+ IrodsUtilities.getPathSeperator() + fileName;
					log.info("savePathWithFileName" + savePathWithFileName);
					IJ.save(iplugin.getImagePlus(), savePathWithFileName);
					/*
					 * IJ.saveAs(iplugin.getImagePlus(), "tiff",
					 * savePathWithFileName);
					 */
					log.info("File save to local with filename and extention");
					JOptionPane.showMessageDialog(null,
							"File save to local with filename and extention");
				}

			} else {
				log.error("ImageJCacheFolder or ImagePlus is null");
			}

		}

	}

	private void saveCurrentEditedFileToIrods() {
		try {

			String sourceFilePath = null;
			String destinationFilePath = null;
			String targetResourceName = "";
			targetResourceName = iplugin.getIrodsAccount()
					.getDefaultStorageResource();
			File sourceLocalfile = null;
			IRODSFile destinaitonIrodsFile = null;
			if (fileChooser.getSelectedFile().getAbsolutePath() != null
					&& fileChooser.getSelectedFile().getAbsolutePath() != "") {
				sourceFilePath = fileChooser.getSelectedFile()
						.getAbsolutePath();
				sourceLocalfile = new File(sourceFilePath);
				if (iplugin.getSelectedNodeInTreeForSingleClick() != null
						&& iplugin.getSelectedNodeInTreeForSingleClick() != ""
						&& null != textField_irodsDestinationPath
						&& textField_irodsDestinationPath.getText() != "") {
					String SelectedNodeInTreeForSingleClick = iplugin
							.getSelectedNodeInTreeForSingleClick();
					log.info("destination path || selectedNodeInTreeForSingleClick"
							+ SelectedNodeInTreeForSingleClick);
					destinationFilePath = IrodsUtilities.getPathSeperator()
							+ iplugin.getIrodsAccount().getZone()
							+ IrodsUtilities.getPathSeperator()
							+ Constants.HOME
							+ IrodsUtilities.getPathSeperator()
							+ textField_irodsDestinationPath.getText();
					destinaitonIrodsFile = iplugin.getiRODSFileFactory()
							.instanceIRODSFile(destinationFilePath);
					log.info("sourceLocalfile absolute path: "
							+ sourceLocalfile.getAbsolutePath() + "\n"
							+ "destinaitonIrodsFile absolutepath: "
							+ destinaitonIrodsFile.getAbsoluteFile());
					try {
						// dataTransferOperationsAO.putOperation(sourceLocalfile.getAbsolutePath(),destinaitonIrodsFile.getAbsolutePath(),targetResourceName,irodsTransferStatusCallbackListener,transferControlBlock);
						if (null != iplugin && null != sourceLocalfile
								&& null != destinaitonIrodsFile
								&& null != targetResourceName) {
							log.info("Inside core save functionality - just before executing PutFileToIrodsSwingWorker method");
							putFile = new PutFileToIrodsSwingWorker(iplugin,
									sourceLocalfile, destinaitonIrodsFile,
									targetResourceName);
							putFile.execute();
							log.info("Executed PutFile method!");
						}
					} catch (Exception exception) {
						log.error(exception.getMessage());
						JOptionPane.showMessageDialog(null,
								exception.getMessage());
					}
				}
			} else {
				JOptionPane.showMessageDialog(null, "Source is empty!");
				log.error("Source is empty!");
			}

		} catch (JargonException jargonException) {
			log.error(jargonException.getMessage());
			jargonException.printStackTrace();
		}

	}

	private void browseFileFromLocalMachine() {

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		int option = fileChooser
				.showOpenDialog(SaveImagePanelImplementation.this);
		if (option == JFileChooser.APPROVE_OPTION) {
			textField_FileName
					.setText(((fileChooser.getSelectedFile() != null) ? fileChooser
							.getSelectedFile().getAbsolutePath()
							: "nothing is selected"));
		} else {
			textField_FileName.setName("File selection canceled !");
		}
	}

}
