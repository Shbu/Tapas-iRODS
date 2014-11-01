package org.bio5.irods.iplugin.utilities;

public class Constants {
	public static final String NEW_LINE_STRING = "\n";
	public static final String DEFAULT_STORAGE_RESOURCE = "";
	public static String IMAGEJ_CACHE_FOLDER = System.getProperty("user.home")
			+ IrodsUtilities.getPathSeperator() + ".tapas";
	public static String DEFAULT_PATH_SEPERATOR = "/";
	public static String ZONE_IPLANT = "iplant";
	public static String ZONE_SPX = "spx";
	public static final String HOST_IPLANT = "data.iplantcollaborative.org";
	public static final String HOST_SPX = "spxirods.dyndns.org";
	public static String PORT = "1247";
	public static String HOME_STRING = "home";
	public static String FOLDER_SELECTION_CANCELED = "Folder Selection is canceled";
	public static String JTABBEDPANE_SELECTED_TAB_FILE_INFORMATION = "File Information";
	public static boolean JPROGRESS_SET_STRING_PAINTED = true;
	public static boolean SAVE_PANEL_VISIBILITY = true;
	public static boolean IS_HOME_DIRECTORY_THE_ROOT_NODE = false; /*
																	 * true - if
																	 * you want
																	 * to pull
																	 * everything
																	 * from home
																	 * directory
																	 * (This
																	 * includes
																	 * shared
																	 * files
																	 * too).
																	 * False- if
																	 * you want
																	 * to pull
																	 * collections
																	 * from only
																	 * your
																	 * account
																	 */

	/* Enhanced jargon Properties */
	public static boolean COMPUTE_AND_VERIFY_CHECKSUM_AFTER_TRANSFER_OPTION = false;
	public static int MAX_THREADS = 4;
	public static boolean USE_PARALLEL_TRANSFERS_OPTION = true;

	/* Error Strings */
	public static String ERROR_STRING_CONNECTION_REFUSED = "Connection refused";
	public static String ERROR_STRING_UNKNOWN_HOST = "UnknownHostException";

	/* Property file keys */
	public static final String PROPERTY_FILE_NAME = "tapas.properties";
	public static final String PROPERTY_USER_NAME = "login.username";
	public static final String PROPERTY_ZONE_NAME = "login.zone";
	public static final String PROPERTY_HOST_NAME = "login.host";
	public static final String PROPERTY_MAX_THREADS = "tapas.parallel.transfer.max.threads";

	/* OBJstat Details */
	public static final String OBJECT_TYPE_COLLECTION = "COLLECTION";
	public static final String OBJECT_TYPE_DATA_OBJECT = "DATA_OBJECT";

	/* irods labels */
	public static final String PLUGIN_TITLE = "Imagej v1.0.1";

	/* irods Login Panel Lables */
	public static final String CANCEL_BUTTON = "Cancel";
	public static final String CHOOSE_FOLDER = "Choose folder";
	public static final String IMAGE_J_CACHE_FOLDER = "ImageJ Cache Folder:";
	public static final String ENTER_IMAGE_J_CACHE_FOLDER_PATH = "Enter ImageJ Cache folder path";
	public static final String HOME_DIRECTORY = "Home Directory";
	public static final String FILE_SELECTION = "File Selection:";
	public static final String HOST = "Host:";
	public static final String ZONE = "Zone:";
	public static final String PORT_LABEL = "Port:";
	public static final String PORT_NO = "Port No.";
	public static final String PASSWORD = "Password:";
	public static final String USER_NAME = "User Name:";
	
	
	public static final String MD5_CONSTANT = "MD5";

}
