package com.example.piedraPapelTijeras.ui.util

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun localizedString(
    @StringRes resId: Int,
    vararg args: Any
): String {
    val context = LocalContext.current
    val baseConfig = LocalConfiguration.current           // configuración "oficial" de Compose
    val locale: Locale = LocalAppLocale.current           // el locale que inyectas desde MainActivity

    // Creamos unas Resources específicas para ese locale
    val resources = remember(baseConfig, locale) {
        val config = Configuration(baseConfig).apply {
            setLocale(locale)
        }
        context.createConfigurationContext(config).resources
    }

    return if (args.isNotEmpty()) {
        resources.getString(resId, *args)
    } else {
        resources.getString(resId)
    }
}