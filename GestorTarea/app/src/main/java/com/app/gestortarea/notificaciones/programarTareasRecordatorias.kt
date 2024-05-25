package com.app.gestortarea.notificaciones
import android.content.Context
import androidx.work.WorkManager
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import java.util.concurrent.TimeUnit

fun programarTareasRecordatorias(context: Context, usuarioId: String) {
    val workManager = WorkManager.getInstance(context)
    val data = Data.Builder().putString("usuarioId", usuarioId).build()
    val periodicRefreshRequest = PeriodicWorkRequest.Builder(
        RecordatorioTareasWorker::class.java, // Your worker class
        15, // repeating interval
        TimeUnit.MINUTES
    ).setInputData(data).build()

    workManager.enqueueUniquePeriodicWork(
        "TaskReminderWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicRefreshRequest
    )
}
