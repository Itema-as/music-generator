package no.itema.abcconverter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class ABCFile {

    private List<String> lines;
    private Header header;

    public ABCFile(List<String> lines) {
        this.lines = lines;
    }

    public ABCFile(String fileContents) {
        lines = new ArrayList<>();
        String referenceNumber = "";
        String composer = "";
        String metronome = "";
        String key = "";
        String length = "";
        boolean headerIsRead = false;

        String[] l = fileContents.split("\n");
        for (String line : l) {
            if (line.startsWith("X")) {
                referenceNumber = line.substring(2).trim();
            } else if (line.startsWith("T")) {
                composer = line.substring(2).trim();
            } else if (line.startsWith("M")) {
                metronome = line.substring(2).trim();
            } else if (line.startsWith("L")) {
                length = line.substring(2).trim();
            } else if (line.startsWith("K")) {
                key = line.substring(2).trim();
                headerIsRead = true;
                continue;
            }

            header = new Header(referenceNumber, composer, metronome, key, length);

            if (headerIsRead) {
                if (!line.trim().isEmpty())
                lines.add(line);
            }
        }
        //System.out.println(fileContents);
    }

    public String getLength() {
        return header.getLength();
    }

    public String getKey() {
        return header.getKey();
    }

    public String getComposer() {
        return header.getComposer();
    }

    public String getReferenceNumber() {
        return header.getReferenceNumber();
    }

    public String getMetronome() {
        return header.getMetronome();
    }

    public List<String> getLines() {
        return lines;
    }
}