package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "news_script_drafts")
data class NewsScriptDraft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val body: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface NewsScriptDao {
    @Query("SELECT * FROM news_script_drafts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<NewsScriptDraft>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: NewsScriptDraft)

    @Query("DELETE FROM news_script_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Int)

    @Query("DELETE FROM news_script_drafts")
    suspend fun clearAll()
}

@Database(entities = [NewsScriptDraft::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun newsScriptDao(): NewsScriptDao

    companion object {
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsDatabase::class.java,
                    "news_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class NewsRepository(private val newsScriptDao: NewsScriptDao) {
    val allDrafts: Flow<List<NewsScriptDraft>> = newsScriptDao.getAllDrafts()

    suspend fun insert(draft: NewsScriptDraft) {
        newsScriptDao.insertDraft(draft)
    }

    suspend fun delete(id: Int) {
        newsScriptDao.deleteDraftById(id)
    }

    suspend fun clear() {
        newsScriptDao.clearAll()
    }
}
