package org.bio5.irods.imagej.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import javax.swing.JTree;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.bio5.irods.imagej.fileoperations.FileOperations;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.FileNotFoundException;
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.UserAO;
import org.irods.jargon.core.pub.io.FileIOOperations;
import org.irods.jargon.core.pub.io.FileIOOperations.SeekWhenceType;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;
import org.irods.jargon.core.pub.io.IRODSFileReader;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DirectoryContentsPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 722996165620904921L;
	private JTree userDirectoryTree;
	private DefaultMutableTreeNode rootNode;
	/**
	 * Create the panel.
	 * @throws JargonException 
	 */
	public DirectoryContentsPane(List<String> ContentsInHome,final IRODSFile irodsAccountFile,final IRODSAccount irodsAccount) throws JargonException {

		final IRODSFileFactory iRODSFileFactory = FileOperations.getIrodsAccountFileFactory(irodsAccount);
		String irodsZone =irodsAccount.getZone();
		
		rootNode = new DefaultMutableTreeNode("home");
		/*Iterator<String> itr=ContentsInHome.iterator();
		while(itr.hasNext())
		{
			childNode = new DefaultMutableTreeNode(itr.next());
			rootNode.add(childNode);
		}*/

		final File localFiles = (File) irodsAccountFile;
		parseDirectoryContents(iRODSFileFactory, localFiles, rootNode);
		
		userDirectoryTree.setToolTipText("Directory list");
		userDirectoryTree.setVisibleRowCount(50);
		userDirectoryTree.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		JButton btnNewButton_AddToImageJ = new JButton("Add to ImageJ");
		
		JButton btnNewButton_OpenImage = new JButton("Open Image");
		btnNewButton_OpenImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
				//IRODSFileSystem irodsFileSystem= IRODSFileSystem.instance();
				//UserAO  userAccount = irodsFileSystem.getIRODSAccessObjectFactory().getUserAO(irodsAccount);
				
				
				//IRODSFileReader iofileReader = new IRODSFileReader(irodsAccountFile, iRODSFileFactory);
				
				IRODSFile[] direcFiles = (IRODSFile[]) irodsAccountFile.listFiles();
				/*IRODSFile dirFile =null;
				for(int i=0; i<direcFiles.length;i++)
				{
					dirFile=direcFiles[i];
					if(dirFile.isFile())
					break;	
				}*/
				System.out.println("URI" +irodsAccountFile.getAbsolutePath());
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
					.addComponent(userDirectoryTree, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(18)
							.addComponent(btnNewButton_OpenImage, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton_AddToImageJ))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnHome)))
					.addContainerGap(136, Short.MAX_VALUE))
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
	
	void parseDirectoryContents(final IRODSFileFactory iRODSFileFactory,final File irodsAccountFile, DefaultMutableTreeNode node)
	{
		
		/*File[] f =irodsAccountFile.listFiles();
		for(int i=0; i<f.length;i++)
		{
			if(f[i].isDirectory()){
				node.add(new DefaultMutableTreeNode(f[i].getName()));
				System.out.println(f[i].getName() +node.getDepth());
				parseDirectoryContents(f[i],new DefaultMutableTreeNode(f[i].getName()));
			}
			else{
				Need to update code to display leaf nodes in a dir -- currently check console o/p 
				System.out.println(f[i].getName());
				DefaultMutableTreeNode parentNode= new DefaultMutableTreeNode(f[i].getParent());
				parentNode.add(new DefaultMutableTreeNode(f[i].getName()));
			}
		}*/
		
		
		if(!irodsAccountFile.isDirectory()){

			System.out.println("File name" +irodsAccountFile.getName() +irodsAccountFile.getAbsolutePath());
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
				parseDirectoryContents(iRODSFileFactory, direcFiles[i], child);
			}
		}
		
		userDirectoryTree= new JTree(rootNode);
		userDirectoryTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				if(e.getClickCount()==2){
					String treePath=irodsZone +getJtreeSelection(e);
					try {
						pullFiles(iRODSFileFactory, treePath);
					} catch (JargonException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
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
	
	void pullFiles(IRODSFileFactory iRODSFileFactory,String treePath) throws JargonException, IOException
	{
		
		System.out.println("finalTreePath:" +treePath);
		IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream(treePath);
		
		BufferedImage bi = ImageIO.read(irodsfileistream);
		JFrame frame = new JFrame();  
        JLabel label = new JLabel(new ImageIcon(bi));  
        frame.getContentPane().add(label, BorderLayout.CENTER);  
        frame.pack();  
        frame.setVisible(true); 
	}
}
