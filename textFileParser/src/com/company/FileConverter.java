package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Created by Robert on 11-1-2017.
 */
public class FileConverter {
    private JButton btn_Convert;
    private JPanel panelMain;
    private JTextField txtField_FilePath;
    private JButton btn_SelectFile;

    String sourceFile = "";
    String outputFile = "";

    public FileConverter() {
        btn_Convert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!sourceFile.isEmpty()){
                    try{
                        ListToCSVParser text = new ListToCSVParser();
                        JOptionPane.showMessageDialog(null, "Conversion Started");
                        text.ParseFile(sourceFile, outputFile);
                    }
                    catch(Exception x){
                        JOptionPane.showMessageDialog(null, "Oops");
                        x.printStackTrace();
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "Select a file first");
                }
            }
        });
        btn_SelectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ChooseFile();
            }
        });
    }

    public void ChooseFile(){
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            sourceFile = chooser.getSelectedFile().toString();
            sourceFile = sourceFile.replace("\\", "/");
            ListToCSVParser.log(sourceFile);
            //final String sourceFile = chooser.getSelectedFile().toString();
            outputFile = sourceFile + ".csv";
            ListToCSVParser.log(outputFile);
            txtField_FilePath.setText(sourceFile);
        }
        //if (returnVal == JFileChooser.CANCEL_OPTION) {
        //}

    }

    public static void main(String[] args) throws IOException{
        JFrame frame = new JFrame("App");
        frame.setContentPane(new FileConverter().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(720,480);
        frame.setVisible(true);
    }
}
