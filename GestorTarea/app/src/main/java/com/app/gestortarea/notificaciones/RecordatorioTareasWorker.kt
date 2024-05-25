package com.app.gestortarea.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.gestortarea.MainActivity
import com.app.gestortarea.R
import com.app.gestortarea.modeloDatos.Tarea
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class RecordatorioTareasWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        val usuarioId = inputData.getString("usuarioId") ?: return Result.failure()

        // Obtener tareas de Firebase
        val tareas = obtenerTareasDesdeFirebase(usuarioId)

        // Verificar si alguna tarea está a punto de finalizar
        for (tarea in tareas) {
            if (isTareaPorFinalizar(tarea)) {
                // Mostrar notificación
                mostrarNotificacion(tarea)
            }
        }

        return Result.success()
    }

    private suspend fun obtenerTareasDesdeFirebase(usuarioId: String): List<Tarea> {
        return suspendCancellableCoroutine { continuation ->
            obtenerTareas(usuarioId) { tareas ->
                Log.d("notificacion", "obtenida las tareas: "+tareas.size)
                continuation.resume(tareas)
            }
        }
    }

    // Adaptación de tu método original
    private fun obtenerTareas(usuarioId: String, onComplete: (List<Tarea>) -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)

        usuarioDocRef.collection("tareas").get()
            .addOnSuccessListener { querySnapshot ->
                val tareas = mutableListOf<Tarea>()
                for (document in querySnapshot.documents) {
                    val tarea = document.toObject(Tarea::class.java)
                    tarea?.let { tareas.add(it) }
                }
                onComplete(tareas)
            }
            .addOnFailureListener { e ->
                Log.d("notificacion", "Error al obtener tareas del usuario", e)
                onComplete(emptyList())
            }
    }

    private fun isTareaPorFinalizar(tarea: Tarea): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        val tareaTime = tarea.fecha?.time ?: 0
        val diff = tareaTime - currentTime
        val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
        return diff in 0..oneDayInMillis
    }

    private fun mostrarNotificacion(tarea: Tarea) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación (solo para Android O y superior)
        val channel = NotificationChannel(
            "TASK_REMINDER_CHANNEL",
            "Task Reminder",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // Crear la notificación
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, "TASK_REMINDER_CHANNEL")
            .setContentTitle("Tarea por finalizar")
            .setContentText("La tarea ${tarea.nombre} está por finalizar")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        Log.d("notificacion", "se lanza la notificacion"+notification)
        notificationManager.notify(tarea.nombre.hashCode(), notification)
    }
}

