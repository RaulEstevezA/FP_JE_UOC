package com.example.piedraPapelTijeras.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.piedraPapelTijeras.R


class SoundPlayer(context: Context) {
    //variable donde almacenamos los sonido en concreto un maximo de 5 a la vez
    private val soundPool: SoundPool

    //indentificados de un sonido en concreto dentro del soundPool
     val sonidoSeleccionId: Int//sonido eleccion jugada
     val sonidoBotonId: Int //sonido boton click
     val sonidoVictoriaId: Int //sonido victoria
     val sonidoEmpateId: Int //sonido empate
     val sonidoDerrotaId: Int //sonido derrota


    //comprueba si se cargo el sonido
    private var cargado = false


    //se ejecuta al inicio de la creacion de la instancia
    //esperamos a que se carge el sonido
    init {
        //parametros de configuracion del soundPool
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attrs)
            .setMaxStreams(5)
            .build()




        soundPool.setOnLoadCompleteListener { _, _, status ->
            cargado = status == 0
        }
        //le asiganmos los sonido y le damos prioridad
        sonidoSeleccionId = soundPool.load(context, R.raw.seleccion_jugada, 1)
        sonidoBotonId = soundPool.load(context, R.raw.click_boton, 1)
        sonidoVictoriaId = soundPool.load(context, R.raw.congratulation_sound, 1)
        sonidoDerrotaId = soundPool.load(context, R.raw.lose_sound, 1)
        sonidoEmpateId = soundPool.load(context, R.raw.empate_sound, 1)


    }


    //funcion que reproduce los efectos de sonidos
    fun playSounds(soundId: Int, volume: Float = 1.0f) {
        if (cargado) {

            soundPool.play(soundId, volume, volume, 1, 0, 1f)

        }
    }



        //Libera sonidos de memoria con el fin de liberar recursos
        fun release() {
            soundPool.release()
        }
}










