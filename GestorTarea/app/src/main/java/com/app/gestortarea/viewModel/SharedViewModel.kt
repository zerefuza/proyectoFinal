package com.app.gestortarea.viewModel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.gestortarea.log.FileLogger
import com.app.gestortarea.modeloDatos.Tarea
import com.app.gestortarea.modeloDatos.Usuario
import com.app.gestortarea.notificaciones.programarTareasRecordatorias
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import java.util.Date

/**
 * ViewModel que gestiona la lógica de negocio y la interacción con Firebase para la funcionalidad relacionada con las tareas y usuarios.
 */
class SharedViewModel : ViewModel() {
    // Autenticación de Firebase
    private val auth: FirebaseAuth = Firebase.auth

    // Indicador de carga
    private val _loading = MutableLiveData(false)

    // Instancia de Firestore
    private val db = Firebase.firestore

    // Email del usuario actual
    private val _userEmail = mutableStateOf<String>("")
    val userEmail: State<String> = _userEmail

    /**
     * Establece el email del usuario actual.
     *
     * @param _userEmail Email del usuario.
     */
    fun setuserEmail(_userEmail: String) {
        this._userEmail.value = _userEmail
    }

    // Título de la tarea seleccionada
    private val _tituloTarea = mutableStateOf<String>("")
    val tituloTarea: State<String> = _tituloTarea

    /**
     * Establece el título de la tarea seleccionada.
     *
     * @param _tituloTarea Título de la tarea.
     */
    fun setTituloTarea(_tituloTarea: String) {
        this._tituloTarea.value = _tituloTarea
    }

    // Fecha de la tarea seleccionada
    private val _fechaTarea = mutableStateOf<Date?>(null)
    val fechaTarea: State<Date?> = _fechaTarea

    /**
     * Establece la fecha de la tarea seleccionada.
     *
     * @param _fechaTarea Fecha de la tarea.
     */
    fun setFechaTarea(_fechaTarea: Date?) {
        this._fechaTarea.value = _fechaTarea
    }

