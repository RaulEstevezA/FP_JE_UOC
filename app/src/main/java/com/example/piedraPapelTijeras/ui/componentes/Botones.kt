package com.example.piedraPapelTijeras.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgregarBoton(
    onclick: () -> Unit,
    icon: ImageVector?,
    des: String,
    text: String,
    fontsize: Int = 20,
    modifier: Modifier
){

    Button(
        onClick = onclick,
        modifier = modifier.height(60.dp)

    ) {
        Row(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = des,
                    modifier = Modifier.Companion.size(30.dp)
                )
            }
            Text(
                text = text,
                fontSize = fontsize.sp
            )


        }
    }
}