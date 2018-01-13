/**
 * File name: WebPage.java
 * Author: David Houtman
 * Updated by: Truong Giang Vu - 040885372
 * Course: CST8284 - Object-Oriented Programming (Java)
 * Assignment: 2
 * Date: 01/12/2018
 * Professor: David Houtman
 * Class list: WebPage
 */
package assignment2;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/***
 * 
 *  This code provided by Dave Houtman [2017]
 *
 */
public class WebPage {
	
	private WebView webview = new WebView();
	private WebEngine engine;
	
	public WebEngine createWebEngine(Stage stage) {
		
		WebView wv = getWebView();
		engine = wv.getEngine();
		
		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (newState == Worker.State.RUNNING) {
					stage.setTitle(engine.getLocation());
					Menus.webpageChanging(wv);
				}
				if (newState == Worker.State.SUCCEEDED) {
					Menus.webpageChanged(wv);
				}
			}
		});
		
		return engine;
	}
	
	public WebView getWebView() {
		return webview;
	}
	
	public WebEngine getWebEngine() {
		return engine;
	}
	
}
