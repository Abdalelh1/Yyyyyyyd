package com.otaku.fetch

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.core.app.TaskStackBuilder
import com.otaku.fetch.base.download.DownloadItem

interface AppModule {
    /**
     *  Unique identifier
     * */
    val name: String

    val notificationDeeplink: String

    fun onSearch(query: String) {}

    fun getNavigationGraph(): Int = 0

    suspend fun triggerNotification(context: Context, defaultIntent: TaskStackBuilder) {}

    fun initialize(query: String?, link: String = "") {}
    fun getBottomNavigationMenu(): Int = 0

    @Composable
    fun ComposeTheme(content: @Composable() () -> Unit) {
    }

    suspend fun findEpisode(mediaId: String, mediaLink: String, mediaType: String): DownloadItem? =
        null
}
