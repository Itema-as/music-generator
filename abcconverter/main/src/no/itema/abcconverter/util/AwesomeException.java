package no.itema.abcconverter.util;

/**
 * Created by jih on 14/09/16.
 */
public class AwesomeException extends Throwable {

    private final String msg;

    public AwesomeException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
