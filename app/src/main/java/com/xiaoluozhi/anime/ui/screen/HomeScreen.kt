package com.xiaoluozhi.anime.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun HomeScreen() {
    // 创建viewmodel
    val viewModel: HomeViewModel = viewModel()
    // 获取state
    val state = viewModel.state.collectAsState()

    var scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(HomeIntent.LoadingCarouse)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 10.dp, end = 10.dp)
    ) {
        PopularItems(
            state.value.popularItems,
            state.value.popularLoading,
            state.value.popularLoadError
        ) {
            viewModel.sendIntent(HomeIntent.LoadingCarouse)
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
            "受欢迎的番剧",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 5.dp)
        )
        if (!loadError) {
            key(items) {
                HorizontalMultiBrowseCarousel(
                    state = rememberCarouselState { if (loading) 3 else items.count() },
                    modifier = Modifier
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

@Composable
fun RecommendedList() {

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

@Preview(showBackground = true, name = "推荐列表") // 可以给 Preview 命名
@Composable
fun RecommendedListPreview() {
    AnimeTheme {
        RecommendedList()
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