package com.app.gestortarea.notificaciones

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.app.gestortarea.MainActivity
import com.app.gestortarea.R
import com.app.gestortarea.log.FileLogger
import com.app.gestortarea.modeloDatos.Tarea
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * Clase que representa un Worker para gestionar recordatorios de tareas.
 * Este Worker realiza tareas en segundo plano, como verificar si alguna tarea está por finalizar y mostrar notificaciones de recordatorio.
 *
 * @param appContext El contexto de la aplicación.
 * @param workerParams Los parámetros del Worker.
 */
class RecordatorioTareasWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Realiza el trabajo en segundo plano.
     * En este método, se obtienen las tareas del usuario desde Firebase, se verifica si alguna tarea está por finalizar
     * y se muestra una notificación si es necesario.
     *
     * @return El resultado del trabajo, que puede ser [Result.success], [Result.failure] o [Result.retry].
     */
    override suspend fun doWork(): Result {
        var tareasPorFinalizar:Int=0
        val usuarioId = inputData.getString("usuarioId") ?: return Result.failure()

        // Obtener tareas de Firebase
        val tareas = obtenerTareasDesdeFirebase(usuarioId)

        // Verificar si alguna tarea está a punto de finalizar
        for (tarea in tareas) {
            if (isTareaPorFinalizar(tarea)) {
                tareasPorFinalizar++
            }
        }

        if (tareasPorFinalizar>0){
            mostrarNotificacion(tareasPorFinalizar)
        }

        return Result.success()
    }

    /**
     * Obtiene las tareas del usuario desde Firebase de forma asíncrona.
     *
     * @param usuarioId El ID del usuario.
     * @return Una lista de tareas obtenidas desde Firebase.
     */
    private suspend fun obtenerTareasDesdeFirebase(usuarioId: String): List<Tarea> {
        return suspendCancellableCoroutine { continuation ->
            obtenerTareas(usuarioId) { tareas ->
                continuation.resume(tareas)
            }
        }
    }

    /**
     * Obtiene las tareas de un usuario desde Firebase.
     *
     * @param usuarioId El ID del usuario.
     * @param onComplete La función de callback que se llama cuando se completan las tareas.
     */
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
                onComplete(emptyList())
            }
    }

    /**
     * Verifica si una tarea está por finalizar.
     *
     * @param tarea La tarea a verificar.
     * @return true si la tarea está por finalizar, de lo contrario false.
     */
    private fun isTareaPorFinalizar(tarea: Tarea): Boolean {
        val currentTime = Calendar.getInstance().timeInMillis
        val tareaTime = tarea.fecha?.time ?: 0
        val diff = tareaTime - currentTime
        val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
        return diff in 0..oneDayInMillis
    }

    /**
     * Muestra una notificación de recordatorio de tareas.
     *
     * @param numeroDeTareas El número de tareas por finalizar.
     */
    private fun mostrarNotificacion(numeroDeTareas: Int) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal de notificación
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

        val contentText = if (numeroDeTareas == 1) {
            "Tienes una tarea que está por finalizar"
        } else {
            "Tienes $numeroDeTareas tareas que están por finalizar"
        }

        val notification = NotificationCompat.Builder(applicationContext, "TASK_REMINDER_CHANNEL")
            .setContentTitle("Tareas por finalizar")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}

