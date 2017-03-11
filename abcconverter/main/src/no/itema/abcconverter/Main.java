package no.itema.abcconverter;

import no.itema.abcconverter.io.FileManager;
import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEChannel;
import no.itema.abcconverter.model.AWEFile;
import no.itema.abcconverter.util.AwesomeException;
import no.itema.abcconverter.util.InstrumentCategories;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws AwesomeException {

        try {
            //TuneBook tuneBook = new TuneBook(new File("resources/rondo.abc"));
            //Tune tune = tuneBook.getTune(0);

            //convertAll("/media/lars/HDD2/13000midiabc");
            //convertAll("resources/");
            convertAllBackAndForth("C:\\Users\\Lars\\Desktop\\A");
            //convertToAwe("C:\\Users\\Lars\\Desktop\\A\\A\\A Force - Crystal Dawn.mid.abc", "resources/derp.awe");
            //convertToAwe("resources/Robyn.-.Hang.With.Me.Avicii.s.Exclusive.Club.Mix.abc", "resources/hangwithme.awe");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //aweFile = ABCToAWEParser.getAWEFile(abcFile);
    }

    private static void convertAllBackAndForth(final String dir) throws IOException {
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
        final Holder<Integer> ignored = new Holder<Integer>(0);
        final Holder<Integer> invalids = new Holder<Integer>(0);
        Path path = Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".abc")) {
                    i.setValue(i.getValue() + 1);
                    if (i.getValue() % 1000 == 0) {
                        System.out.println(file.toString());
                        System.out.format("%d (valids: %d, ignored %d, invalids %d)", i.getValue(), valids.getValue(), ignored.getValue(), invalids.getValue());
                    }
                    try {
                        String outfileAwe = dir + "/awe/" + file.getFileName().toString() +  ".awe";
                        for (String instrumentAweFile : convertToAwe(file.toString(), outfileAwe)) {
                            String fullAbc = FileManager.getFileContents(file.toString());
                            //String awe = FileManager.getFileContents(outfileAwe.toString());
                            String abc = convertToAbc(instrumentAweFile, instrumentAweFile + ".abc");
                            Assert.assertEquals(fullAbc, abc); //wont be equal, but we can manually look them here
                        }
                        valids.setValue(valids.getValue() + 1);
                    } catch (Exception | AwesomeException e) {
                        invalids.setValue(invalids.getValue() + 1);
                        /*System.out.println("Woopsie! " + file.toString());
                        e.printStackTrace();
                        System.out.flush();
                        System.err.flush();*/
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

    private static ArrayList<String> convertToAwe(String file, String outfile) throws IOException, AwesomeException {
        if (new File(file).length() > 1000000) {
            throw new AwesomeException("File is too big"); //skip crazy big files, they're just mistakes by midi2abc
        }
        String fileContents = FileManager.getFileContents(file);
        ABCFile abcFile = new ABCFile(fileContents);
        AWEFile aweFile;
        aweFile = ABCToAWEParser.getAWEFile(abcFile, true);
        aweFile.ensureIsValid();

        String aweContents = aweFile.getFileString();
        return writeFilePerInstrumentCategory(aweFile, outfile);
        //FileManager.saveFileContents(outfile, aweContents);
    }


    private static String convertToAbc(String file, String outfile) throws IOException, AwesomeException {
        if (new File(file).length() > 1000000) {
            throw new AwesomeException("File is too big"); //skip crazy big files, they're just mistakes by midi2abc
        }
        String aweContents = FileManager.getFileContents(file);
        ArrayList<String> lines = new ArrayList<>();
        lines.add(aweContents);
        AWEFile abcFile = AWEToABCParser.getABCFile(lines);
        //abcFile.ensureIsValid();

        String abcContents = abcFile.getFileString();
        FileManager.saveFileContents(outfile, abcContents);

        return abcContents;
    }


    private static ArrayList<String> writeFilePerInstrumentCategory(AWEFile aweFile, String outfile) {
        //List<Integer> categories = aweFile.getChannels().stream().map(c -> InstrumentCategories.getCategory(c.getInstrument())).collect(Collectors.toList());
        //int[] allInstruments = { InstrumentCategories.PIANO, InstrumentCategories.BASS, InstrumentCategories.DRUMS, InstrumentCategories.GUITAR };
        /*boolean allPresent = true;
        for (int instrument : allInstruments) {
            if (!categories.contains(instrument)) {
                allPresent = false;
            }
        }*/

        Optional<AWEChannel> piano = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.PIANO).findFirst();
        Optional<AWEChannel> guitar = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.GUITAR).findFirst();
        Optional<AWEChannel> bass = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.BASS).findFirst();
        Optional<AWEChannel> drums = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.DRUMS).findFirst();

        ArrayList<String> filesWritten = new ArrayList<>();
        try {
            if (piano.isPresent()) {
                piano.get().writeToFile(outfile + "PIANO.awe");
                filesWritten.add(outfile + "PIANO.awe");
            }
            if (guitar.isPresent()) {
                guitar.get().writeToFile(outfile + "GUITAR.awe");
                filesWritten.add(outfile + "GUITAR.awe");
            }
            if (bass.isPresent())  {
                bass.get().writeToFile(outfile + "BASS.awe");
                filesWritten.add(outfile + "BASS.awe");
            }
            if (drums.isPresent()) {
                drums.get().writeToFile(outfile + "DRUMS.awe");
                filesWritten.add(outfile + "DRUMS.awe");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filesWritten;
        /*
        if (allPresent) {
            AWEChannel piano = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.PIANO).findFirst().get();
            AWEChannel guitar = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.GUITAR).findFirst().get();
            AWEChannel bass = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.BASS).findFirst().get();
            AWEChannel drums = aweFile.getChannels().stream().filter(c -> c.getInstrument() == InstrumentCategories.DRUMS).findFirst().get();

            try {
                piano.writeToFile(outfile + ".PIANO");
                guitar.writeToFile("GUITAR" + outfile);
                bass.writeToFile("BASS" + outfile);
                drums.writeToFile("DRUMS" + outfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}
