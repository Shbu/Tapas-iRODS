package org.bio5.irods.imagej.bean;

import java.io.Serializable;

import org.bio5.irods.imagej.utilities.IrodsTransferStatusCallbackListener;
import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.packinstr.TransferOptions;
import org.irods.jargon.core.pub.IRODSFileSystem;
import org.irods.jargon.core.pub.io.IRODSFile;
import org.irods.jargon.core.transfer.TransferControlBlock;

public class IrodsImageJ implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IrodsImageJ() {
		super();
	}

	private IRODSAccount irodsAccount = null;

    private IRODSFileSystem irodsFileSystem = null;
    
    private static IRODSFile iRodsFile = null;
    
    private TransferControlBlock transferControlBlock= null;
    
    private TransferOptions transferOptions= null;
    
    public TransferOptions getTransferOptions() {
		return transferOptions;
	}

	public void setTransferOptions(TransferOptions transferOptions) {
		this.transferOptions = transferOptions;
	}

	public TransferControlBlock getTransferControlBlock() {
		return transferControlBlock;
	}

	public void setTransferControlBlock(TransferControlBlock transferControlBlock) {
		this.transferControlBlock = transferControlBlock;
	}

	private IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener= null; 

	public IrodsTransferStatusCallbackListener getIrodsTransferStatusCallbackListener() {
		return irodsTransferStatusCallbackListener;
	}

	public void setIrodsTransferStatusCallbackListener(
			IrodsTransferStatusCallbackListener irodsTransferStatusCallbackListener) {
		this.irodsTransferStatusCallbackListener = irodsTransferStatusCallbackListener;
	}

	public static IRODSFile getiRodsFile() {
		return iRodsFile;
	}

	public static void setiRodsFile(IRODSFile iRodsFile) {
		IrodsImageJ.iRodsFile = iRodsFile;
	}

	public IRODSAccount getIrodsAccount() {
		return irodsAccount;
	}

	public void setIrodsAccount(IRODSAccount irodsAccount) {
		this.irodsAccount = irodsAccount;
	}

	public IRODSFileSystem getIrodsFileSystem() {
		return irodsFileSystem;
	}

	public void setIrodsFileSystem(IRODSFileSystem irodsFileSystem) {
		this.irodsFileSystem = irodsFileSystem;
	}
	
	
}
