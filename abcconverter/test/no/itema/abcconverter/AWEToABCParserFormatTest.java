package no.itema.abcconverter;

import no.itema.abcconverter.model.*;
import no.itema.abcconverter.util.AwesomeException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by jih on 14/09/16.
 */
public class AWEToABCParserFormatTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    private AWEFile aweFile;
    private List<AWELine> line;

    @Before
    public void setUp() throws Exception {
        line = new ArrayList<AWELine>();
    }

    @Test
    public void testEmptyABCFileIsCreated() throws AwesomeException {
        List<AWEChannel> channels = new ArrayList<AWEChannel>();
        channels.add(new AWEChannel(0));
        aweFile = new AWEFile(channels);
        ABCFile abcFile = AWEToABCParser.getABCFile(aweFile);
        assertTrue(abcFile != null);
        assertEquals(abcFile.getLines().size(), 0);
    }

    @Test
    public void testCleanNote() throws AwesomeException {
        String abcString = "A | ";
        AWELine aweLine = new AWELine();
        AWEBar aweBar = new AWEBar();
        AWETimeSlot timeSlot = new AWETimeSlot();
        AWEUnit unit = new AWEUnit();
        unit.addSymbol("A");
        timeSlot.addUnit(unit);
        aweBar.addTimeSlot(timeSlot);
        aweLine.addBar(aweBar);
        assertEquals(abcString, getABCLineFromAWEString(aweLine));
    }

    @Test
    public void testSharpNote() throws AwesomeException {
        String abcString = "^A | ";
        String aweString = "^A | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testDoubleSharpNote() throws AwesomeException {
        String abcString = "^^A | ";
        String aweString = "^^A | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testFlatNote() throws AwesomeException {
        String abcString = "_A | ";
        String aweString = "_A | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testDoubleNote() throws AwesomeException {
        String abcString = "A2 | ";
        String aweString = "A ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testFourNote() throws AwesomeException {
        String abcString = "A4 | ";
        String aweString = "A ☃ ☃ ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testEightNote() throws AwesomeException {
        String abcString = "A8 | ";
        String aweString = "A ☃ ☃ ☃ ☃ ☃ ☃ ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testFourNoteFlat() throws AwesomeException {
        String abcString = "_A4 | ";
        String aweString = "_A ☃ ☃ ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testEightNoteSharp() throws AwesomeException {
        String abcString = "^A8 | ";
        String aweString = "^A ☃ ☃ ☃ ☃ ☃ ☃ ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testTieHandling() throws AwesomeException {
        String aweString = "A ☃ | ☃ | ";
        String abcString = "A2- | A | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testTieHandling2() throws AwesomeException {
        String aweString = "A ☃ | ☃ C | ";
        String abcString = "A2- | AC | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testTieHandling3() throws AwesomeException {
        String aweString = "A ☃ | ☃ ☃ ☃ ☃ | ☃ | ";
        String abcString = "A2- | A4- | A | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testTieHandling4() throws AwesomeException {
        String aweString = "A ☃ | ☃ ☃ ☃ ☃ | ☃ ☃ ☃ ☃ | ☃ ☃ ☃ ☃ | ☃ ☃ ☃ ☃ | ☃ | ";
        String abcString = "A2- | A4- | A4- | A4- | A4- | A | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testTwoDifferentSimpleNotes() throws AwesomeException {
        String abcString = "AC | ";
        String aweString = "A C | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testTwoDifferentComplexNotes1() throws AwesomeException {
        String abcString = "^A8C | ";
        String aweString = "^A ☃ ☃ ☃ ☃ ☃ ☃ ☃ C | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testTwoDifferentComplexNotes2() throws AwesomeException {
        String abcString = "^A8_C4 | ";
        String aweString = "^A ☃ ☃ ☃ ☃ ☃ ☃ ☃ _C ☃ ☃ ☃ | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testMultipleNotes() throws AwesomeException {
        String abcString = "xB2A2^G2A2c8d2c2B2c2e8f2e2^d2e2b2a2^g2a2b2a2^g2a2c'8a4c'4b4a4g4a4b4a4g4a4b4a4g4^f4e8B2A2^G2A2 | ";
        String aweString = "x B ☃ A ☃ ^G ☃ A ☃ c ☃ ☃ ☃ ☃ ☃ ☃ ☃ d ☃ c ☃ B ☃ c ☃ e ☃ ☃ ☃ ☃ ☃ ☃ ☃ f ☃ e ☃ ^d ☃ e ☃ b ☃ a ☃ ^g ☃ a ☃ b ☃ a ☃ ^g ☃ a ☃ c' ☃ ☃ ☃ ☃ ☃ ☃ ☃ a ☃ ☃ ☃ c' ☃ ☃ ☃ b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ a ☃ ☃ ☃ b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ a ☃ ☃ ☃ b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ ^f ☃ ☃ ☃ e ☃ ☃ ☃ ☃ ☃ ☃ ☃ B ☃ A ☃ ^G ☃ A ☃ | ";

        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testMultipleNotesWithBarsAndSpaces() throws AwesomeException {
        String abcString = "xxB2A2^G2A2 | c8d2c2B2c2 | e8f2e2^d2e2 | b2a2^g2a2b2a2^g2a2 | c'8a4c'4 | b4a4g4a4 | b4a4g4a4 | b4a4g4^f4 | e8B2A2^G2A2 | ";
        String aweString = "x x B ☃ A ☃ ^G ☃ A ☃ | c ☃ ☃ ☃ ☃ ☃ ☃ ☃ d ☃ c ☃ B ☃ c ☃ | e ☃ ☃ ☃ ☃ ☃ ☃ ☃ f ☃ e ☃ ^d ☃ e ☃ | b ☃ a ☃ ^g ☃ a ☃ b ☃ a ☃ ^g ☃ a ☃ | c' ☃ ☃ ☃ ☃ ☃ ☃ ☃ a ☃ ☃ ☃ c' ☃ ☃ ☃ | b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ a ☃ ☃ ☃ | b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ a ☃ ☃ ☃ | b ☃ ☃ ☃ a ☃ ☃ ☃ g ☃ ☃ ☃ ^f ☃ ☃ ☃ | e ☃ ☃ ☃ ☃ ☃ ☃ ☃ B ☃ A ☃ ^G ☃ A ☃ | ";

        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }
    /*
      @Test
      public void testCorrectNumberOfBars() throws AwesomeException {
          String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
          String aweString = "xxB☃A☃^G☃A☃|c☃☃☃☃☃☃☃d☃c☃B☃c☃|e☃☃☃☃☃☃☃f☃e☃^d☃e☃|b☃a☃^g☃a☃b☃a☃^g☃a☃|c'☃☃☃☃☃☃☃a☃☃☃c'☃☃☃|b☃☃☃a☃☃☃g☃☃☃a☃☃☃|b☃☃☃a☃☃☃g☃☃☃a☃☃☃|b☃☃☃a☃☃☃g☃☃☃^f☃☃☃|e☃☃☃☃☃☃☃B☃A☃^G☃A☃|";
          AWELine line = getAWELineFromABCString(abcString);
          assertEquals(line.getBars().size(), 9); // Including start
      }
      @Test
      public void testCorrectNumberOfUnits() throws AwesomeException {
          String abcString = "x x B2 A2 ^G2 A2 | c8   d2 c2 B2 c2 | e8   f2 e2 ^d2 e2 | b2 a2 ^g2 a2 b2 a2 ^g2 a2 | c'8 a4 c'4 | b4 a4 g4 a4 | b4 a4 g4 a4 | b4 a4 g4 ^f4 | e8 B2 A2 ^G2 A2 |";
          String aweString = "xxB☃A☃^G☃A☃|c☃☃☃☃☃☃☃d☃c☃B☃c☃|e☃☃☃☃☃☃☃f☃e☃^d☃e☃|b☃a☃^g☃a☃b☃a☃^g☃a☃|c'☃☃☃☃☃☃☃a☃☃☃c'☃☃☃|b☃☃☃a☃☃☃g☃☃☃a☃☃☃|b☃☃☃a☃☃☃g☃☃☃a☃☃☃|b☃☃☃a☃☃☃g☃☃☃^f☃☃☃|e☃☃☃☃☃☃☃B☃A☃^G☃A☃|";
          AWELine line = getAWELineFromABCString(abcString);
          assertEquals(line.getBar(1).getTimeSlots().size(), 16); // First "whole" bar
      }*/

    @Test
    public void testChordHandling() throws AwesomeException {
        String aweString = "[^F,/2G,,/2B,,,/2] x/2 | ";
        String abcString = "[^F,/2G,,/2B,,,/2]x/2 | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testLongChordHandling() throws AwesomeException {
        String aweString = "[a/2f/2f/2c/2c/2B/2A/2=F/2B,,,/2][a/2f/2f/2c/2c/2_B/2A/2F/2] [a/2f/2f/2c/2c/2B/2A/2F/2][a/2f/2f/2c/2c/2B/2A/2F/2] | ";
        String abcString = "[a/2f/2f/2c/2c/2B/2A/2=F/2B,,,/2][a/2f/2f/2c/2c/2_B/2A/2F/2][a/2f/2f/2c/2c/2B/2A/2F/2][a/2f/2f/2c/2c/2B/2A/2F/2] | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testLongChordHandling2() throws AwesomeException {
        //[a/2] | [☃/2]
        //[a/2-] | [a/2]
        //a | ☃
        //a- | a
        String aweString = "[a/2f/2f/2c/2c/2B/2A/2A/2F/2F,/2C,/2] | [☃/2☃/2☃/2☃/2☃/2☃/2☃/2☃/2☃/2☃/2☃/2]";
        String abcString = "[a/2-f/2-f/2-c/2-c/2-B/2-A/2-A/2-F/2-F,/2-C,/2-] | [a/2f/2f/2c/2c/2B/2A/2A/2F/2F,/2C,/2] | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testChordHandling2() throws AwesomeException {
        String aweString = "[ba]e[e'd] | ";
        String abcString = "[ba]e[e'd] | ";
        assertEquals(abcString, getABCLineFromAWEString(aweString));
    }

    @Test
    public void testSimultaneouslyTwoSimpleNotes() throws AwesomeException {
        String abcString = "[AC] | ";
        String aweString = "[AC] | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testTwoSimultaneouslyComplexNotes() throws AwesomeException {
        String abcString = "[_A^C] | ";
        String aweString = "[_A^C] | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testMultipleChords() throws AwesomeException {
        String abcString = "[CEG][FAC] | ";
        String aweString = "[CEG] [FAC] | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testMultipleChordsAndSingleNotes() throws AwesomeException {
        String abcString = "[CEG]D2[FAC]G4[CEG] | ";
        String aweString = "[CEG] D ☃ [FAC] G ☃ ☃ ☃ [CEG] | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testSlurStartOnly() throws AwesomeException {
        String aweString = "G D/2E/2 ☃ ☃ ☃ ☃/2E/2 (  ☃ ☃ D E D | ";
        String abcString = "GD/2E4-E/2  (3DED | ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    @Test
    public void testSlurs() throws AwesomeException {
        String aweString = "G D/2E/2 ☃ ☃ ☃ ☃/2E/2 (  ☃ ☃ D E) D | ";
        String abcString = "GD/2E4-E/2  (3DE)D| ";
        assertEquals(getABCLineFromAWEString(aweString), abcString);
    }

    /*
          @Test
          public void testDividedNotes() throws AwesomeException {
              exception.expect(AwesomeException.class);
              String abcString = "C/2|";
              assertEquals(getABCLineFromAWEString(aweString), abcString);
          }
      */
    private String getABCLineFromAWEString(String aweLine) throws AwesomeException {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(aweLine);
        return AWEToABCParser.getABCFile(lines).getAbcString();
    }

    private String getABCLineFromAWEString(AWELine aweLine) throws AwesomeException {
        return aweLine.getAbcString();
    }

    @Test
    public void aweToAbc() throws AwesomeException {
        String awe = "x ☃ ☃ ☃ ☃ ☃ D ☃ | G ☃ A ☃ B ☃ G ☃ | A ☃ B ☃ c ☃ c ☃ | d ☃ e ☃ f ☃ A ☃ | A ☃ G ☃ F ☃ d ☃ | e ☃ d c c c B ☃ | c B A ☃ G ☃ G ☃ | F ☃ ☃ ☃ x ☃ F ☃ | B ☃ c ☃ d ☃ A ☃ | G ☃ A ☃ B ☃ G ☃ | A ☃ B c d ☃ A ☃ | G ☃ A ☃ G ☃ F ☃ | E ☃ E ☃ F ☃ D ☃ | G ☃ A ☃ A ☃ B ☃ | A ☃ A ☃ G ☃ G ☃ | G ☃ G ☃ c ☃ A ☃ | G ☃ F ☃ F ☃ ☃ ☃ | B ☃ A ☃ G ☃ F ☃ | E ☃ ☃ ☃ D ☃ ☃ ☃ | x ☃ ☃ ☃ ☃ ☃ D ☃ | E ☃ D ☃ D ☃ D ☃ | D ☃ D ☃ D ☃ E ☃ | A, ☃ ☃ ☃ G, ☃ E ☃ | D ☃ E ☃ D ☃ ☃ ☃ | E ☃ E ☃ =C B,/2A,/2 B, ☃ | B, ☃ A, ☃ A, ☃ G, ☃ | G, ☃ ☃ F, G, ☃ E, ☃ | A, ☃ ^G, ☃ A, ☃ ☃ ☃ | x ☃ ☃ ☃ ☃ ☃ G, ☃ | C B, A, G, A, ☃ G, ☃ | F, ☃ ☃ ☃ x ☃ G, ☃ | E, ^F, G, ☃ A, ☃ B, ☃ | C B, A, G, F, ☃ x ☃ | E, G, D C B, ☃ C D | D ☃ G ☃ G ☃ F ☃ | E ☃ D ☃ C ☃ B, ☃ | A, ☃ D ☃ D ☃ ☃ C | B, ☃ ☃ ☃ x ☃ C ☃ | C ☃ B, ☃ A, ☃ ☃ G, | A, ☃ B, ☃ C ☃ C ☃ | F ☃ E ☃ D ☃ E D/2C/2 | B, ☃ G, ☃ A, ☃ B, ☃ | A, ☃ ☃ ☃ x ☃ G, ☃ | G, ☃ C ☃ C ☃ B, ☃ | C ☃ ^D ☃ =D ☃ A, ☃ | D ☃ D ☃ D ☃ D ☃ | E D C ☃ B, ☃ F E | D ☃ C ☃ C ☃ =D C | B, ☃ C D E D C ☃ | F ☃ ☃ E E ☃ D ☃ | C ☃ D ☃ D C B, ☃ | A, B, C ☃ F ☃ ☃ E | D ☃ E ☃ F ☃ E D | C ☃ ☃ ☃ ☃ ☃ ☃ ☃ | x ☃ ☃ ☃ ☃ ☃ =G, A, | F, ☃ G, ☃ A, ☃ F, ☃ | G, ☃ D, ☃ E, ☃ E, ☃ | F, ☃ ☃ ☃ =F, ☃ E, F, | G, ☃ =A, G, F, E, D, C, | B,, ☃ ☃ ☃ ☃ ☃ A,, ☃ | D, ☃ C, ☃ B,, ☃ C, ☃ | D, ☃ E, ☃ F, ☃ G, ☃ | A, B, C ☃ B, ☃ =B, ☃ | C ☃ C, ☃ G, ☃ G, ☃ | C, ☃ D, ☃ E, ☃ F, ☃ | C, ☃ D, ☃ G,, ☃ D, ☃ | G, ☃ F, ☃ E, ☃ D, ☃ | C, ☃ ☃ C, C, ☃ C, ☃ | F, ☃ G, A, B, ☃ F, ☃ | B, ☃ F, ☃ B,, ☃ F, ☃ | B,, C, D, ☃ C, ☃ F, ☃ | G, ☃ C, ☃ F, ☃ A,, G,, | ^A, ☃ ^A,, ☃ B,, ☃ B,, ☃ | C, ☃ F, ☃ C, ☃ F, E, | D, ☃ E, ☃ ^A,, ☃ B,, ☃ | C, ☃ F, ☃ E, ☃ D, ☃ | G, ☃ F, ☃ E, ☃ D, C, | B,, ☃ C, ☃ F,, ☃ F, ☃ | ";
        String abc = getABCLineFromAWEString(awe);
        String derp = abc;
    }
}