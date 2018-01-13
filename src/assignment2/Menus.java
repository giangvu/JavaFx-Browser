/**
 * File name: Menus.java
 * Author: Truong Giang Vu - 040885372
 * Course: CST8284 - Object-Oriented Programming (Java)
 * Assignment: 2
 * Date: 01/12/2018
 * Professor: David Houtman
 * Purpose: Providing utility methods to generate Menu, AddressBar, BottomPane and RightPane
 * Class list: Menus
 */
package assignment2;

import java.io.File;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;

/**
 * Menus class is responsible for generating MenuBar, Menus, MenuItems, Right Pane and Bottom Pane
 * @author Giang
 * @version 1.0
 *
 */
public class Menus {
	private static final String GOOGLE_URL = "http://www.google.ca";
	private static final String DEVELOPER_INFO = "Name: Truong Giang Vu \nStudent Number: 040885372";
	
	/** File handler objects */
	private static File fileBookMark = new File("bookmark.web");
	private static File fileDefault = new File("default.web");
	
	private static Menu mnuBookMarks;

	/** TopPane, AddressBar, RightPane and BottomPane */
	private static VBox topPane = new VBox();
	private static HBox addressBar = new HBox();
	private static ScrollPane bottomPane = new ScrollPane();
	private static ScrollPane historyPane = new ScrollPane();
	private static Pane rightPane = new Pane();
	private static TextField txtAddress = new TextField();
	private static Button btnPrevious = new Button("\u23F4");
	private static Button btnNext = new Button("\u23F5");
	
	/** Storing a list of bookmarks loaded from file */
	private static ArrayList<String> listBookMark = new ArrayList<>();
	
	static {
		// if bookmarks.web exists, load its content to listBookMark
		if (FileUtils.fileExists(fileBookMark)) {
			listBookMark = FileUtils.getFileContentAsArrayList(fileBookMark);
		}
	}
	
	/**
	 * This method should be fired while a new page is loading in the WebPage class
	 * @param wv 	the WebView object of WebPage
	 */
	public static void webpageChanging(WebView wv) {
		updateAddressTextField(wv);
	}
	
	/**
	 * This method should be fired after a new page is loaded in the WebPage class
	 * @param wv 	the WebView object of WebPage
	 */
	public static void webpageChanged(WebView wv) {
		updateHistory(wv);
		updateBottomPaneContent(wv);
	}
	
	/**
	 * Save current BookMark list to file
	 */
	public static void saveListBookMarkToFile() {
		FileUtils.saveFileContents(fileBookMark, listBookMark);
	}
	
	/**
	 * Get Start Up page
	 * @return Start Up page
	 */
	public static String getStartUpPage() {
		if (!FileUtils.fileExists(fileDefault)) {
			FileUtils.saveFileContents(fileDefault, GOOGLE_URL);
		}
		return FileUtils.getFileContentAsString(fileDefault);
	}
	
	/**
	 * Get the top pane of browser
	 * @param wv 	the WebView object of WebPage
	 * @return top pane
	 */
	public static VBox getTopPane(WebView wv) {
		topPane.getChildren().add(getMenuBar(wv));
		return topPane;
	}
	
	/**
	 * Initialize bottom pane of browser
	 */
	public static void initBottomPane() {
		bottomPane.setPrefHeight(300);
		bottomPane.setPadding(new Insets(20));
	}
	
	/**
	 * Initialize the address bar of browser
	 * @param wv 	the WebView object of WebPage
	 */
	public static void initAddressBar(WebView wv) {
		Label lblAddress = new Label("Enter Address");
		lblAddress.setStyle("-fx-padding: 5px;");

		Button btnGo = new Button("Go");
		btnGo.setOnAction(e -> loadURL(wv, txtAddress.getText()));
		
		/*
		 * Listen for enter key event on a TextField
		 * Retrieved from: https://tagmycode.com/snippet/1522/listen-for-enter-key-event-on-a-textfield#.Wlk5XqinFPZ
		 */
		txtAddress.setOnKeyPressed(e -> { 
			if (e.getCode().equals(KeyCode.ENTER)) {
				/*
				 * Provided by Professor David Houtman
				 */
				btnGo.fire();
			}
		});

		addressBar.getChildren().addAll(lblAddress, txtAddress, btnGo);
		
		/*
		 * javafx HBox Sets the horizontal grow tutorial
		 * Retrieved from: http://book2s.com/java/api/javafx/scene/layout/hbox/sethgrow-2.html
		 * */
		HBox.setHgrow(txtAddress, Priority.ALWAYS);
	}
	
