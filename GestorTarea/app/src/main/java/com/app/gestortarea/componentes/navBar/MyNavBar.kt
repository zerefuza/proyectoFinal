package com.app.gestortarea.componentes.navBar

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkManager
import com.app.gestortarea.R
import com.app.gestortarea.nav.Vistas
import com.app.gestortarea.viewModel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object MyNavBar : NavBarInt {
    @Composable
    override fun Content(
        navController: NavController,
        sharedViewModel: SharedViewModel,
        contenidoUser: @Composable (NavController, SharedViewModel) -> Unit
    ) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen || drawerState.isClosed,
            drawerContent = {
                MyDrawerContentUser(
                    navController = navController,
                    onBackPress = {
                        if (drawerState.isOpen) {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    },
                )
            },
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(hostState = snackbarHostState)
                },
                topBar = {
                    MyTopBarUser(
                        onMenuClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    )

                },
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFFFFFFF)),
                ) {
                    contenidoUser(navController, sharedViewModel)
                }
            }
        }

        LaunchedEffect(snackbarHostState.currentSnackbarData?.visuals?.duration) {
            delay(2000)
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBarUser(
    onMenuClick: () -> Unit,
) {
    val colors = TopAppBarDefaults.largeTopAppBarColors(Color(0xFF42A5F5))

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        colors = colors,
        navigationIcon = {
            IconButton(
                onClick = {
                    onMenuClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menú",
                )
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Gestor de tareas"
                )
            }
        }
    )
}


@Composable
fun MyDrawerContentUser(
    navController: NavController,
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit,
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val email = sharedPreferences.getString("user_email", null)

    ModalDrawerSheet(modifier) {
        Column(
            modifier
                .fillMaxSize()
                .background(Color(0xFF8D0DF5))
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = email ?: "",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally),
            )

            Divider(
                color = Color.White,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DrawerItem(
                text = "Administrar Tareas",
                icon = Icons.Filled.Task
            ) {
                navController.navigate(Vistas.VistaTareas.route)
            }

            DrawerItem(
                text = "Calendario",
                icon = Icons.Default.CalendarToday
            ) {
                navController.navigate(Vistas.VistaCalendario.route)
            }

            DrawerItem(
                text = "Cerrar sesión",
                icon = Icons.Default.Logout
            ) {
                with(sharedPreferences.edit()) {
                    remove("user_email")
                    putBoolean("is_logged_in", false)
                    apply()
                }
                WorkManager.getInstance(context).cancelUniqueWork("TaskReminderWork")
                navController.navigate(Vistas.LoginVista.route)
            }
        }
    }
    BackPressHandlerUser {
        onBackPress()
    }
}

@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(35.dp)
                .padding(end = 16.dp)
        )
        Text(
            text = text
        )
    }
}


@Composable
fun BackPressHandlerUser(enabled: Boolean = true, onBackPressed: () -> Unit) {
    val currentOnBackPressed by rememberUpdatedState(onBackPressed)
    val backCallback = remember {
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                currentOnBackPressed()
            }
        }
    }
    SideEffect {
        backCallback.isEnabled = enabled
    }

    val backDispatcher = checkNotNull(LocalOnBackPressedDispatcherOwner.current) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }.onBackPressedDispatcher

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}

