package no.itema.abcconverter;

import abc.notation.Tune;
import abc.parser.TuneBook;
import no.itema.abcconverter.io.FileManager;
import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEFile;
import no.itema.abcconverter.util.AwesomeException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class Main {

    public static void main(String[] args) throws AwesomeException {

        try {
            TuneBook tuneBook = new TuneBook(new File("resources/rondo.abc"));
            Tune tune = tuneBook.getTune(0);

            //convertAll("/media/lars/HDD2/130000_Pop_Rock_Classical_Videogame_EDM_MIDI_Archive[6_19_15]");
            //convertAll("resources/");
            //convert("resources/rondo.abc", "resources/rondo.awe");
            convert("resources/Robyn.-.Hang.With.Me.Avicii.s.Exclusive.Club.Mix.abc", "resources/hangwithme.awe");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //aweFile = ABCToAWEParser.getAWEFile(abcFile);
    }

    private static void convertAll(String dir) throws IOException {
        class Holder<T> {
            private T value;

            Holder(T value) {
                setValue(value);
            }

            T getValue() {
                return value;
            }

            void setValue(T value) {
                this.value = value;
            }
        }
        final Holder<Integer> i = new Holder<Integer>(0);
        final Holder<Integer> valids = new Holder<Integer>(0);
        final Holder<Integer> invalids = new Holder<Integer>(0);
        Path path = Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".abc")) {
                    i.setValue(i.getValue() + 1);
                    //if (i.getValue() % 1000 == 0) {
                        System.out.println(file.toString());
                        System.out.format("%d (valids: %d, invalids %d)", i.getValue(), valids.getValue(), invalids.getValue());
                    //}
                    try {
                        String outfile = dir + "/awe/" + file.getFileName().toString() +  ".awe";
                        if (convert(file.toString(), outfile)) {
                            valids.setValue(valids.getValue() + 1);
                        } else {
                            invalids.setValue(invalids.getValue() + 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (AwesomeException e) {
                        System.out.println("Woopsie! " + file.toString());
                        e.printStackTrace();
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean convert(String file, String outfile) throws IOException, AwesomeException {
        if (new File(file).length() > 1000000) {
            return false; //skip crazy big files, they're just mistakes by midi2abc
        }
        String fileContents = FileManager.getFileContents(file);
        ABCFile abcFile = new ABCFile(fileContents);
        AWEFile aweFile;
        try {
            aweFile = ABCToAWEParser.getAWEFile(abcFile);
        } catch (Exception e) {
            return false;
        }
        //if (!aweFile.isValid()) {
        //    return false;
        //}
        String aweContents = aweFile.getFileString();
        //FileManager.saveFileContents(outfile, aweContents);
        return true;
    }

}
