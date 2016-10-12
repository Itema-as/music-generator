package no.itema.abcconverter.model;

/**
 * Created by robert on 12/10/2016.
 */
public class Header {

    private String x;
    private String t;
    private String m;
    private String l;
    private String k;

    public Header(String x, String t, String m, String k, String l) {
        this.x = x;
        this.t = t;
        this.m = m;
        this.k = k;
        this.l = l;
    }

    public String getReferenceNumber() {
        return this.x;
    }

    public String getComposer() {
        return t;
    }

    public String getMetronome() {
        return m;
    }

    public String getKey() {
        return k;
    }

    public String getLength() {
        return l;
    }
}
