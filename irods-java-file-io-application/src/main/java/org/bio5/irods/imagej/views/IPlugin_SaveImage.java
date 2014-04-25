package org.bio5.irods.imagej.views;

import ij.IJ;
import ij.plugin.frame.PlugInFrame;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.bio5.irods.imagej.bean.IPlugin;
import org.bio5.irods.imagej.utilities.Constants;

public class IPlugin_SaveImage extends PlugInFrame {

	private static final long serialVersionUID = -502073300497809137L;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IPlugin_SaveImage.class.getName());

	public IPlugin iplugin;
	private SaveImagePanelImplementation savePanelFrame;

	public IPlugin_SaveImage() {
		super("Saving Image");
		log.info("Executing Irods_Plugin_SaveImage option in ImageJ");
		init();
	}

	public void init() {
		if (null != IPlugin_OpenImage.getIrodsImagej()) {
			iplugin = IPlugin_OpenImage.getIrodsImagej();
			if (iplugin.isImageOpened()) {
				log.info("irodsImagej.isImageOpened() is true");
				HashMap<String, Object> saveDetails = iplugin.getSaveDetails();

				setVisibility(Constants.SAVE_PANEL_VISIBILITY);

			} else {
				log.error("irodsImagej.isImageOpened() is false");
				IJ.error("No image available to save !");
			}

		} else {
			log.error("Irods_Plugin_OpenImage.getIrodsImagej() is null");
			IJ.error("Irods ImageJ instance is null !");
		}
	}

	private void setVisibility(boolean isVisibility) {
		if (isVisibility) {
			log.info("Inside setVisibility method");
			savePanelFrame = new SaveImagePanelImplementation(iplugin);
			savePanelFrame.setVisible(true);
		} else {
			log.error("Save Panel Visibility is set to false !");
		}

	}
}
