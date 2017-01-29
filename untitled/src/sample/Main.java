package sample;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.io.File;

public class Main extends Application {

    Charset charset = StandardCharsets.ISO_8859_1;

    File worldCSV = new File("D:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/countries.csv");
    Image imgWorld = new Image("file:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/WorldMap.jpg");

    File europeCSV = new File("D:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/Europe.csv");
    Image imgEurope = new Image("file:///OneDrive/Documenten/school/Jaar2/Periode 2/Big Data/BigData/untitled/Europe.jpg");

    String[] europe = {"Albania","Armenia","Austria","Azerbaijan","Belarus","Belgium","Bosnia and Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic","Denmark","Estonia",
            "Finland","France","Georgia","Germany","Greece","Hungary","Ireland","Italy","Kazakhstan","Kosovo","Latvia","Lithuania","Luxembourg","Macedonia",
            "Moldova","Netherlands","Norway","Poland","Portugal","Romania","Russia","Serbia","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey","Ukraine","UK"};

    GraphicsContext gc;
    Map<String,Point2D.Double> coordsListWorld;
    Map<String,Point2D.Double> coordsListEurope;

    Connection m_Connection;
    Statement m_Statement;
    ResultSet m_ResultSet;

    public enum Questions { Vraag_ActeursEuropa, Vraag_ImmigratieNederlanders}
    Questions currentQuestion;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Database connection
        GetDatabaseConnection();

        Group root = new Group();
        final Canvas canvas = new Canvas(2000,1500);
        canvas.setOnMouseClicked(event -> {
            if (currentQuestion == Questions.Vraag_ActeursEuropa){
                Vraag_ImmigratieNederlanders();
                currentQuestion = Questions.Vraag_ImmigratieNederlanders;
            }
            else if (currentQuestion == Questions.Vraag_ImmigratieNederlanders){
                Vraag_ActeursEuropa();
                currentQuestion = Questions.Vraag_ActeursEuropa;
            }
        });
        root.getChildren().add(canvas);

        gc = canvas.getGraphicsContext2D();

        //Create scene
        Scene scene = new Scene(root, 400,400);
        scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        primaryStage.setFullScreen(true);
        primaryStage.setTitle("Visualisatie");
        primaryStage.setScene(scene);

        //Set coords
        SetCoordsToImage();

        //Vraag_ActeursEuropa();
        //currentQuestion = Questions.Vraag_ActeursEuropa;

        Vraag_ImmigratieNederlanders();
        currentQuestion = Questions.Vraag_ImmigratieNederlanders;

        primaryStage.show();
    }

    public void ResetGraphicsContext(){
        gc.clearRect(0,0,1920,1080);
    }

    public void Vraag_ImmigratieNederlanders(){
        ResetGraphicsContext();
        ExecuteQuery("");

        gc.drawImage(imgWorld,0,0,1920,1080);

        // Draw lines
        for(int x = 0; x < europe.length; x++) {
            String euCountry = europe[x];

            ExecuteQuery("SELECT country FROM actorscsv AS a INNER JOIN biographiescsv AS b ON a.actor=b.actor AS i INNER JOIN countriescsv AS c ON a.movie_or_series=c.movie_or_series WHERE birth_country NOT LIKE country AND birth_country LIKE 'Netherlands' UNION ALL SELECT country FROM actressescsv AS d INNER JOIN biographiescsv AS b ON d.actor=b.actor AS i INNER JOIN countriescsv AS c ON d.movie_or_series=c.movie_or_series WHERE birth_country NOT LIKE country AND birth_country LIKE 'Netherlands'");
            try{
                m_ResultSet.next();
                gc.setStroke(Color.BLACK);

                gc.strokeLine(coordsListWorld.get("Netherlands").getX(), coordsListWorld.get("Netherlands").getY(), coordsListWorld.get(m_ResultSet.getString("country")).getX(), coordsListWorld.get(m_ResultSet.getString("country")).getY());
            } catch (Exception e) { e.printStackTrace(); }

        }
    }

    public void Vraag_ActeursEuropa(){
        ResetGraphicsContext();
        gc.drawImage(imgEurope,0,0,1920,1080);

        for(int x = 0; x < europe.length; x++) {
            String euCountry = europe[x];
            ExecuteQuery("SELECT COUNT(*) AS actorCount FROM BiographiesCSV WHERE birth_country LIKE '" + euCountry + "'");
            try{
                m_ResultSet.next();

                gc.setStroke(Color.BLACK);
                gc.strokeText("" + m_ResultSet.getInt("actorCount"),coordsListEurope.get(euCountry).getX(), coordsListEurope.get(euCountry).getY());
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void GetDatabaseConnection(){
        try{
            Class.forName("org.postgresql.Driver");
            m_Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/BigData", "postgres", "admin");
            System.out.println(m_Connection);
            m_Statement = m_Connection.createStatement();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void SetCoordsToImage() throws Exception{
        //World
        Scanner scanner = new Scanner(worldCSV);
        coordsListWorld = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineparts = line.split(",");

            double xCoord = Double.parseDouble(lineparts[4]);
            double yCoord = Double.parseDouble(lineparts[3]);

            if(yCoord >920)
                yCoord -= 110;
            else if(yCoord <920 && yCoord >720)
                yCoord -= 100;
            else if(yCoord <720 && yCoord >540)
                yCoord -= 50;

            coordsListWorld.put(lineparts[2], new Point2D.Double(xCoord, yCoord));
        }

        //Europe
        scanner = new Scanner(europeCSV);
        coordsListEurope = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] lineparts = line.split(",");

                double xCoord = Double.parseDouble(lineparts[1]);
                double yCoord = Double.parseDouble(lineparts[2]);

                /*if(yCoord >920)
                    yCoord -= 110;
                else if(yCoord <920 && yCoord >720)
                    yCoord -= 100;
                else if(yCoord <720 && yCoord >540)
                    yCoord -= 50;*/

                coordsListEurope.put(lineparts[0], new Point2D.Double(xCoord, yCoord));
            }
    }

    public void ExecuteQuery(String query){
        try{
            m_ResultSet = m_Statement.executeQuery(query);

            /*
            while (m_ResultSet.next()) {
                System.out.println(m_ResultSet.getString(1) + ", " + m_ResultSet.getString(2) + ", "
                        + m_ResultSet.getString(3));
            }*/
        } catch(Exception e){
            e.printStackTrace();
        }

    }

}

