package moviescraper.doctord.model.dataitem.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import moviescraper.doctord.model.dataitem.ID;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * JUnit test class to test the equalsJavID() method in ID
 */
public class EqualsJavIDTester {

	@BeforeClass
	public static void initialize() {
		/*
		 * do nothing for now
		 */
	}

	@Test
	public void testEqualsJavIDComplicatedCase() {
		ID id1 = new ID("73ABC-001SO");
		ID id2 = new ID("ABC-1");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsSimpleCase() {
		ID id1 = new ID("ABC-123");
		ID id2 = new ID("ABC-123");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsSimpleCaseIgnoreCase() {
		ID id1 = new ID("ABC-123");
		ID id2 = new ID("abc-123");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsSimpleCaseDashesIgnored() {
		ID id1 = new ID("ABC-123");
		ID id2 = new ID("ABC123");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsSimpleCaseDashesIgnoredSuffixInOne() {
		ID id1 = new ID("ABC-123");
		ID id2 = new ID("ABC123SO");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsDifferentNumberFormatSuffixAndPrefix() {
		ID id1 = new ID("73ABC-01");
		ID id2 = new ID("ABC001SO");
		assertTrue(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsJavIDInvalidFormat() {
		ID id1 = new ID("ABC-SO");
		ID id2 = new ID("ABC-001");
		assertFalse(id1.equalsJavID(id2));
	}

	@Test
	public void testEqualsJavIDNegativeCase() {
		ID id1 = new ID("ABC-123");
		ID id2 = new ID("ABC-001");
		assertFalse(id1.equalsJavID(id2));
	}

	@Test
	public void testBlanksIDs() {
		ID id1 = new ID("");
		ID id2 = new ID("");
		assertFalse(id1.equalsJavID(id2));
	}

	@Test
	public void testNullIds() {
		ID id1 = new ID("ABC-123");
		ID id2 = null;
		assertFalse(id1.equalsJavID(id2));
	}
}
