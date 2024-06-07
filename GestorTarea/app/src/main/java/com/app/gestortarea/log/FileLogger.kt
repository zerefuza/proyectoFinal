package com.app.gestortarea.log

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Objeto para el registro de mensajes en un archivo de registro.
 */
object FileLogger {
    private const val LOG_TAG = "FileLogger"
    private const val LOG_FILE_NAME = "app_logs.log"

    /**
     * Registra un mensaje en un archivo de registro.
     *
     * @param context El contexto de la aplicación.
     * @param tag El tag del mensaje.
     * @param message El mensaje a registrar.
     */
    fun logToFile(context: Context,tag: String, message: String) {
        val logFile = File(context.filesDir,LOG_FILE_NAME)
        val timeStamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val logMessage = "$timeStamp $tag: $message\n"

        try {
            val fileWriter = FileWriter(logFile, true)
            fileWriter.append(logMessage)
            fileWriter.flush()
            fileWriter.close()
        } catch (e: IOException) {
            Log.e(LOG_TAG, "Error writing log to file", e)
        }
    }

    fun clearLogs(context: Context) {
        val logFile = File(context.filesDir, LOG_FILE_NAME)
        if (logFile.exists()) {
            logFile.delete()
        }
    }
}