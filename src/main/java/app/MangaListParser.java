/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author lucas
 */
public class MangaListParser {

    // Paths
    String inputPath = "L:\\Documents\\Projects\\NetBeansProjects\\MangaListParser\\files\\input.txt";
    String outputPath = "L:\\Documents\\Projects\\NetBeansProjects\\MangaListParser\\files\\output.txt";
    // Media mediaBuffer
    Media mediaBuffer;
    // File writter
    FileWriter writer;

    public static void main(String[] args) {
        MangaListParser main = new MangaListParser();
        System.out.println("RETURNED " + main.parseFile());
    }

    public int parseFile() {
        try {
            File output = new File(outputPath);
            if (!output.exists()) {
                output.createNewFile();
            }
            writer = new FileWriter(output);
        } catch (IOException ex) {
            Logger.getLogger(MangaListParser.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

        try ( Stream<String> stream = Files.lines(Paths.get(inputPath))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<medias>");
            stream.forEach(this::parseLine);
            writer.write("</medias>");
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(MangaListParser.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

        return 0;
    }

    public void parseLine(String line) {
        switch (Media.checkLine(line)) {
            case 1:
                writeBuffer();
                mediaBuffer = Media.parse(line);
                break;
            case 2:
                if(mediaBuffer != null)
                    mediaBuffer.addAltName(line);
                break;
            default:
                writeBuffer();
        }
    }

    public void writeBuffer() {
        if (mediaBuffer == null) 
            return;
        // Write the parsed lines        
        try {
            writer.write(mediaBuffer.toString() + '\n');
            mediaBuffer = null;
        } catch (IOException ex) {
            Logger.getLogger(MangaListParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
