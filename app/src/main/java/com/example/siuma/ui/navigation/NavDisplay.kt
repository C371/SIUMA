package com.example.siuma.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun NavDisplay(
    backStack: SnapshotStateList<Route>,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable (Route) -> Unit
) {
    val currentRoute = backStack.lastOrNull()
    if (currentRoute != null) {
        content(currentRoute)
    }
}
