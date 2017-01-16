package no.itema.abcconverter;

import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEFile;
import no.itema.abcconverter.model.AWELine;

/**
 * Created by jih on 16/01/2017.
 */
public class AWEToABCParser {
    public static ABCFile getABCFile(AWEFile aweFile) {

        ABCFile abcFile = parse(aweFile);

        return abcFile;
    }

    private static ABCFile parse(AWEFile aweFile) {

        ABCFile abcFile = new ABCFile("");
        for(AWELine line: aweFile.getChannels().get(0).getLines()) {
            abcFile.getLines().add(parseLine(String.valueOf(line)));
        }

        return abcFile;
    }

    private static String parseLine(String line) {

        String abcLine = line;

        return abcLine;
    }
}
