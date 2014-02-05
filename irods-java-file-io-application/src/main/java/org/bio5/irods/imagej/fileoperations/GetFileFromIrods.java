package org.bio5.irods.imagej.fileoperations;

import ij.IJ;
import ij.ImagePlus;
import ij.io.Opener;

import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJ;
import org.bio5.irods.imagej.utilities.Constants;
import org.bio5.irods.imagej.utilities.IrodsUtilities;
import org.bio5.irods.imagej.views.DirectoryContentsPane;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.exception.OverwriteException;
import org.irods.jargon.core.pub.DataObjectAO;
import org.irods.jargon.core.pub.DataTransferOperations;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.pub.io.IRODSFileFactory;
import org.irods.jargon.core.pub.io.IRODSFileInputStream;

public class GetFileFromIrods extends SwingWorker<Void, Integer> {

	private IRODSFileFactory iRODSFileFactory;
	private String treePath;
	private IRODSAccount irodsAccount;
	private long totalLengthOfFile = 0L;
	private long copiedLengthOfFile = 0L;
	private DataTransferOperations dataTransferOperationsAO;
	private IrodsImageJ irodsImagej;
	private DataObjectAO dataObjectAO;
	private JProgressBar jprogressbar;
	
	/*Logger instantiation*/
	static Logger log = Logger.getLogger(
			DirectoryContentsPane.class.getName());

	/*Get files from iRODS Server*/
	public GetFileFromIrods(IRODSFileFactory iRODSFileFactory, String treePath,
			IrodsImageJ irodsImagej, JProgressBar progressbar) {
		this.iRODSFileFactory = iRODSFileFactory;
		this.treePath = treePath;
		this.irodsAccount = irodsImagej.getIrodsAccount();
		this.irodsImagej  =irodsImagej;
		this.jprogressbar  =progressbar;
	}

	/*Using SwingWorker-doInBackGround() function to do processing in background*/
	@SuppressWarnings("deprecation")
	@Override
	public Void doInBackground() throws Exception {

		//getImageFile(iRODSFileFactory,treePath,irodsAccount );
		System.out.println("finalTreePath:" +treePath);

		/*Recheck irodsAccounZone for all accounts*/
		//IRODSFileInputStream irodsfileistream = iRODSFileFactory.instanceIRODSFileInputStream(IrodsUtilities.pathSeperator() +irodsAccount.getZone() +treePath);

		//Get file to local directory using getDataTransferOperations --- Need to check benchmarks
		dataTransferOperationsAO =  irodsImagej.getIrodsFileSystem().
				getIRODSAccessObjectFactory().
				getDataTransferOperations(
						irodsAccount);
		IRODSFile sourceIrodsFilePath = iRODSFileFactory.instanceIRODSFile(IrodsUtilities.pathSeperator() +irodsAccount.getZone() +treePath);

		dataObjectAO=  irodsImagej.getIrodsFileSystem().getIRODSAccessObjectFactory().getDataObjectAO(irodsAccount);

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
			jprogressbar.setValue(i);
		}
	}

	@Override
	public void done()
	{
		publish(100);
	}
}
