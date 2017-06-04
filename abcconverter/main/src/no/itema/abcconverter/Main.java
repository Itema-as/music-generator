package no.itema.abcconverter;

import no.itema.abcconverter.io.FileManager;
import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEChannel;
import no.itema.abcconverter.model.AWEFile;
import no.itema.abcconverter.util.AwesomeException;
import no.itema.abcconverter.util.InstrumentCategories;
import org.junit.Assert;

import java.io.*;
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
            String dir = "/Users/robert/Documents/Itema/MusicGenerator/5";
            String songTarget = "/Users/robert/Documents/Itema/MusicGenerator/5/songs";
            //String dir = "/Users/robert/Documents/Itema/MusicGenerator/abcfiler";
            //String songTarget = "/Users/robert/Documents/Itema/MusicGenerator/songs";
            //String dir = "resources";
            //String songTarget = "resources/songs";
            convertAllBackAndForth(dir);
            //convertAll(dir);
            //convertAllBackAndForth(dir);
            //convertToAwe("C:\\Users\\Lars\\Desktop\\A\\A\\A Force - Crystal Dawn.mid.abc", "resources/derp.awe");
            //convertToAwe("/Users/robert/Documents/Itema/MusicGenerator/5/50's-Rock.mid.abc", "/Users/robert/Documents/Itema/MusicGenerator/5/50'sRock/derp.awe");
            //convertToAwe("resources/Robyn.-.Hang.With.Me.Avicii.s.Exclusive.Club.Mix.abc", "resources/hangwithme.awe");

            collectSameSongFiles(dir, songTarget);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //aweFile = ABCToAWEParser.getAWEFile(abcFile);
    }

    private static void collectSameSongFiles(String dir, String songTarget) {
        File directory = new File(dir + "/awe/");
        File[] pianoFiles = directory.listFiles(getFilesOfType("PIANO"));
        File[] guitarFiles = directory.listFiles(getFilesOfType("GUITAR"));
        File[] bassFiles = directory.listFiles(getFilesOfType("BASS"));
        File[] drumFiles = directory.listFiles(getFilesOfType("DRUMS"));

        File targetDir = new File(songTarget);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        } else {
            // TODO: Remove the old files ?

        }

        List<List<File>> allSongFiles = new ArrayList<>();
        List<File> sameSongFiles = new ArrayList<>();
        List<String> songTitles = new ArrayList<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File searchFile : files) {
                String searchName = "";
                boolean isPiano = false;
                boolean isGuitar = false;
                boolean isBass = false;
                boolean isDrums = false;
                if (searchFile.getName().endsWith("PIANO.awe")) {
                    searchName = searchFile.getName().substring(0, searchFile.getName().indexOf("PIANO"));
                    isPiano = true;
                } else if (searchFile.getName().endsWith("GUITAR.awe")) {
                    searchName = searchFile.getName().substring(0, searchFile.getName().indexOf("GUITAR"));
                    isGuitar = true;
                } else if (searchFile.getName().endsWith("BASS.awe")) {
                    searchName = searchFile.getName().substring(0, searchFile.getName().indexOf("BASS"));
                    isBass = true;
                } else if (searchFile.getName().endsWith("DRUMS.awe")) {
                    searchName = searchFile.getName().substring(0, searchFile.getName().indexOf("DRUMS"));
                    isDrums = true;
                }
                if (songTitles.contains(searchName) || searchName.isEmpty()) {
                    continue;
                }
                collectSongFiles(pianoFiles, guitarFiles, bassFiles, drumFiles, sameSongFiles, searchFile, searchName, isPiano, isGuitar, isBass, isDrums);

                if (sameSongFiles.size() > 0) {
                    songTitles.add(searchName);
                    List<File> copiedFiles = new ArrayList<>();
                    copiedFiles.addAll(sameSongFiles);
                    allSongFiles.add(copiedFiles);
                    sameSongFiles.clear();
                }
            }

            String instrumentA = "PIANO.awe";
            String instrumentB = "DRUMS.awe";
            String instrumentC = "BASS.awe";
            String instrumentD = "GUITAR.awe";
            boolean hasInstrumentA;
            boolean hasInstrumentB;
            boolean hasInstrumentC;
            boolean hasInstrumentD;
            List<File> instrumentAFiles = new ArrayList<>();
            List<File> instrumentBFiles = new ArrayList<>();
            List<File> instrumentCFiles = new ArrayList<>();
            List<File> instrumentDFiles = new ArrayList<>();
            for (List<File> fileList : allSongFiles) {
                hasInstrumentA = false;
                hasInstrumentB = false;
                hasInstrumentC = false;
                hasInstrumentD = false;
                for (File file : fileList) {
                    if (file.getName().endsWith(instrumentA)) {
                        hasInstrumentA = true;
                    } else if (file.getName().endsWith(instrumentB)) {
                        hasInstrumentB = true;
                    } else if (file.getName().endsWith(instrumentC)) {
                        hasInstrumentC = true;
                    } else if (file.getName().endsWith(instrumentD)) {
                        hasInstrumentD = true;
                    }
                }
                if (hasInstrumentA && hasInstrumentB && hasInstrumentC && hasInstrumentD) {
                    instrumentAFiles.add(getInstrument(fileList, instrumentA));
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                    instrumentDFiles.add(getInstrument(fileList, instrumentD));
                } else if (hasInstrumentA && hasInstrumentB && hasInstrumentC) {
                    instrumentAFiles.add(getInstrument(fileList, instrumentA));
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                } else if (hasInstrumentA && hasInstrumentB) {
                    instrumentAFiles.add(getInstrument(fileList, instrumentA));
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                } else if (hasInstrumentB && hasInstrumentC && hasInstrumentD) {
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                    instrumentDFiles.add(getInstrument(fileList, instrumentD));
                } else if (hasInstrumentB && hasInstrumentC) {
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                } else if (hasInstrumentC && hasInstrumentD) {
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                    instrumentDFiles.add(getInstrument(fileList, instrumentD));
                } else if (hasInstrumentA && hasInstrumentC) {
                    instrumentAFiles.add(getInstrument(fileList, instrumentA));
                    instrumentCFiles.add(getInstrument(fileList, instrumentC));
                } else if (hasInstrumentA && hasInstrumentD) {
                    instrumentAFiles.add(getInstrument(fileList, instrumentA));
                    instrumentDFiles.add(getInstrument(fileList, instrumentD));
                } else if (hasInstrumentB && hasInstrumentD) {
                    instrumentBFiles.add(getInstrument(fileList, instrumentB));
                    instrumentDFiles.add(getInstrument(fileList, instrumentD));
                }
            }

            if (instrumentAFiles.size() > 0 && instrumentBFiles.size() > 0 && instrumentCFiles.size() > 0 && instrumentDFiles.size() > 0
                    && instrumentAFiles.size() == instrumentBFiles.size()
                    && instrumentBFiles.size() == instrumentCFiles.size()
                    && instrumentCFiles.size() == instrumentDFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentAFiles, instrumentA);
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);
                writeConcatenatedFile(songTarget, instrumentDFiles, instrumentD);

            } else if (instrumentAFiles.size() > 0 && instrumentBFiles.size() > 0 && instrumentCFiles.size() > 0) {
                writeConcatenatedFile(songTarget, instrumentAFiles, instrumentA);
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);

            } else if (instrumentAFiles.size() > 0 && instrumentBFiles.size() > 0
                    && instrumentAFiles.size() == instrumentBFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentAFiles, instrumentA);
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);

            } else if (instrumentBFiles.size() > 0 && instrumentCFiles.size() > 0 && instrumentDFiles.size() > 0
                    && instrumentBFiles.size() == instrumentCFiles.size()
                    && instrumentCFiles.size() == instrumentDFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);
                writeConcatenatedFile(songTarget, instrumentDFiles, instrumentD);

            } else if (instrumentBFiles.size() > 0 && instrumentCFiles.size() > 0
                    && instrumentBFiles.size() == instrumentCFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);

            } else if (instrumentCFiles.size() > 0 && instrumentDFiles.size() > 0
                    && instrumentCFiles.size() == instrumentDFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);
                writeConcatenatedFile(songTarget, instrumentDFiles, instrumentD);

            } else if (instrumentAFiles.size() > 0 && instrumentCFiles.size() > 0
                    && instrumentAFiles.size() == instrumentCFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentAFiles, instrumentA);
                writeConcatenatedFile(songTarget, instrumentCFiles, instrumentC);

            } else if (instrumentAFiles.size() > 0 && instrumentDFiles.size() > 0
                    && instrumentAFiles.size() == instrumentDFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentAFiles, instrumentA);
                writeConcatenatedFile(songTarget, instrumentDFiles, instrumentD);

            } else if (instrumentBFiles.size() > 0 && instrumentDFiles.size() > 0
                    && instrumentBFiles.size() == instrumentDFiles.size()) {
                writeConcatenatedFile(songTarget, instrumentBFiles, instrumentB);
                writeConcatenatedFile(songTarget, instrumentDFiles, instrumentD);
            }
        }
    }

    private static void writeConcatenatedFile(String songTarget, List<File> instrumentFiles, String instrument) {
        //for (File file : instrumentFiles) {
        File concatenatedInstrumentDir = new File(songTarget + "/" + instrument.substring(0, instrument.indexOf(".")));
        if (!concatenatedInstrumentDir.exists()) {
            concatenatedInstrumentDir.mkdir();
        }
        String outPath = concatenatedInstrumentDir + "/" + instrument.toLowerCase();
        try (OutputStream out = new FileOutputStream(outPath)) {
            byte[] buf = new byte[1 << 20];
            for (File file : instrumentFiles) {
                InputStream in = new FileInputStream(file);
                int b;
                while ((b = in.read(buf)) >= 0) {
                    out.write(buf, 0, b);
                    out.flush();
                }
                in.close();
            }
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File getInstrument(List<File> fileList, String instrument) {
        for (File file : fileList) {
            if (file.getName().endsWith(instrument)) {
                return file;
            }
        }
        throw new IllegalArgumentException("Could not find file ending with" + instrument);
    }

    private static void collectSongFiles(File[] pianoFiles,
                                         File[] guitarFiles,
                                         File[] bassFiles,
                                         File[] drumFiles,
                                         List<File> sameSongFiles,
                                         File searchFile,
                                         String searchName,
                                         boolean isPiano,
                                         boolean isGuitar,
                                         boolean isBass,
                                         boolean isDrums) {
        if (isPiano) {
            if (guitarFiles != null) {
                for (File guitar : guitarFiles) {
                    if (guitar.getName().contains(searchName) && guitar.getName().endsWith("awe")) {
                        sameSongFiles.add(guitar);
                    }
                }
            }
            if (bassFiles != null) {
                for (File bass : bassFiles) {
                    if (bass.getName().contains(searchName) && bass.getName().endsWith("awe")) {
                        sameSongFiles.add(bass);
                    }
                }
            }
            if (drumFiles != null) {
                for (File drums : drumFiles) {
                    if (drums.getName().contains(searchName) && drums.getName().endsWith("awe")) {
                        sameSongFiles.add(drums);
                    }
                }
            }
            if (sameSongFiles.size() > 0) {
                sameSongFiles.add(searchFile);
            }
        } else if (isGuitar) {
            if (pianoFiles != null) {
                for (File piano : pianoFiles) {
                    if (piano.getName().contains(searchName) && piano.getName().endsWith("awe")) {
                        sameSongFiles.add(piano);
                    }
                }
            }
            if (bassFiles != null) {
                for (File bass : bassFiles) {
                    if (bass.getName().contains(searchName) && bass.getName().endsWith("awe")) {
                        sameSongFiles.add(bass);
                    }
                }
            }
            if (drumFiles != null) {
                for (File drums : drumFiles) {
                    if (drums.getName().contains(searchName) && drums.getName().endsWith("awe")) {
                        sameSongFiles.add(drums);
                    }
                }
            }
            if (sameSongFiles.size() > 0) {
                sameSongFiles.add(searchFile);
            }
        } else if (isBass) {
            if (pianoFiles != null) {
                for (File piano : pianoFiles) {
                    if (piano.getName().contains(searchName) && piano.getName().endsWith("awe")) {
                        sameSongFiles.add(piano);
                    }
                }
            }
            if (guitarFiles != null) {
                for (File guitar : guitarFiles) {
                    if (guitar.getName().contains(searchName) && guitar.getName().endsWith("awe")) {
                        sameSongFiles.add(guitar);
                    }
                }
            }
            if (drumFiles != null) {
                for (File drums : drumFiles) {
                    if (drums.getName().contains(searchName) && drums.getName().endsWith("awe")) {
                        sameSongFiles.add(drums);
                    }
                }
            }
            if (sameSongFiles.size() > 0) {
                sameSongFiles.add(searchFile);
            }
        } else if (isDrums) {
            if (pianoFiles != null) {
                for (File piano : pianoFiles) {
                    if (piano.getName().contains(searchName) && piano.getName().endsWith("awe")) {
                        sameSongFiles.add(piano);
                    }
                }
            }
            if (guitarFiles != null) {
                for (File guitar : guitarFiles) {
                    if (guitar.getName().contains(searchName) && guitar.getName().endsWith("awe")) {
                        sameSongFiles.add(guitar);
                    }
                }
            }
            if (bassFiles != null) {
                for (File bass : bassFiles) {
                    if (bass.getName().contains(searchName) && bass.getName().endsWith("awe")) {
                        sameSongFiles.add(bass);
                    }
                }
            }
            if (sameSongFiles.size() > 0) {
                sameSongFiles.add(searchFile);
            }
        }
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
                    if (i.getValue() % 10 == 0) {
                        System.out.println(file.toString());
                        System.out.format("%d (valids: %d, ignored %d, invalids %d)", i.getValue(), valids.getValue(), ignored.getValue(), invalids.getValue());
                    }
                    try {
                        String outfileAwe = dir + "/awe/" + file.getFileName().toString() + ".awe";
                        for (String instrumentAweFile : convertToAwe(file.toString(), outfileAwe)) {
                            String fullAbc = FileManager.getFileContents(file.toString());
                            //String awe = FileManager.getFileContents(outfileAwe.toString());
                            String abc = convertToAbc(instrumentAweFile, instrumentAweFile + ".abc");
                            String derp = abc;
                            System.out.println("OK");
                            //Assert.assertEquals(fullAbc, abc); //wont be equal, but we can manually look them here
                        }
                        valids.setValue(valids.getValue() + 1);
                    } catch (Exception | AwesomeException e) {
                        invalids.setValue(invalids.getValue() + 1);
                        System.out.println("Woopsie! " + file.toString() + "  -  " + e.getMessage());
                        System.out.flush();
                        System.err.flush();/**/
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

    private static FilenameFilter getFilesOfType(String type) {
        return (dir, name) -> name.contains(type + ".awe");
    }

    private static ArrayList<String> convertToAwe(String file, String outfile) throws IOException, AwesomeException {
        if (new File(file).length() > 1000000) {
            throw new AwesomeException("File is too big"); //skip crazy big files, they're just mistakes by midi2abc
        }
        if (!new File(file).exists()) {
            throw new AwesomeException("File does not exist");
        }
        if (new File(file).length() == 0) {
            throw new AwesomeException("File is empty");
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
        if (!new File(file).exists()) {
            throw new AwesomeException("File does not exist");
        }
        if (new File(file).length() == 0) {
            throw new AwesomeException("File is empty");
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

        Optional<AWEChannel> piano = aweFile.getChannels().stream().filter(c -> c.getInstrumentCategory() == InstrumentCategories.PIANO).findFirst();
        Optional<AWEChannel> guitar = aweFile.getChannels().stream().filter(c -> c.getInstrumentCategory() == InstrumentCategories.GUITAR).findFirst();
        Optional<AWEChannel> bass = aweFile.getChannels().stream().filter(c -> c.getInstrumentCategory() == InstrumentCategories.BASS).findFirst();
        Optional<AWEChannel> drums = aweFile.getChannels().stream().filter(c -> c.getInstrumentCategory() == InstrumentCategories.DRUMS).findFirst();

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
            if (bass.isPresent()) {
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
