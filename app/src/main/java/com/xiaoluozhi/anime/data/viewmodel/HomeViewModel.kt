package com.xiaoluozhi.anime.data.viewmodel

import androidx.lifecycle.scopeNetLife
import com.drake.net.Get
import com.drake.net.Post
import com.google.gson.JsonParser
import org.jsoup.Jsoup
import java.math.BigInteger
import java.security.MessageDigest

private const val url = "https://www.cycani.org"

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
    val tags: String,
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
    val page: String = "1",
    val isLoadingMore: Boolean = false,
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
                            copy(
                                popularItems = loadingCarouse(html),
                                popularLoading = false,
                                popularLoadError = false
                            )
                        }
                    } catch (e: Exception) {
                        setState {
                            copy(popularLoading = false, popularLoadError = true)
                        }
                    }

                }
            }

            is HomeIntent.LoadingHomeAnime -> {
                scopeNetLife {
                    setState {
                        copy(page = "1", animeLoading = true, animeLoadError = false)
                    }
                    try {
                        val (key, time) = generateApiKeyParts()

                        val json = Post<String>("${url}/index.php/api/vod") {
                            param("type", 20)
                            param("class", "")
                            param("area", "")
                            param("lang", "")
                            param("version", "")
                            param("state", "")
                            param("letter", "")
                            param("page", 1)
                            param("time", time)
                            param("key", key)
                        }.await()

                        setState {
                            copy(
                                animeList = loadingAnime(json),
                                animeLoading = false,
                                animeLoadError = false
                            )
                        }
                    } catch (e: Exception) {
                        setState {
                            copy(animeLoading = false, animeLoadError = true)
                        }
                    }
                }
            }

            is HomeIntent.LoadingMoreAnime -> {
                scopeNetLife {
                    setState {
                        copy(
                            page = (page.toInt() + 1).toString(),
                            isLoadingMore = true
                        )
                    }
                    try {
                        val (key, time) = generateApiKeyParts()

                        val json = Post<String>("${url}/index.php/api/vod") {
                            param("type", 20)
                            param("class", "")
                            param("area", "")
                            param("lang", "")
                            param("version", "")
                            param("state", "")
                            param("letter", "")
                            param("page", state.value.page)
                            param("time", time)
                            param("key", key)
                        }.await()

                        val animeList: List<Anime> = state.value.animeList + loadingAnime(json)

                        setState {
                            copy(
                                animeList = animeList,
                                animeLoading = false,
                                animeLoadError = false,
                                isLoadingMore = false
                            )
                        }
                    } catch (e: Exception) {
                        setState {
                            copy(animeLoading = false, animeLoadError = true, isLoadingMore = false)
                        }
                    }
                }
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

private fun loadingAnime(json: String): List<Anime> {
    val animeList: MutableList<Anime> = mutableListOf()
    val animeJsonList = JsonParser.parseString(json).asJsonObject.get("list").asJsonArray
    animeJsonList.forEach {
        val itemObject = it.asJsonObject
        val name = itemObject.get("vod_name").asString
        val cover = itemObject.get("vod_pic").asString
        val detailUrl = "bangumi/${itemObject.get("vod_id").asString}.html"
        val tags = itemObject.get("vod_class").asString
        val intro = itemObject.get("vod_blurb").asString
        val score = itemObject.get("vod_score").asString
        val remarks = itemObject.get("vod_remarks").asString
        animeList.add(Anime(name, cover, detailUrl, tags, intro, score, remarks))
    }
    return animeList
}

/*
private fun loadingAnime(html: String): List<Anime> {
    val doc = Jsoup.parse(html)
    Log.d("ceshi", html)
    val animeList: MutableList<Anime> = mutableListOf()
    Log.d("ceshi", doc.select("#dataList").attr("data-txt"))
    doc.select(".list-vod > div").forEach {
        Log.d("ceshi", it.toString())
        Log.d("ceshi", "执行了")
        val name = it.select(".time-title").text()
        val cover = it.select(".gen-movie-img").attr("src")
        val detailUrl = it.select(".public-list-exp").attr("href")
        animeList.add(Anime(name, cover, detailUrl, "", "", "", ""))
    }
    Log.d("ceshi", animeList.toString())
    return animeList
}
废弃代码
 */

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

/**
 * 计算字符串的 MD5 哈希值并返回 32 位小写十六进制字符串。
 * @param input 输入字符串
 * @return 32 位小写十六进制 MD5 哈希值
 */
private fun getMd5Hex(input: String): String {
    try {
        // 获取MD5MessageDigest实例
        val md = MessageDigest.getInstance("MD5")

        // 计算哈希值
        val messageDigest: ByteArray = md.digest(input.toByteArray(Charsets.UTF_8))

        // 将字节数组转换为BigInteger
        val no = BigInteger(1, messageDigest)

        // 将BigInteger转换为16进制字符串
        var hashtext = no.toString(16)

        // 补齐前导零
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }

        // 返回哈希值
        return hashtext.lowercase()

    } catch (e: Exception) {
        throw RuntimeException("计算出现错误", e)
    }
}

/**
 * 生成 API 请求所需的 key 和 time 参数。
 * @return Pair<String, String> 第一个元素是计算出的 key，第二个元素是用于计算的时间戳（字符串格式）
 */
fun generateApiKeyParts(): Pair<String, String> {
    // 获取时间戳
    val timestampSeconds = System.currentTimeMillis() / 1000

    // 定义前缀和盐值
    val fixedPrefix = "DS"
    val fixedSalt = "DCC147D11943AF75"

    // 拼接
    val inputString = "$fixedPrefix$timestampSeconds$fixedSalt"

    // 计算哈希值
    val apiKey = getMd5Hex(inputString)

    // Key和时间戳
    return Pair(apiKey, timestampSeconds.toString())
}