package org.bio5.irods.imagej.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;

import javax.swing.JScrollBar;

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
	/**
	 * Create the panel.
	 * @throws JargonException 
	 */

	public DirectoryContentsPane(List<String> ContentsInHome,final IRODSFile irodsAccountFile,final IRODSAccount irodsAccount) throws JargonException {

		final IRODSFileFactory iRODSFileFactory = FileOperations.getIrodsAccountFileFactory(irodsAccount);
		String irodsZone =irodsAccount.getZone();
		System.out.println("irodsZone" +irodsZone);

		rootNode = new DefaultMutableTreeNode("home");
		irodsFileSystem= IRODSFileSystem.instance();
		userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
		DataTransferOperations dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().getDataTransferOperations(
						irodsAccount);

		final File localFiles = (File) irodsAccountFile;
		parseDirectoryContents(iRODSFileFactory, localFiles, rootNode, irodsAccount);

		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(50);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));

		JButton btnNewButton_AddToImageJ = new JButton("Add to ImageJ");

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
					List<String> dirList= FileOperations.getDirectoryContents(irodsAccount, null);

					IRODSFile irodsAccountFile =FileOperations.getIrodsAccountFile(irodsAccount);
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
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
						.addGap(10)
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addGap(18)
										.addComponent(btnNewButton_OpenImage, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(btnNewButton_AddToImageJ))
										.addGroup(groupLayout.createSequentialGroup()
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(btnHome)))
												.addGap(33))
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(22)
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnNewButton_OpenImage)
										.addComponent(btnNewButton_AddToImageJ))
										.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
												.addComponent(userDirectoryTree, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE)
												.addComponent(btnHome)))
												.addContainerGap())
				);
		setLayout(groupLayout);
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
				System.out.println("File number" +i);
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child, irodsAccount);
			}
		}

		userDirectoryTree= new JTree(rootNode);
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if(e.getClickCount()==2){
					String treePath=getJtreeSelection(e);
					System.out.println("tree path after double click" +treePath);
					try {
						getImageFile(iRODSFileFactory, treePath,irodsAccount);
					} catch (JargonException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
		
		/*BufferedImage bufferImageIrodsFile = ImageIO.read(irodsfileistream);
		irodsfileistream.close();
		JFrame frame = new JFrame();
		JLabel label = new JLabel(new ImageIcon(bufferImageIrodsFile));
		JScrollPane scrollPane = new JScrollPane(label);*/
		
		//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
		DataTransferOperations dataTransferOperationsAO =  irodsFileSystem
				.getIRODSAccessObjectFactory().
				getDataTransferOperations(
						irodsAccount);
		IRODSFile irodsfile = iRODSFileFactory.instanceIRODSFile("/" +irodsAccount.getZone() +treePath);
		
		/*Change directory address*/
		File localfile =new File("D:\\iRODS");
		dataTransferOperationsAO.getOperation(irodsfile, localfile, null, null);
		
		/*scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);  
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
		frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true); */
		
		
		
	}
	
}
