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
        RecordatorioTareasWorker::class.java,
        3,
        TimeUnit.HOURS
    ).setInputData(data).build()

    workManager.enqueueUniquePeriodicWork(
        "TaskReminderWork",
        ExistingPeriodicWorkPolicy.REPLACE,
        periodicRefreshRequest
    )
}
