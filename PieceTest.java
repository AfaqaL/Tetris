import junit.framework.TestCase;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/*
  Unit test for Piece class -- starter shell.
 */
public class PieceTest extends TestCase {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.
	private Piece pyr1, pyr2, pyr3, pyr4;
	private Piece s, sRotated;

	protected void setUp() throws Exception {
		super.setUp();

		pyr1 = new Piece(Piece.PYRAMID_STR);
		pyr2 = pyr1.computeNextRotation();
		pyr3 = pyr2.computeNextRotation();
		pyr4 = pyr3.computeNextRotation();

		s = new Piece(Piece.S1_STR);
		sRotated = s.computeNextRotation();
	}
	
	// Here are some sample tests to get you started

	public void testSampleSize() {
		// Check size of pyr piece
		assertEquals(3, pyr1.getWidth());
		assertEquals(2, pyr1.getHeight());
		
		// Now try after rotation
		// Effectively we're testing size and rotation code here
		assertEquals(2, pyr2.getWidth());
		assertEquals(3, pyr2.getHeight());
		
		// Now try with some other piece, made a different way
		Piece l = new Piece(Piece.STICK_STR);
		assertEquals(1, l.getWidth());
		assertEquals(4, l.getHeight());
	}
	
	
	// Test the skirt returned by a few pieces
	public void testSampleSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyr1.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyr3.getSkirt()));
		
		assertTrue(Arrays.equals(new int[] {0, 0, 1}, s.getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0}, sRotated.getSkirt()));
	}

	public void testskirtPyr(){
		Piece pc = new Piece(Piece.PYRAMID_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0, 0, 0 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestS(){
		Piece pc = new Piece(Piece.S1_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0, 0, 1 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestMirS(){
		Piece pc = new Piece(Piece.S2_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 1, 0, 0 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestL(){
		Piece pc = new Piece(Piece.L1_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0, 0 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestMirL(){
		Piece pc = new Piece(Piece.L2_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0, 0 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestStick(){
		Piece pc = new Piece(Piece.STICK_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0 };
		assertArrayEquals(arr, sk);
	}

	public void testSkirtTestSquare(){
		Piece pc = new Piece(Piece.SQUARE_STR);
		int[] sk = pc.getSkirt();
		int[] arr = new int[]{ 0, 0 };
		assertArrayEquals(arr, sk);
	}


	public void testSimpleEquals(){
		Piece pc1 = new Piece(Piece.L1_STR);
		Piece pc2 = new Piece(Piece.L1_STR);

		assertTrue(pc1.equals(pc2));

		Piece same = pc1;
		assertTrue(pc1.equals(same));
	}

	public void testWierdEquals(){
		Piece pc = new Piece(Piece.SQUARE_STR);
		TPoint pt = new TPoint(1, 3);

		assertFalse(pc.equals(pt));
		assertFalse(pc.equals("foo"));

	}

	public void testSubEquals(){
		Piece pc = new Piece(Piece.STICK_STR);
		Piece sub = new Piece("0 0 0 1");

		assertFalse(pc.equals(sub));

		sub = new Piece("0 0 0 1 0 2");
		assertFalse(pc.equals(sub));
	}

	public void testRotationEquals(){
		Piece pc = new Piece(Piece.STICK_STR);
		Piece rot = pc.computeNextRotation();
		assertFalse(pc.equals(rot));

		rot = rot.computeNextRotation();
		assertTrue(pc.equals(rot));
	}


	public void testRotationEqualsCube(){
		Piece pc = new Piece(Piece.SQUARE_STR);
		Piece rot = pc.computeNextRotation();

		assertTrue(pc.equals(rot));

		rot = rot.computeNextRotation();
		assertTrue(pc.equals(rot));
	}

	public void testGetPieces(){
		Piece[] pieces = Piece.getPieces();
		assertEquals(7, pieces.length);
	}

	public void testPreRotations(){
		Piece[] pieces = Piece.getPieces();

		Piece frompcs = pieces[Piece.S1];
		Piece actual = new Piece(Piece.S1_STR);
		assertTrue(frompcs.equals(actual));

		frompcs = frompcs.fastRotation();
		actual = actual.computeNextRotation();
		assertTrue(frompcs.equals(actual));

		frompcs = frompcs.fastRotation();
		actual = actual.computeNextRotation();
		assertTrue(frompcs.equals(actual));

		frompcs = frompcs.fastRotation();
		actual = actual.computeNextRotation();
		assertTrue(frompcs.equals(actual));
	}

	public void testPreBodies(){
		Piece[] pieces = Piece.getPieces();

		Piece fromPcs = pieces[Piece.PYRAMID];
		Piece actual = new Piece(Piece.PYRAMID_STR);
		assertArrayEquals(fromPcs.getBody(), actual.getBody());

		fromPcs = fromPcs.fastRotation();
		actual = actual.computeNextRotation();
		assertArrayEquals(fromPcs.getBody(), actual.getBody());

		fromPcs = fromPcs.fastRotation();
		actual = actual.computeNextRotation();
		assertArrayEquals(fromPcs.getBody(), actual.getBody());

		fromPcs = fromPcs.fastRotation();
		actual = actual.computeNextRotation();
		assertArrayEquals(fromPcs.getBody(), actual.getBody());

		fromPcs = fromPcs.fastRotation();
		actual = actual.computeNextRotation();
		assertArrayEquals(fromPcs.getBody(), actual.getBody());

		fromPcs = fromPcs.fastRotation();
		actual = actual.computeNextRotation();
		assertArrayEquals(fromPcs.getBody(), actual.getBody());
	}

	public void testParseThrows(){
		Exception exc = assertThrows(RuntimeException.class, () -> new Piece("baro"));
		assertTrue(exc.getMessage().contains("Could not parse x,y string:baro"));

		exc = assertThrows(RuntimeException.class, () -> new Piece("0 1 2 lashuka 2 1 --"));
		assertTrue(exc.getMessage().contains("Could not parse x,y string:0 1 2 lashuka 2 1 --"));
	}
}
