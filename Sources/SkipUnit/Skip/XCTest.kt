// Copyright 2023 Skip
//
// This is free software: you can redistribute and/or modify it
// under the terms of the GNU Lesser General Public License 3.0
// as published by the Free Software Foundation https://fsf.org
package skip.unit

/// Alias for the @Test annotation to be able to use it without importing JUnit
typealias Test = org.junit.Test

/// Alias for an org.junit.AssumptionViolatedException, which will skip a test in the same way as conditionally throwing XCTSkip
typealias XCTSkip = org.junit.AssumptionViolatedException

/// Mimics the API of `XCTest` for a JUnit 4 test.
///
/// Behavior difference: JUnit `assert*` thows an exception, but `XCTAssert*` just reports the failure and continues
/// NOTE: the parameter order of JUnit 5's `org.junit.jupiter.api.Assertions` is the reverse of JUnit 4's `org.junit.Assert`
interface XCTestCase {

	@org.junit.Before
	fun beforeTest() {
		setUp()
	}

	/// The equivalent to XCTestCase setUp(), meant to be overridden in subclasses.
	fun setUp() {
	}

	@org.junit.After
	fun afterTest() {
		tearDown()
	}

	/// The equivalent to XCTestCase tearDown(), meant to be overridden in subclasses.
	fun tearDown() {
	}

    fun XCTFail(): Unit = org.junit.Assert.fail()

    fun XCTFail(msg: String): Unit = org.junit.Assert.fail(msg)

