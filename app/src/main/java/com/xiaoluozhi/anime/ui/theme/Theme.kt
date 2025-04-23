package com.xiaoluozhi.anime.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// --- 深色模式配色方案 ---
// 深色模式下，“亮粉色”通常指更饱和、更鲜艳的粉色，而不是浅色
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF8AB4),        // 主色：更明亮的粉色 (比之前更亮)
    secondary = Color(0xFFFFA8C7),      // 次色：亮樱花粉
    tertiary = Color(0xFFFFC0D1),       // 第三色：亮浅粉
    surface = Color(0xFF251A1D),        // 背景/卡片色：非常深的紫粉色
    onPrimary = Color.Black,            // 主色上的文字：黑色（确保高对比）
    onSecondary = Color.Black,          // 次色上的文字：黑色
    onSurface = Color(0xFFEDE0E2),      // 常规文字：非常浅的粉灰

    // --- NavigationBar 亮粉色主题 (暗色模式) ---
    surfaceContainer = Color(0xFF3A2A2F), // 导航栏背景: 略带粉调的暗色
    secondaryContainer = Color(0xFFD85D8B), // 选中项背景: 使用一个鲜艳的玫瑰粉
    onSecondaryContainer = Color(0xFFFFFFFF), // 选中项文字/图标: 白色，确保在鲜艳粉色上清晰
    onSurfaceVariant = Color(0xFFDDC1C9)  // 未选中项文字/图标: 浅粉灰色
)

// --- 浅色模式配色方案 (重点调整这里以实现“亮粉色”) ---
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFF4729B),        // 主色：明亮的桃粉色
    secondary = Color(0xFFFF9EB5),      // 次色：鲜亮的樱花粉 (可以用作选中色)
    tertiary = Color(0xFFFFB8D1),       // 第三色：活泼的浅粉色
    surface = Color(0xFFFFF8F9),        // 背景/卡片色：非常浅的、接近白色的粉底
    onPrimary = Color.White,            // 主色上的文字：白色
    onSecondary = Color.Black,          // 次色上的文字：黑色
    onSurface = Color(0xFF201A1B),      // 常规文字：深色（接近黑，带微弱粉调）

    // --- NavigationBar 亮粉色主题 (浅色模式) ---
    surfaceContainer = Color(0xFFFFE4EB), // 导航栏背景: 清晰明亮的浅粉色
    secondaryContainer = Color(0xFFFFB0C8), // 选中项背景: 选用一个更活泼、饱和度稍高的亮粉色作为指示器
    onSecondaryContainer = Color(0xFF5E1D3E), // 选中项文字/图标: 深洋红色，在亮粉背景上提供良好对比
    onSurfaceVariant = Color(0xFF6A4F58)    // 未选中项文字/图标: 柔和的粉调灰褐色，与背景区分但不过于抢眼
)


@Composable
fun AnimeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, content = content
    )
}