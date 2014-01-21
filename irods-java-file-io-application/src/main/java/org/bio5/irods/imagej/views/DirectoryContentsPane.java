package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.ScrollPaneConstants;

public class DirectoryContentsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 722996165620904921L;
	private String irodsZone;
	public IRODSFileSystem irodsFileSystem;
	public UserAO  userAccount;
	private DataTransferOperations dataTransferOperationsAO;
	final IRODSFileFactory iRODSFileFactory;
	private String selectedNodeInTreeForDoubleClick;
	private String selectedNodeInTreeForSingleClick;
	private GetFile getFile;
	private JProgressBar progressBar;
	private JFileChooser chooser;
	private DirectoryContentsPane directoryContentsPane;
	private DataObjectAO dataObjectAO;

	private DefaultTreeModel treeModel;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;

	private JScrollPane jScrollPane2;
	private JScrollPane scrollPane;

	/*Logger instantiation*/
	static Logger log = Logger.getLogger(
			DirectoryContentsPane.class.getName());

	/**
	 * Create the panel.
	 * @throws JargonException 
	 */

	public DirectoryContentsPane(List<String> ContentsInHome,final IRODSFile irodsAccountFile,final IRODSAccount irodsAccount) throws JargonException {
		log.info("Local path before refactoring: " +Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);
		//Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY.replaceAll(IrodsUtilities.pathSeperator(), "//");
		log.info("Local directory to store ImageJ files: " +Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);

		iRODSFileFactory= FileOperations.getIrodsAccountFileFactory(irodsAccount);
		String irodsZone =irodsAccount.getZone();
		System.out.println("irodsZone" +irodsZone);
		rootNode = new DefaultMutableTreeNode(Constants.HOME);
		treeModel = new DefaultTreeModel(rootNode);

		irodsFileSystem= IRODSFileSystem.instance();
		userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
		dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().getDataTransferOperations(
						irodsAccount);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setToolTipText("scroll");
		
		final File localFiles = (File) irodsAccountFile;
		parseDirectoryContents(iRODSFileFactory, localFiles, rootNode, irodsAccount);

		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(100);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		

		final JButton button_saveToIrodsServer = new JButton("Save to iRODS Server");
		button_saveToIrodsServer.setEnabled(false);
		final JLabel label_localFilePath = new JLabel("Local file");
		label_localFilePath.setText(Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);
		JButton btnSelectLocalFile = new JButton("Select local file");


		btnSelectLocalFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = chooser.showOpenDialog(DirectoryContentsPane.this);
				if (option == JFileChooser.APPROVE_OPTION) {
					label_localFilePath.setText("You opened " + ((chooser.getSelectedFile()!=null)?
							chooser.getSelectedFile().getAbsolutePath():"nothing is selected"));
				}
				else {
					label_localFilePath.setName("You canceled.");
				}
			}
		});

		label_localFilePath.setEnabled(false);
		final JLabel label_destinationFilePath = new JLabel("Destination");
		label_destinationFilePath.setEnabled(false);
		JButton btnSelectDestination = new JButton("Select Destination");
		btnSelectDestination.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent destinationButtonActionEvent) {

				String destinationFolderPath = selectedNodeInTreeForSingleClick;
				if(selectedNodeInTreeForSingleClick.contains("."))
				{
					log.info("Destination before Splitting: " +selectedNodeInTreeForSingleClick);
					File destinationFile =new File(selectedNodeInTreeForSingleClick);
					destinationFolderPath =destinationFile.getParent();
					log.info("Destination after Splitting: " +destinationFolderPath);
				}
				label_destinationFilePath.setText(destinationFolderPath);
				button_saveToIrodsServer.setEnabled(true);
			}
		});

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setToolTipText("Progress of action");
		
		

		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnSelectDestination)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(label_destinationFilePath))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(btnSelectLocalFile)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(label_localFilePath))
								.addComponent(button_saveToIrodsServer)))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addGap(33))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(24)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 438, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(35)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(84)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnSelectLocalFile)
								.addComponent(label_localFilePath))
							.addGap(10)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnSelectDestination)
								.addComponent(label_destinationFilePath))
							.addGap(11)
							.addComponent(button_saveToIrodsServer)))
					.addGap(9))
		);
		setLayout(groupLayout);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{button_saveToIrodsServer, userDirectoryTree}));

		/*Put to iRODS server*/
		button_saveToIrodsServer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					log.info("Save to iRODS Server - Button Clicked");
					String sourceFilePath=null;
					String destinationFilePath=null;
					String targetResourceName ="abcd";
					File sourceLocalfile = null;
					IRODSFile destinaitonIrodsFile = null;
					if(chooser.getSelectedFile().getAbsolutePath()!=null && chooser.getSelectedFile().getAbsolutePath()!=""){
						sourceFilePath=chooser.getSelectedFile().getAbsolutePath();
						sourceLocalfile=new File(sourceFilePath);
						if(selectedNodeInTreeForSingleClick!=null && selectedNodeInTreeForSingleClick!=""){
							System.out.println("destination path || selectedNodeInTreeForSingleClick" +selectedNodeInTreeForSingleClick);
							destinationFilePath=IrodsUtilities.pathSeperator() +irodsAccount.getZone()+IrodsUtilities.pathSeperator()+label_destinationFilePath.getText();
							destinaitonIrodsFile = iRODSFileFactory.instanceIRODSFile(destinationFilePath);
							System.out.println("sourceLocalfile absolute path: " +sourceLocalfile.getAbsolutePath() +"\n"  +"destinaitonIrodsFile absolutepath: " +destinaitonIrodsFile.getAbsoluteFile());
							dataTransferOperationsAO.putOperation(sourceLocalfile.getAbsolutePath(),destinaitonIrodsFile.getAbsolutePath(),targetResourceName,null,null);
							JOptionPane.showMessageDialog(null, "File Transfer done successfully");

							/*refresh the page once transfer is done! - Pending*/
							directoryContentsPane   =  new DirectoryContentsPane(null, irodsAccountFile, irodsAccount);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Source is empty!");
						log.error("Source is empty!");
					}

				} catch (JargonException jargonException) {
					log.error(jargonException.getMessage());
					jargonException.printStackTrace();
				}
			}
		});
	}


	public void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,final File irodsAccountFile, DefaultMutableTreeNode node, final IRODSAccount irodsAccount)
	{
		if(!irodsAccountFile.isDirectory()){
			System.out.println("File name" +irodsAccountFile.getName() +":" +irodsAccountFile.getAbsolutePath());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName());
			node.add(child);
		}

		else{
			System.out.println("Direc name" + irodsAccountFile.getName());
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(irodsAccountFile.getName());
			node.add(child);
			File[] direcFiles=irodsAccountFile.listFiles();
			for(int i=0;i<direcFiles.length;i++){
				System.out.println("File number" +i +"\n depth:" +direcFiles.length);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child, irodsAccount);
			}
		}

		userDirectoryTree= new JTree(treeModel);
		//userDirectoryTree.setModel(treeModel);
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseEvent) {

				if(mouseEvent.getClickCount()==2){
					/*resetting progress bar to 0 if a double click is detected*/
					progressBar.setValue(0);
					log.info("tree path after double click" +selectedNodeInTreeForDoubleClick);
					System.out.println("double click selection: " +selectedNodeInTreeForDoubleClick);

					selectedNodeInTreeForDoubleClick=IrodsUtilities.getJtreeSelection(mouseEvent,userDirectoryTree );
					//getImageFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);
					getFile= new GetFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);
					getFile.execute();
				}
				else if(mouseEvent.getClickCount()==1){
					//resetting progress bar to 0 if a single click is detected
					progressBar.setValue(0);
					selectedNodeInTreeForSingleClick=IrodsUtilities.getJtreeSelection(mouseEvent,userDirectoryTree);
					//log.info("Single click selection: " +selectedNodeInTreeForSingleClick);
				}
			}
		});

		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		userDirectoryTree.setVisible(true);
		//jScrollPane2 = new JScrollPane(userDirectoryTree);
		//jScrollPane2 = new JScrollPane();
		JViewport viewport1 = scrollPane.getViewport(); 
		viewport1.removeAll();
		viewport1.add(userDirectoryTree);
		viewport1.setVisible(true);
		viewport1.repaint();  
		viewport1.revalidate();

		//jScrollPane2.add(userDirectoryTree);
		//add(userDirectoryTree);
		add(scrollPane,BorderLayout.CENTER);
		setVisible(true);
		revalidate();
		repaint();
	}


	class GetFile extends SwingWorker<Void, Integer>
	{
		private IRODSFileFactory iRODSFileFactory;
		private String treePath;
		private IRODSAccount irodsAccount;
		private long totalLengthOfFile = 0L;
		private long copiedLengthOfFile = 0L;


		/*Get files from iRODS Server*/
		public GetFile(IRODSFileFactory iRODSFileFactory, String treePath,
				IRODSAccount irodsAccount) {
			this.iRODSFileFactory = iRODSFileFactory;
			this.treePath = treePath;
			this.irodsAccount = irodsAccount;
		}

		/*Using SwingWorker-doInBackGround() function to do processing in background*/
		@SuppressWarnings("deprecation")
		@Override
		public Void doInBackground() throws Exception {

			//getImageFile(iRODSFileFactory,treePath,irodsAccount );
			System.out.println("finalTreePath:" +treePath);

			/*Recheck irodsAccounZone for all accounts*/
			IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream(IrodsUtilities.pathSeperator() +irodsAccount.getZone() +treePath);



			//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
			dataTransferOperationsAO =  irodsFileSystem
					.getIRODSAccessObjectFactory().
					getDataTransferOperations(
							irodsAccount);
			IRODSFile sourceIrodsFilePath = iRODSFileFactory.instanceIRODSFile(IrodsUtilities.pathSeperator() +irodsAccount.getZone() +treePath);

			dataObjectAO=  irodsFileSystem.getIRODSAccessObjectFactory().getDataObjectAO(irodsAccount);

			/*Getting MD5 checksum of the current file from iRODS*/
			String md5ChecksumLocalFile =null;
			String md5ChecksumServerFile=null;

			try{
				md5ChecksumServerFile = dataObjectAO.computeMD5ChecksumOnDataObject(sourceIrodsFilePath);
				log.info("MD5checksum of iRODS server file: " +md5ChecksumServerFile);
			}
			catch(Exception e){
				System.out.println("Error while reading MD5 checksum");
				e.printStackTrace();
			}

			File destinationLocalFilePath =new File(Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);

			try
			{
				if(null!=sourceIrodsFilePath){
					totalLengthOfFile = sourceIrodsFilePath.length();
					dataTransferOperationsAO.getOperation(sourceIrodsFilePath, destinationLocalFilePath, null, null);
				}
			}
			catch (OverwriteException oe)
			{
				log.error("File with same name already exist in local directory! " +oe.getMessage());
				JOptionPane.showMessageDialog(null, "File with same name already exist in local directory!");

				/*Getting MD5 checksum of local file, if exists*/
				File localFile= new File(destinationLocalFilePath.getAbsolutePath()+ IrodsUtilities.pathSeperator() +sourceIrodsFilePath.getName());
				md5ChecksumLocalFile= IrodsUtilities.calculateMD5CheckSum(localFile);
				log.info("MD5checksum of local file: " +md5ChecksumLocalFile);

				log.info("MD5 checksum compared - Similar files:" +md5ChecksumLocalFile.equals(md5ChecksumServerFile));

				if(!md5ChecksumLocalFile.equals(md5ChecksumServerFile))
					JOptionPane.showMessageDialog(null, "File names are same but MD5 checksum is different!");

				oe.printStackTrace();
			}

			/*Opening the selected ImageJ*/
			Opener imageOpener = new Opener(); 
			String imageFilePath = Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY + IrodsUtilities.pathSeperator() +sourceIrodsFilePath.getName();
			log.info("Current file path: " +sourceIrodsFilePath.getName());
			log.info("Current file opened by user: " +imageFilePath);
			ImagePlus imp = imageOpener.openImage(imageFilePath);
			//ImagePlus imp = IJ.openImage(imageFilePath);

			if(imp!=null){
				log.info("ImagePlus is not null and before calling show() function of ImagePlus class");
				imp.show();
			}
			else
			{
				IJ.showMessage("Opening file Failed.");
				IJ.showStatus("Opening file Failed.");
				log.error("ImagePlus instance is null and opening file Failed.");
			}
			return null;
		}

		@Override
		public void process(List<Integer> chunks)
		{
			for(int i : chunks)
			{
				progressBar.setValue(i);
			}
		}

		@Override
		public void done()
		{
			publish(100);
		}
	}
}
