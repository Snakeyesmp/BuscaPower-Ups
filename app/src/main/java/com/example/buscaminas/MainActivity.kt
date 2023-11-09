package com.example.buscaminas

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private var dificultadSeleccionada = 0 // Valor predeterminado
    private var tamanoTablero =
        8 // Tamaño por defecto por si el usuario empieza partida sin elegir dificultad

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // se crea un objeto toolbar, ¡importante importar el correcto!
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // hace que la toolbar funcione como actionbar para la activity window actual
        setSupportActionBar(toolbar)

    }

    /**
     *Metodo que se usa para inflar un menu dentro de la toolbar y que aparezca
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Este metodo es llamado cuando seleccionas un item del menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val texto: String
        // dependiendo de que item pulses cambia el texto que se mostrará en el toast
        when (item.itemId) {
            R.id.Instrucciones -> {
                mostrarInstrucciones()
            }

            R.id.NuevoJuego -> {
                empezarPartida()
            }

            R.id.Configuracion -> {
                mostrarSeleccionDificultad()
            }

            R.id.SeleccionPersonaje -> {

            }

            else -> return false
        }


        return true;
    }

    /**
     *  Metodo para que al pulsar el boton de instrucciones en el menu se abra una ventana que te indique cuales son
     */
    private fun mostrarInstrucciones() {
        // Saco el texto de las instrucciones del archivo string de resources
        val textoInstrucciones = R.string.InstruccionesCompletas

        // Crear un constructor de cuadro de diálogo
        val builder = AlertDialog.Builder(this)
        // Establezo el título instrucciones
        builder.setTitle(R.string.Instrucciones)


        // Establecer el mensaje y otros atributos del cuadro de diálogo
        builder.setMessage(textoInstrucciones)
            .setCancelable(false)
            // Esto es para cuando se pulse el boton
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()  // Cerrar el cuadro de diálogo
            }

        // Crear el cuadro de diálogo y mostrarlo
        val alertDialog = builder.create()
        alertDialog.show()

    }

    private fun mostrarSeleccionDificultad() {

        // Saco el array de las dificultades de mi archivo xml
        val dificultades = resources.getStringArray(R.array.dificultades)
        // Creo un AlertDialog en la activity actual en la que estoy
        val builder = AlertDialog.Builder(this)
        // Establezco el titulo que va a tener el AlertDialog
        builder.setTitle("Seleccionar Dificultad")

        // Esto es para decir que va a tener varias opciones, pero solo puedes seleccionar una
        // ESTO ES UN CALLBACK, se ejecuta cuando el usuario pulsa en una de las opciones
        // "which" es el elemento que está pulsando (es un int así que es el índice)
        builder.setSingleChoiceItems(dificultades, dificultadSeleccionada) { _, which ->
            dificultadSeleccionada = which
        }

        // Esto es para cuando el usuario pulse el boton aceptar
        // dialog es el objeto alertDialog
        builder.setPositiveButton("Entendido!") { dialog, _ ->
            // Actualizar el tamaño del tablero según la dificultad seleccionada
            tamanoTablero =
                resources.getIntArray(R.array.tamanos_tablero)[dificultadSeleccionada]
            // Cierra el cuadro de texto
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun empezarPartida() {

        // Se crea un gridLayout con el que tenemos en el xml
        val gridLayout: GridLayout = findViewById(R.id.grid)
        // Esto es para que se borren todos los botones previos, porque si no lo que me hacía era sumarlos al grid existente
        gridLayout.removeAllViews()
        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        // for anidado con el numero de filas y de columnas
        for (fila in 1..tamanoTablero) {
            for (columna in 1..tamanoTablero) {
                // se crea un boton
                val boton = Button(this)

                // Esto es para ajustar los parámetros del boton (tamaño, etc.)
                boton.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    // Columnspec y rowspec se utiliza para que los botones se distribuyan por la pantalla (1f es el peso)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(0, 0, 0, 0)

                }
                // Se agrega cada boton a su espacio en el grid
                boton.setPadding(0, 0, 0, 0)

                gridLayout.addView(boton)
            }
        }


    }


}