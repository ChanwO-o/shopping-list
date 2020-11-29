package com.androiddevs.shoppinglisttestingyt

import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // activities/fragments that are tested must be labeled
// This is a test activity; don't include in original manifest. Instead, create new manifest in debug
class HiltTestActivity : AppCompatActivity() {

}