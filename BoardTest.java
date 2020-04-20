import junit.framework.TestCase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class BoardTest extends TestCase {
	Board b;
	Piece pyr1, pyr2, pyr3, pyr4, s, sRotated;
	/* my test variables */
	Board brd;
	Piece[] pieces;

	// This shows how to build things in setUp() to re-use
	// across tests.

	// In this case, setUp() makes shapes,
	// and also a 3X6 board, with pyr placed at the bottom,
	// ready to be used by tests.

	protected void setUp() throws Exception {
		b = new Board(3, 6);

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();

		b.place(pyr1, 0, 0);
		b.commit();
		/* My setup */
		brd  = new Board(7, 12);
		brd.undo();
		pieces = Piece.getPieces();
	}

	// Check the basic width/height/max after the one placement
	public void testSample1() {
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(2, b.getColumnHeight(1));
		assertEquals(2, b.getMaxHeight());
		assertEquals(3, b.getRowWidth(0));
		assertEquals(1, b.getRowWidth(1));
		assertEquals(0, b.getRowWidth(2));
	}

	// Place sRotated into the board, then check some measures
	public void testSample2() {
		b.commit();
		int result = b.place(sRotated, 1, 1);
		b.commit();

		assertEquals(Board.PLACE_OK, result);
		assertEquals(1, b.getColumnHeight(0));
		assertEquals(4, b.getColumnHeight(1));
		assertEquals(3, b.getColumnHeight(2));
		assertEquals(4, b.getMaxHeight());
	}

	// Make  more tests, by putting together longer series of
	// place, clearRows, undo, place ... checking a few col/row/max
	// numbers that the board looks right after the operations.

	public void testInvalidGrid(){
		assertTrue(b.getGrid(1 , b.getHeight() + 2));
		assertTrue(b.getGrid(1, -1));
		assertTrue(b.getGrid(b.getWidth() + 2, 5));
		assertTrue(b.getGrid(-1, 5));
		assertTrue(b.getGrid(1, 1));
		assertFalse(b.getGrid(0, 1));
	}
	public void testAddition(){
		Piece flatStick = pieces[Piece.STICK].fastRotation();
		int res = brd.place(flatStick, 1, 0);
		brd.commit();

		assertEquals(Board.PLACE_OK, res);
		assertEquals(4, brd.getRowWidth(0));
		assertEquals(1, brd.getMaxHeight());
	}
	public void testIntersection(){
		Piece pyr = pieces[Piece.PYRAMID];
		int res = brd.place(pyr, 0, 0);
		brd.commit();

		assertEquals(Board.PLACE_OK, res);
		assertEquals(3, brd.getRowWidth(0));
		assertEquals(1, brd.getRowWidth(1));
		assertEquals(2, brd.getMaxHeight());

		Piece stick = pieces[Piece.STICK];
		res = brd.place(stick, 1, 0);
		brd.commit();

		assertEquals(Board.PLACE_BAD, res);
	}
	public void testOutOfBound(){
		Piece s_pc = pieces[Piece.S2];

		int res = brd.place(s_pc, -1, 5);
		brd.commit();
		assertEquals(Board.PLACE_OUT_BOUNDS, res);

		res = brd.place(s_pc, 6, 0);
		brd.commit();
		assertEquals(Board.PLACE_OUT_BOUNDS, res);
	}
	public void testMultiBounds(){
		Piece square = pieces[Piece.SQUARE];

		int res = brd.place(square, -1, 5);
		brd.commit();
		assertEquals(res, Board.PLACE_OUT_BOUNDS);

		res = brd.place(square, brd.getWidth() + 2, 3);
		brd.commit();
		assertEquals(res, Board.PLACE_OUT_BOUNDS);

		res = brd.place(square, 3, -1);
		brd.commit();
		assertEquals(res, Board.PLACE_OUT_BOUNDS);

		res = brd.place(square, 3, brd.getHeight() + 2);
		brd.commit();
		assertEquals(res, Board.PLACE_OUT_BOUNDS);
	}

	public void testClearRows(){
		Piece flatStick = pieces[Piece.STICK].fastRotation();
		Piece pyr = pieces[Piece.PYRAMID];

		int res = brd.place(flatStick, 0,0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		res = brd.place(pyr, 4,0);
		assertEquals(Board.PLACE_ROW_FILLED, res);

		assertEquals(7, brd.getRowWidth(0));
		assertEquals(2, brd.getMaxHeight());
		assertEquals(2, brd.getColumnHeight(5));

		brd.clearRows();
		brd.commit();

		assertEquals(1, brd.getRowWidth(0));
		assertEquals(1, brd.getMaxHeight());
	}

	public void testMultipleClears(){
		Piece stick = pieces[Piece.STICK];
		Piece flatStick = pieces[Piece.STICK].computeNextRotation();
		Piece sqr = pieces[Piece.SQUARE];

		int res = brd.place(flatStick, 0, 0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		res = brd.place(flatStick, 0, 1);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		res = brd.place(sqr, 4, 0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		res = brd.place(sqr, 2, 1);
		brd.commit();
		assertEquals(Board.PLACE_BAD, res);

		assertEquals(2, brd.getMaxHeight());
		assertEquals(6, brd.getRowWidth(1));
		assertEquals(6, brd.getRowWidth(0));

		res = brd.place(stick, 6, 0);
		brd.commit();
		assertEquals(Board.PLACE_ROW_FILLED, res);
		brd.setDebugMode(false);
		int cl = brd.clearRows();
		brd.commit();
		assertEquals(2, cl);

		assertEquals(2, brd.getMaxHeight());
		assertEquals(1, brd.getRowWidth(1));
		assertEquals(1, brd.getRowWidth(0));
	}

	public void testUnderInsert(){
		Piece stick = pieces[Piece.STICK];
		Piece s2_pc = pieces[Piece.S2];

		int res = brd.place(stick, 0,0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		res = brd.place(s2_pc, 0, 3);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		assertEquals(5, brd.getMaxHeight());

		assertEquals(5, brd.getColumnHeight(0));
		assertEquals(5, brd.getColumnHeight(1));
		assertEquals(4, brd.getColumnHeight(2));

		assertEquals(2, brd.getRowWidth(4));
		assertEquals(3, brd.getRowWidth(3));
		assertEquals(1, brd.getRowWidth(2));

		Piece pyr = pieces[Piece.PYRAMID];
		res = brd.place(pyr, 1, 0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		assertEquals(5, brd.getMaxHeight());

		assertEquals(5, brd.getColumnHeight(0));
		assertEquals(5, brd.getColumnHeight(1));
		assertEquals(4, brd.getColumnHeight(2));

		assertEquals(3, brd.getRowWidth(3));
		assertEquals(2, brd.getRowWidth(1));
		assertEquals(4, brd.getRowWidth(0));
	}

	public void testDropHeights(){
		Piece flatStick = pieces[Piece.STICK].fastRotation();
		Piece pyr = pieces[Piece.PYRAMID];
		brd.setDebugMode(false);

		assertEquals(0, brd.dropHeight(flatStick, 0));
		int res = brd.place(flatStick, 0,0);
		brd.commit();
		assertEquals(Board.PLACE_OK, res);

		assertEquals(0, brd.dropHeight(pyr, 4));
		res = brd.place(pyr, 4,0);
		brd.commit();
		assertEquals(Board.PLACE_ROW_FILLED, res);

		assertEquals(2, brd.dropHeight(flatStick, 3));
		assertEquals(2, brd.dropHeight(flatStick, 2));
		assertEquals(1, brd.dropHeight(flatStick, 1));

		pyr = pyr.fastRotation().fastRotation();
		assertEquals(2, brd.dropHeight(pyr, 4));
	}

	public void testUndo(){
		Piece pyr = pieces[Piece.PYRAMID];
		brd.place(pyr,0 ,0);
		brd.undo();

		assertEquals(0, brd.getRowWidth(0));
		assertEquals(0, brd.getRowWidth(1));
		assertEquals(0, brd.getColumnHeight(0));
		assertEquals(0, brd.getColumnHeight(1));
		assertEquals(0, brd.getColumnHeight(2));
		assertEquals(0, brd.getMaxHeight());

		brd.place(pieces[Piece.STICK].fastRotation(), 2, 1);
		brd.commit();
	}

	public void testDoublePlace(){
		brd.place(pieces[Piece.L1], 1,0);
		Exception exc = assertThrows(RuntimeException.class, () -> brd.place(pieces[Piece.SQUARE], 0, 4));
		assertTrue(exc.getMessage().contains("place commit problem"));
	}

	public void testToString(){
		Board full = new Board(4, 4);
		Board empty = new Board(4,4);
		Piece stick = pieces[Piece.STICK];
		for (int i = 0; i < 4; i++) {
			full.place(stick, i, 0);
			full.commit();
		}
		assertEquals(4, full.getMaxHeight());
		assertEquals(4, full.getRowWidth(2));
		assertEquals(4, full.getColumnHeight(1));
		full.clearRows();
		full.commit();

		assertEquals(0, full.getMaxHeight());

		full.place(pyr1, 0, 0);
		full.commit();
		empty.place(pyr1, 0,0);
		empty.commit();
		assertTrue(full.toString().equals(empty.toString()));
	}

	public void testIntrigan(){
		Board intrigBoard = new Board(4,7);
		Piece flatStick = pieces[Piece.STICK].fastRotation();

		intrigBoard.place(flatStick, 0, 2);
		intrigBoard.commit();

		assertEquals(3, intrigBoard.getMaxHeight());
		assertEquals(0, intrigBoard.getRowWidth(0));
		assertEquals(0, intrigBoard.getRowWidth(1));
		assertEquals(4, intrigBoard.getRowWidth(2));

		intrigBoard.clearRows();
		intrigBoard.commit();

		assertEquals(0, intrigBoard.getMaxHeight());
		assertEquals(0, intrigBoard.getRowWidth(0));
		assertEquals(0, intrigBoard.getRowWidth(1));
		assertEquals(0, intrigBoard.getRowWidth(2));
	}

	public void testSanityCheck(){
		Piece stick = pieces[Piece.STICK];

		brd.place(stick.fastRotation(), 0,0);
		brd.commit();
		brd.place(stick.fastRotation(), 0,1);
		brd.commit();
		brd.place(stick, 4,0);
		brd.commit();
		brd.place(stick, 1,2);
		brd.commit();

		Exception exc = assertThrows(RuntimeException.class, () -> brd.checkColSanity(2, 6));
		assertTrue(exc.getMessage().contains("Incorrectly stored column height on col: " + 6));

		int[] arr = new int[]{ 3, 5, 2, 2, 1, 1, 0, 0, 0, 0, 0, 0 };
		exc = assertThrows(RuntimeException.class, () -> brd.checkRowSanity(arr, brd.getMaxHeight()));
		assertTrue(exc.getMessage().contains("Incorrectly stored width"));

		arr[0] = 5;
		exc = assertThrows(RuntimeException.class, () -> brd.checkRowSanity(arr, 2));
		assertTrue(exc.getMessage().contains("Incorrectly stored max height"));
	}
}
