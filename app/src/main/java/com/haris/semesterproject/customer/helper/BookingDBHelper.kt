package com.haris.semesterproject.customer.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.haris.semesterproject.customer.data.NewBooking

class BookingDBHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private const val DB_NAME = "bookings.db"
        private const val DB_VERSION = 1
        private const val TABLE_BOOKINGS = "bookings"

        private const val COL_ID = "booking_id"
        private const val COL_WORKSHOP = "workshop_name"
        private const val COL_STATUS = "status"
        private const val COL_DATE = "date"
        private const val COL_TIME = "time"
        private const val COL_ADDRESS = "address"
        private const val COL_SERVICES = "services"
        private const val COL_PRICE = "price"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_BOOKINGS (
                $COL_ID TEXT PRIMARY KEY,
                $COL_WORKSHOP TEXT,
                $COL_STATUS TEXT,
                $COL_DATE TEXT,
                $COL_TIME TEXT,
                $COL_ADDRESS TEXT,
                $COL_SERVICES TEXT,
                $COL_PRICE TEXT
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_BOOKINGS")
        onCreate(db)
    }

    fun insertBookings(bookings: List<NewBooking>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete(TABLE_BOOKINGS, null, null) // clear old data
            for (b in bookings) {
                val cv = ContentValues().apply {
                    put(COL_ID, b.bookingId)
                    put(COL_WORKSHOP, b.workshopName)
                    put(COL_STATUS, b.status)
                    put(COL_DATE, b.date)
                    put(COL_TIME, b.time)
                    put(COL_ADDRESS, b.address)
                    put(COL_SERVICES, b.services.joinToString(","))
                    put(COL_PRICE, b.price)
                }
                db.insert(TABLE_BOOKINGS, null, cv)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getAllBookings(): List<NewBooking> {
        val list = mutableListOf<NewBooking>()
        val db = readableDatabase
        val cursor = db.query(TABLE_BOOKINGS, null, null, null, null, null, "$COL_DATE DESC")
        if (cursor.moveToFirst()) {
            do {
                val booking = NewBooking(
                    bookingId = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)),
                    workshopName = cursor.getString(cursor.getColumnIndexOrThrow(COL_WORKSHOP)),
                    status = cursor.getString(cursor.getColumnIndexOrThrow(COL_STATUS)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)),
                    time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME)),
                    address = cursor.getString(cursor.getColumnIndexOrThrow(COL_ADDRESS)),
                    services = cursor.getString(cursor.getColumnIndexOrThrow(COL_SERVICES)).split(","),
                    price = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRICE))
                )
                list.add(booking)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}
