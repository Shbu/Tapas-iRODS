package org.bio5.irods.sampleapplications.irods_java_file_io_application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 *
 */
public class IrodsConnection 
{
    public static void main( String[] args ) throws IOException
    {
        System.out.println( "Hello World!" );
        
        System.out.println("Enter 1 to get login window.");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String decision=br.readLine();
    }
    
    public void callLoginWindow(String decision)
    {
    	if(decision.equals("1"))
    	{
    		LoginWindow lw =new LoginWindow();
    	}
    }
}
