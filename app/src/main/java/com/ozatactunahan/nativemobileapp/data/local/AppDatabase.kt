package com.ozatactunahan.nativemobileapp.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.ozatactunahan.nativemobileapp.data.model.CartItem
import com.ozatactunahan.nativemobileapp.data.model.Order
import com.ozatactunahan.nativemobileapp.data.model.Product
import com.ozatactunahan.nativemobileapp.data.local.dao.ProductDao
import com.ozatactunahan.nativemobileapp.data.local.dao.CartDao
import com.ozatactunahan.nativemobileapp.data.local.dao.OrderDao
import com.ozatactunahan.nativemobileapp.data.local.dao.FavoriteDao

@Database(
    entities = [
        Product::class,
        CartItem::class,
        Order::class,
        com.ozatactunahan.nativemobileapp.data.local.entity.FavoriteEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Favorites tablosunu oluştur
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `favorites` (
                        `productId` TEXT NOT NULL,
                        `name` TEXT NOT NULL,
                        `brand` TEXT NOT NULL,
                        `price` TEXT NOT NULL,
                        `imageUrl` TEXT NOT NULL,
                        `timestamp` INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(`productId`)
                    )
                """)
            }
        }
    }
}