	/**
	 * Verify and load URL. Show an alert box if the URL is invalid
	 * @param wv 		the WebView object of WebPage
	 * @param address   the URL to be loaded
	 */
	private static void loadURL(WebView wv, String address) {
		try {
			URL url = new URL(address);
			wv.getEngine().load(url.toString());
		}
		catch (MalformedURLException malformedException) {
			showAlert(AlertType.WARNING, "Invalid URL", "Warning", null);
		}
		catch (Exception ex) { } //ignore
	}
	
	/**
	 * Initialize the right pane of browser
	 * @param wv 	the WebView object of WebPage
	 */
	public static void initRightPane(WebView wv) {
		Label lbl = new Label("History");
		lbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 15px;");
	
		historyPane.setPadding(new Insets(10));
		btnPrevious.setOnAction(e -> goToHistory(wv, -1));
		btnNext.setOnAction(e -> goToHistory(wv, 1));

		// buttons container
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(20);
		buttonBox.setPadding(new Insets(15));
		buttonBox.getChildren().addAll(btnPrevious, btnNext);
		
		VBox vb = new VBox(lbl, historyPane, buttonBox);
		vb.setAlignment(Pos.TOP_CENTER);
		
		rightPane.getChildren().addAll(vb);
	}
	
	/**
	 * Go to pages in history by step
	 * @param wv	the WebView object of WebPage
	 * @param step	number of steps to go. 1 to go one step forward, -1 to go one step backward
	 */
	private static void goToHistory(WebView wv, int step) {
		/*
		 * How to program Back and Forward buttons in JavaFX with WebView and WebEngine?
		 * Retrieved from: https://stackoverflow.com/questions/18928333/how-to-program-back-and-forward-buttons-in-javafx-with-webview-and-webengine
		 */
		WebHistory history = wv.getEngine().getHistory();
		int destinationIndex = history.getCurrentIndex() + step;
		if (destinationIndex >= 0 && destinationIndex < history.getEntries().size()) {
			try {
				history.go(step);
			}
			catch (Exception ex) { } // ignore
		}
	}
	
