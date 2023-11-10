package com.azamovhudstc.graphqlanilist.data.local

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.azamovhudstc.graphqlanilist.data.model.ui_models.AnimeMetaModel
import com.azamovhudstc.graphqlanilist.data.model.ui_models.EpisodeEntity

/**
 *  It's a database class that has two tables, one for anime metadata and one for episode metadata
 *
 * */
@Database(
    entities = [ EpisodeEntity::class],
    version = 1,
    exportSchema = true
//    autoMigrations = [AutoMigration(from = 2, to = 3)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun episodeDao(): EpisodeDao
}