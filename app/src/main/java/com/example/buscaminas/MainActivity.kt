package com.example.buscaminas

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
    // Array con cuantas casilla tiene el lado del tablero
    private val tamanosTablero = arrayOf(8, 12, 16) // Principiante - Amateur - Profesional

    // Array con cuantas minas tiene el tablero
    private val numeroMinas = arrayOf(10, 30, 60) // Principiante - Amateur - Profesional
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
        // Llamo a mi menu.xml para inflarlo
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Este metodo es llamado cuando seleccionas un item del menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // dependiendo de que item pulses en el menu llama a una función u otra
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

            R.id.BotonPersonaje -> {
                mostrarPopupSeleccionPersonaje()
            }

            else -> return false
        }

        return true
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
        builder.setMessage(textoInstrucciones).setCancelable(false)
            // Esto es para cuando se pulse el boton
            .setPositiveButton(R.string.aceptar) { dialog, _ ->
                dialog.dismiss()  // Cerrar el cuadro de diálogo
            }

        // Crear el cuadro de diálogo y mostrarlo
        val alertDialog = builder.create()
        alertDialog.show()

    }

    /**
     * Metodo para cuando pulsas el botón de elegir dificultad en el menú
     */
    private fun mostrarSeleccionDificultad() {

        // Saco el array de las dificultades de mi archivo xml
        val dificultades = resources.getStringArray(R.array.dificultades)
        // Creo un AlertDialog en la activity actual en la que estoy
        val builder = AlertDialog.Builder(this)
        // Establezco el titulo que va a tener el AlertDialog
        builder.setTitle("Seleccionar Dificultad")

        // Esto es para decir que va a tener varias opciones, pero solo puedes seleccionar una
        // ESTO ES UN CALLBACK, se ejecuta cuando el usuario pulsa en una de las opciones
        // La "_" representa el DialogInterface que ha llamado a este método, como no lo necesito se deja en blanco
        // "which" es el elemento que está pulsando (es un int así que es el índice)
        builder.setSingleChoiceItems(dificultades, dificultadSeleccionada) { _, which ->
            dificultadSeleccionada = which
        }

        // Esto es para cuando el usuario pulse el boton aceptar
        // dialog es el objeto alertDialog
        builder.setPositiveButton("Entendido!") { dialog, _ ->
            // Actualizar el tamaño del tablero según la dificultad seleccionada
            tamanoTablero = tamanosTablero[dificultadSeleccionada]
            // Cierra el cuadro de texto
            dialog.dismiss()
        }

        builder.create().show()
    }

    /**
     * Metodo para cuando empiezas partida desde el menú
     */
    private fun empezarPartida() {

        // Se crea un gridLayout con el que tenemos en el xml
        val gridLayout: GridLayout = findViewById(R.id.grid)
        // Esto es para que se borren todos los botones previos, porque si no lo que me hacía era sumarlos al grid existente
        gridLayout.removeAllViews()
        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        // Inicializar el array bidimensional del tablero
        // Lo que hay entre llaves es una lambda, inicializa todos los valores a 0
        val estadoTablero = Array(tamanoTablero) { Array(tamanoTablero) { 0 } }

        // Colocar las minas en el tablero
        colocarminas(estadoTablero)

        // Calcular el número de minas adyacentes
        calcularminasAdyacentes(estadoTablero)

        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        // for anidado con el numero de filas y de columnas
        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                // se crea un boton
                val boton = Button(this)
                // Configurar el botón según el estado del tablero
                when (estadoTablero[fila][columna]) {
                    -1 -> {
                        // Si hay una mina, se pone una imagen al botón
                        boton.setBackgroundResource(R.mipmap.toad_fumon_foreground)
                    }

                    else -> {
                        // Si no hay una mina, te dirá cuantas hay alrededor
                        boton.text = estadoTablero[fila][columna].toString()
                    }
                }
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
                // se añade el boton al grid
                gridLayout.addView(boton)
            }
        }


    }

    /**
     * Coloca minas en el tablero de manera aleatoria.
     *
     * @param estadoTablero El tablero en el que se van a colocar minas
     */
    private fun colocarminas(estadoTablero: Array<Array<Int>>) {
        // Obtener el número de minas según la dificultad( del array de strings.xml)
        val numerominas = numeroMinas[dificultadSeleccionada]

        // Lógica para colocar las minas de manera aleatoria utilizando random
        val random = java.util.Random()
        // Se inicializa a 0 el número de minasa
        var minasColocadas = 0

        // Se colocan las minas hasta que haya las especificadas para esa dificultad
        while (minasColocadas < numerominas) {
            // se dan valores aleatorios a la fila y la columna (0 el número mínimo y el máximo el tamaño del tablero)
            val fila = random.nextInt(tamanoTablero)
            val columna = random.nextInt(tamanoTablero)

            // Si la casilla está vacía se coloca una mina, si no, se volverá a generar una casilla aleatoria
            if (estadoTablero[fila][columna] != -1) {
                // Se da el valor "-1" a esa casilla en específico
                estadoTablero[fila][columna] = -1
                // Se añade +1 al contador de minas para que cuando llegue al máximo deje de poner minas
                minasColocadas++
            }
        }
    }

    /**
     * Calcula el número de minas adyacentes para cada casilla en el tablero.
     *
     * @param estadoTablero El estado actual del tablero.
     */
    private fun calcularminasAdyacentes(estadoTablero: Array<Array<Int>>) {
        // Recorre todas las filas del tablero
        for (fila in 0 until tamanoTablero) {
            // Recorre todas las columnas del tablero
            for (columna in 0 until tamanoTablero) {
                // Si hay mina, no necesita calcular nada
                if (estadoTablero[fila][columna] != -1) {
                    // Calcular el número de minas adyacentes
                    var contadorminas = 0
                    // Recorre las las filas adyacentes (arriba, mismo nivel y abajo)
                    for (i in -1..1) {
                        // Recorre las columnas adyacentes (Izquierda, mismo nivel y derecha)
                        for (j in -1..1) {
                            val filaVecina = fila + i
                            val columnaVecina = columna + j
                            // Verificar si la casilla vecina está dentro del tablero
                            if (filaVecina in 0 until tamanoTablero && columnaVecina in 0 until tamanoTablero) {
                                // Verificar si la casilla vecina tiene una mina
                                if (estadoTablero[filaVecina][columnaVecina] == -1) {
                                    contadorminas++
                                }
                            }
                        }
                    }
                    // Actualizar el valor en la casilla actual con el número de minas adyacentes
                    estadoTablero[fila][columna] = contadorminas
                }
            }
        }
    }

    /**
     * Metodo que genera un AlertDialog con un spinner para cuando pulses el boton del menu para elegir personaje
     */
    private fun mostrarPopupSeleccionPersonaje() {
        val popupMenu = PopupMenu(this, findViewById(R.id.BotonPersonaje))
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_spinner, popupMenu.menu)

        // Obtener el item del menú que contiene el Spinner
        val menuItem = popupMenu.menu.findItem(R.id.spinner_personajes)

        // Obtener la vista del Spinner desde el ítem del menú
        val actionView = menuItem.actionView as Spinner

        // Un array para seleccionar el personaje TODO que tenga fotos y tal
        val personajes = arrayOf(
            "Personaje 1",
            "Personaje 2",
            "Personaje 3",
            "Personaje 4",
            "Personaje 5",
            "Personaje 6"
        )

        // Crear un ArrayAdapter utilizando el contexto actual, el diseño de la lista simple y la matriz de elementos
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, personajes)

        // Establecer el adaptador en el Spinner
        actionView.adapter = adapter

        // Manejar la selección del Spinner si es necesario
        actionView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Implementa los métodos necesarios según tus necesidades
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Acciones cuando se selecciona un elemento del Spinner
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acciones cuando no se selecciona nada
            }
        }

        // Muestra el menú emergente
        popupMenu.show()
    }

    /*
    private fun seleccionPersonaje2() {
        val spinner: Spinner = findViewById(R.id.spinner_personajes)
        // Se crea un arrayAdapter con el array de strings que tengo de las ciudades
        spinner.onItemSelectedListener = this
        // El primer parámetro es contexto, el segundo es el array con los nombres de las ciudades, y el tercero el identificador del layout para crear los items
        ArrayAdapter.createFromResource(
            this,
            R.array.dificultades,
            android.R.layout.simple_spinner_item
        )
            .also { adapter ->
                // Este es el layout que se va a usar cuando aparezcan la lista de opciones
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Se aplica el adaptador al spinner
                spinner.adapter = adapter
            }

    }
    */

}
