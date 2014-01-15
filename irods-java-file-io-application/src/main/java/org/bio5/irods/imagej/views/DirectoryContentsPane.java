package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.io.Opener;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;


import org.eclipse.wb.swing.FocusTraversalOnArray;

import java.awt.Component;

import javax.swing.JProgressBar;

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
	private MainWindow mainwindow;
	
	private DefaultTreeModel dataRoot;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	
	private JScrollPane jScrollPane1;


	/*Logger instantiation*/
	static Logger log = Logger.getLogger(
			DirectoryContentsPane.class.getName());

	/**
	 * Create the panel.
	 * @throws JargonException 
	 */

	public DirectoryContentsPane(List<String> ContentsInHome,final IRODSFile irodsAccountFile,final IRODSAccount irodsAccount) throws JargonException {
		log.info("Local path before refactoring: " +Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);
		//Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY.replaceAll("/", "//");
		log.info("Local directory to store ImageJ files: " +Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY);
		
		iRODSFileFactory= FileOperations.getIrodsAccountFileFactory(irodsAccount);
		String irodsZone =irodsAccount.getZone();
		System.out.println("irodsZone" +irodsZone);

		
		
		rootNode = new DefaultMutableTreeNode("home");
		dataRoot = new DefaultTreeModel(rootNode);

		
		irodsFileSystem= IRODSFileSystem.instance();
		userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
		dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().getDataTransferOperations(
						irodsAccount);
		

		final File localFiles = (File) irodsAccountFile;
		parseDirectoryContents(iRODSFileFactory, localFiles, rootNode, irodsAccount);

		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(50);
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
							chooser.getSelectedFile().getAbsolutePath():"nothing"));
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
			public void actionPerformed(ActionEvent e) {

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
							destinationFilePath="/" +irodsAccount.getZone()+"/"+label_destinationFilePath.getText();
							destinaitonIrodsFile = iRODSFileFactory.instanceIRODSFile(destinationFilePath);
							System.out.println("sourceLocalfile absolute path: " +sourceLocalfile.getAbsolutePath() +"\n"  +"destinaitonIrodsFile absolutepath: " +destinaitonIrodsFile.getAbsoluteFile());
							dataTransferOperationsAO.putOperation(sourceLocalfile.getAbsolutePath(),destinaitonIrodsFile.getAbsolutePath(),targetResourceName,null,null);
							JOptionPane.showMessageDialog(null, "File Transfer done successfully");
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

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);

		progressBar.setToolTipText("Progress of action");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE)
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
																		.addComponent(button_saveToIrodsServer))))
																		.addGap(33))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(24)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(userDirectoryTree, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(121)
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(btnSelectLocalFile)
												.addComponent(label_localFilePath))
												.addGap(10)
												.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
														.addComponent(btnSelectDestination)
														.addComponent(label_destinationFilePath))
														.addGap(11)
														.addComponent(button_saveToIrodsServer)))
														.addContainerGap())
				);
		setLayout(groupLayout);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{button_saveToIrodsServer, userDirectoryTree}));
	}
	

	void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,final File irodsAccountFile, DefaultMutableTreeNode node, final IRODSAccount irodsAccount)
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

		userDirectoryTree= new JTree(rootNode);
		userDirectoryTree.setModel(dataRoot);
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2){
					//resetting progress bar to 0 if a double click is detected
					progressBar.setValue(0);
					log.info("tree path after double click" +selectedNodeInTreeForDoubleClick);
					System.out.println("double click selection: " +selectedNodeInTreeForDoubleClick);

					selectedNodeInTreeForDoubleClick=getJtreeSelection(e);
					//getImageFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);
					getFile= new GetFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);
					getFile.execute();
				}
				else if(e.getClickCount()==1){
					//resetting progress bar to 0 if a single click is detected
					progressBar.setValue(0);
					selectedNodeInTreeForSingleClick=getJtreeSelection(e);
					//log.info("Single click selection: " +selectedNodeInTreeForSingleClick);
				}
			}
		});

		userDirectoryTree.setShowsRootHandles(true);
		userDirectoryTree.setEditable(true);
		add(userDirectoryTree);
	}

	String getJtreeSelection(MouseEvent me)
	{
		String fullTreePath="";
		TreePath tp =userDirectoryTree.getPathForLocation(me.getX(), me.getY());
		if(tp!=null)
		{
			Object treepath[] =tp.getPath();
			for(int i=0;i<treepath.length;i++)
			{
				fullTreePath  += "/" +treepath[i].toString();
			}
		}
		return fullTreePath;
	}

	/*void getImageFile(IRODSFileFactory iRODSFileFactory,String treePath,IRODSAccount irodsAccount) throws JargonException, IOException
	{

		System.out.println("finalTreePath:" +treePath);

		Recheck irodsAccounZone for all accounts
		IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream("/" +irodsAccount.getZone() +treePath);

		//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
		dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().
				getDataTransferOperations(
						irodsAccount);
		IRODSFile sourceIrodsFilePath = iRODSFileFactory.instanceIRODSFile("/" +irodsAccount.getZone() +treePath);


		Change directory address
		File destinationLocalFilePath =new File("D:\\a");
		try
		{
			//totalLengthOfFile = sourceIrodsFilePath.length();
			dataTransferOperationsAO.getOperation(sourceIrodsFilePath, destinationLocalFilePath, null, null);
		}
		catch (OverwriteException oe)
		{
			JOptionPane.showMessageDialog(null, "file already exist in specified directory!");
			oe.printStackTrace();
		}

		Image processing
		Opener opener = new Opener(); 
		String imageFilePath = "D:\\a\\"+sourceIrodsFilePath.getName();
		ImagePlus imp = opener.openImage(imageFilePath);
		if(imp!=null){
			imp.show();
		}
		else
		{
			IJ.showMessage("Opening file Failed.");
		}
		ImagePlus imp = open(directory, file);
		if (imp != null ) {
			imp.show();
		} else {
			IJ.showMessage("Open SPE...", "Failed.");
		}


		Displaying images in Frame 
		BufferedImage bufferImageIrodsFile = ImageIO.read(irodsfileistream);
		irodsfileistream.close();
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(bufferImageIrodsFile));
		JScrollPane scrollPane = new JScrollPane(label);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true); 

	}*/

	/*Put file to iRODS server - Not using as of now*/
	/*void putFile(IRODSAccount irodsAccount)
	{
		try {
			dataTransferOperationsAO =  irodsFileSystem
					.getIRODSAccessObjectFactory().
					getDataTransferOperations(
							irodsAccount);

			File sourceLocalfile =new File("D:\\iRODS\\Images\\bio5_header.jpeg");
			IRODSFile irodsfile = iRODSFileFactory.instanceIRODSFile("/iplant/home/sharanbabuk/Analysis");
			dataTransferOperationsAO.putOperation(sourceLocalfile, irodsfile, null, null);
		} catch (JargonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/



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
			IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream("/" +irodsAccount.getZone() +treePath);
		
		

			//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
			dataTransferOperationsAO =  irodsFileSystem
					.getIRODSAccessObjectFactory().
					getDataTransferOperations(
							irodsAccount);
			IRODSFile sourceIrodsFilePath = iRODSFileFactory.instanceIRODSFile("/" +irodsAccount.getZone() +treePath);

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
				File localFile= new File(destinationLocalFilePath.getAbsolutePath()+"\\" +sourceIrodsFilePath.getName());
				md5ChecksumLocalFile= IrodsUtilities.calculateMD5CheckSum(localFile);
				log.info("MD5checksum of local file: " +md5ChecksumLocalFile);
				
				log.info("MD5 checksum compared - Similar files:" +md5ChecksumLocalFile.equals(md5ChecksumServerFile));
				
				if(!md5ChecksumLocalFile.equals(md5ChecksumServerFile))
				JOptionPane.showMessageDialog(null, "File names are same but MD5 checksum is different!");
				
				oe.printStackTrace();
			}

			/*Opening the selected ImageJ*/
			Opener imageOpener = new Opener(); 
			String imageFilePath = Constants.IMAGEJ_LOCAL_WORKING_DIRECTORY +"\\" +sourceIrodsFilePath.getName();
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
