package com.example.rtugroupproject

import android.view.LayoutInflater  // Importē LayoutInflater, lai izveidotu skatus no XML izkārtojuma failiem
import android.view.View            // Importē View, lai pārvaldītu UI komponentu pamata klasi
import android.view.ViewGroup       // Importē ViewGroup, lai pārvaldītu skatu grupas izkārtojumu
import android.widget.Button        // Importē Button, lai izmantotu pogas elementus izkārtojumā, piemēram, dzēšanas pogu
import android.widget.TextView      // Importē TextView, lai izmantotu teksta parādīšanas elementus izkārtojumā
import androidx.recyclerview.widget.RecyclerView // Importē RecyclerView, lai parādītu un pārvaldītu lielu datu kopu sarakstu
import java.text.SimpleDateFormat   // Importē SimpleDateFormat, lai formatētu un parsētu datumu un laiku
import java.util.Locale             // Importē Locale, lai pārvaldītu lokalizāciju un formātu noteikšanu, pamatojoties uz lietotāja iestatījumiem


// TaskAdapter klase pārvalda uzdevumu saraksta rādīšanu RecyclerView
class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    // ViewHolder klase saglabā atsauces uz katra vienuma skatiem
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle) // Uzdevuma virsraksta skats
        val textViewDescription: TextView = view.findViewById(R.id.textViewDescription) // Uzdevuma apraksta skats
        val textViewDate: TextView = view.findViewById(R.id.textViewDate) // Uzdevuma datuma skats
        val textViewTime: TextView = view.findViewById(R.id.textViewTime) // Uzdevuma laika skats
        val deleteButton: Button = view.findViewById(R.id.deleteButton) // Dzēšanas pogas skats
    }

    // onCreateViewHolder metode izsaucas, lai izveidotu jaunu ViewHolder instance, kad tā ir nepieciešama
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    // onBindViewHolder metode izsaucas, lai piesaistītu datus ViewHolder elementiem noteiktā pozīcijā
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]
        holder.textViewTitle.text = task.title // Iestata uzdevuma virsrakstu
        holder.textViewDescription.text = task.description // Iestata uzdevuma aprakstu

        // Formatē datumu uz nepieciešamo formātu un iestata to tekstu skatā
        val formatter = SimpleDateFormat("EEE MMM d", Locale.getDefault())
        val formattedDate = formatter.format(task.date)
        holder.textViewDate.text = formattedDate

        holder.textViewTime.text = task.time // Iestata uzdevuma laiku

        // Apstrādā dzēšanas pogas klikšķi
        holder.deleteButton.setOnClickListener {
            val taskId = task.id
            (holder.itemView.context as? MainActivity)?.deleteTask(taskId) // Izsauc dzēšanas metodi MainActivity
        }
    }

    // Atgriež uzdevumu saraksta vienumu skaitu
    override fun getItemCount() = taskList.size
}
