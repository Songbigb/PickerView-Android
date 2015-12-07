package com.lantouzi.library.pickerview;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
	@Test
	public void addition_isCorrect() throws Exception {
		assertEquals(4, 2 + 2);
	}

	@Test
	public void testMathFloor() {
		float t = 0.1f;
		for (int i = 0; i < 10; i++) {
			t = (t + i * 0.2f);
			System.out.println("t=" + t + ",floor:" + Math.floor(t) + ",round:" + Math.round(t));
		}
		assert true;
	}
}