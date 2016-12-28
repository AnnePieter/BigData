package com.company;

import javax.swing.*;
import java.io.IOException;

public class Main {


    static String sourceFile;
    static JFileChooser chooser = new JFileChooser();
    static boolean quit = false;

    public static void main(String... aArgs) throws IOException {
        while (!quit){
            ListToCSVParser text = new ListToCSVParser();

            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                sourceFile = chooser.getSelectedFile().toString();
                sourceFile = sourceFile.replace("\\", "/");
                ListToCSVParser.log(sourceFile);
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                quit = true;
                break;
            }

            //final String sourceFile = chooser.getSelectedFile().toString();
            final String outputFile = sourceFile + ".csv";
            ListToCSVParser.log(outputFile);

            //parse the sourceFile to the outputFile
            text.ParseFile(sourceFile, outputFile);
    }
}
}