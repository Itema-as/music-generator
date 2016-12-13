package no.itema.abcconverter.util;

import java.util.HashMap;

/**
 * Created by Lars on 2016-12-13.
 */
public class InstrumentCategories {
    /*
    *
Pianoaktig / melodi:
    Piano:
    1 Acoustic Grand Piano
    2 Bright Acoustic Piano
    3 Electric Grand Piano
    4 Honky-tonk Piano
    5 Electric Piano 1
    6 Electric Piano 2
    7 Harpsichord
    8 Clavinet

    Chromatic Percussion:
    9 Celesta
    10 Glockenspiel
    11 Music Box
    12 Vibraphone
    13 Marimba
    14 Xylophone
    15 Tubular Bells

    Organ:
    17 Drawbar Organ
    18 Percussive Organ [mer bass-aktig?]
    19 Rock Organ
    20 Church Organ
    21 Reed Organ
    22 Accordion
    23 Harmonica
    24 Tango Accordion

    Ethnic
    109 Kalimba
    110 Bag pipe
    111 Fiddle
    112 Shanai

    Strings: [trekke ut egen kategori for strykeinstrument? evt "langtoneinstrument", feks obo]
    41 Violin
    42 Viola
    43 Cello
    45 Tremolo Strings
    46 Pizzicato Strings
    47 Orchestral Harp

    Strings (continued):
    49 String Ensemble 1
    50 String Ensemble 2
    51 Synth Strings 1
    52 Synth Strings 2
    53 Choir Aahs
    54 Voice Oohs
    55 Synth Voice
    56 Orchestra Hit

    Brass:
    57 Trumpet
    58 Trombone
    59 Tuba
    60 Muted Trumpet
    61 French Horn
    62 Brass Section
    63 Synth Brass 1
    64 Synth Brass 2

    Reed:
    65 Soprano Sax
    66 Alto Sax
    67 Tenor Sax
    68 Baritone Sax
    69 Oboe
    70 English Horn
    71 Bassoon
    72 Clarinet



    Pipe:
    73 Piccolo
    74 Flute
    75 Recorder
    76 Pan Flute
    77 Blown Bottle
    78 Shakuhachi
    79 Whistle
    80 Ocarina

    Synth Lead:
    81 Lead 1 (square)
    82 Lead 2 (sawtooth)
    83 Lead 3 (calliope)
    84 Lead 4 (chiff)
    85 Lead 5 (charang)
    86 Lead 6 (voice)
    87 Lead 7 (fifths)
    88 Lead 8 (bass + lead)

Gitaraktig:
    Guitar:
    25 Acoustic Guitar (nylon)
    26 Acoustic Guitar (steel)
    27 Electric Guitar (jazz)
    28 Electric Guitar (clean)
    29 Electric Guitar (muted)
    30 Overdriven Guitar
    31 Distortion Guitar
    32 Guitar harmonics

    Chromatic Percussion:
    16 Dulcimer

    Ethnic:
    105 Sitar
    106 Banjo
    107 Shamisen
    108 Koto




Bass:
    33 Acoustic Bass
    34 Electric Bass (finger)
    35 Electric Bass (pick)
    36 Fretless Bass
    37 Slap Bass 1
    38 Slap Bass 2
    39 Synth Bass 1
    40 Synth Bass 2

    Strings:
    44 Contrabass

Trommer
    Strings:
    48 Timpani

    Percussive:
    113 Tinkle Bell
    114 Agogo
    115 Steel Drums
    116 Woodblock
    117 Taiko Drum
    118 Melodic Tom
    119 Synth Drum

Ignore:
    Sound effects:
    120 Reverse Cymbal
    121 Guitar Fret Noise
    122 Breath Noise
    123 Seashore
    124 Bird Tweet
    125 Telephone Ring
    126 Helicopter
    127 Applause
    128 Gunshot

    Synth Pad:
    89 Pad 1 (new age)
    90 Pad 2 (warm)
    91 Pad 3 (polysynth)
    92 Pad 4 (choir)
    93 Pad 5 (bowed)
    94 Pad 6 (metallic)
    95 Pad 7 (halo)
    96 Pad 8 (sweep)

    Synth Effects:
    97 FX 1 (rain)
    98 FX 2 (soundtrack)
    99 FX 3 (crystal)
    100 FX 4 (atmosphere)
    101 FX 5 (brightness)
    102 FX 6 (goblins)
    103 FX 7 (echoes)
    104 FX 8 (sci-fi)

    * */
    public final static int IGNORE = -1;
    public final static int PIANO = 0;
    public final static int GUITAR = 1;
    public final static int BASS = 2;
    public final static int DRUMS = 3;

    private static int[] piano = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 24, 109, 110, 111, 112, 41, 42, 43, 45, 46, 47, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88 };
    private static int[] guitar = { 25, 26, 27, 28, 29, 30, 31, 32, 16, 105, 106, 107, 108 };
    private static int[] bass = { 33, 34, 35, 36, 37, 38, 39, 40, 44 };
    private static int[] drums = { 48, 113, 114, 115, 116, 117, 118, 119};

    private static HashMap<Integer, Integer> instrumentToGroup;

    public static int getCategory(int instrument) {
        if (instrumentToGroup == null) {
            initIndex();
        }
        if (instrumentToGroup.containsKey(instrument)) {
            return instrumentToGroup.get(instrument);
        }
        return IGNORE;
    }

    private static void initIndex() {
        instrumentToGroup = new HashMap<>();
        for (int id : piano) {
            instrumentToGroup.put(id, PIANO);
        }
        for (int id : guitar) {
            instrumentToGroup.put(id, GUITAR);
        }
        for (int id : bass) {
            instrumentToGroup.put(id, BASS);
        }
        for (int id : drums) {
            instrumentToGroup.put(id, DRUMS);
        }
    }
}
