package com.otaku.kickassanime.page.search

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.otaku.kickassanime.db.KickassAnimeDb
import com.otaku.kickassanime.db.models.entity.AnimeEntityWithPage
import com.otaku.kickassanime.db.models.entity.SearchResultEntity
import com.otaku.kickassanime.utils.asAnimeEntity
import com.otaku.kickassanime.utils.asAnimeGenreEntity
import com.otaku.kickassanime.utils.asLanguageEntity

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val repository: SearchRepository,
    private val db: KickassAnimeDb,
    private val query: String,
    private val genre: List<String>? = null,
    private val language: List<String>? = null,
    private val year: Int? = null,
    private val status: String? = null,
    private val type: String? = null
) : RemoteMediator<Int, AnimeEntityWithPage>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeEntityWithPage>
    ): MediatorResult {
        try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.pageNumber?.plus(1) ?: 1
            }

            val search = repository.search(query, page, genre, language, year, status)


            db.withTransaction {
                val id = hashCode()
                db.animeEntityDao().insertAll(search.result.map { it.asAnimeEntity() })
                db.animeLanguageDao().insertAll(search.result.flatMap { it.asLanguageEntity() })
                db.animeGenreDao().insertAll(search.result.flatMap { it.asAnimeGenreEntity() })
                db.searchDao().insertAllReplace(
                    search.result.map {
                        SearchResultEntity(
                            id,
                            it.slug ?: "",
                            page,
                            System.currentTimeMillis()
                        )
                    }
                )

                if (db.searchDao().uniqueCount() > 10) {
                    val leastRecentlyUsed = db.searchDao().getLeastRecentlyUsed()
                    if (leastRecentlyUsed.id != id) {
                        db.searchDao().deleteAllById(leastRecentlyUsed.id)
                    }
                }
            }

            return MediatorResult.Success(endOfPaginationReached = search.result.isEmpty())
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SearchRemoteMediator

        if (query != other.query) return false
        if (genre != other.genre) return false
        if (language != other.language) return false
        if (year != other.year) return false
        if (status != other.status) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = query.hashCode()
        result = 31 * result + (genre?.hashCode() ?: 0)
        result = 31 * result + (language?.hashCode() ?: 0)
        result = 31 * result + (year ?: 0)
        result = 31 * result + (status?.hashCode() ?: 0)
        result = 31 * result + (type?.hashCode() ?: 0)
        return result
    }

}

