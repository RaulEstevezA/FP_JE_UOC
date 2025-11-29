import com.example.piedraPapelTijeras.data.source.JugadoresDatabase



import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.piedraPapelTijeras.repositorio.JugadorRepositorio
import com.example.piedraPapelTijeras.viewmodel.JuegoViewModel
import com.example.piedraPapelTijeras.viewmodel.LanguageViewModel
import com.example.piedraPapelTijeras.viewmodel.LoginViewModel
import com.example.piedraPapelTijeras.viewmodel.Top10Viewmodel


object Injeccion {

    @Volatile
    private var jugadorRepositorio: JugadorRepositorio? = null

    private fun provideJugadorRepositorio(context: Context): JugadorRepositorio {

        return jugadorRepositorio ?: synchronized(this) {
            val database = JugadoresDatabase.getInstance(context.applicationContext)
            val nuevoRepositorio = JugadorRepositorio(database.jugadorDao)
            jugadorRepositorio = nuevoRepositorio
            nuevoRepositorio // Devolvemos la nueva instancia
        }
    }

    fun provideJuegoViewModelFactory(context: Context, top10ViewModel: Top10Viewmodel): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {


            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                if (modelClass.isAssignableFrom(JuegoViewModel::class.java)) {

                    val repositorio = provideJugadorRepositorio(context)
                    @Suppress("UNCHECKED_CAST")
                    return JuegoViewModel(repositorio = repositorio, top10Viewmodel = top10ViewModel, context = context) as T
                }


                throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
            }
        }
    }
    fun provideLoginViewModelFactory(context: Context): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {


            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {

                    val repositorio = provideJugadorRepositorio(context)


                    @Suppress("UNCHECKED_CAST")
                    return LoginViewModel(repositorio, context) as T
                }


                throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
            }
        }
    }
    fun provideTop10ViewModelFactory(context: Context): ViewModelProvider.Factory {

        return object : ViewModelProvider.Factory {

            override fun <T : ViewModel> create(modelClass: Class<T>): T {

                if (modelClass.isAssignableFrom(Top10Viewmodel::class.java)) {

                    val repositorio = provideJugadorRepositorio(context)
                    @Suppress("UNCHECKED_CAST")
                    return Top10Viewmodel(repositorio) as T
                }


                throw IllegalArgumentException("Clase de ViewModel desconocida: ${modelClass.name}")
            }
        }
    }

    fun provideLanguageViewModelFactory(context: Context): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LanguageViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return LanguageViewModel(context) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
