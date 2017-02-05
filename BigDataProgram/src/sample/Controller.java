package sample;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable{
    private static Controller instance;
    public static Controller GetInstance(){
        return instance;
    }

    private Main main;

    private ArrayList<Thread> parserThreads = new ArrayList<>();

    @FXML
    Label lbl_statusMessage;
    @FXML
    ListView<String> listV_StatusMessages;

    // Parser related variables
    @FXML
    TextField txtF_FilePath;
    @FXML
    TextField txtF_FolderPath;
    @FXML
    Button btn_ParseFiles;
    @FXML
    ListView<String> listV_SelectedFiles;

    // Database related variables
    @FXML
    Button btn_ConnectToDatabase;
    @FXML
    TextField txtF_DatabaseAddress;
    @FXML
    TextField txtF_DatabaseUserName;
    @FXML
    TextField txtF_DatabaseUserPassword;

    //Query Tool related variables
    @FXML
    Button btn_ExecuteQuery;
    @FXML
    Button btn_QueryToolRefreshTable;
    @FXML
    TextArea txtA_UserQuery;
    @FXML
    TableView<ObservableList> tblV_UserQueryResult;

    // Visualisation related variables
    @FXML
    Button btn_ShowVisualisation;


    public void initialize(URL location, ResourceBundle resources){
        instance = this;
        main = Main.GetInstance();
    }


    /** Method for updating GUI Status label and Status Messages listView (for showing errors and such) */
    public void UpdateStatusLabel(String text) {
        Platform.runLater( () -> {
            lbl_statusMessage.setText("Status: " + text);

            Calendar cal = Calendar.getInstance();
            listV_StatusMessages.getItems().add((new SimpleDateFormat("HH:mm:ss")).format(cal.getTime()) + " - " + text);
            listV_StatusMessages.scrollTo(listV_StatusMessages.getItems().size() - 1);
        });
    }

    //
    // Methods related to parsing files
    //

    /** Button method for selecting a single file */
    public void btnPress_SelectFile(ActionEvent e){
        FileChooser chooser = new FileChooser();
        File selectedFile = chooser.showOpenDialog(main.GetStage());
        if (selectedFile != null) {
            String sourceFile = selectedFile.getPath();

            // Update variables in our Main class
            main.GetSourceFiles().add(sourceFile);

            //Update some GUI stuff
            listV_SelectedFiles.setItems(main.GetSourceFiles());
            txtF_FilePath.setText(sourceFile);
        }
    }

    /** Button method for selecting all files in a folder */
    public void btnPress_SelectFolder(ActionEvent e){
        DirectoryChooser chooser = new DirectoryChooser();
        File selectedFolder = chooser.showDialog(main.GetStage());
        if (selectedFolder != null){
            String sourceFolder = selectedFolder.getPath();

            // Filling our files list with (ONLY) .list files
            File folder = new File(sourceFolder);
            File[] listOfFiles = folder.listFiles();
            for (int i = 0; i < listOfFiles.length; i++){
                if (listOfFiles[i].isFile() && listOfFiles[i].getName().endsWith(".list")){
                    main.GetSourceFiles().add(listOfFiles[i].getPath());
                }
            }
            // Update some GUI stuff
            listV_SelectedFiles.setItems(main.GetSourceFiles());
            txtF_FolderPath.setText(sourceFolder);
        }
    }

    /** Button method for parsing all files in a folder */
    public void btnPress_ParseFiles(ActionEvent e){
        if (main.GetSourceFiles().size() != 0){

            // Remove duplicates by creating a HashSet
            Set<String> hashSet = new HashSet<>(main.GetSourceFiles());
            main.GetSourceFiles().clear();
            main.GetSourceFiles().addAll(hashSet);
            listV_SelectedFiles.setItems(main.GetSourceFiles());

            UpdateStatusLabel("Removed possible duplicates before parsing");

            // Finally parse the files
            ParseFilesOnThread(main.GetSourceFiles());
        }
        else
            UpdateStatusLabel("Select a folder first!");
    }

    /** Method for parsing multiple files on a new thread */
    private void ParseFilesOnThread(ObservableList<String> sourceFiles){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (sourceFiles != null) {
                        for (int i = 0; i < sourceFiles.size(); i++) {
                            main.GetParser().ParseFile(sourceFiles.get(i));
                        }
                    }
                }
                catch(Exception x){UpdateStatusLabel(x.getMessage()); }
            }
        }); t.start();
    }

    //
    // Methods related to executing queries and database connections
    //

    /** Button method that tries to create a connection with the specified database */
    public void btnPress_ConnectToDatabase(ActionEvent e) {
        if (btn_ConnectToDatabase.getText().equals("Connect")) {
            try {
                main.CreateDatabaseConnection(txtF_DatabaseAddress.getText(), txtF_DatabaseUserName.getText(), txtF_DatabaseUserPassword.getText());

                // Enable connection related buttons
                btn_ExecuteQuery.setDisable(false);
                btn_ShowVisualisation.setDisable(false);

                btn_ConnectToDatabase.setText("Disconnect");
                UpdateStatusLabel("Successfully connected to the database\t-\tAddress: " + txtF_DatabaseAddress.getText() + "\t-\tUser: " + txtF_DatabaseUserPassword.getText() + "");
            } catch (Exception x) { UpdateStatusLabel(x.getMessage());}
        } else {
            try {
                main.CloseDatabaseConnection();

                // Disable connection related buttons to avoid errors
                btn_ExecuteQuery.setDisable(true);
                btn_ShowVisualisation.setDisable(true);

                btn_ConnectToDatabase.setText("Connect");
                UpdateStatusLabel("Successfully disconnected from the database\t-\tAddress: " + txtF_DatabaseAddress.getText());
            } catch (Exception x) { UpdateStatusLabel(x.getMessage());}
        }
    }

    public void btnPress_RefreshQueryToolTableView(ActionEvent e){
        tblV_UserQueryResult.getColumns().clear();
        tblV_UserQueryResult.setItems(FXCollections.observableArrayList());
        tblV_UserQueryResult.refresh();
    }

    /** Button method that executes a query (result is saved in QueryTool class)*/
    public void btnPress_ExecuteUserQuery(ActionEvent e){
        if (!txtA_UserQuery.getText().isEmpty()){
            ExecuteUserQueryOnThread(txtA_UserQuery.getText());
        }
        else
            UpdateStatusLabel("Make a valid query first!");
    }

    /** Method for executing a user query on a new thread */
    private void ExecuteUserQueryOnThread(String query){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    main.GetQueryTool().ExecuteUserQuery(query);
                }
                catch(Exception x){ UpdateStatusLabel(x.getMessage()); }
            }
        }); t.start();
    }

    /** Method for executing a query on a new thread */
    private void ExecuteQueryOnThread(String query){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    main.GetQueryTool().ExecuteQuery(query);
                }
                catch(Exception x){ UpdateStatusLabel(x.getMessage()); }
            }
        }); t.start();
    }

    //
    // Methods related to our visualisation
    //

    /** Method for showing the chosen visualisation */
    public void btnPress_ShowVisualisation(ActionEvent e){
        main.GetVisualisation().ShowVisualisation();
    }

}
