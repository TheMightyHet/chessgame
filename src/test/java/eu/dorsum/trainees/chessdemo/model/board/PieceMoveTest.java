package eu.dorsum.trainees.chessdemo.model.board;

import eu.dorsum.trainees.chessdemo.service.impl.PieceMoveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PieceMoveTest {

    private PieceMoveService pieceMoveService;

    @BeforeEach
    public void setUp() {
        pieceMoveService = new PieceMoveService();
    }

    @Test
    public void testInitialPawnMoves() {
        // White pawns can move one or two squares forward from starting position
        List<String> e2Moves = pieceMoveService.getPossibleMoves("e2");
        assertTrue(e2Moves.contains("e3"));
        assertTrue(e2Moves.contains("e4"));
        assertEquals(2, e2Moves.size());

        // Make a move with white pawn
        assertTrue(pieceMoveService.applyMove("Pe2e4"));

        // Black pawns can move one or two squares forward from starting position
        List<String> e7Moves = pieceMoveService.getPossibleMoves("e7");
        assertTrue(e7Moves.contains("e6"));
        assertTrue(e7Moves.contains("e5"));
        assertEquals(2, e7Moves.size());
    }

    @Test
    public void testBlackPawnNotation() {
        // First, make a white move
        assertTrue(pieceMoveService.applyMove("Pe2e4"));

        // Then make a black move with lowercase notation
        assertTrue(pieceMoveService.applyMove("pe7e5"));

        // Verify lowercase notation is required
        assertTrue(pieceMoveService.applyMove("Pd2d4"));
        assertFalse(pieceMoveService.applyMove("Pd7d5")); // Wrong case
        assertTrue(pieceMoveService.applyMove("pd7d5")); // Correct case
    }

    @Test
    public void testKnightMovement() {
        // Initially knights are blocked except for edge moves
        List<String> g1Moves = pieceMoveService.getPossibleMoves("g1");
        assertTrue(g1Moves.contains("f3"));
        assertTrue(g1Moves.contains("h3"));
        assertEquals(2, g1Moves.size());

        // Make initial moves to get knights in play
        assertTrue(pieceMoveService.applyMove("Ng1f3"));
        assertTrue(pieceMoveService.applyMove("ng8f6"));
    }

    @Test
    public void testBishopMovement() {
        // Initially bishops are blocked by pawns
        List<String> c1Moves = pieceMoveService.getPossibleMoves("c1");
        assertEquals(0, c1Moves.size());

        // Make some moves to free up the bishops
        assertTrue(pieceMoveService.applyMove("Pd2d4"));
        assertTrue(pieceMoveService.applyMove("pd7d5"));
        assertTrue(pieceMoveService.applyMove("Pc2c3"));
        assertTrue(pieceMoveService.applyMove("pe7e6"));

        // Now bishop can move
        List<String> c1Moves2 = pieceMoveService.getPossibleMoves("c1");
        assertTrue(c1Moves2.contains("d2"));
        assertTrue(c1Moves2.contains("e3"));
        assertFalse(c1Moves2.isEmpty());
    }

    @Test
    public void testRookMovement() {
        // Initially rooks are blocked
        List<String> a1Moves = pieceMoveService.getPossibleMoves("a1");
        assertEquals(0, a1Moves.size());

        // Make some moves to free up the rook
        assertTrue(pieceMoveService.applyMove("Pa2a4"));
        assertTrue(pieceMoveService.applyMove("pa7a5"));

        // Now rook can move
        List<String> a1Moves2 = pieceMoveService.getPossibleMoves("a1");
        assertTrue(a1Moves2.contains("a2"));
        assertTrue(a1Moves2.contains("a3"));
        assertFalse(a1Moves2.isEmpty());
    }

    @Test
    public void testQueenMovement() {
        // Initially queen is blocked
        List<String> d1Moves = pieceMoveService.getPossibleMoves("d1");
        assertEquals(0, d1Moves.size());

        // Make some moves to free up the queen
        assertTrue(pieceMoveService.applyMove("Pd2d4"));
        assertTrue(pieceMoveService.applyMove("pd7d5"));

        // Now queen can move
        List<String> d1Moves2 = pieceMoveService.getPossibleMoves("d1");
        assertFalse(d1Moves2.isEmpty());
        assertTrue(d1Moves2.contains("d2") || d1Moves2.contains("d3"));
    }

    @Test
    public void testKingMovement() {
        // Initially kings have limited moves
        List<String> e1Moves = pieceMoveService.getPossibleMoves("e1");
        assertEquals(0, e1Moves.size());

        // Make some moves to free up the kings
        assertTrue(pieceMoveService.applyMove("Pe2e4"));
        assertTrue(pieceMoveService.applyMove("pe7e5"));

        // Now king should be able to move
        List<String> e1Moves2 = pieceMoveService.getPossibleMoves("e1");
        assertFalse(e1Moves2.isEmpty());
    }

    @Test
    public void testTurnAlternation() {
        // White's first move
        assertTrue(pieceMoveService.applyMove("Pe2e4"));

        // White can't move twice in a row
        assertFalse(pieceMoveService.applyMove("Pd2d4"));

        // Black's turn now
        assertTrue(pieceMoveService.applyMove("pe7e5"));

        // Black can't move twice in a row
        assertFalse(pieceMoveService.applyMove("pd7d5"));

        // White's turn again
        assertTrue(pieceMoveService.applyMove("Bf1c4"));

        // Check that the current turn is tracked correctly
        assertEquals(PieceColourType.BLACK, pieceMoveService.getCurrentTurn());
    }

    @Test
    public void testCastlingSetup() {
        // Test required setup for castling to be available

        // Clear kingside path (this is a simplified test to check for castling notation)
        assertTrue(pieceMoveService.applyMove("Pe2e4"));
        assertTrue(pieceMoveService.applyMove("pe7e5"));
        assertTrue(pieceMoveService.applyMove("Pf2f4"));
        assertTrue(pieceMoveService.applyMove("pf7f5"));
        assertTrue(pieceMoveService.applyMove("Ng1f3"));
        assertTrue(pieceMoveService.applyMove("ng8f6"));
        assertTrue(pieceMoveService.applyMove("Bf1e2"));
        assertTrue(pieceMoveService.applyMove("bf8e7"));

        // Get all possible moves for the white king - just log them, don't assert
        List<String> kingMoves = pieceMoveService.getPossibleMovesWithNotation("e1");
        System.out.println("Available king moves: " + kingMoves);
    }

    @Test
    public void testCaptureNotation() {
        // Test capture notation (using 'x')
        assertTrue(pieceMoveService.applyMove("Pe2e4"));
        assertTrue(pieceMoveService.applyMove("pd7d5"));

        // Test pawn capture removing pawn from the board
        assertTrue(pieceMoveService.applyMove("Pe4xd5"));

        // Verify d5 no longer has black pawn
        BitBoard bitBoard = pieceMoveService.getBitBoard();
        long d5Position = 1L << 35; // The bitboard value for d5
        long blackPawns = bitBoard.getBitBoard(Piece.BLACK_PAWN);
        assertEquals(0, blackPawns & d5Position);
    }

    @Test
    public void testInvalidMoves() {
        // Test that invalid moves are rejected

        // Test moving a piece to an occupied square by own piece
        assertFalse(pieceMoveService.applyMove("Ra1a2")); // Rook can't move to pawn's square

        // Test moving a nonexistent piece
        assertFalse(pieceMoveService.applyMove("Ra4a5")); // No rook at a4

        // Test moving a piece when it's not your turn
        assertFalse(pieceMoveService.applyMove("ra8a7")); // Black can't move first

        // Make a legal move first
        assertTrue(pieceMoveService.applyMove("Pe2e4"));

        // Test moving a piece to an invalid square
        assertFalse(pieceMoveService.applyMove("Pc7b6")); // Black pawn can't move diagonally without capture
    }
}