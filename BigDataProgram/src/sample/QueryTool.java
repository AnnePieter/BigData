package sample;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by Robert on 3-2-2017.
 */
public class QueryTool {

    private Main main;
    private Controller controller;

    private ResultSet m_ResultSet;
    public ResultSet GetResultSet () { return this.m_ResultSet; }

    public QueryTool(){
        this.main = Main.GetInstance();
        this.controller = Controller.GetInstance();
    }

    public void ExecuteQuery(String query) {
        controller.btn_ExecuteQuery.setDisable(true);

        try {
            this.m_ResultSet = main.GetStatement().executeQuery(query);

        } catch (Exception e) { controller.UpdateStatusLabel("Make sure you are connected to the database!"); }

        controller.btn_ExecuteQuery.setDisable(false);
    }

    public void ExecuteTableFillQuery(String query){
        controller.btn_ExecuteQuery.setDisable(true);

        try {
            this.m_ResultSet = main.GetStatement().executeQuery(query);
            FillResultsTable();

        } catch (Exception e) { controller.UpdateStatusLabel("Make sure you are connected to the database!"); }

        controller.btn_ExecuteQuery.setDisable(false);
    }

    public void FillResultsTable(){
        try{
            ObservableList<ObservableList> data = FXCollections.observableArrayList();

            // clear tableview
            Platform.runLater( () -> controller.tblV_UserQueryResult.getColumns().clear());

            // create columns
            for(int i = 0; i < m_ResultSet.getMetaData().getColumnCount(); i++) {
                final int j = i;
                    TableColumn col = new TableColumn(m_ResultSet.getMetaData().getColumnName(i + 1));
                    col.setSortable(false);

                    // Get the cell value for the column
                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            // Check for null values to avoid tableView errors
                            if (param.getValue().get(j) != null)
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            else
                                return new SimpleStringProperty("NULL");
                        }
                    });
                    Platform.runLater(() -> controller.tblV_UserQueryResult.getColumns().addAll(col));
            }

            // fill tableview
            while(m_ResultSet.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i = 1; i <= m_ResultSet.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(m_ResultSet.getString(i));
                }
                data.add(row);

            }
            controller.tblV_UserQueryResult.setItems(data);
            Platform.runLater( () -> controller.tblV_UserQueryResult.refresh());

        } catch(Exception e){ e.printStackTrace(); controller.UpdateStatusLabel("Make sure you are connected to the database!"); }
    }
}
