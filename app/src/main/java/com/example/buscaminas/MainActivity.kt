package com.example.buscaminas

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private lateinit var juegoBuscaminas: JuegoBuscaminas
    private lateinit var tableroMinas: TableroMinas
    private var tamanoTablero = 8

    private lateinit var fuenteRetro: Typeface
    private val fontPath = "font/8-BIT WONDER.TTF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        juegoBuscaminas = JuegoBuscaminas()
        iniciarPartida()

        fuenteRetro = Typeface.createFromAsset(assets, fontPath)

        // Resto de tu código de inicialización, como configurar el Spinner
        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        val nombresPersonajes = arrayOf("Personaje 1", "Personaje 2", "Personaje 3")
        val adaptadorPersonalizado =
            AdaptadorPersonalizado(this, R.layout.spinner_personajes, nombresPersonajes)
        selectorPersonaje.adapter = adaptadorPersonalizado
    }

    private fun iniciarPartida() {
        // Restaurar el juego y el tablero
        tableroMinas = TableroMinas(tamanoTablero, juegoBuscaminas.obtenerNumeroMinas())

        val gridLayout: GridLayout = findViewById(R.id.grid)
        gridLayout.removeAllViews()
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                val boton = Button(this)
                boton.text = ""
                boton.typeface = fuenteRetro
                boton.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(0, 0, 0, 0)
                }
                boton.setBackgroundResource(R.drawable.bloqueinterrogacion)

                boton.setOnClickListener {
                    revelarCelda(fila, columna, boton)
                }

                gridLayout.addView(boton)
            }
        }
    }

    private fun revelarCelda(fila: Int, columna: Int, boton: Button) {
        val resultado = juegoBuscaminas.hacerMovimiento(fila, columna, tableroMinas)

        when (resultado) {
            JuegoBuscaminas.ResultadoJuego.EN_CURSO -> {
                // El juego continúa
                actualizarInterfaz(boton, fila, columna)
            }
            JuegoBuscaminas.ResultadoJuego.GANADO -> {
                mostrarFinDelJuego(true)
            }
            JuegoBuscaminas.ResultadoJuego.PERDIDO -> {
                mostrarFinDelJuego(false)
            }
        }
    }

    private fun actualizarInterfaz(boton: Button, fila: Int, columna: Int) {
        // Lógica para actualizar la interfaz después de cada movimiento
        // Puedes adaptar esto según la lógica específica de tu juego

        val valor = tableroMinas.obtenerEstadoTablero()[fila][columna]
        boton.text = if (valor == 0) "" else valor.toString()
        boton.setBackgroundResource(R.drawable.bloquepulsado)
    }

    private fun mostrarFinDelJuego(victoria: Boolean) {
        val mensaje = if (victoria) "¡Ganaste!" else "¡Perdiste!"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fin del juego")
            .setMessage(mensaje)
            .setPositiveButton("Nuevo Juego") { _, _ -> iniciarPartida() }
            .setNegativeButton("Salir") { _, _ -> finish() }
            .show()
    }

    private inner class AdaptadorPersonalizado(
        context: Context,
        resource: Int,
        objects: Array<String>
    ) : ArrayAdapter<String>(context, resource, objects) {

        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }

        private fun crearFilaPersonalizada(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val inflater = LayoutInflater.from(context)
            val fila = inflater.inflate(R.layout.spinner_personajes, parent, false)
            val textView = fila.findViewById<TextView>(R.id.textView)
            textView.text = getItem(position)
            return fila
        }
    }
}