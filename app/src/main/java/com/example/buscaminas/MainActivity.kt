package com.example.buscaminas

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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

/**
 * Clase principal donde, por desgracia, está to-do el código
 *
 */
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    // Variable que voy a usar luego para la fuente (Si la inicializo ahora da excepcion)
    private lateinit var fuenteRetro:Typeface

    private var dificultadSeleccionada = 0 // Valor predeterminado
    private var tamanoTablero =
        8 // Tamaño por defecto por si el usuario empieza partida sin elegir dificultad

    // TODO cuando se pierde la partida hacer que se muestre ubicación de todas las minas, cambiar el menu de fin de partida.
    private lateinit var estadoTablero: Array<Array<Int>> // Estado del tablero (almacena las minas y los números adyacentes)

    lateinit var nombresPersonajes: Array<String>


    // Para saber que imagen va a seleccionar el tablero
    private var seleccion = 0


    // El numero de banderas que ha puesto el usuario
    private var banderasColocadas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // las dos exclamaciones al final son porque este valor no puede ser nulo, y están lanzando un nullPointerException (Algo así)
        fuenteRetro  = ResourcesCompat.getFont(this, R.font.retrofont2)!!

        // se crea un objeto toolbar, ¡importante importar el correcto!
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        // hace que la toolbar funcione como actionbar para la activity window actual
        setSupportActionBar(toolbar)
        estadoTablero = Array(tamanoTablero) { Array(tamanoTablero) { 0 } }

        nombresPersonajes = arrayOf(
            getString(R.string.Setaroja),
            getString(R.string.Setaverde),
            getString(R.string.Estrella),
            getString(R.string.Flor)
        )

        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        val adaptadorPersonalizado =
            AdaptadorPersonalizado(this, R.layout.spinner_personajes, nombresPersonajes)
        selectorPersonaje.adapter = adaptadorPersonalizado

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
                mostrarPopupSeleccionPersonaje2()
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
        estadoTablero = Array(tamanoTablero) { Array(tamanoTablero) { 0 } }

        // Colocar las minas en el tablero
        colocarminas(estadoTablero)

        // Calcular el número de minas adyacentes
        calcularminasAdyacentes(estadoTablero)

        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                // se crea un boton
                val boton = Button(this)
                // Pongo el texto de todos los botones a vacío, para que no aparezca nada
                boton.text = ""
                // Le pongo la fuente retro de 8bit
                boton.typeface = fuenteRetro
                // Esto es para ajustar los parámetros del boton (tamaño, etc.)
                boton.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    // Columnspec y rowspec se utiliza para que los botones se distribuyan por la pantalla (1f es el peso)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    setMargins(0, 0, 0, 0)
                }
                boton.setBackgroundResource(R.drawable.bloqueinterrogacion)
                // Se agrega cada boton a su espacio en el grid
                boton.setPadding(0, 0, 0, 0)
                // Un listener de cuando el usuario pulsa para cada boton
                boton.setOnClickListener {
                    revelarCelda(fila, columna, boton)
                }
                // Listener de cuando el usuario mantiene el boton pulsado
                boton.setOnLongClickListener {
                    colocarBandera(fila, columna, boton)
                    true  // Indica que el evento ha sido manejado
                }

                /*
                // Configurar el color del texto según el número de minas adyacentes
                when (estadoTablero[fila][columna]) {
                    1 -> {
                        boton.setTextColor(ContextCompat.getColor(this, R.color.verde))
                    }

                    2 -> {
                        boton.setTextColor(ContextCompat.getColor(this, R.color.naranja))
                    }

                    in 3..8 -> {
                        boton.setTextColor(ContextCompat.getColor(this, R.color.rojo))
                    }
                    // Puedes agregar más casos según sea necesario
                }
                */

                boton.setTextColor(ContextCompat.getColor(this, R.color.blanco))

                // se añade el boton al grid
                gridLayout.addView(boton)
            }
        }

    }

    private fun colocarBandera(fila: Int, columna: Int, boton: Button) {
        val valor = estadoTablero[fila][columna]

        // Verificar si la celda ya está revelada
        if (valor == -2) {
            return
        }

        // Si la celda no tiene bandera y aún hay banderas disponibles, colocar una bandera (-3)
        if (valor != -3 && banderasColocadas < numeroMinas[dificultadSeleccionada]) {
            boton.setBackgroundResource(R.drawable.bandera)
            estadoTablero[fila][columna] = -3
            // Aumentar una bandera colocada
            banderasColocadas++
        } else if (valor == -3) {
            // Si la celda ya tiene bandera, no hacer nada (quitar la bandera está deshabilitado)
            return
        }

        // Verificar si se colocó una bandera en una casilla sin mina
        if (valor != -1 && valor != -3) {
            // El usuario colocó una bandera en una casilla sin mina, mostrar mensaje de pérdida
            mostrarFinDelJuego(false)
        }

        // Verificar si el usuario ha colocado todas las banderas correctamente
        if (banderasColocadas == numeroMinas[dificultadSeleccionada]) {
            mostrarFinDelJuego(true)
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

    private fun mostrarPopupSeleccionPersonaje2() {

        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)

        // Cambia la visibilidad del Spinner a VISIBLE
        selectorPersonaje.visibility = View.VISIBLE


    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        seleccion = position

        val selectedItem = parent?.getItemAtPosition(position).toString()
        // Agrega un log para imprimir el ítem seleccionado
        Log.d("SpinnerSelection", "Item seleccionado: $selectedItem")

        // Oculta el Spinner después de seleccionar un personaje
        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        selectorPersonaje.visibility = View.GONE

    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

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

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }

        private fun crearFilaPersonalizada(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val layoutInflater = LayoutInflater.from(context)
            val rowView =
                convertView ?: layoutInflater.inflate(R.layout.spinner_personajes, parent, false)

            // Accede directamente al recurso de cadena usando el índice
            val nombrePersonajeResourceId = nombresPersonajes[position]

            // Establece el texto del TextView con el recurso de cadena
            rowView.findViewById<TextView>(R.id.nombre).text = nombrePersonajeResourceId

            rowView.findViewById<ImageView>(R.id.imagenPersonaje)
                .setImageResource(imagenesPersonajes[position])


            return rowView
        }
    }


    /**
     * Este metodo hace que se muestre el contenido de un botón(Mina, numero de minas o vacío)
     *
     * @param fila: La fila en la que está el boton que llamas
     * @param columna : La columna en la que está el botón al que llamas
     * @param boton : El objeto botón en sí
     *
     */
    private fun revelarCelda(fila: Int, columna: Int, boton: Button) {
        val valor = estadoTablero[fila][columna]

        // Verificar si la celda ya está revelada o marcada con bandera
        if (valor == -2 || valor == -3) {
            return
        }

        // Si la celda contiene una mina
        if (valor == -1) {
            boton.setBackgroundResource(imagenesPersonajes[seleccion])
            mostrarFinDelJuego(false)
        } else {
            // Si la celda no contiene una mina
            when {
                valor == 0 -> {

                    //boton.setBackgroundColor(ContextCompat.getColor(this, R.color.grisOscuro))
                    // Llamar al metodo para que se revelen todas las celdas que sean 0
                    revelarCasillasAdyacentes(fila, columna)
                }
            }


            // Si en la casilla hay 0 minas alrededor se deja vacío, si no se muestra el número que hay
            boton.text = if (valor == 0) "" else valor.toString()
            // Cambiar el fondo del botón cuando el valor es 0
            boton.setBackgroundResource(R.drawable.bloquepulsado)
            // Se pone valor -2 para indicar que ya no hay minas alrededor
            estadoTablero[fila][columna] = -2
        }
    }


    /**
     *
     * Este metodo se usa para obtener un boton de una fila y columna determinada, se usa en el metodo de obtener los 0 que hay alrededor
     *
     * @param fila : La fila en la que está el botón
     * @param columna : La columna en la que está el boton
     *
     * @return el objeto botón encontrado
     */
    private fun obtenerBoton(fila: Int, columna: Int): Button {
        val gridLayout: GridLayout = findViewById(R.id.grid)
        val indiceBoton = fila * tamanoTablero + columna
        return gridLayout.getChildAt(indiceBoton) as Button
    }


    /**
     *
     * Mostrará un alert dialog para indicar que se ha terminado la partida, dirá si has ganado o perdido
     * te dejará empezar otra partida o salir de la aplicación
     *
     * @param victoria: para saber si has ganado o no la partida
     *
     */
    private fun mostrarFinDelJuego(victoria: Boolean) {
        // Cambiar mensaje según se haya ganado o no
        val mensaje = if (victoria) "¡Ganaste!" else "¡Perdiste!"
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fin del juego")
            .setMessage(mensaje)
            .setPositiveButton("Nuevo Juego") { _, _ -> empezarPartida() }
            .setNegativeButton("Salir") { _, _ -> finish() }
            .show()
    }

    private fun revelarCasillasAdyacentes(fila: Int, columna: Int) {
        // Verificar si la casilla ya está revelada
        if (estadoTablero[fila][columna] == -2) {
            return
        }
        // Marcar la casilla como revelada
        estadoTablero[fila][columna] = -2
        // Recorrer las filas adyacentes (arriba, mismo nivel y abajo)
        for (i in -1..1) {
            // Recorrer las columnas adyacentes (izquierda, mismo nivel y derecha)
            for (j in -1..1) {
                val filaVecina = fila + i
                val columnaVecina = columna + j
                // Verificar si la casilla vecina está dentro del tablero
                if (filaVecina in 0 until tamanoTablero && columnaVecina in 0 until tamanoTablero) {
                    // Verificar si la casilla vecina no está revelada ni marcada
                    if (estadoTablero[filaVecina][columnaVecina] !in listOf(-2, -3)) {
                        // Obtener el botón correspondiente a la casilla vecina
                        val botonVecino = obtenerBoton(filaVecina, columnaVecina)
                        // Llamar recursivamente a revelarCelda para la casilla vecina
                        revelarCelda(filaVecina, columnaVecina, botonVecino)
                    }
                }
            }
        }
    }
}
