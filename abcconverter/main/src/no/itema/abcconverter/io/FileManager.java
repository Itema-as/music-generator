package no.itema.abcconverter.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jih on 16/09/16.
 */
public class FileManager {

    public static void saveAwesomeFile(String filename, String text) throws IOException {
        // Files.newBufferedWriter() uses UTF-8 encoding by default
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
            writer.write(text);
        }
    }
}
