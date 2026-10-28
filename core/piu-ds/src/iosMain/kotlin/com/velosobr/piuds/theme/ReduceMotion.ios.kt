// "Reduzir animações" no iOS: o ajuste "Reduzir movimento" de Acessibilidade.

package com.velosobr.piuds.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

@Composable
internal actual fun systemReduceMotionReader(): () -> Boolean = remember { { UIAccessibilityIsReduceMotionEnabled() } }
