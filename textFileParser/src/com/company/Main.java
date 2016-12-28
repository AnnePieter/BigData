package com.company;

import java.io.IOException;

public class Main {

    public static void main(String... aArgs) throws IOException {
        ListToCSVParser text = new ListToCSVParser();

        final String sourceFile = "C://users//annepieter//Downloads//countries.list//countries.list";
        final String outputFile = "C://users//annepieter//Downloads//countries.list//countries.list.csv";

        //parse the sourceFile to the outputFile
        text.ParseFile(sourceFile, outputFile);
    }
}
