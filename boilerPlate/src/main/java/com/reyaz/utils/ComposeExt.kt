package com.reyaz.utils

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.reyaz.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@Composable
inline fun <reified VM : BaseViewModel> baseViewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    },
): VM {
    val viewModel = viewModel<VM>(viewModelStoreOwner)
    val context = LocalContext.current
    ObserveAsEvents(sharedFlow = viewModel.showToast) {
        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
    }
    return viewModel
}

@Composable
fun <T> ObserveAsEvents(sharedFlow: SharedFlow<T>, onEvent: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(sharedFlow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            sharedFlow.collectLatest(onEvent)
        }
    }
}
