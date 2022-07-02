package StoreTests.SpellCheckerTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import SpellChecker.Spelling;
import static org.junit.jupiter.api.Assertions.*;

class SpellCheckerTest {

    private Spelling spelling;
    @BeforeEach
    void setUp() {
        spelling = new Spelling();
    }

    @Test
    void wordMissingLetter1() {
        String result = spelling.correct("tomat");
        assertEquals(result, "tomato");
    }

    @Test
    void wordMissingLetter2() {
        String result = spelling.correct("ugar");
        assertEquals(result, "sugar");
    }

    @Test
    void wordMissingLetter3() {
        String result = spelling.correct("mlk");
        assertEquals(result, "milk");
    }

    @Test
    void wordExtraLetter1() {
        String result = spelling.correct("tomaton");
        assertEquals(result, "tomato");
    }

    @Test
    void wordExtraLetter2() {
        String result = spelling.correct("asugar");
        assertEquals(result, "sugar");
    }

    @Test
    void wordExtraLetter3() {
        String result = spelling.correct("milnk");
        assertEquals(result, "milk");
    }

    @Test
    void wordReplaceLetter1() {
        String result = spelling.correct("timato");
        assertEquals(result, "tomato");
    }

    @Test
    void wordReplaceLetter2() {
        String result = spelling.correct("sufar");
        assertEquals(result, "sugar");
    }

    @Test
    void wordReplaceLetter3() {
        String result = spelling.correct("mijk");
        assertEquals(result, "milk");
    }


}