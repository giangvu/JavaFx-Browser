/**
 * File name: FileUtils.java
 * Author: Truong Giang Vu - 040885372
 * Course: CST8284 - Object-Oriented Programming (Java)
 * Assignment: 2
 * Date: 01/12/2018
 * Professor: David Houtman
 * Purpose: Providing utility methods to manipulate files
 * Class list: FileUtils
 */
package assignment2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Utility class to getting and saving file content
 * @author Giang
 * @version 1.0
 *
 */
public class FileUtils {

	/**
	 * Get content of a file as array list of string
	 * @param f		the File object
	 * @return		content of file as array list of string
	 */
   public static ArrayList<String> getFileContentAsArrayList(File f) {
	   ArrayList<String> content = new ArrayList<String>();
	   
	   try (Scanner input = new Scanner(f)) {
		   while(input.hasNext()) {
			   content.add(input.nextLine());
		   }
	   } 
	   catch (FileNotFoundException e) { } // ignore
	   
	   return content;
   }
   
   /**
    * Save an array list of string to file
    * @param f		the File object
    * @param ar		list of string to be saved
    */
   public static void saveFileContents(File f, ArrayList<String> ar) {
	   try (PrintWriter writer = new PrintWriter(f)) {
		   for(String text : ar) {
			   writer.println(text);
		   }
	   } 
	   catch (FileNotFoundException e) { } // ignore
   }
   
   /**
    * Save a string to file
    * @param f		the File object
    * @param text	the string to be saved
    */
   public static void saveFileContents(File f, String text){
	    saveFileContents(f, new ArrayList<String>(Arrays.asList(text)));
   }
   
   /**
    * Get content of a file as a string
    * @param f	the File object
    * @return	string content of file
    */
   public static String getFileContentAsString(File f) {
	   StringBuilder builder = new StringBuilder();
	   for(String text : getFileContentAsArrayList(f)) {
		   builder.append(text);
	   }
		   
	   return builder.toString();
   }
   
   /**
    * Check if a file exists or not
    * @param f	the File object
    * @return	<code>true</code> if file exists, <code>false</code> otherwise
    */
   public static boolean fileExists(File f) {
	   return f.exists();
   }
   
   /**
    * Check if a file exists or not
    * @param s	path to the file
    * @return	<code>true</code> if file exists, <code>false</code> otherwise
    */
   public static boolean fileExists(String s) {
	   return fileExists(new File(s));
   } 
  
}
