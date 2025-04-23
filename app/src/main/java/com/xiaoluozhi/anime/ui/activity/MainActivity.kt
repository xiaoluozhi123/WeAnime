package com.xiaoluozhi.anime.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem // 导入 Material 3 的 NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // 导入 getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy // 正确导入 hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination // 正确导入 findStartDestination
import androidx.navigation.compose.* // 导入 navigation-compose 的扩展
import com.xiaoluozhi.anime.data.viewmodel.MainIntent
import com.xiaoluozhi.anime.data.viewmodel.MainViewModel
import com.xiaoluozhi.anime.ui.screen.HomeScreen
import com.xiaoluozhi.anime.ui.screen.CategoryScreen
import com.xiaoluozhi.anime.ui.screen.UserScreen
import com.xiaoluozhi.anime.ui.theme.AnimeTheme

// 定义导航目的地 (屏幕)
// 使用密封类来定义每个屏幕，包含路由、标签和图标
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Home : Screen("home", "首页", Icons.Outlined.Home)
    object Category : Screen("category", "分类", Icons.Outlined.Category)
    object User : Screen("user", "我的", Icons.Outlined.Person) // 使用 Category 图标
}

// 创建包含所有屏幕的列表
val items = listOf(
    Screen.Home,
    Screen.Category,
    Screen.User,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 启用沉浸式布局 (Edge-to-Edge)，建议在 setContent 之前调用
        enableEdgeToEdge()
        setContent {
            AnimeTheme {
                App()
            }
        }
    }
}

@Composable
private fun App() {
    // 创建ViewModel
    val viewModel: MainViewModel = viewModel()
    // 获取State
    val state by viewModel.state.collectAsState()

    // 创建导航控制器
    val navController = rememberNavController()

    // 监听导航变化并发送 Intent
    LaunchedEffect(navController, viewModel) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val route = backStackEntry.destination.route
            val newTitle = items.find { it.route == route }?.label ?: Screen.Home.label
            viewModel.sendIntent(MainIntent.UpdateTitle(newTitle))
        }
    }

    // 使用 Scaffold 构建基本布局
    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface, // 使用主题背景色
        topBar = { MainTopAppBar(title = state.title) },
        bottomBar = {
            // 在 Scaffold 的 bottomBar 中创建 NavigationBar
            MainNavigationBar(navController)
        }
    ) { innerPadding -> // Scaffold 提供内边距，用于主内容区域
        // 使用 NavHost 作为主内容区域，根据路由显示不同屏幕
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route, // 设置起始页为首页
//            enterTransition = { EnterTransition.None }, // 无进入动画
//            exitTransition = { ExitTransition.None },   // 无退出动画
//            popEnterTransition = { EnterTransition.None }, // 无返回进入动画 (例如按返回键)
//            popExitTransition = { ExitTransition.None },    // 无返回退出动画
            modifier = Modifier
                .padding(innerPadding) // 应用 Scaffold 提供的内边距，避免内容与导航栏重叠
                .fillMaxSize() // 让内容区域充满可用空间
        ) {
            // 9. 定义每个路由对应的 Composable 界面
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Category.route) { CategoryScreen() }
            composable(Screen.User.route) { UserScreen() }
        }
    }
}

@Composable
fun MainNavigationBar(navController: NavController) {
    NavigationBar {
        // 获取当前的导航栈条目状态
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        // 获取当前的目的地
        val currentDestination = navBackStackEntry?.destination

        // 遍历所有屏幕，为每个屏幕创建一个 NavigationBarItem
        items.forEach { screen ->
            NavigationBarItem(
                // *** 关键修改在这里 ***
                colors = NavigationBarItemDefaults.colors(
                    // -- 选中时的颜色 --
                    // 使用主题中定义的、与选中指示器背景对比度好的颜色
                    // 通常是 onSecondaryContainer 或 primary
                    selectedTextColor = MaterialTheme.colorScheme.primary, // 文字颜色与图标一致

                    // -- 未选中时的颜色 --
                    // 使用主题中定义的、在导航栏背景上可见但又不抢眼的颜色
                    // 通常是 onSurfaceVariant
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                icon = { Icon(screen.icon, contentDescription = screen.label) }, // 设置图标
                label = { Text(screen.label) }, // 设置标签文字
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true, // 判断是否选中

                onClick = {
                    // 点击时导航到对应的路由
                    navController.navigate(screen.route) {
                        // 弹出到导航图的起始目的地，避免在后退栈中堆积大量目的地
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true // 保存状态
                        }
                        // 避免重复创建同一个目的地的多个实例
                        launchSingleTop = true
                        // 重新选择先前选中的项目时恢复状态
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainTopAppBar(title: String) {
    TopAppBar(
        title = {
            Text(title)
        },
        actions = {
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Outlined.Search, contentDescription = "搜索")
            }
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Outlined.Settings, contentDescription = "设置")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            // 将容器颜色设置为主题的 surface 颜色
            containerColor = MaterialTheme.colorScheme.surface,

            // (可选) 如果你的 surface 颜色比较特殊（比如很亮或很暗），
            // 你可能需要显式设置标题和图标的颜色以保证对比度
            // titleContentColor = MaterialTheme.colorScheme.onSurface,
            // actionIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant // 或者 onSurface
        )
    )
}

// --- 预览 ---
@Preview(showBackground = true, name = "首页") // 可以给 Preview 命名
@Composable
fun HomePreview() {
    // 在预览中也调用 App() 来查看整体布局
    // 如果有主题，预览时也最好包裹起来 YourTheme { App() }
    AnimeTheme {
        App()
    }
}

// --- 预览 ---
@Preview(showBackground = true, name = "导航栏") // 可以给 Preview 命名
@Composable
fun NavigationBarPreview() {
    // 在预览中也调用 App() 来查看整体布局
    // 如果有主题，预览时也最好包裹起来 YourTheme { App() }
    AnimeTheme {
        val navController = rememberNavController()
        MainNavigationBar(navController)
    }
}

// --- 预览 ---
@Preview(showBackground = true, name = "顶部栏") // 可以给 Preview 命名
@Composable
fun TopAppBarPreview() {
    // 在预览中也调用 App() 来查看整体布局
    // 如果有主题，预览时也最好包裹起来 YourTheme { App() }
    AnimeTheme {
        MainTopAppBar(title = "首页")
    }
}
