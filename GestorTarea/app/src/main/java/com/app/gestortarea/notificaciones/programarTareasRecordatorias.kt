package com.app.gestortarea.notificaciones

import android.content.Context
import androidx.work.WorkManager
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

/**
 * Programa tareas para recordatorios periódicos.
 * Este método utiliza WorkManager para programar un Worker que verifica periódicamente si alguna tarea está por finalizar y muestra notificaciones de recordatorio.
 *
 * @param context El contexto de la aplicación.
 * @param usuarioId El ID del usuario para el cual se programan los recordatorios de tareas.
 */
fun programarTareasRecordatorias(context: Context, usuarioId: String) {
    val workManager = WorkManager.getInstance(context)
    val data = Data.Builder().putString("usuarioId", usuarioId).build()

    // Se programa un trabajo periódico con intervalo de 3 horas
    val periodicRefreshRequest = PeriodicWorkRequest.Builder(
        RecordatorioTareasWorker::class.java,
        3,
        TimeUnit.HOURS
    ).setInputData(data).build()

    // Se encola el trabajo periódico con una política de reemplazo
    workManager.enqueueUniquePeriodicWork(
        "TaskReminderWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicRefreshRequest
    )
}
