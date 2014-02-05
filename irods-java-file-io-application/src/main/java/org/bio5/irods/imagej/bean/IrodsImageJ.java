package org.bio5.irods.imagej.bean;

import java.io.Serializable;

import org.irods.jargon.core.connection.IRODSAccount;
import org.irods.jargon.core.pub.IRODSFileSystem;

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
    
    private String treeSelectionString = null;

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
