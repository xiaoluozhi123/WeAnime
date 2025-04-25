package com.xiaoluozhi.anime.data.viewmodel

import android.util.Log
import androidx.lifecycle.scopeNetLife
import com.drake.net.Get
import org.jsoup.Jsoup

private const val url = "https://www.cycani.org/"

// 轮播图
data class CarouselItem(
    val name: String,
    val imageUrl: String,
    val intro: String,
    val detailUrl: String
)

// 番剧
data class Anime(
    // 番剧名
    val name: String,
    // 封面链接
    val cover: String,
    // 详情页链接
    val detailUrl: String,
    // 番剧标签
    val tags: List<String>,
    // 番剧简介
    val intro: String,
    // 番剧评分
    val score: String,
    // 番剧时间或集数
    val remarks: String
)

data class HomeState(
    val popularItems: List<CarouselItem> = emptyList(),
    val popularLoading: Boolean = true,
    val popularLoadError: Boolean = false,

    val animeList: List<Anime> = emptyList(),
    val animeLoading: Boolean = true,
    val animeLoadError: Boolean = false
) : BaseState

sealed class HomeIntent : BaseIntent {
    // 获取轮播图列表
    data object LoadingCarouse : HomeIntent()
    data object LoadingHomeAnime : HomeIntent()
    data object LoadingMoreAnime : HomeIntent()
}

class HomeViewModel : BaseViewModel<HomeState, HomeIntent>() {
    override fun initState(): HomeState = HomeState()

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadingCarouse -> {
                scopeNetLife {
                    setState {
                        copy(popularLoading = true, popularLoadError = false)
                    }
                    try {
                        val html = Get<String>(url).await()
                        setState {
                            copy(popularItems = loadingCarouse(html), false, false)
                        }
                    } catch (e: Exception) {
                        setState {
                            copy(popularLoading = false, popularLoadError = true)
                        }
                    }

                }
            }

            is HomeIntent.LoadingHomeAnime -> {

            }

            is HomeIntent.LoadingMoreAnime -> {

            }
        }
    }
}

private fun loadingCarouse(html: String): List<CarouselItem> {
    val doc = Jsoup.parse(html)
    val carouselList: MutableList<CarouselItem> = mutableListOf()
    doc.select(".slide-time-list").select(".swiper-wrapper > div").forEach {
        val name = it.select(".slide-info-title").text()
        val imageUrl = extractUrlUsingSubstrings(it.select(".slide-wap").attr("style"))
        val intro = it.select(".slide-info").text()
        val detailUrl = it.select(".lank").attr("href")
        val carouselItem = CarouselItem(name, imageUrl!!, intro, detailUrl)
        carouselList.add(carouselItem)
    }
    return carouselList
}

private fun extractUrlUsingSubstrings(cssString: String): String? {
    val prefix = "url('"
    val suffix = "');"

    // 检查字符串是否包含预期的前缀和后缀
    if (cssString.contains(prefix) && cssString.endsWith(suffix)) {
        // 1. 获取 "url('" 之后的部分
        val afterPrefix = cssString.substringAfter(prefix)
        // 2. 获取 ");" 之前的部分
        val url = afterPrefix.substringBeforeLast(suffix)
        return url
    }
    return null // 格式不匹配
}