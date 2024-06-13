package com.example.rtugroupproject

import android.content.ContentValues                // Importē ContentValues, lai saglabātu vērtības, kas tiks ievietotas vai atjauninātas datubāzē
import android.content.Context                       // Importē Context, lai piekļūtu resursiem un citiem lietojumprogrammas specifiskiem datiem
import android.database.Cursor                      // Importē Cursor, lai pārvaldītu rezultātu kopas, kas tiek atgrieztas no SQL vaicājumiem
import android.database.sqlite.SQLiteDatabase       // Importē SQLiteDatabase, lai pārvaldītu SQLite datubāzes
import android.database.sqlite.SQLiteOpenHelper     // Importē SQLiteOpenHelper, lai pārvaldītu datubāzes izveidi un atjaunināšanu


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Definē datubāzes nosaukumu un versiju
        private const val DATABASE_NAME = "UserInput.db"
        private const val DATABASE_VERSION = 6

        // Tabulas un kolonnu nosaukumi, kas palīdz uzturēt datu struktūru konsekventu
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_TIME = "time"
    }

    // Šī metode izveido tabulu, kad datubāze tiek pirmo reizi izveidota
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT" + ")")
        // Izpilda SQL vaicājumu tabulas izveidei
        db.execSQL(createTable)
    }

    // Šī metode tiek izsaukta, kad datubāze tiek atjaunināta, nodrošinot datu struktūras saderību
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Dzēš esošo tabulu, ja tā pastāv, lai nodrošinātu, ka struktūras izmaiņas tiek piemērotas pareizi
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Funkcija pievieno jaunu uzdevumu datubāzei
    fun insertTask(title: String, description: String, date: String, time: String): Long {
        val db = this.writableDatabase                                      // Iegūst rakstāmu datubāzes instance
        val contentValues = ContentValues()                                 // Izveido ContentValues objektu, lai saglabātu datus
        contentValues.put(COLUMN_TITLE, title)                              // Pievieno uzdevuma virsrakstu
        contentValues.put(COLUMN_DESCRIPTION, description)                 // Pievieno uzdevuma aprakstu
        contentValues.put(COLUMN_DATE, date)                               // Pievieno uzdevuma datumu
        contentValues.put(COLUMN_TIME, time)                               // Pievieno uzdevuma laiku
        return db.insert(TABLE_NAME, null, contentValues)     // Ievieto datus tabulā un atgriež jaunā ieraksta ID
    }

    // Funkcija iegūst visus uzdevumus no datubāzes
    fun getAllTasks(): List<Task> {
        val taskList = mutableListOf<Task>()                                                // Izveido tukšu uzdevumu sarakstu
        val db = this.readableDatabase                                                     // Iegūst lasāmu datubāzes instance
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null) // Izpilda SQL vaicājumu, lai iegūtu visus ierakstus no tabulas

        if (cursor.moveToFirst()) { // Pārbauda, vai ir vismaz viens ieraksts
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))                     // Iegūst uzdevuma ID
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))             // Iegūst uzdevuma virsrakstu
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) // Iegūst uzdevuma aprakstu
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))         // Iegūst uzdevuma datumu
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))               // Iegūst uzdevuma laiku
                taskList.add(Task(id, title, description, dateString, time))                         // Pievieno uzdevumu sarakstam
            } while (cursor.moveToNext())                                                            // Turpina ar nākamo ierakstu, ja tāds ir
        }
        cursor.close()                         // Aizver kursoru, lai atbrīvotu resursus
        return taskList.sortedBy { it.date }   // Atgriež uzdevumu sarakstu, sakārtotu pēc datuma
    }

    // Funkcija dzēš konkrētu uzdevumu no datubāzes pēc ID
    fun deleteTask(taskId: Long) {
        val db = this.writableDatabase                                                 // Iegūst rakstāmu datubāzes instance
        db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(taskId.toString())) // Dzēš uzdevumu, izmantojot ID kā kritēriju
        db.close()                                                                    // Aizver datubāzes savienojumu, lai atbrīvotu resursus
    }

    // Funkcija iztīra visus ierakstus no uzdevumu tabulas
    fun clearDatabase() {
        val db = this.writableDatabase                  // Iegūst rakstāmu datubāzes instance
        db.execSQL("DELETE FROM $TABLE_NAME")       // Izpilda SQL vaicājumu, lai dzēstu visus ierakstus no tabulas
        db.close()                                     // Aizver datubāzes savienojumu, lai atbrīvotu resursus
    }
}
