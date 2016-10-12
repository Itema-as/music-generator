package no.itema.abcconverter;

import no.itema.abcconverter.io.FileManager;
import no.itema.abcconverter.model.ABCFile;
import no.itema.abcconverter.model.AWEFile;
import no.itema.abcconverter.model.AWELine;
import no.itema.abcconverter.util.AwesomeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jih on 14/09/16.
 */
public class ABCToAWEParserFormatTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private ABCFile abcFile;
    private List<String> line;

    @Before
    public void setUp() throws Exception {
        line = new ArrayList<>();
    }

    @Test
    public void testEmptyAWEFileIsCreated() throws AwesomeException {
        abcFile = new ABCFile(line);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(abcFile);
        assertTrue(aweFile != null);
        assertEquals(aweFile.getLines().size(), 0);
    }

    @Test
    public void testOneLineAWEFileIsCreated() throws AwesomeException {
        line.add(0, "ABC");
        abcFile = new ABCFile(line);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(abcFile);
        assertEquals(1, aweFile.getLines().size());
    }

    @Test
    public void testCleanNote() throws AwesomeException {
        String abcString = "A|";
        String aweString = "A | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testSharpNote() throws AwesomeException {
        String abcString = "^A | ";
        String aweString = "^A | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testDoubleSharpNote() throws AwesomeException {
        String abcString = "^^A | ";
        String aweString = "^^A | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFlatNote() throws AwesomeException {
        String abcString = "_A | ";
        String aweString = "_A | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testDoubleNote() throws AwesomeException {
        String abcString = "A2 | ";
        String aweString = "A - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFourNote() throws AwesomeException {
        String abcString = "A4 | ";
        String aweString = "A - - - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }
    @Test
    public void testEightNote() throws AwesomeException {
        String abcString = "A8 | ";
        String aweString = "A - - - - - - - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFourNoteFlat() throws AwesomeException {
        String abcString = "_A4| ";
        String aweString = "_A - - - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testEightNoteSharp() throws AwesomeException {
        String abcString = "^A8| ";
        String aweString = "^A - - - - - - - | ";
        assertEquals(aweString, getAWELineFromABCString(abcString).getLineString());
    }

    @Test
    public void testTwoDifferentSimpleNotes() throws AwesomeException {
        String abcString = "AC|";
        String aweString = "A C | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testTwoDifferentComplexNotes1() throws AwesomeException {
        String abcString = "^A8C | ";
        String aweString = "^A - - - - - - - C | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testTwoDifferentComplexNotes2() throws AwesomeException {
        String abcString = "^A8_C4 | ";
        String aweString = "^A - - - - - - - _C - - - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testMultipleNotes() throws AwesomeException {
        String abcString = "xB2A2^G2A2c8d2c2B2c2e8f2e2^d2e2b2a2^g2a2b2a2^g2a2c'8a4c'4b4a4g4a4b4a4g4a4b4a4g4^f4e8B2A2^G2A2|";
        String aweString = "x B - A - ^G - A - c - - - - - - - d - c - B - c - e - - - - - - - f - e - ^d - e - b - a - ^g - a - b - a - ^g - a - c' - - - - - - - a - - - c' - - - b - - - a - - - g - - - a - - - b - - - a - - - g - - - a - - - b - - - a - - - g - - - ^f - - - e - - - - - - - B - A - ^G - A - | ";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testMultipleNotesWithBarsAndSpaces() throws AwesomeException {
        String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
        String aweString = "x x B - A - ^G - A - | c - - - - - - - d - c - B - c - | e - - - - - - - f - e - ^d - e - | b - a - ^g - a - b - a - ^g - a - | c' - - - - - - - a - - - c' - - - | b - - - a - - - g - - - a - - - | b - - - a - - - g - - - a - - - | b - - - a - - - g - - - ^f - - - | e - - - - - - - B - A - ^G - A - | ";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(line.getLineString(), aweString);
    }
    @Test
    public void testCorrectNumberOfBars() throws AwesomeException {
        String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
        String aweString = "xxB-A-^G-A-|c-------d-c-B-c-|e-------f-e-^d-e-|b-a-^g-a-b-a-^g-a-|c'-------a---c'---|b---a---g---a---|b---a---g---a---|b---a---g---^f---|e-------B-A-^G-A-|";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(line.getBars().size(), 9); // Including start
    }
    @Test
    public void testCorrectNumberOfUnits() throws AwesomeException {
        String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
        String aweString = "xxB-A-^G-A-|c-------d-c-B-c-|e-------f-e-^d-e-|b-a-^g-a-b-a-^g-a-|c'-------a---c'---|b---a---g---a---|b---a---g---a---|b---a---g---^f---|e-------B-A-^G-A-|";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(line.getBar(1).getTimeSlots().size(), 16); // First "whole" bar
    }
    private AWELine getAWELineFromABCString(String abcString) throws AwesomeException {
        List<String> lines = new ArrayList<>();
        lines.add(abcString);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(new ABCFile(lines));
        return aweFile.getAWELine(0);
    }

    @Test
    public void testSimultaneouslyTwoSimpleNotes() throws AwesomeException {
        String abcString = "[AC] | ";
        String aweString = "[AC] | ";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(aweString, getAWELineFromABCString(abcString).getLineString());
    }
    @Test
    public void testTwoSimultaneouslyComplexNotes() throws AwesomeException {
        String abcString = "[_A^C] | ";
        String aweString = "[_A^C] | ";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(aweString, getAWELineFromABCString(abcString).getLineString());
    }

    @Test
    public void testMultipleChords() throws AwesomeException {
        String abcString = "[CEG][FAC] | ";
        String aweString = "[CEG] [FAC] | ";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(aweString, getAWELineFromABCString(abcString).getLineString());
    }
    @Test
    public void testMultipleChordsAndSingleNotes() throws AwesomeException {
        String abcString = "[CEG]D2[FAC]G4[CEG]|";
        String aweString = "[CEG] D - [FAC] G - - - [CEG] | ";
        AWELine line = getAWELineFromABCString(abcString);
        assertEquals(aweString, getAWELineFromABCString(abcString).getLineString());
    }

    @Test
    public void testDividedNotes() throws AwesomeException {
        exception.expect(AwesomeException.class);
        String abcString = "C/2|";
        AWELine line = getAWELineFromABCString(abcString);
    }

    @Test
    public void testParseFile() throws AwesomeException, IOException {
        String file = "resources/rondo.abc";
        ABCFile abcFile = new ABCFile(FileManager.getFileContents(file));
        assertEquals("14", abcFile.getReferenceNumber());
        assertEquals("K331 piano sonata n11 3mov simplified", abcFile.getComposer());
        assertEquals("2/4", abcFile.getMetronome());
        assertEquals("1/16", abcFile.getLength());
        assertEquals("C", abcFile.getKey());
        assertEquals(1, abcFile.getLines().size());
    }

}