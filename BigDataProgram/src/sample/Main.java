package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main extends Application {

    // Main related variables
    private static Main instance = null;
    public static Main GetInstance(){
        if (instance == null)
            instance = new Main();
        return instance;
    }

    public Stage primaryStage;
    public Stage GetStage () { return this.primaryStage; }
    public void SetStageScene (Scene value) { primaryStage.setScene(value);}

    private Scene scene1;

    // Controller related variables
    private Controller controller;
    public Controller GetController () { return this.controller; }

    // Database connection related variables
    private Connection m_Connection;
    public Connection GetConnection () {
        return this.m_Connection;
    }
    private Statement m_Statement;
    public Statement GetStatement () {
        if (this.m_Statement == null)
            try { this.m_Statement = GetConnection().createStatement(); } catch (Exception e) { e.printStackTrace(); }
        return this.m_Statement;
    }

    // Query Tool related variables
    private QueryTool queryTool;
    public QueryTool GetQueryTool () {
        if (this.queryTool == null)
            this.queryTool = new QueryTool();
        return this.queryTool;
    }

    // Parser related variables
    private Parser parser;
    public Parser GetParser(){
        if (this.parser == null)
            this.parser = new Parser();
        return this.parser;
    }

    private ObservableList<String> sourceFiles = FXCollections.observableArrayList();
    public ObservableList<String> GetSourceFiles () { return this.sourceFiles; }
    public void SetSourceFiles (ObservableList<String> value) { this.sourceFiles = value; }

    // Visualisation related variables
    private Visualisation visualisation;
    public Visualisation GetVisualisation () {
        if (this.visualisation == null)
            this.visualisation = new Visualisation();
        return this.visualisation;
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = fxmlLoader.load();
        this.controller = fxmlLoader.getController();

        this.parser = new Parser();
        this.queryTool = new QueryTool();
        this.visualisation = new Visualisation();

        this.scene1 = new Scene(root, 1024, 768);

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Big resources - Groep 19");
        this.primaryStage.setScene(scene1);
        this.primaryStage.setResizable(false);
        this.primaryStage.show();
    }

    public void CreateDatabaseConnection(String databaseAddress, String databaseUserName, String databaseUserPassword) throws  Exception{
        Class.forName("org.postgresql.Driver");

        this.m_Connection = DriverManager.getConnection(databaseAddress, databaseUserName, databaseUserPassword);
        this.m_Statement = m_Connection.createStatement();
    }

    public void CloseDatabaseConnection() throws Exception{
        this.m_Connection.close();
        this.m_Statement.close();

    }
}