    /**
     * Inicia sesión de un usuario utilizando su correo electrónico y contraseña.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param context Contexto de la aplicación.
     * @param onSuccess Callback para la acción exitosa.
     * @param onError Callback para la acción con error.
     */    fun loginUsuario(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) =
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FileLogger.logToFile(context, "Login", "Logueo del usuario $email exitoso")

                            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putString("user_email", email)
                                putBoolean("is_logged_in", true)
                                apply()
                            }

                            onSuccess()

                            // Llama a programarTareasRecordatorias después del inicio de sesión exitoso
                            programarTareasRecordatorias(context, email)
                        } else {
                            FileLogger.logToFile(context, "Login", "Error, en el logueo del usuario $email: ${task.exception?.message}")
                            onError("Credenciales inválidas. Por favor, inténtalo de nuevo.")
                        }
                    }
            } catch (ex: Exception) {
                FileLogger.logToFile(context, "Login", "Error al iniciar sesión: ${ex.message}")
                onError("Ocurrió un error al iniciar sesión. Por favor, inténtalo de nuevo más tarde.")
            }
        }

    /**
     * Registra un nuevo usuario en Firebase utilizando su correo electrónico y contraseña.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param onSuccess Callback para la acción exitosa.
     * @param onError Callback para la acción con error.
     */
    fun registrarUsuario(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _loading.value = false
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onError("Error al crear el usuario: ${task.exception?.message}")
                    }
                }
        }
    }

    /**
     * Agrega un usuario a la base de datos.
     *
     * @param context Contexto de la aplicación.
     * @param usuario Objeto de tipo Usuario que se va a agregar.
     */
    fun agregarUsuario(context: Context,usuario: Usuario) {
        val collectionRef = db.collection("usuarios")
        val documentRef = collectionRef.document(usuario.email)

        documentRef.set(usuario)
            .addOnSuccessListener {
                FileLogger.logToFile(context, "Firestore", "Usuario agregado con ID: ${usuario.email}")
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al agregar usuario: ${e.message}")
            }
    }

    /**
     * Agrega una nueva tarea a la base de datos del usuario. Si el usuario no tiene una colección de tareas, se creará una.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioId ID del usuario al que pertenece la tarea.
     * @param tarea Tarea que se va a agregar.
     * @param onSuccess Callback para la acción exitosa.
     */
    fun agregarTarea(context: Context,usuarioId: String, tarea: Tarea,onSuccess: () -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)

        // Verificar si la subcolección de tareas ya existe
        usuarioDocRef.collection("tareas").get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Si la subcolección de tareas no existe, crear un documento inicial
                    usuarioDocRef.collection("tareas").document(tarea.nombre).set(tarea)
                        .addOnSuccessListener {
                            FileLogger.logToFile(context, "Firestore", "Se ha creado el documento inicial de tareas")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            FileLogger.logToFile(context, "Firestore", "Error al crear el documento inicial de tareas: ${e.message}")
                        }
                } else {
                    // Si la subcolección de tareas ya existe, agregar la tarea directamente
                    agregarTareaReal(context,usuarioDocRef, tarea){
                        onSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al obtener la subcolección de tareas del usuario: ${e.message}")
            }
    }

    /**
     * Agrega una tarea a la base de datos.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioDocRef Referencia al documento del usuario en Firestore.
     * @param tarea Tarea que se va a agregar.
     * @param onSuccess Callback para la acción exitosa.
     */
    private fun agregarTareaReal(context: Context,usuarioDocRef: DocumentReference, tarea: Tarea,onSuccess: () -> Unit) {
        val documentRef = usuarioDocRef.collection("tareas").document(tarea.nombre)

        documentRef.set(tarea)
            .addOnSuccessListener { documentReference ->
                onSuccess()
                FileLogger.logToFile(context, "Firestore", "Tarea agregada con éxito")
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al agregar tarea para el usuario: ${e.message}")
            }
    }

    /**
     * Modifica una tarea en la base de datos.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioId ID del usuario al que pertenece la tarea.
     * @param tituloAntiguo Título antiguo de la tarea.
     * @param tarea Nueva información de la tarea.
     * @param onSuccess Callback para la acción exitosa.
     */
    fun modificarTarea(context: Context,usuarioId: String, tituloAntiguo: String, tarea: Tarea,onSuccess: () -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)
        val tareaAntiguaDocRef = usuarioDocRef.collection("tareas").document(tituloAntiguo)

        tareaAntiguaDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Si el documento de la tarea antigua existe, elimínalo
                    tareaAntiguaDocRef.delete()
                        .addOnSuccessListener {
                            FileLogger.logToFile(context, "Firestore", "Tarea antigua eliminada con éxito")

                            // Después de eliminar el documento antiguo, agregar el documento con el nuevo título
                            val nuevaTareaDocRef = usuarioDocRef.collection("tareas").document(tarea.nombre)
                            nuevaTareaDocRef.set(tarea)
                                .addOnSuccessListener {
                                    FileLogger.logToFile(context, "Firestore", "Tarea modificada con éxito")
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    FileLogger.logToFile(context, "Firestore", "Error al agregar la tarea con el nuevo título: ${e.message}")
                                }
                        }
                        .addOnFailureListener { e ->
                            FileLogger.logToFile(context, "Firestore", "Error al eliminar la tarea antigua: ${e.message}")
                        }
                } else {
                    // Si el documento de la tarea antigua no existe, registrar el error
                    FileLogger.logToFile(context, "Firestore", "La tarea antigua no existe y no se puede modificar")
                }
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al obtener el documento de la tarea antigua: ${e.message}")
            }
    }

    /**
     * Obtiene todas las tareas de un usuario.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioId ID del usuario del que se obtienen las tareas.
     * @param onComplete Callback que se ejecuta al completar la obtención de las tareas.
     */
    fun obtenerTareas(context: Context,usuarioId: String, onComplete: (List<Tarea>) -> Unit) {
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
                FileLogger.logToFile(context, "Firestore", "Error al obtener tareas del usuario: ${e.message}")
                onComplete(emptyList())
            }
    }

    /**
     * Obtiene una tarea por su título.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioId ID del usuario del que se obtiene la tarea.
     * @param titulo Título de la tarea a obtener.
     * @param onComplete Callback que se ejecuta al completar la obtención de la tarea.
     */
    fun obtenerTareaPorTitulo(context: Context,usuarioId: String, titulo: String, onComplete: (Tarea?) -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)

        usuarioDocRef.collection("tareas")
            .whereEqualTo("nombre", titulo) // Suponiendo que el campo del título en Firestore se llama "nombre"
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tarea = querySnapshot.documents.firstOrNull()?.toObject(Tarea::class.java)
                onComplete(tarea)
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al obtener tarea por título: ${e.message}")
                onComplete(null)
            }
    }

    /**
     * Borra tareas por su título.
     *
     * @param context Contexto de la aplicación.
     * @param usuarioId ID del usuario del que se eliminan las tareas.
     * @param titulo Título de las tareas a eliminar.
     * @param onComplete Callback que se ejecuta al completar la eliminación de las tareas.
     */
    fun borrarTareasPorTitulo(context: Context,usuarioId: String, titulo: String, onComplete: (Boolean) -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)

        // Consultar las tareas con el título especificado
        usuarioDocRef.collection("tareas")
            .whereEqualTo("nombre", titulo)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Eliminar cada documento encontrado
                val batch = db.batch()
                querySnapshot.documents.forEach { document ->
                    batch.delete(document.reference)
                }
                batch.commit()
                    .addOnSuccessListener {
                        onComplete(true) // Éxito al eliminar las tareas
                    }
                    .addOnFailureListener { e ->
                        FileLogger.logToFile(context, "Firestore", "Error al eliminar tareas por título: ${e.message}")
                        onComplete(false) // Error al eliminar las tareas
                    }
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al obtener tareas por título: ${e.message}")
                onComplete(false) // Error al obtener las tareas
            }
    }

    /**
     * Verifica si un usuario está registrado en la base de datos Firestore.
     *
     * @param context El contexto de la aplicación.
     * @param usuarioId El ID del usuario que se quiere verificar.
     * @param onComplete Callback que se llama con el resultado de la verificación.
     *                   Devuelve true si el usuario está registrado, de lo contrario false.
     */
    fun isUsuarioRegistrado(context: Context, usuarioId: String, onComplete: (Boolean) -> Unit) {
        val usuarioDocRef = db.collection("usuarios").document(usuarioId)

        usuarioDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                FileLogger.logToFile(context, "Firestore", "Error al verificar si el usuario está registrado: ${e.message}")
                onComplete(false)
            }
    }


    /**
     * Envía un correo electrónico de recuperación de contraseña.
     *
     * @param email Correo electrónico al que se envía el enlace de recuperación.
     * @param onSuccess Callback para la acción exitosa.
     * @param onError Callback para la acción con error.
     */
    fun emailRecuperacionContrasenia(email: String, onSuccess: () -> Unit,onError: () -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    onSuccess()
                }else{
                    onError()
                }
            }
    }
}