package no.itema.abcconverter.model;

import java.util.List;

/**
 * Created by jih on 14/09/16.
 */
public class ABCFile {

    private List<String> lines;
    private String length;
    private String key;
    private String metronome;

    public ABCFile(List<String> lines) {
        length = "1/16";
        key = "C";
        metronome = "2/4";
        this.lines = lines;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getMetronome() {
        return metronome;
    }

    public void setMetronome(String metronome) {
        this.metronome = metronome;
    }

    public List<String> getLines() {
        return lines;
    }
}