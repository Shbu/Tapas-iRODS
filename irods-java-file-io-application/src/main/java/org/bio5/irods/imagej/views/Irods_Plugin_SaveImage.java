package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.frame.PlugInFrame;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IrodsImageJBean;

public class Irods_Plugin_SaveImage extends PlugInFrame {

	private static final long serialVersionUID = -502073300497809137L;

	/* Logger instantiation */
	static Logger log = Logger
			.getLogger(Irods_Plugin_SaveImage.class.getName());

	public IrodsImageJBean irodsImagej;

	public Irods_Plugin_SaveImage() {
		super("Saving Image");
		log.info("Executing Irods_Plugin_SaveImage option in ImageJ");
		init();
	}

	public void init() {
		if (null != Irods_Plugin_OpenImage.getIrodsImagej()) {
			irodsImagej = Irods_Plugin_OpenImage.getIrodsImagej();
			if (irodsImagej.isImageOpened()) {
				log.info("ImageJ cache folder: "
						+ irodsImagej.getImageJCacheFolder());
				HashMap<String, Object> saveDetails = irodsImagej
						.getSaveDetails();
			} else {
				log.error("irodsImagej.isImageOpened() is false");
				IJ.error("No image available to save !");
			}

		} else {
			log.error("Irods_Plugin_OpenImage.getIrodsImagej() is null");
			IJ.error("Irods ImageJ instance is null !");
		}
	}
}
