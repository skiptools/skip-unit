// Copyright 2023–2026 Skip
package skip.unit

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.JUnitCore
import org.junit.runner.RunWith

class ParameterizedTestingTests {
    class Cases: SkipTestArgumentsProvider() {
        override fun arguments(): List<List<Any?>> = listOf(listOf(1), listOf(2), listOf(3))
    }

    @RunWith(TestParameterInjector::class)
    class Fixture {
        private var calls = 0

        companion object {
            var verifyFailure = false
        }

        @Test
        fun independentCases(@TestParameter(valuesProvider = Cases::class) arguments: List<Any?>) {
            assertEquals(0, calls++)
            if (verifyFailure) assertNotEquals(2, arguments[0])
        }
    }

    @Test
    fun failureDoesNotStopOtherCases() {
        Fixture.verifyFailure = true
        val result = try {
            JUnitCore.runClasses(Fixture::class.java)
        } finally {
            Fixture.verifyFailure = false
        }
        assertEquals(3, result.runCount)
        assertEquals(1, result.failureCount)
        assertTrue(result.failures.single().description.displayName.contains("2"))
    }
}

private object SharedTestEnvironment {
    fun verifyClassLoader() {
        val key = "skip.unit.sharedTestEnvironment"
        val identity = System.identityHashCode(this).toString()
        val previous = System.getProperty(key)
        if (previous == null) {
            System.setProperty(key, identity)
        } else {
            assertEquals(previous, identity)
        }
    }
}

@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RegularEnvironmentTests {
    @Test
    fun sharesEnvironment() {
        SharedTestEnvironment.verifyClassLoader()
    }
}

@RunWith(SkipParameterizedTestRunner::class)
class ParameterizedEnvironmentTests {
    @Test
    fun sharesEnvironment(@TestParameter(valuesProvider = ParameterizedTestingTests.Cases::class) arguments: List<Any?>) {
        assertNotNull(arguments)
        SharedTestEnvironment.verifyClassLoader()
    }
}