    fun <T> XCTUnwrap (ob: T?): T { org.junit.Assert.assertNotNull(ob); if (ob != null) { return ob }; throw AssertionError() }
    fun <T> XCTUnwrap(ob: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, ob); if (ob != null) { return ob }; throw AssertionError() }

    fun XCTAssert(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
    fun XCTAssert(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)
    fun XCTAssertTrue(a: Boolean): Unit = org.junit.Assert.assertTrue(a)
    fun XCTAssertTrue(a: Boolean, msg: String): Unit = org.junit.Assert.assertTrue(msg, a)
    fun XCTAssertFalse(a: Boolean): Unit = org.junit.Assert.assertFalse(a)
    fun XCTAssertFalse(a: Boolean, msg: String): Unit = org.junit.Assert.assertFalse(msg, a)

    fun XCTAssertNil(a: Any?): Unit = org.junit.Assert.assertNull(a)
    fun XCTAssertNil(a: Any?, msg: String): Unit = org.junit.Assert.assertNull(msg, a)
	fun <T> XCTAssertNotNil(a: T?): T { org.junit.Assert.assertNotNull(a); return a!! }
	fun <T> XCTAssertNotNil(a: T?, msg: String): T { org.junit.Assert.assertNotNull(msg, a); return a!! }

    fun XCTAssertIdentical(a: Any?, b: Any?): Unit = org.junit.Assert.assertSame(b, a)
    fun XCTAssertIdentical(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertSame(msg, b, a)
    fun XCTAssertNotIdentical(a: Any?, b: Any?): Unit = org.junit.Assert.assertNotSame(b, a)
    fun XCTAssertNotIdentical(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertNotSame(msg, b, a)

    fun XCTAssertEqual(a: Any?, b: Any?): Unit = org.junit.Assert.assertEquals(b, a)
    fun XCTAssertEqual(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertEquals("${a} is not equal to ${b} – " + msg, b, a)

    fun XCTAssertNotEqual(a: Any?, b: Any?): Unit = org.junit.Assert.assertNotEquals(b, a)
    fun XCTAssertNotEqual(a: Any?, b: Any?, msg: String): Unit = org.junit.Assert.assertNotEquals(msg, b, a)

	fun <T : Comparable<T>> XCTAssertEqual(a: T?, b: T?): Unit = org.junit.Assert.assertTrue("${a} != ${b}", a == b)
	fun <T : Comparable<T>> XCTAssertEqual(a: T?, b: T?, msg: String): Unit = org.junit.Assert.assertTrue(msg, a == b)
	fun <T : Comparable<T>> XCTAssertNotEqual(a: T?, b: T?): Unit = org.junit.Assert.assertFalse("${a} == ${b}", a == b)
	fun <T : Comparable<T>> XCTAssertNotEqual(a: T?, b: T?, msg: String): Unit = org.junit.Assert.assertFalse(msg, a == b)

	fun XCTAssertEqual(a: Double, b: Double, accuracy: Double): Unit = org.junit.Assert.assertEquals(b, a, accuracy)
	fun XCTAssertEqual(a: Double, b: Double, accuracy: Double, msg: String): Unit = org.junit.Assert.assertEquals(msg, b, a, accuracy)

	fun XCTAssertEqual(a: Float, b: Float, accuracy: Float): Unit = org.junit.Assert.assertEquals(b, a, accuracy)
	fun XCTAssertEqual(a: Float, b: Float, accuracy: Float, msg: String): Unit = org.junit.Assert.assertEquals(msg, b, a, accuracy)

    fun <T : Comparable<T>> XCTAssertGreaterThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !> ${b}", a > b)
    fun <T : Comparable<T>> XCTAssertGreaterThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a > b)
    fun <T : Comparable<T>> XCTAssertGreaterThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !>= ${b}", a >= b)
    fun <T : Comparable<T>> XCTAssertGreaterThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a >= b)

    fun <T : Comparable<T>> XCTAssertLessThan(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !< ${b}", a < b)
    fun <T : Comparable<T>> XCTAssertLessThan(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a < b)
    fun <T : Comparable<T>> XCTAssertLessThanOrEqual(a: T, b: T): Unit = org.junit.Assert.assertTrue("${a} !<= ${b}", a <= b)
    fun <T : Comparable<T>> XCTAssertLessThanOrEqual(a: T, b: T, msg: String): Unit = org.junit.Assert.assertTrue(msg, a <= b)

    // MARK: Additional overloads needed for XCTAssert*() which have different signatures on Linux (@autoclosures) than on Darwin platforms (direct values)

    fun XCTUnwrap(ob: () -> Any?) = { val x = ob(); org.junit.Assert.assertNotNull(x); x }
    fun XCTUnwrap(ob: () -> Any?, msg: () -> String) = { val x = ob(); org.junit.Assert.assertNotNull(msg(), x); x }

    fun XCTAssertTrue(a: () -> Boolean) = org.junit.Assert.assertTrue(a())
    fun XCTAssertTrue(a: () -> Boolean, msg: () -> String) = org.junit.Assert.assertTrue(msg(), a())
    fun XCTAssertFalse(a: () -> Boolean) = org.junit.Assert.assertFalse(a())
    fun XCTAssertFalse(a: () -> Boolean, msg: () -> String) = org.junit.Assert.assertFalse(msg(), a())

    fun XCTAssertNil(a: () -> Any?) = org.junit.Assert.assertNull(a())
    fun XCTAssertNil(a: () -> Any?, msg: () -> String) = org.junit.Assert.assertNull(msg(), a())
    fun XCTAssertNotNil(a: () -> Any?) = org.junit.Assert.assertNotNull(a())
    fun XCTAssertNotNil(a: () -> Any?, msg: () -> String) = org.junit.Assert.assertNotNull(msg(), a())

    fun XCTAssertIdentical(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertSame(b(), a())
    fun XCTAssertIdentical(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertSame(msg(), b(), a())
    fun XCTAssertNotIdentical(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertNotSame(b(), a())
    fun XCTAssertNotIdentical(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertNotSame(msg(), b(), a())

    fun XCTAssertEqual(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertEquals(b(), a())
    fun XCTAssertEqual(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertEquals(msg(), b(), a())
    fun XCTAssertNotEqual(a: () -> Any?, b: () -> Any?) = org.junit.Assert.assertNotEquals(b(), a())
    fun XCTAssertNotEqual(a: () -> Any?, b: () -> Any?, msg: () -> String) = org.junit.Assert.assertNotEquals(msg(), b(), a())

	fun measure(block: () -> Unit) = block()
}
