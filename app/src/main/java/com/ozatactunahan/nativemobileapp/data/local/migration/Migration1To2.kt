package com.ozatactunahan.nativemobileapp.data.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `cart_items` (
                `productId` TEXT NOT NULL,
                `name` TEXT NOT NULL,
                `brand` TEXT NOT NULL,
                `price` TEXT NOT NULL,
                `imageUrl` TEXT NOT NULL,
                `quantity` INTEGER NOT NULL DEFAULT 1,
                `addedAt` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`productId`)
            )
        """)
    }
}
