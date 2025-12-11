package com.example.piedraPapelTijeras.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun AgregarSurface(

    color: Color,
    shape: Shape,
    modifier: Modifier,
    imagen : Painter,
    textdes: String,
    seleccionado: Boolean,
    onClick: () -> Unit



)
{

    Surface(
        modifier = modifier
            .clickable { onClick() }
            .border(
                width = if (seleccionado) 10.dp else 0.dp,
                color = if (seleccionado) Color.Blue else Color.Transparent,
                shape = shape
            ),
        shape = shape,
        color = color


    ) {

        Image(
            painter = imagen,
            contentDescription = textdes,

            )

    }
}