package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jih on 14/09/16.
 */
public class ABCToAWEParserFormatTest {

    private ABCFile abcFile;
    private List<String> line;

    @Before
    public void setUp() throws Exception {
        line = new ArrayList<String>();
    }

    @Test
    public void testEmptyAWEFileIsCreated() {
        abcFile = new ABCFile(line);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(abcFile);
        assertTrue(aweFile != null);
        assertEquals(aweFile.getLines().size(), 0);
    }

    @Test
    public void testOneLineAWEFileIsCreated() {
        line.add(0, "ABC");
        abcFile = new ABCFile(line);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(abcFile);
        assertEquals(1, aweFile.getLines().size());
    }

    @Test
    public void testCleanNote() throws AwesomeException {
        String abcString = "A|";
        String aweString = "A|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testSharpNote() throws AwesomeException {
        String abcString = "^A|";
        String aweString = "^A|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testDoubleSharpNote() throws AwesomeException {
        String abcString = "^^A|";
        String aweString = "^^A|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFlatNote() throws AwesomeException {
        String abcString = "_A|";
        String aweString = "_A|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testDoubleNote() throws AwesomeException {
        String abcString = "A2|";
        String aweString = "A-|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFourNote() throws AwesomeException {
        String abcString = "A4|";
        String aweString = "A---|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }
    @Test
    public void testEightNote() throws AwesomeException {
        String abcString = "A8|";
        String aweString = "A-------|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testFourNoteFlat() throws AwesomeException {
        String abcString = "_A4|";
        String aweString = "_A---|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testEightNoteSharp() throws AwesomeException {
        String abcString = "^A8|";
        String aweString = "^A-------|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testTwoDifferentSimpleNotes() throws AwesomeException {
        String abcString = "AC|";
        String aweString = "AC|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testTwoDifferentComplexNotes1() throws AwesomeException {
        String abcString = "^A8C|";
        String aweString = "^A-------C|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testTwoDifferentComplexNotes2() throws AwesomeException {
        String abcString = "^A8_C4|";
        String aweString = "^A-------_C---|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testMultipleNotes() throws AwesomeException {
        String abcString = "xB2A2^G2A2c8d2c2B2c2e8f2e2^d2e2b2a2^g2a2b2a2^g2a2c'8a4c'4b4a4g4a4b4a4g4a4b4a4g4^f4e8B2A2^G2A2|";
        String aweString = "xB-A-^G-A-c-------d-c-B-c-e-------f-e-^d-e-b-a-^g-a-b-a-^g-a-c'-------a---c'---b---a---g---a---b---a---g---a---b---a---g---^f---e-------B-A-^G-A-|";
        assertEquals(getAWELineFromABCString(abcString).getLineString(), aweString);
    }

    @Test
    public void testMultipleNotesWithBarsAndSpaces() throws AwesomeException {
        String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
        String aweString = "xxB-A-^G-A-|c-------d-c-B-c-|e-------f-e-^d-e-|b-a-^g-a-b-a-^g-a-|c'-------a---c'---|b---a---g---a---|b---a---g---a---|b---a---g---^f---|e-------B-A-^G-A-|";
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
        assertEquals(line.getBar(1).getUnits().size(), 16); // First "whole" bar
    }
    private AWELine getAWELineFromABCString(String abcString) throws AwesomeException {
        List<String> lines = new ArrayList<String>();
        lines.add(abcString);
        AWEFile aweFile = ABCToAWEParser.getAWEFile(new ABCFile(lines));
        return aweFile.getAWELine(0);
    }

}