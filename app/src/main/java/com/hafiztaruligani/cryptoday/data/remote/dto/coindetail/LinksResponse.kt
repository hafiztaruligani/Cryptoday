package com.hafiztaruligani.cryptoday.data.remote.dto.coindetail

import com.google.gson.annotations.SerializedName

data class LinksResponse(

    @field:SerializedName("subreddit_url")
    val subredditUrl: String? = null,

    @field:SerializedName("blockchain_site")
    val blockchainSite: List<String?>? = null,

    @field:SerializedName("official_forum_url")
    val officialForumUrl: List<String?>? = null,

    @field:SerializedName("telegram_channel_identifier")
    val telegramChannelIdentifier: String? = null,

    @field:SerializedName("twitter_screen_name")
    val twitterScreenName: String? = null,

    @field:SerializedName("bitcointalk_thread_identifier")
    val bitcointalkThreadIdentifier: Any? = null,

    @field:SerializedName("announcement_url")
    val announcementUrl: List<String?>? = null,

    @field:SerializedName("facebook_username")
    val facebookUsername: String? = null,

    @field:SerializedName("chat_url")
    val chatUrl: List<String?>? = null,

    @field:SerializedName("homepage")
    val homepage: List<String?>? = null
)
