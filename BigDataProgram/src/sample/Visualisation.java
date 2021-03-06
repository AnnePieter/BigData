package sample;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.geom.Point2D;
import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Robert on 3-2-2017.
 */
public class Visualisation {

    private Controller controller;
    private Main main;

    private GraphicsContext gc;
    private Map<String,Point2D.Double> coordsListWorld;
    private Map<String,Point2D.Double> coordsListEurope;

    private File worldCSV = new File("src\\resources\\countries.csv");
    private Image imgWorld = new Image("file:///" + System.getProperty("user.dir") + "\\src\\resources\\WorldMap.jpg");

    private File europeCSV = new File("src\\resources\\Europe.csv");
    private Image imgEurope = new Image("file:///" + System.getProperty("user.dir") + "\\src\\resources\\Europe.jpg");

    private String[] europe = {"Albania","Armenia","Austria","Azerbaijan","Belarus","Belgium","Bosnia and Herzegovina","Bulgaria","Croatia","Cyprus","Czech Republic","Denmark","Estonia",
            "Finland","France","Georgia","Germany","Greece","Hungary","Ireland","Italy","Kazakhstan","Kosovo","Latvia","Lithuania","Luxembourg","Macedonia",
            "Moldova","Netherlands","Norway","Poland","Portugal","Romania","Russia","Serbia","Slovakia","Slovenia","Spain","Sweden","Switzerland","Turkey","Ukraine","UK"};

    public Visualisation(){
        this.controller = Controller.GetInstance();
        this.main = Main.GetInstance();

        //Set coords to match our image files
        try {SetCoordsToImage(); } catch (Exception e) { controller.UpdateStatusLabel(e.getMessage()); }
    }

    /** Method that displays the selected visualisation question */
    public void ShowVisualisation(String question){
        try {
            Group root = new Group();
            final Canvas canvas = new Canvas(2000,1500);
            root.getChildren().add(canvas);

            this.gc = canvas.getGraphicsContext2D();

            //Create scene
            Scene scene = new Scene(root, 400,400);
            scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
            Stage visualisationStage = new Stage();
            visualisationStage.setFullScreen(true);
            visualisationStage.setTitle("Visualisation");
            visualisationStage.setScene(scene);

            ResetGraphicsContext();

            switch (question){
                case "ActeursEuropa": Question_ActorCountEurope(); break;
                case "ImmigratieNederlanders": Question_ActorImmigration(); break;
                default: break;
            }

            visualisationStage.show();
        }
        catch (Exception e) { controller.UpdateStatusLabel(e.getMessage()); }
    }

    /** Method that clears the visualisation window */
    public void ResetGraphicsContext() {
        gc.clearRect(0, 0, 1920, 1080);
    }

    /** Method that visualises the actor immigration from the specified country*/
    public void Question_ActorImmigration() {
        gc.drawImage(imgWorld, 0, 0, 1920, 1080);
        String actorCountry = controller.txtF_ActorImmigrationCountry.getText();

        // Draw lines
        main.GetQueryTool().ExecuteQuery("SELECT DISTINCT country FROM actorscsv a INNER JOIN biographiescsv b ON a.actor=b.actor INNER JOIN countriescsv c ON a.movie_or_series=c.movie_or_series WHERE birth_country NOT LIKE country AND birth_country LIKE '%" + actorCountry + "%' UNION ALL SELECT DISTINCT country FROM actressescsv d INNER JOIN biographiescsv b ON d.actor=b.actor INNER JOIN countriescsv c ON d.movie_or_series=c.movie_or_series WHERE birth_country NOT LIKE country AND birth_country LIKE '%" + actorCountry +"%'");
        ResultSet m_ResultSet = main.GetQueryTool().GetResultSet();
        for (int x = 0; x < 12; x++) {
            try {
                m_ResultSet.next();
                gc.setStroke(Color.BLACK);

                gc.strokeLine(coordsListWorld.get(actorCountry).getX(), coordsListWorld.get(actorCountry).getY(), coordsListWorld.get(m_ResultSet.getString("country")).getX(), coordsListWorld.get(m_ResultSet.getString("country")).getY());
            } catch (Exception e) { controller.UpdateStatusLabel(e.getMessage()); }
        }
    }

    /** Method that visualises the actor count in Europe*/
    public void Question_ActorCountEurope() {
        gc.drawImage(imgEurope, 0, 0, 1920, 1080);

        for (int x = 0; x < europe.length; x++) {
            String euCountry = europe[x];
            main.GetQueryTool().ExecuteQuery("SELECT COUNT(*) AS actorCount FROM BiographiesCSV WHERE birth_country LIKE '" + euCountry + "'");
            ResultSet m_ResultSet = main.GetQueryTool().GetResultSet();
            try {
                m_ResultSet.next();

                gc.setStroke(Color.BLACK);
                gc.strokeText("" + m_ResultSet.getInt("actorCount"), coordsListEurope.get(euCountry).getX(), coordsListEurope.get(euCountry).getY());
            } catch (Exception e) { controller.UpdateStatusLabel(e.getMessage()); }
        }
    }

    /** Method that */
    public void SetCoordsToImage() throws Exception {
        //World
        Scanner scanner = new Scanner(worldCSV);
        coordsListWorld = new HashMap<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] lineparts = line.split(",");

            double xCoord = Double.parseDouble(lineparts[4]);
            double yCoord = Double.parseDouble(lineparts[3]);

            if (yCoord > 920)
                yCoord -= 110;
            else if (yCoord < 920 && yCoord > 720)
                yCoord -= 100;
            else if (yCoord < 720 && yCoord > 540)
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

            coordsListEurope.put(lineparts[0], new Point2D.Double(xCoord, yCoord));
        }
    }
}