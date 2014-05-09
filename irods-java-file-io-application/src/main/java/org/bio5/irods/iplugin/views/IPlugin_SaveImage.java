package org.bio5.irods.iplugin.views;

import ij.IJ;
import ij.plugin.frame.PlugInFrame;

import org.apache.log4j.Logger;
import org.bio5.irods.iplugin.bean.IPlugin;

public class IPlugin_SaveImage extends PlugInFrame {

	private static final long serialVersionUID = -502073300497809137L;

	/* Logger instantiation */
	static Logger log = Logger.getLogger(IPlugin_SaveImage.class.getName());

	public IPlugin iplugin;
	private SaveImagePanelImplementation saveImagePanelImplementation;

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

				// setVisibility(Constants.SAVE_PANEL_VISIBILITY);
				new SaveImageImplementation(iplugin);

			} else {
				log.error("irodsImagej.isImageOpened() is false");
				IJ.error("Error while saving file",
						"No image available to save !");
			}

		} else {
			log.error("Irods_Plugin_OpenImage.getIrodsImagej() is null");
			IJ.error("Error while saving file",
					"Irods ImageJ instance is null !");
		}
	}

	/* suing this will lead to new panel - not required as of now */
	private void setVisibility(boolean isVisibility) {
		if (isVisibility) {
			log.info("Inside setVisibility method");
			saveImagePanelImplementation = new SaveImagePanelImplementation(
					iplugin);
			if (null != iplugin.getSaveImagePanelImplementation()) {
				iplugin.setSaveImagePanelImplementation(saveImagePanelImplementation);
			} else {
				log.error("saveImagePanelImplementation instance is null");
			}
			saveImagePanelImplementation.setVisible(true);
		} else {
			log.error("Save Panel Visibility is set to false !");
		}

	}
}
