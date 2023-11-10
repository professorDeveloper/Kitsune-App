package com.azamovhudstc.graphqlanilist.data.local

import androidx.room.*
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeEntity
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episodeEntity: EpisodeEntity)

    @Update
    suspend fun updateEpisode(episodeEntity: EpisodeEntity)

    @Query("SELECT * FROM EpisodeEntity WHERE episodeUrl =:episodeUrl")
    fun getEpisodeContent(episodeUrl: String): Flow<EpisodeEntity>

    @Query("SELECT * FROM EpisodeEntity WHERE malId = :malId")
    fun getEpisodesByAnime(malId: Int): Flow<List<EpisodeEntity>>

    @Query("SELECT EXISTS(SELECT * FROM EpisodeEntity WHERE episodeUrl = :episodeUrl LIMIT 1)")
    suspend fun isEpisodeOnDatabase(episodeUrl: String): Boolean
}
