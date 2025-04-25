package com.xiaoluozhi.anime.ui.component

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xiaoluozhi.anime.ui.theme.AnimeTheme

@Composable
fun LoadingError(refresh: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(15.dp),
    ) {
        Row(
            // 让 Row 填满 Card 的空间，并添加内边距
            modifier = Modifier
                .fillMaxSize() // 填满 Card 的宽度和高度
                .padding(horizontal = 16.dp), // 添加水平内边距
            // 设置垂直居中对齐
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = "加载失败",
                tint = MaterialTheme.colorScheme.error,
            )
            Text(
                "加载失败",
                modifier = Modifier.padding(start = 15.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(Modifier.weight(1f))

            TextButton(onClick = refresh, shape = RoundedCornerShape(20.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "重试",
                    tint = Color(0xFF3F51B5),
                )
                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    text = "重试",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color(0xFF3F51B5),
                )

            }
        }
    }
}

@Preview(showBackground = true, name = "加载失败") // 可以给 Preview 命名
@Composable
fun HomeScreenBarPreview() {
    AnimeTheme {
        LoadingError(refresh = {
            Log.d("ceshi", "重试")
        })
    }
}