/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.swing.JProgressBar;

/**
 *
 * @author lucas
 */
public class FileParser {

    // Media mediaBuffer
    Media mediaBuffer;
    // File writter
    FileWriter writer;
    // File configuration params inside the file
    HashMap<String, String> fileParams = new HashMap<>();
    // Progress bar
    JProgressBar progressBar = null;
    // Current file size and reading progress
    long currentProgress = 0;
    long fileSize = 0;

    public FileParser(JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public FileParser() {
    }

    /**
     * Method made for checking the file permissions, preparing a new file and
     * count the ammount of lines that a file has
     *
     * @param path The full path of the file (including name)
     * @param write Checks the relevant info of the file to be able to write on
     * it
     * @param create Creates a file if it don't exists (works only if write is
     * true)
     * @return Returns the ammount of lines of the file if write is false, if
     * write is true then return 0, return -1 if an error occoured regardless of
     * the value of the write variable
     * @throws java.io.IOException
     */
    public static long checkFiles(String path, boolean write, boolean create) throws IOException {
        File file = new File(path);

        if (write) {
            if (file.exists() && !file.canWrite()) {
                // If can't write return error
                throw new IOException("Output file exists and can't be written!");
            } else if (create && !file.exists()) {
                // If don't exists create when passed as param
                file.createNewFile();
            }
        } else if (file.exists() && file.canRead()) {
            // Count the ammount of lines if the file will be read
            return file.length();
        }
        return 0;
    }

    /**
     * Method made for checking the file permissions and count the ammount of
     * lines that a file has
     *
     * @param path The full path of the file (including name)
     * @return Returns the ammount of lines of the file and return -1 if an
     * error occoured
     * @throws java.io.IOException
     */
    public static long checkFiles(String path) throws IOException {
        return FileParser.checkFiles(path, false, false);
    }

    /**
     * Verify a line for a file param and if its true, parse it into the file
     * params variable
     *
     * @param line
     */
    public void checkFileParam(String line) {
        // Checks for a valid file metadata
        Pattern pattern = Pattern.compile("#(.+)[ ]*:[ ]*(.*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            this.fileParams.put(matcher.group(1), matcher.group(2));
        }
    }

    /**
     * Method that receives a input file path and output file path, the input
     * file is read and interpreted converting its media to a xml format in the
     * output file.
     *
     * @param inputPath Path of the file that will be interpreted
     * @param outputPath Path of the file with the resulting xml data
     * @return Returns 0 for successful conversion and 1 otherwise
     */
    public int parseFile(String inputPath, String outputPath) {
        try {
            this.fileSize = FileParser.checkFiles(inputPath);
            FileParser.checkFiles(outputPath, true, true);
        } catch (IOException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

        // If has progress bar setup it
        if (this.progressBar != null) {
            this.currentProgress = 0;
            this.progressBar.setMinimum(0);
            this.progressBar.setValue(0);
            this.progressBar.setMaximum(100);
        }

        try {
            File output = new File(outputPath);
            writer = new FileWriter(output);
        } catch (IOException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

        try ( Stream<String> stream = Files.lines(Paths.get(inputPath))) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<medias>");
            stream.forEach(this::parseLine);
            writer.write("</medias>");
            writer.close();
            stream.close();
            if (this.progressBar != null) {
                this.progressBar.setValue(100);
            }
        } catch (IOException ex) {
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

        return 0;
    }

    /**
     * Method used to parse each line of the input file, it uses the Media class
     * to check the type of line (media, altName or invalid) and creates a media
     * when the line represents a media and adds alternative names for those
     * media when they are followed by altName lines, otherwise it tries to
     * write what it stored on the buffer.
     *
     * @param line The line read from the input file
     */
    public void parseLine(String line) {
        // If has progress bar, increase its value by one line
        if (this.progressBar != null) {
            this.currentProgress += line.length();
            this.progressBar.setValue((int) (((float) this.currentProgress / this.fileSize) * 100));
        }

        switch (Media.checkLine(line)) {
            case 1:
                writeBuffer();
                mediaBuffer = Media.parse(line, this.fileParams);
                break;
            case 2:
                if (mediaBuffer != null) {
                    mediaBuffer.addAltName(line);
                }
                break;
            default:
                this.checkFileParam(line);
                this.writeBuffer();
        }
    }

    /**
     * Method that writes the contents of the media buffer on the output file in
     * xml format using the Media class toString() method
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
            Logger.getLogger(FileParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
