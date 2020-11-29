package com.androiddevs.shoppinglisttestingyt

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// IRL coroutines require a main dispatcher, which needs Android context (device or emulator)
// the main looper is not available for local tests.
// This MainCoroutineRule will replace the main dispatcher with a test dispatcher
// use this custom dispatcher Rule in every test class that uses coroutines
@ExperimentalCoroutinesApi
class MainCoroutineRule(
    private val dispatcher: CoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher(), TestCoroutineScope by TestCoroutineScope(dispatcher) {

    // called when starting a coroutine with this dispatcher and this rule active
    override fun starting(description: Description?) {
        super.starting(description)
        // choose different dispatcher for main dispatcher
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        cleanupTestCoroutines()
        Dispatchers.resetMain() // un-do .setMain() above by setting main dispatcher to the real one again
    }
}