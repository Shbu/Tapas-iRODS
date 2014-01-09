package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.ImagePlus;
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
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.core.transfer.DefaultTransferControlBlock;
import org.irods.jargon.core.transfer.ParallelGetFileTransferStrategy;
import org.irods.jargon.core.transfer.TransferControlBlock;
import org.irods.jargon.core.transfer.TransferStatusCallbackListener;

import javax.swing.JScrollBar;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import java.awt.Component;

import javax.swing.JProgressBar;

public class DirectoryContentsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 722996165620904921L;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
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

	/**
	 * Create the panel.
	 * @throws JargonException 
	 */

	public DirectoryContentsPane(List<String> ContentsInHome,final IRODSFile irodsAccountFile,final IRODSAccount irodsAccount) throws JargonException {


		iRODSFileFactory= FileOperations.getIrodsAccountFileFactory(irodsAccount);
		String irodsZone =irodsAccount.getZone();
		System.out.println("irodsZone" +irodsZone);

		rootNode = new DefaultMutableTreeNode("home");
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

		final JButton btnNewButton_AddToImageJ = new JButton("Save to iRODS Server");
		btnNewButton_AddToImageJ.setEnabled(false);

		JButton btnNewButton_OpenImage = new JButton("Open Image");
		btnNewButton_OpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{

					//IRODSFileReader iofileReader = new IRODSFileReader(irodsAccountFile, iRODSFileFactory);

					IRODSFile[] direcFiles = (IRODSFile[]) irodsAccountFile.listFiles();
					System.out.println("Absolute Path: " +irodsAccountFile.getAbsolutePath());
					IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream(irodsAccountFile.getAbsolutePath() +"/TestImages/SpitzerTelescope_PinWheelGalaxy.jpg");

					BufferedImage bi = ImageIO.read(irodsfileistream);
					JFrame frame = new JFrame();
					JLabel label = new JLabel(new ImageIcon(bi));
					frame.getContentPane().add(label, BorderLayout.CENTER);  
					frame.pack();
					frame.setVisible(true);
				}
				catch (Exception ex){
					ex.printStackTrace();
				}
			}
		});

		JButton btnHome = new JButton("Home");
		btnHome.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<String> dirList= FileOperations.getDirectoryContents(irodsAccount);

					IRODSFile irodsAccountFile =FileOperations.getiRodsFile();
					DirectoryContentsPane directoryContents  =new DirectoryContentsPane(dirList,irodsAccountFile,irodsAccount);
					MainWindow mw  =new MainWindow();
					mw.setContentPane(directoryContents);
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Directory doesnt exists", "Not Home directory", 1);

					e1.printStackTrace();
				} catch (JargonException e1) {
					JOptionPane.showMessageDialog(null, "Jargon Exception", "Jargon Exception", 1);
					e1.printStackTrace();
				}
			}
		});
		final JLabel label_localFilePath = new JLabel("Local file");
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

				label_destinationFilePath.setText(selectedNodeInTreeForSingleClick);
				btnNewButton_AddToImageJ.setEnabled(true);
			}
		});


		/*Put to iRODS server*/
		btnNewButton_AddToImageJ.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					//String sourceFilePath="D:\\iRODS\\FayetteVille_Portrait_LiDAR 20x25 300dpi.png";
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
							destinationFilePath="/" +irodsAccount.getZone()+"/"+selectedNodeInTreeForSingleClick;
							destinaitonIrodsFile = iRODSFileFactory.instanceIRODSFile(destinationFilePath);
							System.out.println("sourceLocalfile absolute path: " +sourceLocalfile.getAbsolutePath() +"\n"  +"destinaitonIrodsFile absolutepath: " +destinaitonIrodsFile.getAbsoluteFile());
							dataTransferOperationsAO.putOperation(sourceLocalfile.getAbsolutePath(),destinaitonIrodsFile.getAbsolutePath(),targetResourceName,null,null);
							JOptionPane.showMessageDialog(null, "File Transfer done successfully");
							
							directoryContentsPane   =  new DirectoryContentsPane(null, irodsAccountFile, irodsAccount);
						}
					}
					else{
						JOptionPane.showMessageDialog(null, "Source is empty!");
					}

				} catch (JargonException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
						.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(10)
										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
												.addComponent(btnHome)
												.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
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
																				.addComponent(btnNewButton_AddToImageJ)))
																				.addGroup(groupLayout.createSequentialGroup()
																						.addPreferredGap(ComponentPlacement.UNRELATED)
																						.addComponent(btnNewButton_OpenImage, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)))
																						.addGap(33))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(24)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(userDirectoryTree, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(btnHome)
										.addGap(69)
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
														.addComponent(btnNewButton_AddToImageJ)
														.addPreferredGap(ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
														.addComponent(btnNewButton_OpenImage)))
														.addContainerGap())
				);
		setLayout(groupLayout);
		setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{btnNewButton_AddToImageJ, userDirectoryTree, btnHome, btnNewButton_OpenImage}));
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
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2){
					selectedNodeInTreeForDoubleClick=getJtreeSelection(e);
					System.out.println("tree path after double click" +selectedNodeInTreeForDoubleClick);
					//getImageFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);

					getFile= new GetFile(iRODSFileFactory, selectedNodeInTreeForDoubleClick,irodsAccount);
					getFile.execute();
				}
				else if(e.getClickCount()==1){
					selectedNodeInTreeForSingleClick=getJtreeSelection(e);
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

	void getImageFile(IRODSFileFactory iRODSFileFactory,String treePath,IRODSAccount irodsAccount) throws JargonException, IOException
	{

		System.out.println("finalTreePath:" +treePath);

		/*Recheck irodsAccounZone for all accounts*/
		IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream("/" +irodsAccount.getZone() +treePath);

		//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
		dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().
				getDataTransferOperations(
						irodsAccount);
		IRODSFile sourceIrodsFilePath = iRODSFileFactory.instanceIRODSFile("/" +irodsAccount.getZone() +treePath);


		/*Change directory address*/
		File destinationLocalFilePath =new File("D:\\iRODS");
		try
		{
			//totalLengthOfFile = sourceIrodsFilePath.length();
			dataTransferOperationsAO.getOperation(sourceIrodsFilePath, destinationLocalFilePath, null, null);
		}
		catch (OverwriteException oe)
		{
			JOptionPane.showMessageDialog(null, "file already exist in specified directory!");
		}

		/*Image processing*/
		Opener opener = new Opener(); 
		String imageFilePath = "D:\\iRODS\\"+sourceIrodsFilePath.getName();
		ImagePlus imp = opener.openImage(imageFilePath);
		if(imp!=null){
			imp.show();
		}
		else
		{
			IJ.showMessage("Opening file Failed.");
		}
		/*ImagePlus imp = open(directory, file);
		if (imp != null ) {
			imp.show();
		} else {
			IJ.showMessage("Open SPE...", "Failed.");
		}*/


		/*Displaying images in Frame */
		/*BufferedImage bufferImageIrodsFile = ImageIO.read(irodsfileistream);
		irodsfileistream.close();
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(bufferImageIrodsFile));
		JScrollPane scrollPane = new JScrollPane(label);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true); */

	}

	/*Put file to iRODS server*/
	void putFile(IRODSAccount irodsAccount)
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
	}



	class GetFile extends SwingWorker<Void, Integer>
	{
		private IRODSFileFactory iRODSFileFactory;
		private String treePath;
		private IRODSAccount irodsAccount;
		private long totalLengthOfFile = 0L;
		private long copiedLengthOfFile = 0L;


		public GetFile(IRODSFileFactory iRODSFileFactory, String treePath,
				IRODSAccount irodsAccount) {
			this.iRODSFileFactory = iRODSFileFactory;
			this.treePath = treePath;
			this.irodsAccount = irodsAccount;
		}

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

			/*Change directory address*/
			File destinationLocalFilePath =new File("D:\\iRODS");
			try
			{
				totalLengthOfFile = sourceIrodsFilePath.length();
				dataTransferOperationsAO.getOperation(sourceIrodsFilePath, destinationLocalFilePath, null, null);
			}
			catch (OverwriteException oe)
			{
				JOptionPane.showMessageDialog(null, "file already exist in specified directory!");
			}

			/*Image processing*/
			Opener opener = new Opener(); 
			String imageFilePath = "D:\\iRODS\\"+sourceIrodsFilePath.getName();
			ImagePlus imp = opener.openImage(imageFilePath);
			if(imp!=null){
				imp.show();
			}
			else
			{
				IJ.showMessage("Opening file Failed.");
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
