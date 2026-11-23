// Copyright 2023–2026 Skip
package skip.unit

import com.google.testing.junit.testparameterinjector.TestParameterValuesProvider
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.manipulation.Filter
import org.junit.runner.manipulation.Filterable
import org.junit.runner.manipulation.Sortable
import org.junit.runner.manipulation.Sorter
import org.junit.runner.notification.RunNotifier

/** Uses the existing parameter injector for both host and instrumented tests. */
class SkipParameterizedTestRunner(testClass: Class<*>): Runner(), Filterable, Sortable {
    private val delegate: Runner = run {
        val runnerName = if (System.getProperty("java.vm.name") == "Dalvik") {
            "com.google.testing.junit.testparameterinjector.TestParameterInjector"
        } else {
            "org.robolectric.RobolectricTestParameterInjector"
        }
        Class.forName(runnerName).getConstructor(Class::class.java).newInstance(testClass) as Runner
    }

    override fun getDescription(): Description = delegate.description
    override fun run(notifier: RunNotifier) = delegate.run(notifier)
    override fun filter(filter: Filter) = (delegate as Filterable).filter(filter)
    override fun sort(sorter: Sorter) = (delegate as Sortable).sort(sorter)
}

/** Argument providers are generated from Swift and evaluated in the test's class loader. */
abstract class SkipTestArgumentsProvider: TestParameterValuesProvider() {
    abstract fun arguments(): List<List<Any?>>

    override fun provideValues(context: Context): List<*> {
        val cases = arguments()
        if (cases.isEmpty()) {
            // JUnit reports an empty collection as skipped rather than an initialization error.
            return listOf(value(null).withName("no arguments"))
        }
        return cases.mapIndexed { index, arguments ->
            value(arguments).withName("$index: $arguments")
        }
    }
}
