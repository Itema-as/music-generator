package no.itema.abcconverter;

import no.itema.abcconverter.io.FileManager;
import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEFile;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            ABCFile abcFile = new ABCFile(FileManager.getFileContents("resources/rondo.abc"));
            AWEFile aweFile = ABCToAWEParser.getAWEFile(abcFile);
            String aweContents = aweFile.getFileString();
            FileManager.saveFileContents("resources/rondo.awe", aweContents);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //aweFile = ABCToAWEParser.getAWEFile(abcFile);
    }
}
