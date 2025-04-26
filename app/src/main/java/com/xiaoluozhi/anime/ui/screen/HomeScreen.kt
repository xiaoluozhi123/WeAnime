package com.xiaoluozhi.anime.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.xiaoluozhi.anime.data.viewmodel.CarouselItem
import com.xiaoluozhi.anime.data.viewmodel.HomeIntent
import com.xiaoluozhi.anime.data.viewmodel.HomeViewModel
import com.xiaoluozhi.anime.ui.component.LoadingError
import com.xiaoluozhi.anime.ui.theme.AnimeTheme
import com.zj.shimmer.shimmer
import kotlin.collections.count

@Composable
fun HomeScreen() {
    // 创建viewmodel
    val viewModel: HomeViewModel = viewModel()
    // 获取state
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(HomeIntent.LoadingCarouse)
        viewModel.sendIntent(HomeIntent.LoadingHomeAnime)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
    ) {  // --- PopularItems 部分 ---
        item {
            PopularItems(
                state.value.popularItems,
                state.value.popularLoading,
                state.value.popularLoadError
            ) {
                viewModel.sendIntent(HomeIntent.LoadingCarouse)
            }
            // PopularItems 后面加间距
            Spacer(modifier = Modifier.height(16.dp)) // 增加一点间距
        }

        // --- RecommendedList 部分 (整合到 LazyColumn) ---
        // 1. "番剧推荐" 标题
        item {
            Text(
                "番剧推荐",
                style = MaterialTheme.typography.titleLarge,
                // PopularItems 已经有左右边距，这里可能不需要再加 start padding
                modifier = Modifier.padding(start = 5.dp, top = 10.dp, end = 5.dp)
            )
            Spacer(modifier = Modifier.height(10.dp)) // 标题和网格之间的间距
        }
        if (!state.value.animeLoadError) {
            // 2. 推荐列表项 - 网格布局 (每行3个)
            val totalRecommendedItems =
                if (state.value.animeLoading) 6 else state.value.animeList.count()
            val itemsPerRow = 3
            // 计算行数，(总数 + 每行数 - 1) / 每行数 是整数除法计算向上取整的常用方法
            val rowCount = (totalRecommendedItems + itemsPerRow - 1) / itemsPerRow

            items(count = rowCount, key = { rowIndex -> "row_$rowIndex" }) { rowIndex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shimmer(state.value.animeLoading)
                        .padding(vertical = 4.dp), // 每行之间的垂直间距
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // 项目之间的水平间距
                ) {
                    // 循环创建当前行的项目
                    for (colIndex in 0 until itemsPerRow) {
                        val itemIndex = rowIndex * itemsPerRow + colIndex

                        // 使用 Box 包裹并应用 weight，确保结构和对齐
                        Box(modifier = Modifier.weight(1f)) {
                            // 检查当前索引是否有效
                            if (itemIndex < totalRecommendedItems) {
                                // --- 这里放置你的实际列表项 UI ---
                                // 例如，一个简单的 Card 或 Text
                                Column( // 用 Column 包裹内容，方便扩展
                                    modifier = Modifier
                                        .fillMaxWidth() // 填满 weight 分配的空间
                                        // .background(MaterialTheme.colorScheme.surfaceVariant) // 加个背景看效果
                                        .padding(start = 4.dp, end = 4.dp) // 内容的内边距
                                ) {
                                    if (state.value.animeLoading) {
                                        Image(
                                            painter = colorBlockPainter(MaterialTheme.colorScheme.surfaceVariant),
                                            contentDescription = "加载中",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(180.dp)
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                        )
                                    } else {
                                        Column {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .height(180.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .fillMaxWidth(),
                                                model = state.value.animeList[itemIndex].cover,
                                                contentScale = ContentScale.Crop,
                                                contentDescription = "图片"
                                            )
                                            // 可以在这里放图片、文字等
                                            Text(
                                                text = state.value.animeList[itemIndex].name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = Ellipsis,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                    // ... 其他内容
                                }
                                // --- 实际列表项 UI 结束 ---
                            } else {
                                // 如果索引超出总数（最后一行不完整），
                                // 则此 Box (已应用 weight) 保持空白，以维持布局
                                Spacer(Modifier.fillMaxSize()) // 或者保持 Box 为空
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp)
                ) {
                    LoadingError {
                        viewModel.sendIntent(HomeIntent.LoadingHomeAnime)
                    }
                }
            }
        }
        item {
            if (state.value.animeList.count() > 0 && !state.value.isLoadingMore) {
                viewModel.sendIntent(HomeIntent.LoadingMoreAnime)
            }
        }
    }
}

@Composable
fun PopularItems(
    items: List<CarouselItem>,
    loading: Boolean,
    loadError: Boolean,
    onRefresh: () -> Unit = {}
) {
    Column {
        Text(
            "大家都在看",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        if (!loadError) {
            key(items) {
                HorizontalMultiBrowseCarousel(
                    state = rememberCarouselState { if (loading) 3 else items.count() },
                    modifier = Modifier
                        .height(205.dp)
                        .padding(start = 5.dp, top = 10.dp, end = 5.dp)
                        .shimmer(loading),
                    preferredItemWidth = 260.dp,
                    itemSpacing = 8.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        if (loading) {
                            Image(
                                painter = colorBlockPainter(MaterialTheme.colorScheme.surfaceVariant),
                                contentDescription = "加载中",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(205.dp)
                                    .fillMaxWidth()
                                    .maskClip(MaterialTheme.shapes.extraLarge)
                            )
                        } else {
                            AsyncImage(
                                modifier = Modifier
                                    .height(205.dp)
                                    .maskClip(MaterialTheme.shapes.extraLarge),
                                model = items[it].imageUrl,
                                contentScale = ContentScale.Crop,
                                contentDescription = items[it].name
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(
                                        start = 10.dp,
                                        bottom = 15.dp,
                                        end = 10.dp
                                    )
                            ) {
                                Text(
                                    text = items[it].name, // Use dynamic data
                                    style = MaterialTheme.typography.titleMedium, // Maybe slightly larger than bodySmall?
                                    maxLines = 1,
                                    overflow = Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                                Text(
                                    text = items[it].intro, // Use dynamic data
                                    style = MaterialTheme.typography.bodySmall, // Maybe slightly larger than bodySmall?
                                    maxLines = 1,
                                    overflow = Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .padding(
                                            top = 3.dp
                                        )
                                )
                            }

                        }
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(start = 5.dp, top = 10.dp, end = 5.dp)
            ) {
                LoadingError {
                    onRefresh()
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "页面") // 可以给 Preview 命名
@Composable
fun HomeScreenBarPreview() {
    AnimeTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, name = "受欢迎的番剧") // 可以给 Preview 命名
@Composable
fun PopularItemsBarPreview() {
    AnimeTheme {
        PopularItems(listOf(), loading = true, loadError = false)
    }
}

// 纯色绘制生成器
@Composable
fun colorBlockPainter(color: Color): Painter = remember(color) {
    object : Painter() {
        override val intrinsicSize: Size
            get() = Size.Unspecified

        override fun DrawScope.onDraw() {
            drawRect(color = color)
        }
    }
}