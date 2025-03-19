package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.service.impl.PieceMoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChessMoveTest {

    private PieceMoveService pieceMoveService;

    @BeforeEach
    public void setUp() {
        pieceMoveService = new PieceMoveService();
    }

    @Test
    public void testInitialBoardState() {
        // Check that initial board has correct number of pieces
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(8, Long.bitCount(bitBoard.getBitBoard(Piece.WHITE_PAWN)));
        assertEquals(8, Long.bitCount(bitBoard.getBitBoard(Piece.BLACK_PAWN)));
        assertEquals(1, Long.bitCount(bitBoard.getBitBoard(Piece.WHITE_KING)));
        assertEquals(1, Long.bitCount(bitBoard.getBitBoard(Piece.BLACK_KING)));

        // Check that kings are in the correct position
        assertEquals(0x10L, bitBoard.getBitBoard(Piece.WHITE_KING)); // e1
        assertEquals(0x1000000000000000L, bitBoard.getBitBoard(Piece.BLACK_KING)); // e8

        // Check that all castling rights are initially available
        assertTrue(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.BLACK_KINGSIDE));
        assertTrue(bitBoard.hasCastlingRight(BitBoard.BLACK_QUEENSIDE));
    }

    @Test
    public void testPawnMoves() {
        // Test initial pawn moves
        List<String> e2Moves = pieceMoveService.getPossibleMoves("e2");
        assertTrue(e2Moves.contains("e3"));
        assertTrue(e2Moves.contains("e4"));
        assertEquals(2, e2Moves.size());

        // Test pawn capture and blocked movement
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("pd7d5");
        pieceMoveService.applyMove("Pe4e5");
        pieceMoveService.applyMove("pd5d4");

        List<String> e5Moves = pieceMoveService.getPossibleMoves("e5");
        assertTrue(e5Moves.contains("e6"));
        assertEquals(1, e5Moves.size());

        // Set up a capture situation
        pieceMoveService.applyMove("Pa2a4");
        pieceMoveService.applyMove("pb7b5");

        List<String> e6Moves = pieceMoveService.getPossibleMoves("a4");
        assertTrue(e6Moves.contains("a5"));
        assertTrue(e6Moves.contains("b5"));
        assertEquals(2, e6Moves.size());
    }

    @Test
    public void testKnightMoves() {
        // Knight should have 2 legal moves from starting position
        List<String> b1Moves = pieceMoveService.getPossibleMoves("b1");
        assertTrue(b1Moves.contains("a3"));
        assertTrue(b1Moves.contains("c3"));
        assertEquals(2, b1Moves.size());

        // Move knight and check its moves
        pieceMoveService.applyMove("Nb1c3");
        List<String> c3Moves = pieceMoveService.getPossibleMoves("c3");
        assertEquals(5, c3Moves.size()); // Knight should have 5 moves from c3
        assertTrue(c3Moves.contains("a4"));
        assertTrue(c3Moves.contains("b5"));
        assertTrue(c3Moves.contains("d5"));
        assertTrue(c3Moves.contains("e4"));
        assertTrue(c3Moves.contains("b1"));
    }

    @Test
    public void testKingMovesAndCheck() {
        // Create a simple check scenario
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("pe7e5");
        pieceMoveService.applyMove("Qd1h5"); // White queen to h5
        pieceMoveService.applyMove("ke8e7"); // Black king moves
        pieceMoveService.applyMove("Qh5e5"); // White queen checks black king

        // Verify king is in check
        assertTrue(pieceMoveService.getBitBoard().isInCheck(PieceColourType.BLACK));

        // King must move out of check - verify moves
        List<String> e7Moves = pieceMoveService.getPossibleMoves("e7");
        assertTrue(e7Moves.contains("d7") || e7Moves.contains("f7") || e7Moves.contains("d8"));

        // Verify moves that don't escape check aren't permitted
        assertFalse(e7Moves.contains("e8"));
        assertFalse(e7Moves.contains("e6"));
    }

    @Test
    public void testCastlingKingside() {
        // Clear pieces between king and rook
        pieceMoveService.applyMove("Pg2g3");
        pieceMoveService.applyMove("pg7g6");
        pieceMoveService.applyMove("Pf2f4");
        pieceMoveService.applyMove("pf7f5");
        pieceMoveService.applyMove("Bf1g2");
        pieceMoveService.applyMove("bf8g7");
        pieceMoveService.applyMove("Ng1h3");
        pieceMoveService.applyMove("ng8h6");

        // Check if castling is available
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertTrue(kingMoves.contains("O-O"));

        // Execute kingside castling
        boolean castlingSuccess = pieceMoveService.applyMove("O-O");
        assertTrue(castlingSuccess);

        // Verify king and rook positions after castling
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x40L, bitBoard.getBitBoard(Piece.WHITE_KING)); // g1
        assertEquals(0x20L, bitBoard.getBitBoard(Piece.WHITE_ROOK) & 0xF0L); // Rook at f1

        // Verify castling rights are removed after castling
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testCastlingQueenside() {
        // Clear pieces between king and queenside rook
        pieceMoveService.applyMove("Pb2b3");
        pieceMoveService.applyMove("pb7b6");
        pieceMoveService.applyMove("Bc1b2");
        pieceMoveService.applyMove("bc8b7");
        pieceMoveService.applyMove("Pd2d3");
        pieceMoveService.applyMove("pd7d6");
        pieceMoveService.applyMove("Qd1d2");
        pieceMoveService.applyMove("qd8d7");
        pieceMoveService.applyMove("Nb1a3");
        pieceMoveService.applyMove("nb8a6");

        // Check if queenside castling is available
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertTrue(kingMoves.contains("O-O-O"));

        // Execute queenside castling
        boolean castlingSuccess = pieceMoveService.applyMove("O-O-O");
        assertTrue(castlingSuccess);

        // Verify king and rook positions after castling
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        assertEquals(0x4L, bitBoard.getBitBoard(Piece.WHITE_KING)); // c1
        assertEquals(0x8L, bitBoard.getBitBoard(Piece.WHITE_ROOK) & 0xFL); // Rook at d1

        // Verify castling rights are removed after castling
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_KINGSIDE));
        assertFalse(bitBoard.hasCastlingRight(BitBoard.WHITE_QUEENSIDE));
    }

    @Test
    public void testCastlingPrevention() {
        // Test that castling is prevented when king moves
        pieceMoveService.applyMove("Ke1f1");
        pieceMoveService.applyMove("ke8f8");
        pieceMoveService.applyMove("Kf1e1");
        pieceMoveService.applyMove("kf8e8");

        List<String> e1Moves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(e1Moves.contains("O-O"));
        assertFalse(e1Moves.contains("O-O-O"));

        // Reset the board and test that castling is prevented when rook moves
        pieceMoveService = new PieceMoveService();
        pieceMoveService.applyMove("Rh1g1");
        pieceMoveService.applyMove("rh8g8");
        pieceMoveService.applyMove("Rg1h1");
        pieceMoveService.applyMove("rg8h8");

        e1Moves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(e1Moves.contains("O-O"));
    }

    @Test
    public void testCastlingPreventedDuringCheck() {
        // Create a position where white king is in check
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("pe7e5");
        pieceMoveService.applyMove("Bf1c4");
        pieceMoveService.applyMove("qd8h4"); // Black queen puts white king in check

        // Verify king is in check
        assertTrue(pieceMoveService.getBitBoard().isInCheck(PieceColourType.WHITE));

        // Verify castling isn't allowed during check
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
        assertFalse(kingMoves.contains("O-O-O"));
    }

    @Test
    public void testCastlingPreventedThroughCheck() {
        // Set up a position where a square the king would pass through is under attack
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("pe7e5");
        pieceMoveService.applyMove("Ng1f3");
        pieceMoveService.applyMove("qd8h4"); // Black queen attacks f1, preventing kingside castling

        // Clear path for castling
        pieceMoveService.applyMove("Bf1e2");
        pieceMoveService.applyMove("nb8c6");

        // Verify kingside castling isn't allowed since king would pass through check
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        assertFalse(kingMoves.contains("O-O"));
    }

    @Test
    public void testInvalidMoves() {
        // Test moving a piece to an occupied square
        assertFalse(pieceMoveService.applyMove("Ra1a2")); // Rook can't move to pawn's square

        // Test moving a nonexistent piece
        assertFalse(pieceMoveService.applyMove("Ra4a5")); // No rook at a4

        // Test moving a piece when it's not your turn
        assertFalse(pieceMoveService.applyMove("ra8a7")); // Black can't move first

        // Test moving a piece to an invalid square
        pieceMoveService.applyMove("Pe2e4"); // White pawn move
        assertFalse(pieceMoveService.applyMove("Pc7b6")); // Black pawn can't move diagonally without capture
    }

    @Test
    public void testCheckmateDetection() {
        // Set up scholar's mate for checkmate detection
        pieceMoveService.applyMove("Pe2e4");
        pieceMoveService.applyMove("pe7e5");
        pieceMoveService.applyMove("Bf1c4");
        pieceMoveService.applyMove("nb8c6");
        pieceMoveService.applyMove("Qd1h5");
        pieceMoveService.applyMove("ng8f6");
        pieceMoveService.applyMove("Qh5xf7");

        // Verify checkmate is detected
        assertTrue(pieceMoveService.getBitBoard().isInCheck(PieceColourType.BLACK));
        assertTrue(pieceMoveService.isInCheckmate(PieceColourType.BLACK));
    }
}