package com.example.rtugroupproject

import java.text.SimpleDateFormat   // Importē SimpleDateFormat, lai formatētu un parsētu datumu un laiku
import java.util.Date               // Importē Date, lai strādātu ar datuma un laika objektiem
import java.text.ParseException     // Importē ParseException, lai apstrādātu kļūdas, kas rodas parsējot datumu un laiku
import android.util.Log             // Importē Log, lai veiktu kļūdu un informācijas pierakstīšanu log failā
import java.util.Locale            // Importē Locale, lai pārvaldītu lokalizāciju un formātu noteikšanu, pamatojoties uz lietotāja iestatījumiem


// Datu klase Task satur uzdevuma informāciju, ieskaitot ID, virsrakstu, aprakstu, datumu un laiku
data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val dateString: String,
    val time: String
) {
    // Inicializē date mainīgo, kas mēģina parsēt dateString kā Date objektu
    val date: Date? = try {
        // Izmanto SimpleDateFormat, lai parsētu datumu no dateString, pamatojoties uz norādīto formātu
        SimpleDateFormat("EEE MMM d", Locale.getDefault()).parse(dateString)
    } catch (e: ParseException) {
        // Ja notiek kļūda parsējot datumu, tā tiek noķerta šeit
        Log.e("Task", "Error parsing date: ${e.message}") // Ieraksta kļūdas ziņojumu logā
        null // Atgriež null, lai norādītu, ka parsēšana neizdevās
        // Vai var apstrādāt kļūdu citādāk, piemēram, iestatīt noklusējuma datumu
    }
}
