package com.xiaoluozhi.anime.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoluozhi.anime.ui.theme.AnimeTheme

@Composable
fun HomeScreen() {
    var scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 10.dp, end = 10.dp)
    ) {
        PopularItems()
    }
}


@Composable
fun PopularItems() {
    Column {
        Text(
            "受欢迎的番剧",
            style = MaterialTheme.typography.headlineSmall
        )

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
        PopularItems()
    }
}