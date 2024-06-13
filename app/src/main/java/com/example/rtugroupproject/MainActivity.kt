// ChatGPT izmantotie prompti
// 1. How to setup SQLite database in Android Studio
// 2. Implament calander and time slider in kotlin
// 3. Create database table with SQLite in Android Studio
// 4. How to see database data in Adnroid studio
// 5. Create popup window on button click in Android Studio
// 6. Send data to databse on button click

package com.example.rtugroupproject

import android.app.AlertDialog              // Importē AlertDialog, lai izveidotu un pārvaldītu dialoga logus
import android.app.DatePickerDialog         // Importē DatePickerDialog, lai parādītu dialogu datuma izvēlei
import android.app.TimePickerDialog         // Importē TimePickerDialog, lai parādītu dialogu laika izvēlei
import android.os.Bundle                    // Importē Bundle, lai saglabātu un atjaunotu aktivitātes stāvokli
import android.util.Log                     // Importē Log, lai veiktu kļūdu un informācijas pierakstīšanu log failā
import android.view.Menu                    // Importē Menu, lai izveidotu un pārvaldītu izvēlnes elementus
import android.view.MenuItem                // Importē MenuItem, lai apstrādātu izvēlnes elementu izvēli
import android.view.View                    // Importē View, lai pārvaldītu UI komponentu pamata klasi
import android.widget.Button                // Importē Button, lai izmantotu pogas elementus izkārtojumā
import android.widget.EditText              // Importē EditText, lai izmantotu teksta ievades laukus izkārtojumā
import android.widget.TextView              // Importē TextView, lai izmantotu teksta parādīšanas elementus izkārtojumā
import android.widget.Toast                 // Importē Toast, lai parādītu īslaicīgus paziņojumus ekrānā
import androidx.appcompat.app.AppCompatActivity // Importē AppCompatActivity, lai nodrošinātu savietojamību ar vecākām Android versijām un izmantotu modernas funkcijas
import androidx.recyclerview.widget.LinearLayoutManager // Importē LinearLayoutManager, lai pārvaldītu RecyclerView izkārtojumu vertikālā vai horizontālā veidā
import androidx.recyclerview.widget.RecyclerView // Importē RecyclerView, lai parādītu un pārvaldītu lielu datu kopu sarakstu
import com.example.rtugroupproject.R.*      // Importē resursus no R faila, lai piekļūtu izkārtojuma un citiem resursiem
import java.text.SimpleDateFormat           // Importē SimpleDateFormat, lai formatētu un parsētu datumu un laiku
import java.util.Calendar                   // Importē Calendar, lai strādātu ar datumu un laika aprēķiniem
import java.util.Locale                     // Importē Locale, lai pārvaldītu lokalizāciju un formātu noteikšanu, pamatojoties uz lietotāja iestatījumiem


class MainActivity : AppCompatActivity() {

    // Deklarē mainīgos, lai tos varētu izmantot visā klasē
    private lateinit var plusButton: Button
    private lateinit var clearButton: Button
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter

