package com.example.siuma.ui.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

val LocalBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error("No BackStack")
}
