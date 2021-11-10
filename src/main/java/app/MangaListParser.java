/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Main class of the program
 * @author lucas
 */
public class MangaListParser {

    // Paths
    static String testInputPath = "L:\\Documents\\Projects\\NetBeansProjects\\MangaListParser\\files\\input.txt";
    static String testoutputPath = "L:\\Documents\\Projects\\NetBeansProjects\\MangaListParser\\files\\output.txt";
    // Media mediaBuffer
    Media mediaBuffer;
    // File writter
    FileWriter writer;

    public static void main(String[] args) {
        MangaListParser main = new MangaListParser();
        System.out.println("RETURNED " + main.parseFile(testInputPath, testoutputPath));
    }

    /**
     * Method made for checking the file permissions, preparing a new file and
     * count the ammount of lines that a file has
     *
     * @param path The full path of the file (including name)
     * @param write Checks the relevant info of the file to be able to write on
     * it
     * @param create Creates a file if it don't exists (works only if write is true)
     * @return Returns the ammount of lines of the file if write is false, if write
     * is true then return 0, return -1 if an error occoured regardless of the value
     * of the write variable
     */
    public static int checkFiles(String path, boolean write, boolean create) {
        try {
            File file = new File(path);

            if (write) {
                if (file.exists() && !file.canWrite()) {
                    // If can't write return error
                    return -1;
                } else if (create && !file.exists()) {
                    // If don't exists create when passed as param
                    file.createNewFile();
                }
                return 0;
            } else if(file.canRead()) {
                // Count the ammount of lines if the file will be read
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                byte[] c = new byte[1024];
                int count = 0;
                int readChars = 0;
                boolean endsWithoutNewLine = false;
                while ((readChars = is.read(c)) != -1) {
                    for (int i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                        }
                    }
                    endsWithoutNewLine = (c[readChars - 1] != '\n');
                }
                if (endsWithoutNewLine) {
                    ++count;
                }
                return count;
            }
        } catch (IOException ex) {
            Logger.getLogger(MangaListParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    /**
     * Method made for checking the file permissions and count the ammount 
     * of lines that a file has
     *
     * @param path The full path of the file (including name)
     * @return Returns the ammount of lines of the file and return -1 if an error occoured
     */
    public static int checkFiles(String path) {
        return MangaListParser.checkFiles(path, false, false);
    }

    /**
     * Method that receives a input file path and output file path, the
     * input file is read and interpreted converting its media to a xml
     * format in the output file.
     * @param inputPath Path of the file that will be interpreted
     * @param outputPath Path of the file with the resulting xml data
     * @return Returns 0 for successful conversion and 1 otherwise
     */
    public int parseFile(String inputPath, String outputPath) {
        int inputSize = MangaListParser.checkFiles(inputPath);
        int outputCheck = MangaListParser.checkFiles(outputPath, true, true);

        if(inputSize == -1 || outputCheck == -1) {
            return 1;
        }        
        
        try {
            File output = new File(outputPath);
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

    /**
     * Method used to parse each line of the input file, it uses the Media
     * class to check the type of line (media, altName or invalid) and creates
     * a media when the line represents a media and adds alternative names for
     * those media when they are followed by altName lines, otherwise it tries to
     * write what it stored on the buffer.
     * @param line The line read from the input file
     */
    public void parseLine(String line) {
        switch (Media.checkLine(line)) {
            case 1:
                writeBuffer();
                mediaBuffer = Media.parse(line);
                break;
            case 2:
                if (mediaBuffer != null) {
                    mediaBuffer.addAltName(line);
                }
                break;
            default:
                writeBuffer();
        }
    }

    /**
     * Method that writes the contents of the media buffer on the output
     * file in xml format using the Media class toString() method
     */
    public void writeBuffer() {
        if (mediaBuffer == null) {
            return;
        }
        // Write the parsed lines        
        try {
            writer.write(mediaBuffer.toString() + '\n');
            mediaBuffer = null;
        } catch (IOException ex) {
            Logger.getLogger(MangaListParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
