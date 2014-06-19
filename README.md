iPlugin (formerly known as iRODS-FileIO)
------------------------

iRODS-FileIO project deals with development of a Desktop Swing Application which can communicate with iRODS data servers and pull high definition image files through ImageJ application.

Ultimate goal of the project is to pack entire source into a Jar, which when executed through ImageJ application as a plugin, should be able to talk to iRODS and get images into ImageJ workspace, which further can be edited at users wish and can be saved back to iRODS servers.


New Requirements - Received on 6/1/2014
--------------------------------------------

1. Implement a technique to preserve user login details to help avoid retyping for same details in each login session. User should be able to customize this functionality and specify values pertaining to his own account. 
    * Implementation completed on 6/13/2014.
    * Testing in Windows, Mac is completed.
2. After loggin in, user should be able to download multiple files depending on his selection.

New Requirements - Received on 4/2/2014 | Completed on 5/6/2014
----------------------
1. User should be able to enter directory path for his landing page. This should happen before application successfully authenticates credentials. If path specified is wrong, then default it to user account folder.

2. User should be given an option to select system cache folder before he logs into system. 
    update on 4/4/2014: Mac testing is pending.

3. If user opts to download a file, system should check if selected file already exists in cache folder and then download depending on user consent. 
    update on 4/4/2014: Full testing pending.

4. If user clicks on a file and navigates to file details tab, then populate file information under "file details" tab.
5. While file transfers are in progress, show percentage(%) of file completed including the no of bytes transferred so far. 
6. Under plugins menu in ImageJ, change the IRODS options to "read file/open file". Add extra option to save files to irods depending on the path selected. Need to experiment with this as I'm not sure on how to trigger extra functionalities in an already running instance with a new option under ImageJ plugin (save to irods).