	/**
	 * Transform HTML/Javascript source code to string and update content of the bottom pane
	 * @param wv 	the WebView object of WebPage
	 */
	private static void updateBottomPaneContent(WebView wv) {
		try {
			/*
			 * JavaFX Tutorial - Obtain the raw XML data as a string of text
			 * Retrieved from: http://www.java2s.com/Tutorials/Java/JavaFX/1500__JavaFX_WebEngine.htm
			 */
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(wv.getEngine().getDocument()), new StreamResult(writer));
            
            bottomPane.setContent(new Text(writer.getBuffer().toString()));
            
        } catch (Exception e) { } // ignore
	}
	
	/**
	 * Update content of the history pane and 
	 * enable/disable backward/forward buttons based on current history index
	 * Should be fired after a new web page is loaded
	 * 
	 * @param wv 	the WebView object of WebPage
	 */
	private static void updateHistory(WebView wv) {
		WebHistory history = wv.getEngine().getHistory();
		VBox box = new VBox();
		ObservableList<Entry> entries = history.getEntries();
		for (int i = entries.size() - 1; i >= 0; i--) {
			// sometimes, the WebHistory could not get page's title
			String text = entries.get(i).getUrl();
			if (!entries.get(i).getTitle().isEmpty()) {
				text = entries.get(i).getTitle() + " - " + text;
			}
			text = "\u25CF " + text;
			
			// build label and tooltip
			Label label = new Label(text);
			label.setTooltip(new Tooltip(text));
			label.setMaxWidth(350);
			label.setLineSpacing(18);
			label.setTextOverrun(OverrunStyle.ELLIPSIS);
			
			// highlight current page
			if (history.getCurrentIndex() == i) {
				label.setStyle("-fx-font-weight: bold");
			}
			box.getChildren().add(label);
		}

		historyPane.setContent(box);
		
		// enable/disable buttons based on current index of history
		btnPrevious.setDisable(history.getCurrentIndex() == 0 ? true : false);
		btnNext.setDisable(history.getCurrentIndex() == history.getEntries().size() - 1 ? true : false);
	}
	
	/**
	 * Set text of the Address TextField
	 * @param wv 	the WebView object of WebPage
	 */
	private static void updateAddressTextField(WebView wv) {
		txtAddress.setText(wv.getEngine().getLocation());
	}
	
	/**
	 * Utility method to create a new menu and add child nodes to it
	 * @param name			name of the menu
	 * @param children		children to be attached to this menu
	 * @return				new menu object
	 */
	private static Menu getMnu(String name, MenuItem... children) {
		Menu menu = new Menu(name);
		menu.getItems().addAll(children);
		return menu;
	}
	
	/**
	 * Utility method to create a new menu, assign a shortcut key and bind event handler
	 * @param name			name of the menu item
	 * @param shortKey		the shortcut key for it
	 * @param handler		handler for action on this menu item
	 * @return				a new menu item object
	 */
	private static MenuItem getMnuItm(String name, char shortKey, EventHandler<ActionEvent> handler) {
		MenuItem item = new MenuItem(name);
		/*
		 * JavaFX 2.0: Set Accelerator (KeyCombination) for menu items
		 * Retrieved from: http://java-buddy.blogspot.ca/2012/02/javafx-20-set-accelerator.html
		 */
		item.setAccelerator(KeyCombination.keyCombination("Ctrl+" + shortKey));
		item.setOnAction(handler);
		return item;
	}
	
	/**
	 * Create File menu and its children
	 * @param wv 	the WebView object of WebPage
	 * @return		File menu object
	 */
	private static Menu getMnuFile(WebView wv) {
		return getMnu("File", getMnuItmRefresh(wv), getMnuItmExit());
	}
	
	/**
	 * Create Settings menu and its children
	 * @param wv 	the WebView object of WebPage
	 * @return		Settings menu object
	 */
	private static Menu getMnuSettings(WebView wv) {
		return getMnu("Settings", getMnuItmToggleAddressBar(), getMnuItmToggleRightPane(), 
				getMnuItmToggleBottomPane(), getMnuItmChangeStartup(wv));
	}
	
	/**
	 * Create BookMarks menu and its children
	 * @param wv 	the WebView object of WebPage
	 * @return		BookMarks menu object
	 */
	private static Menu getMnuBookMarks(WebView wv) {
		mnuBookMarks = getMnu("BookMarks", getMnuItmAddBookMark(wv));
		mnuBookMarks.getItems().addAll(getListMnuItmBookMark(wv));
		return mnuBookMarks;
	}
	
	/**
	 * Create Help menu and its children
	 * @param wv 	the WebView object of WebPage
	 * @return		Help menu object
	 */
	private static Menu getMnuHelp() {
		return getMnu("Help", getMnuItmAbout());
	}
	
	/**
	 * Create menu bar and its menus
	 * @param wv	the WebView object of WebPage
	 * @return		Menu bar object
	 */
	private static MenuBar getMenuBar(WebView wv) {
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(getMnuFile(wv), getMnuSettings(wv), getMnuBookMarks(wv), getMnuHelp());
		return menuBar;
	}
	
	/**
	 * Create Toggle Address Bar menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmToggleAddressBar() {
		return getMnuItm("Toggle Address Bar", 'B', e -> toggleAddressBarHandler());
	}
	
	/**
	 * Create Toggle Javascript/HTML menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmToggleBottomPane() {
		return getMnuItm("Javascript/HTML/CSS", 'J', e -> {
			BorderPane root = (BorderPane)topPane.getParent();
			root.setBottom(root.getBottom() == null ? bottomPane : null);
		});
	}
	
	/**
	 * Create Toggle Right Pane menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmToggleRightPane() {
		return getMnuItm("History", 'H', e -> {
			BorderPane root = (BorderPane)topPane.getParent();
			root.setRight(root.getRight() == null ? rightPane : null);
		});
	}
	
	/**
	 * Create Change Start-Up menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmChangeStartup(WebView wv) {
		return getMnuItm("Change Start-up Page", 'P', e -> 
			FileUtils.saveFileContents(fileDefault, wv.getEngine().getLocation()));
	}
	
	/**
	 * Create Add BookMark menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmAddBookMark(WebView wv) {
		return getMnuItm("Add Bookmark", 'M', e -> addBookMarkHandler(wv));
	}
	
	/**
	 * Create Refresh menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmRefresh(WebView wv) {
		return getMnuItm("Refresh", 'R', e -> wv.getEngine().reload());
	}
	
	/**
	 * Create Exit menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmExit() {
		return getMnuItm("Exit", 'E', e -> Platform.exit());
	}
	
	/**
	 * Create About menu item, assign shortcut key and bind event handler
	 * @return	new menu item 
	 */
	private static MenuItem getMnuItmAbout() {
		return getMnuItm("About", 'I', e -> showAlert(AlertType.INFORMATION, DEVELOPER_INFO, "About", null));
	}
	
	/**
	 * Handler method for Add BookMark menu item
	 * @param wv	the WebView object of WebPage
	 */
	private static void addBookMarkHandler(WebView wv) {
		String address = wv.getEngine().getLocation();
		if (!listBookMark.contains(address)) {
			listBookMark.add(address);	
			mnuBookMarks.getItems().add(createBookMarkMenuItem(address, wv));
		}
	}

	/**
	 * Handler method for Toggle Address Bar menu item
	 */
	private static void toggleAddressBarHandler() {
		if (topPane.getChildren().contains(addressBar)) {
			topPane.getChildren().remove(addressBar);
		}
		else {
			topPane.getChildren().add(addressBar);
		}	
	}
	
	/**
	 * Utility method to show an alert box
	 * @param type			Alert type
	 * @param content		Message inside the alert box
	 * @param title			Title of the box
	 * @param header		Header of the box
	 */
	private static void showAlert(AlertType type, String content, String title, String header) {
		/* 
		 * JavaFX Dialogs Tutorial by code.makery
		 * Retrieved from: http://code.makery.ch/blog/javafx-dialogs-official/
		 */
		Alert info = new Alert(type, content);
		info.setTitle(title);
		info.setHeaderText(header);
		info.showAndWait();
	}
	
	
	/**
	 * Create list menu item. Each item is a bookmark
	 * @param wv 	the WebView object of WebPage
	 * @return		list of bookmark items
	 */
	private static ArrayList<MenuItem> getListMnuItmBookMark(WebView wv) {
		ArrayList<MenuItem> mnuItmListBookMark = new ArrayList<MenuItem>();
		for (String address : listBookMark) {
			mnuItmListBookMark.add(createBookMarkMenuItem(address, wv));
		}	
		return mnuItmListBookMark;
	}
	
	/**
	 * Create a new bookmark menu item and bind event handler to it
	 * @param address	url of the bookmark item
	 * @param wv		the WebView object of WebPage
	 * @return			a new bookmark menu item
	 */
	private static MenuItem createBookMarkMenuItem(String address, WebView wv) {
		/*
		 * StackOverflow - JavaFX MenuItem does not react on MouseEvent.CLICKED
		 * Retrieved from: https://stackoverflow.com/questions/37260118/javafx-menuitem-does-not-react-on-mouseevent-clicked
		 * 
		 * Using JavaFX UI Controls
		 * Retrieved from: https://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
		 */
		Text txt = new Text(address);
		CustomMenuItem item = new CustomMenuItem(txt, false);
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteItem = new MenuItem("Delete");
		contextMenu.getItems().add(deleteItem);

		item.getContent().setOnMouseClicked(e -> {
			// left-click
			if (e.getButton() == MouseButton.PRIMARY) {
				loadURL(wv, address);
				mnuBookMarks.hide();
			}
			// right-click
			if (e.getButton() == MouseButton.SECONDARY) {
				deleteItem.setOnAction(ee -> {
					mnuBookMarks.getItems().remove(item);
					listBookMark.remove(address);
				});
				
				contextMenu.show(txt, e.getScreenX(), e.getScreenY());
			}
		});
		
		return item;
	}
}
