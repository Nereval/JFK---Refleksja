package reflection;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import Callable.Description;
import Callable.ICallable;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;




public class Main extends Application {

	ArrayList<ICallable> callableInst = new ArrayList<>();
	ICallable chosenClass;
	Description chosenDesc;
	ArrayList<Description> descList = new ArrayList<>();
    ListView<String> jarFilePanel;
    ListView<String> resultsPanel;
    ArrayList<String> listaKlas = new ArrayList<>();
    
    
    private static int WINDOW_HEIGHT = 600;
    private static int WINDOW_WIDTH = 475;
    private static int BUTTON_HEIGHT = 25;
    private static int BUTTON_WIDTH = 100;
    

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("JFK - Refleksja");

        Group root = new Group();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        TextArea desc = new TextArea();
        desc.setEditable(false);
        desc.setPrefSize(200, 200);
        desc.setTranslateX(25);
        desc.setTranslateY(380);
        desc.setWrapText(true);
        
        TextField arg1 = new TextField();
        arg1.setPrefSize(85, 20);
        arg1.setEditable(true);

        TextField arg2 = new TextField();
        arg2.setPrefSize(85, 20);
        arg2.setEditable(true);
        
        TextField resultArg = new TextField();
        resultArg.setPrefSize(195, 20);
        resultArg.setTranslateX(250);
        resultArg.setTranslateY(425);
        resultArg.setEditable(false);

        HBox box = new HBox();
        box.setSpacing(25);
        box.setTranslateX(250);
        box.setTranslateY(380);
        box.getChildren().addAll(arg1,arg2);
        
        Button executeButton = new Button("Wykonaj");
        executeButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        executeButton.setOnAction(event -> {
        	String result;
        	String str0 = null;
        	String str1 = null;
        	str0 = arg1.getText();
        	str1 = arg2.getText();
        	
        	if(resultsPanel.getSelectionModel().isEmpty() == true || resultsPanel.getSelectionModel().getSelectedItem().isEmpty() == true) { 
        		result = "Wybierz plik jar oraz klasê!";
        	} else if(str0.length() == 0 || str1.length() == 0) {
        		result = "Stop! Wype³nij pola tekstowe!";
        	} else {
        	result = chosenClass.Call(str0, str1);
        	}
        	resultArg.setText(result);    	
        });
        
        Button clearButton = new Button ("Wyczyœæ");
        clearButton.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        clearButton.setOnAction(event -> {
        	resultsPanel.getItems().clear();
            jarFilePanel.getItems().clear();
            listaKlas.clear();
            desc.clear();
            arg1.clear();
            arg2.clear();
            resultArg.clear();
        });
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
        		new FileChooser.ExtensionFilter("All files", "*.*"),
        		new FileChooser.ExtensionFilter("JAR", "*.jar")
        		);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Button fileExplorer = new Button("Wybierz plik");
        fileExplorer.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        fileExplorer.setOnAction(event -> {
        
            
            resultsPanel.getItems().clear();
            jarFilePanel.getItems().clear();
            listaKlas.clear();
            desc.clear();
            arg1.clear();
            arg2.clear();
            resultArg.clear();
            
        	
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file == null) {
                return;
            }

            if (!file.getPath().endsWith(".jar") && file.isFile()){
                System.out.println("Plik musi byc typu jar!");
            	resultArg.setText("Plik musi byc typu jar!");
                return;
            }
            scanPath(file);
            
        });
       

        jarFilePanel = new ListView<String>();
        jarFilePanel.setPrefSize(200, 300);
        jarFilePanel.setTranslateX(25);
        jarFilePanel.setTranslateY(50);
        jarFilePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        jarFilePanel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String jarPath) {

            	if(jarPath != null) {
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(jarPath);
                    Enumeration<JarEntry> entries = jarFile.entries();

                    URL[] urls = { new URL("jar:file:" + jarPath + "!/") };
                    URLClassLoader cl = URLClassLoader.newInstance(urls);

                    while (entries.hasMoreElements()) {
                        JarEntry je = entries.nextElement();
                        if(je.isDirectory() || !je.getName().endsWith(".class"))
                        {
                            continue;
                        }

                        String className = je.getName().substring(0, je.getName().length()-6);
                        className = className.replace('/', '.');

                        try
                        {
                            Class<?> c = cl.loadClass(className);
                                        
                            if (!ICallable.class.isAssignableFrom(c))
                                throw new Exception("Class " + className + " does not implement the contract.");
                            
                            if (!c.isAnnotationPresent(Description.class))
                                throw new Exception("Description is not present in " + className + "class.");

                             Description description = (Description) c.getAnnotation(Description.class);
                             descList.add(description);
                             
       
                            ICallable callable = (ICallable) c.newInstance();
                            if (null == callable)
                                throw new Exception();
                            callableInst.add(callable);
                            listaKlas.add(className);
                            
                        }
                        catch (ClassNotFoundException exp) {
                            continue;               
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                resultsPanel.getItems().setAll(listaKlas);
            }
            }
        });
        
        

        resultsPanel = new ListView<String>();
        resultsPanel.setPrefSize(200, 300);
        resultsPanel.setTranslateX(250);
        resultsPanel.setTranslateY(50);
        resultsPanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultsPanel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String className) {
            	int count = 0;
            	for(String temp : listaKlas) {
            		
            		if(temp == className) {
            			chosenClass = callableInst.get(count);
            			chosenDesc = descList.get(count);
            			desc.setText(chosenDesc.description());
            			count = 0;
            			break;
            		}
            		count++;
            		
            	}
            }
        });

        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(new Separator(), fileExplorer, new Separator(), executeButton, new Separator(), clearButton, new Separator());
        toolBar.setPrefWidth(WINDOW_WIDTH);
        
        
        root.getChildren().addAll(toolBar, jarFilePanel, resultsPanel, box, desc, resultArg);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


    private void scanPath(final File topDirectory) {
        String treeRoot;
        try {
            treeRoot = topDirectory.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!topDirectory.exists()) {
            throw new RuntimeException("Path: '" + treeRoot + "' does not exist");
        }

                try {
                    ArrayList<String> jarNameList = new ArrayList<String>();
                    if (topDirectory.isFile()) {
                        jarNameList.add(topDirectory.getCanonicalPath());
                       
                    } else {
                        scanDirectory(topDirectory, jarNameList);

                    }
                    Collections.sort(jarNameList);
                    jarFilePanel.getItems().setAll(jarNameList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
      

       

    private void scanDirectory(File f, ArrayList<String> jarNameList) throws IOException {
        File[] children = f.listFiles();
        

        for (File aChildren : children) {

            boolean javaArchive = false;
            
            if (Configuration.getProperty("zip.extensions") == null) {
                Configuration.setProperty("zip.extensions", "jar,zip,war,ear,rar");
            }
            String[] extensions = Configuration.getProperty("zip.extensions").split(",");

            for (String extension : extensions) {
                if (aChildren.isFile() && aChildren.getName().endsWith("." + extension)) {
                    javaArchive = true;
                }
            }

            if (javaArchive) {
                String name = aChildren.getCanonicalPath();
                jarNameList.add(name);
               } else if (aChildren.isDirectory()) {
                scanDirectory(aChildren, jarNameList);
            } else {
            	//ignore
            }
        }
    }
}
