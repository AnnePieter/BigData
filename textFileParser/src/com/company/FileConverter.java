/**
 *  Made by Anne Pieter Boonstra & Robert Bijl
 */

package com.company;

import javax.print.DocFlavor;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileConverter {
    private JPanel panelMain;

    private JProgressBar progressBar;

    private JTextField txtField_FilePath;
    private JButton btn_Convert;
    private JButton btn_SelectFile;
    private JComboBox cBox_methods;

    private String sourceFile = "";
    private String outputFile = "";

    final static Charset ENCODING = StandardCharsets.ISO_8859_1;
    ParserMethods parserMethods = new ParserMethods();

    public FileConverter() {
        btn_Convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sourceFile = txtField_FilePath.getText();
                if(!sourceFile.isEmpty()){
                    Thread t = new Thread(){
                        public void run(){
                            try{
                                ParseFile(sourceFile, outputFile);
                            }
                            catch(Exception x){
                                x.printStackTrace();
                                JOptionPane.showMessageDialog(null, "Oops");
                            }
                        }
                    };
                    t.start();
                }else{
                    JOptionPane.showMessageDialog(null, "Select a file first");
                }
            }
        });
        btn_SelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    sourceFile = chooser.getSelectedFile().toString();
                    sourceFile = sourceFile.replace("\\", "/");
                    //final String sourceFile = chooser.getSelectedFile().toString();
                    outputFile = sourceFile + ".csv";
                    log(outputFile);
                    txtField_FilePath.setText(sourceFile);
                }
                //if (returnVal == JFileChooser.CANCEL_OPTION) {
                //}
            }
        });


        String[] methodsArray = {"Movies", "Actors", "Countries", "Locations"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(methodsArray);
        cBox_methods.setModel(model);
    }

    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("App");
        frame.setContentPane(new FileConverter().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /** Main function for converting files, makes calls to 'ParserMethods' class depending on the file that is being converted*/
    public void ParseFile(String fileToConvert, String fileToConvertTo) throws IOException {
        progressBar.setString("Initializing");
        btn_Convert.setEnabled(false);

        String method = cBox_methods.getSelectedItem().toString();

        Path sourceFile = Paths.get(fileToConvert);
        Path destinationFile = Paths.get(fileToConvertTo);

        BufferedWriter writer = Files.newBufferedWriter(destinationFile,ENCODING);

        final int totalLines = CountFileLines(fileToConvert);

        try (Scanner scanner = new Scanner(sourceFile, ENCODING.name())){
            int currentLine = 0;

            //skip file documentation
            while (scanner.hasNextLine()){
                currentLine++;
                ProgressUpdate(currentLine, totalLines);

                String line = scanner.nextLine();
                if (line.contains("LIST")){
                    scanner.nextLine(); //skip an additional line
                    break;
                }
            }

            while (scanner.hasNextLine()){
                currentLine++;
                ProgressUpdate(currentLine, totalLines);

                //process each line
                String line = scanner.nextLine();
                if (line.isEmpty())
                    continue;
                if (line.contains("-----------------------------------------------------------------------------"))
                    break;


                switch (method){
                    case "Actors": line = parserMethods.ActorsList(line); break;
                    case "Movies": line = parserMethods.MoviesList(line); break;
                    case "Countries": line = parserMethods.CountriesList(line); break;
                    case "Locations": line = parserMethods.LocationsList(line); break;
                    default: break;
                }
                /* Available parser methods, needs improvement */
                //line = parserMethods.MoviesListRegex(line, "^([\\s\\S]*)\\(([\\d{4}]*|\\?*)(?:\\/)?([\\w]*)?\\)(\\s*\\{([\\w!\\s:;\\/\\.\\-\\'\"?`_&@$%^*<>~+=\\|\\,\\(\\)]*)(\\s*\\(#([\\d]*)\\.([\\d]*)\\))?\\})?\\s*([\\d{4}]*)?(?:-)?([\\d{4}]*)?");


                if (line.isEmpty())
                    continue;

                writer.write(line);
                writer.newLine();

                //log(line);
            }
            scanner.close();
            writer.close();

            btn_Convert.setEnabled(true);
            ProgressUpdate(1,1);//Set progress bar to 100%
        }
    }

    /** Updates the progress bar with the percentage of 'currentLine / totalLines' */
    public void ProgressUpdate(final int currentLine, final int totalLines) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                double percent = (double) currentLine / (double) totalLines * 100.0;
                progressBar.setString(String.valueOf((int)percent) + "%");
                progressBar.setValue((int)percent);
            }
        });
    }

    /** Counts the number of lines in the file needed for the progress bar */
    public static int CountFileLines(String fileToCount) throws IOException {
        int count = 0;
        Path sourceFile = Paths.get(fileToCount);

        try (Scanner scanner = new Scanner(sourceFile, ENCODING.name())){
            while (scanner.hasNextLine()){
                scanner.nextLine();
                count++;
            }
            scanner.close();
        }
        return count;
    }

    /** Prints a string from a object in the console */
    public static void log(Object message){
        System.out.println(String.valueOf(message));
    }
}