    // onCreate metode tiek izsaukta, kad aktivitāte tiek izveidota
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main) // Iestata galveno izkārtojumu šai aktivitātei

        // Iniciē DatabaseHelper objektu, kas pārvalda datubāzi
        dbHelper = DatabaseHelper(this)

        // Atrodi un piešķir pogas un RecyclerView no izkārtojuma faila
        plusButton = findViewById(id.plusButton)
        clearButton = findViewById(id.clearButton)
        recyclerView = findViewById(id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this) // Iestata izkārtojuma pārvaldnieku RecyclerView

        // Pievieno klausītāju plus pogai, lai parādītu ievades dialogu
        plusButton.setOnClickListener {
            showInputDialog()
        }

        // Pievieno klausītāju clear pogai, lai iztīrītu datubāzi un atjauninātu sarakstu
        clearButton.setOnClickListener {
            clearDatabaseAndRefresh()
        }
    }

    // onResume metode tiek izsaukta, kad aktivitāte atsāk savu darbību
    override fun onResume() {
        super.onResume()
        loadTasks() // Ielādē uzdevumus, lai atjauninātu sarakstu
    }

    // Parāda ievades dialogu, lai pievienotu jaunu uzdevumu
    private fun showInputDialog() {
        val inflater = layoutInflater
        val dialogView: View = inflater.inflate(layout.input_dialog, null) // Iegūst dialoga izkārtojuma skatu

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView) // Iestata skatu dialoga logā

        // Atrodi un piešķir rediģēšanas laukus un tekstu skatus no dialoga skata
        val editTextTitle: EditText = dialogView.findViewById(id.editTextTitle)
        val editTextDescription: EditText = dialogView.findViewById(id.editTextDescription)
        val textViewDate: TextView = dialogView.findViewById(id.textViewDate)
        val textViewTime: TextView = dialogView.findViewById(id.textViewTime)

        val datePickerCalendar = Calendar.getInstance() // Izveido kalendāra instance datuma izvēlei

        // Pievieno klausītāju tekstu skatu datuma izvēlei
        textViewDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth) // Iestata izvēlēto datumu kalendārā
                val formatter = SimpleDateFormat("EEE MMM d", Locale.getDefault())
                val selectedDate = formatter.format(calendar.time) // Formatē datumu
                textViewDate.text = selectedDate // Iestata tekstu skatu ar izvēlēto datumu
            }, datePickerCalendar.get(Calendar.YEAR), datePickerCalendar.get(Calendar.MONTH), datePickerCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        val timePickerCalendar = Calendar.getInstance() // Izveido kalendāra instance laika izvēlei

        // Pievieno klausītāju tekstu skatu laika izvēlei
        textViewTime.setOnClickListener {
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val selectedTime = String.format("%02d:%02d", hourOfDay, minute) // Formatē laiku
                textViewTime.text = selectedTime // Iestata tekstu skatu ar izvēlēto laiku
            }, timePickerCalendar.get(Calendar.HOUR_OF_DAY), timePickerCalendar.get(Calendar.MINUTE), true).show()
        }

        // Iestata "Add task" loga pogas un to funkcionalitāti
        dialogBuilder
            .setTitle("Add task")
            .setPositiveButton("OK") { _, _ ->
                val title = editTextTitle.text.toString()
                val description = editTextDescription.text.toString()
                val date = textViewDate.text.toString()
                val time = textViewTime.text.toString()
                // Pārbauda, vai visi lauki ir aizpildīti
                if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                    saveTaskToDatabase(title, description, date, time) // Saglabā uzdevumu datubāzē
                } else {
                    Toast.makeText(this@MainActivity, "All fields must be filled", Toast.LENGTH_SHORT).show() // Parāda paziņojumu, ja kāds lauks nav aizpildīts
                }
            }
            .setNegativeButton("Cancel") { _, _ ->
                // Ja lietotājs izvēlas atcelt, nedara neko īpašu
            }
            .create() // Izveido dialoga logu
            .show() // Parāda dialoga logu
    }

    // Saglabā uzdevumu datubāzē un parāda paziņojumu par iznākumu
    private fun saveTaskToDatabase(title: String, description: String, date: String, time: String) {
        val result = dbHelper.insertTask(title, description, date, time) // Ievieto uzdevumu datubāzē
        if (result != -1L) {
            Toast.makeText(this@MainActivity, "Task saved", Toast.LENGTH_SHORT).show() // Parāda paziņojumu par veiksmīgu saglabāšanu
            Log.i("MainActivity", "Task added to database successfully: $title, $description , $date, $time") // Ieraksta logu par veiksmīgu saglabāšanu
            loadTasks() // Ielādē uzdevumus, lai atjauninātu sarakstu
        } else {
            Toast.makeText(this@MainActivity, "Failed to save task", Toast.LENGTH_SHORT).show() // Parāda paziņojumu par neveiksmīgu saglabāšanu
            Log.e("MainActivity", "Failed to add task to database") // Ieraksta logu par neveiksmīgu saglabāšanu
        }
    }

    // Ielādē visus uzdevumus no datubāzes un iestata tos RecyclerView adapterī
    private fun loadTasks() {
        val taskList = dbHelper.getAllTasks() // Iegūst visus uzdevumus no datubāzes
        adapter = TaskAdapter(taskList) // Izveido adapteri ar uzdevumu sarakstu
        recyclerView.adapter = adapter // Iestata adapteri RecyclerView
    }

    // Dzēš konkrētu uzdevumu pēc ID un atjaunina uzdevumu sarakstu
    fun deleteTask(taskId: Long) {
        dbHelper.deleteTask(taskId) // Dzēš uzdevumu no datubāzes
        loadTasks() // Ielādē uzdevumus, lai atjauninātu sarakstu
        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show() // Parāda paziņojumu par veiksmīgu dzēšanu
    }

    // Iztīra visu datubāzi un atjaunina uzdevumu sarakstu
    private fun clearDatabaseAndRefresh() {
        dbHelper.clearDatabase() // Iztīra datubāzi
        loadTasks() // Ielādē uzdevumus, lai atjauninātu sarakstu
        Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show() // Parāda paziņojumu par veiksmīgu iztīrīšanu
    }

    // Izveido izvēlnes elementus no resursu faila
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu) // Piepūš izvēlni ar elementiem no menu_main resursu faila
        return true
    }

    // Apstrādā izvēlnes elementu izvēli
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            id.action_clear -> {
                clearDatabaseAndRefresh() // Ja izvēlēts clear, iztīra datubāzi un atjaunina sarakstu
                true
            }
            else -> super.onOptionsItemSelected(item) // Ja izvēlēts cits elements, izpilda noklusējuma darbību
        }
    }
}
