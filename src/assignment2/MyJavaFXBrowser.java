/**
 * File name: MyJavaFXBrowser.java
 * Author: Truong Giang Vu - 040885372
 * Course: CST8284 - Object-Oriented Programming (Java)
 * Assignment: 2
 * Date: 01/12/2018
 * Professor: David Houtman
 * Purpose: Running the application
 * Class list: MyJavaFXBrowser
 */
package assignment2;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**
 * Custom browser
 * @author Giang
 * @version 1.0
 *
 */
public class MyJavaFXBrowser extends Application {

	@Override
	public void start(Stage primaryStage) {

	    WebPage currentPage = new WebPage();
		WebView webView = currentPage.getWebView();
		
		// initialize AddressBar, RightPane(history), BottomPane(html/javascript) and TopPane
		Menus.initAddressBar(webView);
		Menus.initRightPane(webView);
		Menus.initBottomPane();
		Node topPane = Menus.getTopPane(webView);
		
		WebEngine webEngine = currentPage.createWebEngine(primaryStage);
		webEngine.load(Menus.getStartUpPage());
	
		BorderPane root = new BorderPane();
		root.setCenter(webView);
		root.setTop(topPane);
		
		Scene scene = new Scene(root, 800, 500);
		primaryStage.setScene(scene);
		primaryStage.show();	
	}
	
	@Override
	public void stop() {
		// save bookmark list to file while closing the application
	    Menus.saveListBookMarkToFile();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}

}
