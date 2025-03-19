package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.service.impl.PieceMoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CastlingTest {

    private PieceMoveService pieceMoveService;

    @BeforeEach
    public void setUp() {
        pieceMoveService = new PieceMoveService();
    }

    @Test
    public void testInitialCastlingRights() {
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertTrue(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.BLACK_KINGSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.BLACK_QUEENSIDE));
    }

    @Test
    public void testCastlingPathClear() {
        // Initially, path is blocked by pieces
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
        assertFalse(kingMoves.contains("O-O-O"));

        // Clear kingside path
        pieceMoveService.applyMove("Pf2f3");
        pieceMoveService.applyMove("Pe7e6");
        pieceMoveService.applyMove("Pg2g3");
        pieceMoveService.applyMove("Pd7d6");
        pieceMoveService.applyMove("Bf1g2");
        pieceMoveService.applyMove("Bc8d7");
        pieceMoveService.applyMove("Ng1h3");
        pieceMoveService.applyMove("Nb8c6");

        // Now kingside castling should be available
        kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertTrue(kingMoves.contains("O-O"));
        assertFalse(kingMoves.contains("O-O-O")); // Queenside still blocked

        // Reset and clear queenside path
        pieceMoveService = new PieceMoveService();
        pieceMoveService.applyMove("Pd2d3");
        pieceMoveService.applyMove("Pd7d6");
        pieceMoveService.applyMove("Pc2c3");
        pieceMoveService.applyMove("Pe7e6");
        pieceMoveService.applyMove("Qd1d2");
        pieceMoveService.applyMove("Qd8d7");
        pieceMoveService.applyMove("Pb2b3");
        pieceMoveService.applyMove("Ph7h6");
        pieceMoveService.applyMove("Bc1b2");
        pieceMoveService.applyMove("Bf8e7");
        pieceMoveService.applyMove("Nb1a3");
        pieceMoveService.applyMove("Ng8f6");

        // Now queenside castling should be available
        kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O")); // Kingside still blocked
        assertTrue(kingMoves.contains("O-O-O"));
    }

    @Test
    public void testKingsideCastlingExecution() {
        // Clear kingside path
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("Pf7f5");
        pieceMoveService.applyMove("Ng1f3");
        pieceMoveService.applyMove("Ng8f6");
        pieceMoveService.applyMove("Pg2g4");
        pieceMoveService.applyMove("Pg7g5");
        pieceMoveService.applyMove("Bf1g2");
        pieceMoveService.applyMove("Bf8g7");

        // Execute kingside castling
        assertTrue(pieceMoveService.applyMove("O-O"));

        // Verify king and rook positions
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x40L, bitBoard.getBitBoard(Piece.WHITE_KING)); // g1

        // Check if the rook is on f1
        long rookBitboard = bitBoard.getBitBoard(Piece.WHITE_ROOK);
        assertTrue((rookBitboard & 0x20L) != 0); // f1

        // Verify castling rights are lost
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testQueensideCastlingExecution() {
        // Clear queenside path
        pieceMoveService.applyMove("Pd2d4");
        pieceMoveService.applyMove("Pd7d5");
        pieceMoveService.applyMove("Qd1d3");
        pieceMoveService.applyMove("Qd8d6");
        pieceMoveService.applyMove("Nb1c3");
        pieceMoveService.applyMove("Nb8c6");
        pieceMoveService.applyMove("Bc1e3");
        pieceMoveService.applyMove("Bc8e6");
        pieceMoveService.applyMove("Pc2c4");
        pieceMoveService.applyMove("Pc7c5");
        pieceMoveService.applyMove("Pb2b3");
        pieceMoveService.applyMove("Pb7b6");

        // Execute queenside castling
        assertTrue(pieceMoveService.applyMove("O-O-O"));

        // Verify king and rook positions
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x4L, bitBoard.getBitBoard(Piece.WHITE_KING)); // c1

        // Check if the rook is on d1
        long rookBitboard = bitBoard.getBitBoard(Piece.WHITE_ROOK);
        assertTrue((rookBitboard & 0x8L) != 0); // d1

        // Verify castling rights are lost
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testCastlingRightsAfterRookMove() {
        // Move kingside rook and verify rights lost
        pieceMoveService.applyMove("Rh1g1");
        pieceMoveService.applyMove("Pa7a6");

        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));

        // Move queenside rook and verify rights lost
        pieceMoveService.applyMove("Ra1b1");
        pieceMoveService.applyMove("Ph7h6");

        bitBoard = pieceMoveService.getBitBoard();
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testCastlingRightsAfterKingMove() {
        // Move king and verify all castling rights lost
        pieceMoveService.applyMove("Ke1f1");
        pieceMoveService.applyMove("Pa7a6");
        pieceMoveService.applyMove("Kf1e1"); // Move king back

        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testCastlingBlockedByCheck() {
        // Setup position where king is in check
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("Pa7a6");
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("Qd8h4"); // Black queen checks white king

        // Clear kingside path
        pieceMoveService.applyMove("Ke1f1"); // King moves out of check
        pieceMoveService.applyMove("Pa6a5");
        pieceMoveService.applyMove("Kf1e1"); // King back (but now lost castling rights)
        pieceMoveService.applyMove("Qh4e7"); // Queen backs off

        // Setup again but keep castling rights
        pieceMoveService = new PieceMoveService();
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("Pa7a6");
        pieceMoveService.applyMove("Pg2g4");
        pieceMoveService.applyMove("Qd8h4"); // Black queen checks white king

        // Verify castling not allowed when in check
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
        assertFalse(kingMoves.contains("O-O-O"));
    }

    @Test
    public void testCastlingThroughCheck() {
        // Setup position where a square king would pass through is under attack
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("Pe7e5");
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("Qd8h4"); // Queen attacks f1
        pieceMoveService.applyMove("Pg2g3");
        pieceMoveService.applyMove("Qh4h3"); // Queen moves but still prevents castling

        // Clear kingside path
        pieceMoveService.applyMove("Bf1h3"); // Bishop captures queen
        pieceMoveService.applyMove("Pf7f6");
        pieceMoveService.applyMove("Bh3f1"); // Bishop back to original square
        pieceMoveService.applyMove("Qd8h4"); // New queen attacks f1 again

        // Verify castling not allowed when passing through attacked square
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
    }

    @Test
    public void testCastlingToCheck() {
        // Setup position where king would land in check after castling
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("Pa7a6");
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("Qd8h4"); // Queen attacks h1
        pieceMoveService.applyMove("Pg2g3");
        pieceMoveService.applyMove("Qh4g4"); // Queen now controls g1 where king would land

        // Clear kingside path
        pieceMoveService.applyMove("Bf1h3");
        pieceMoveService.applyMove("Pa6a5");
        pieceMoveService.applyMove("Ng1f3");
        pieceMoveService.applyMove("Pa5a4");

        // Verify castling not allowed when king would land in check
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
    }

    @Test
    public void testBothSidesCastling() {
        // Set up a game where both sides castle
        // White kingside setup
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("Pe7e5");
        pieceMoveService.applyMove("Ng1f3");
        pieceMoveService.applyMove("Ng8f6");
        pieceMoveService.applyMove("Bf1c4");
        pieceMoveService.applyMove("Bf8c5");
        pieceMoveService.applyMove("O-O"); // White castles kingside

        // Verify white castled correctly
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x40L, bitBoard.getBitBoard(Piece.WHITE_KING)); // g1
        assertTrue((bitBoard.getBitBoard(Piece.WHITE_ROOK) & 0x20L) != 0); // f1

        // Black queenside setup
        pieceMoveService.applyMove("Pb7b6");
        pieceMoveService.applyMove("Ph2h3");
        pieceMoveService.applyMove("Bc8b7");
        pieceMoveService.applyMove("Pd2d3");
        pieceMoveService.applyMove("Qd8e7");
        pieceMoveService.applyMove("Bc1e3");
        pieceMoveService.applyMove("Nb8a6");
        pieceMoveService.applyMove("Nb1d2");
        pieceMoveService.applyMove("O-O-O"); // Black castles queenside

        // Verify black castled correctly
        bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x400000000000000L, bitBoard.getBitBoard(Piece.BLACK_KING)); // c8
        assertTrue((bitBoard.getBitBoard(Piece.BLACK_ROOK) & 0x800000000000000L) != 0); // d8
    }
}