/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.FileParser;
import view.FileView;

/**
 * Main class of the program
 * @author lucas
 */
public class Main {

    // Paths
//    static String testInputPath = "L:\\Documents\\Projects\\NetBeansProjects\\media-parser\\files\\input.txt";
//    static String testoutputPath = "L:\\Documents\\Projects\\NetBeansProjects\\media-parser\\files\\output.txt";

    public static void main(String[] args) {
        FileParser fileParser = new FileParser();
        FileView view = new FileView();
    }
}
