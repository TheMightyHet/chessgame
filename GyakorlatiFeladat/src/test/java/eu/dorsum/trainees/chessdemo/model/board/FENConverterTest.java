package eu.dorsum.trainees.chessdemo.model.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FENConverterTest {

    private FENConverter converter;

    @BeforeEach
    public void setUp() {
        converter = new FENConverter();
    }

    // **Test Initialization**
    @Test
    public void testInitializeBoard() {
        Map<Piece, String> bitboards = converter.getBitboards();
        assertEquals("000000000000FF00", bitboards.get(Piece.WHITE_PAWN));
        assertEquals("0000000000000042", bitboards.get(Piece.WHITE_KNIGHT));
        assertEquals("0000000000000024", bitboards.get(Piece.WHITE_BISHOP));
        assertEquals("0000000000000081", bitboards.get(Piece.WHITE_ROOK));
        assertEquals("0000000000000008", bitboards.get(Piece.WHITE_QUEEN));
        assertEquals("0000000000000010", bitboards.get(Piece.WHITE_KING));
        assertEquals("00FF000000000000", bitboards.get(Piece.BLACK_PAWN));
        assertEquals("4200000000000000", bitboards.get(Piece.BLACK_KNIGHT));
        assertEquals("2400000000000000", bitboards.get(Piece.BLACK_BISHOP));
        assertEquals("8100000000000000", bitboards.get(Piece.BLACK_ROOK));
        assertEquals("0800000000000000", bitboards.get(Piece.BLACK_QUEEN));
        assertEquals("1000000000000000", bitboards.get(Piece.BLACK_KING));
    }

    // **Test cellNotationToIndex**
    @Test
    public void testCellNotationToIndex() {
        assertEquals(0, converter.cellNotationToIndex("a1"));
        assertEquals(7, converter.cellNotationToIndex("h1"));
        assertEquals(28, converter.cellNotationToIndex("e4"));
        assertEquals(63, converter.cellNotationToIndex("h8"));
    }

    @Test
    public void testInvalidCellNotation() {
        assertThrows(IllegalArgumentException.class, () -> converter.cellNotationToIndex("a"));
        assertThrows(IllegalArgumentException.class, () -> converter.cellNotationToIndex("a9"));
        assertThrows(IllegalArgumentException.class, () -> converter.cellNotationToIndex("i1"));
        assertThrows(IllegalArgumentException.class, () -> converter.cellNotationToIndex("abc"));
    }

    // **Test applyMove (Non-Capture)**
    @Test
    public void testApplyNonCaptureMove() {
        converter.applyMove("Pe2e4/");
        String whitePawnBitboard = converter.getBitboards().get(Piece.WHITE_PAWN);
        assertFalse(isBitSet(whitePawnBitboard, 12)); // e2 cleared
        assertTrue(isBitSet(whitePawnBitboard, 28));  // e4 set
    }

    // **Test applyMove (Capture)**
    @Test
    public void testApplyCaptureMove() {
        // Move black knight from g8 to f6, capturing a white pawn placed there
        String whitePawnBB = converter.setBit(converter.getBitboards().get(Piece.WHITE_PAWN), 45); // f6
        converter.getBitboards().put(Piece.WHITE_PAWN, whitePawnBB);
        converter.applyMove("ng8xf6/");
        String blackKnightBitboard = converter.getBitboards().get(Piece.BLACK_KNIGHT);
        String whitePawnBitboard = converter.getBitboards().get(Piece.WHITE_PAWN);
        assertFalse(isBitSet(blackKnightBitboard, 62)); // g8 cleared
        assertTrue(isBitSet(blackKnightBitboard, 45));  // f6 set
        assertFalse(isBitSet(whitePawnBitboard, 45));   // f6 cleared (captured)
    }

    // **Test applyMove (Invalid Notation)**
    @Test
    public void testInvalidMoveNotation() {
        assertThrows(IllegalArgumentException.class, () -> converter.applyMove("Pe2e4"));
        assertThrows(IllegalArgumentException.class, () -> converter.applyMove("Xe2e4/"));
        assertThrows(IllegalArgumentException.class, () -> converter.applyMove("Pe2e4//"));
        assertThrows(IllegalArgumentException.class, () -> converter.applyMove("Pexe4/"));
    }

    // **Test Helper Methods**
    @Test
    public void testSetAndClearBit() {
        String hex = "0000000000000000";
        String setHex = converter.setBit(hex, 0);
        assertEquals("0000000000000001", setHex);
        String clearHex = converter.clearBit(setHex, 0);
        assertEquals("0000000000000000", clearHex);

        String setHex63 = converter.setBit(hex, 63);
        assertEquals("8000000000000000", setHex63);
    }

    @Test
    public void testIsBitSet() {
        String hex = "8000000000000001"; // Bits 63 and 0 set
        assertTrue(converter.isBitSet(hex, 63));
        assertTrue(converter.isBitSet(hex, 0));
        assertFalse(converter.isBitSet(hex, 1));
        assertFalse(converter.isBitSet(hex, 62));
    }

    // **Test Edge Cases**
    @Test
    public void testMoveToEdgeOfBoard() {
        converter.applyMove("Rh1h8/");
        String whiteRookBitboard = converter.getBitboards().get(Piece.WHITE_ROOK);
        assertFalse(isBitSet(whiteRookBitboard, 7));  // h1 cleared
        assertTrue(isBitSet(whiteRookBitboard, 63));  // h8 set
    }

    @Test
    public void testCaptureOnEdge() {
        // Black rook captures white rook on h1
        converter.applyMove("rh8xh1/");
        String blackRookBitboard = converter.getBitboards().get(Piece.BLACK_ROOK);
        String whiteRookBitboard = converter.getBitboards().get(Piece.WHITE_ROOK);
        assertFalse(isBitSet(blackRookBitboard, 63)); // h8 cleared
        assertTrue(isBitSet(blackRookBitboard, 7));   // h1 set
        assertFalse(isBitSet(whiteRookBitboard, 7));  // h1 cleared (captured)
    }

    // Test Piece class
    @Test
    public void testPieceEquality() {
        assertEquals(Piece.WHITE_PAWN, Piece.WHITE_PAWN);
        assertNotEquals(Piece.WHITE_PAWN, Piece.BLACK_PAWN);

        // Test that getPiece returns the same instances
        assertSame(Piece.WHITE_PAWN, Piece.getPiece(PieceType.PAWN, PieceColourType.WHITE));
        assertSame(Piece.BLACK_KNIGHT, Piece.getPiece(PieceType.KNIGHT, PieceColourType.BLACK));
    }

    // Helper method to access private isBitSet
    private boolean isBitSet(String hex, int i) {
        int digitIndex = (63 - i) / 4;
        int bitPos = 3 - ((63 - i) % 4);
        char c = hex.charAt(digitIndex);
        int val = Character.digit(c, 16);
        return (val & (1 << bitPos)) != 0;
    }
}