package com.example.buscaminas

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.gridlayout.widget.GridLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlin.random.Random



/**
 * Clase principal donde, por desgracia, está to-do el código
 *
 */
class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var dificultadSeleccionada = 0
    private var tamanoTablero = tamanosTablero[0]

    private lateinit var fuenteRetro: Typeface // Variable para la fuente (Lateinit o excepción)
    private lateinit var estadoTablero: Array<Array<Int>> // Almacena los numeros de cada casilla
    lateinit var nombresPersonajes: Array<String> // Array con los nombres de los personajes
    private var seleccion = 0 // Para saber que item del spinner está seleccionado
    private var banderasColocadas = 0 // El numero de banderas que ha puesto el usuario

    /**
     * Método llamado cuando se crea la actividad
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fuenteRetro = ResourcesCompat.getFont(
            this, R.font.retrofont2
        )!! // "!!" Esto es para que el programa sepa que esto nunca va a ser nulo (?)

        // se crea un objeto toolbar, y hace que funcione como actionbar principal
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        estadoTablero = Array(tamanoTablero) { Array(tamanoTablero) { 0 } }
        // Array con todos los nombres de los personajes
        nombresPersonajes = arrayOf(
            getString(R.string.Estrella),
            getString(R.string.Setaroja),
            getString(R.string.Setaverde),
            getString(R.string.FlorDeFuego)
        )
        // Para que el spinner sea el personalizado
        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        val adaptadorPersonalizado =
            AdaptadorPersonalizado(this, R.layout.spinner_personajes, nombresPersonajes)
        selectorPersonaje.adapter = adaptadorPersonalizado
        selectorPersonaje.onItemSelectedListener = this
    }

    /**
     *  Metodo que se usa para inflar un menu dentro de la toolbar y que aparezca
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Llamo a mi menu.xml para inflarlo
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    /**
     * Se llama cuando se selecciona un elemento del menú de opciones.
     *
     * @param item El elemento del menú que se seleccionó.
     * @return `true` si el evento fue manejado con éxito, `false` en caso contrario.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.Instrucciones -> {
                mostrarInstrucciones()
            }

            R.id.NuevoJuego -> {
                iniciarTablero()
            }

            R.id.Configuracion -> {
                mostrarSeleccionDificultad()
            }

            R.id.BotonPersonaje -> {
                mostrarSpinnerPersonajes()
            }

            else -> return false
        }

        return true
    }

    /**
     *  Metodo para que al pulsar el boton de instrucciones en el menu se abra una ventana que te indique cuales son
     */
    private fun mostrarInstrucciones() {

        val textoInstrucciones = R.string.InstruccionesCompletas

        val builder = AlertDialog.Builder(this) // creo AlertDialog
        builder.setTitle(R.string.Instrucciones) // Le pongo título

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
     * Muestra un cuadro de diálogo para permitir al usuario seleccionar la dificultad del juego.
     * Actualiza el tamaño del tablero según la dificultad seleccionada.
     */
    private fun mostrarSeleccionDificultad() {
        val dificultades =
            resources.getStringArray(R.array.dificultades) // Saco el array de las dificultades de mi archivo xml
        val builder =
            AlertDialog.Builder(this) // Creo un AlertDialog en la activity actual en la que estoy
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
            tamanoTablero = tamanosTablero[dificultadSeleccionada]
            dialog.dismiss() // Cierra el cuadro de texto
        }

        builder.create().show()
    }

    /**
     * Inicia un nuevo tablero de juego.
     * - Crea un nuevo GridLayout con el diseño definido en el archivo XML.
     * - Borra todos los botones previos en el GridLayout existente.
     * - Establece el número de filas y columnas en el GridLayout según el tamaño del tablero.
     * - Inicializa el array bidimensional del tablero con todos los valores a 0.
     * - Comienza una nueva partida llamando al método [empezarPartida] con el GridLayout proporcionado.
     *
     */
    private fun iniciarTablero() {
        banderasColocadas = 0
        val gridLayout: GridLayout = findViewById(R.id.grid) // grid del XML
        gridLayout.removeAllViews() // se limpia el grid
        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = tamanoTablero
        gridLayout.columnCount = tamanoTablero

        // Se inicializa con una lambda, pone todos los valores a 0
        estadoTablero = Array(tamanoTablero) { Array(tamanoTablero) { 0 } }

        empezarPartida(gridLayout)
    }

    /**
     * Inicia una nueva partida del juego. Configura el tablero, coloca las minas, calcula el número de minas adyacentes
     * para cada casilla y configura los botones en el [GridLayout].
     *
     * @param gridLayout El [GridLayout] en el que se colocarán los botones del tablero del juego.
     *
     * @see colocarminas
     * @see calcularminasAdyacentes
     * @see configurarBoton
     */
    private fun empezarPartida(gridLayout: GridLayout) {
        // Llamo al spinner y lo hago invisible
        val spinner = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        spinner.visibility = View.INVISIBLE

        colocarminas(estadoTablero) // Se colocan las minas en el tablero
        calcularminasAdyacentes(estadoTablero) // Se calculan el numero de minas de cada casilla

        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                configurarBoton(fila, columna, gridLayout) // Se configuran todos los botones
            }
        }
    }

    /**
     * Coloca las minas en el tablero de acuerdo a la dificultad seleccionada.
     *
     * @param estadoTablero El estado actual del tablero.
     */
    private fun colocarminas(estadoTablero: Array<Array<Int>>) {
        val numerominas = numeroMinas[dificultadSeleccionada] // numMinas según dificultad
        var minasColocadas = 0 // Se inicializan a 0 el número de minas

        // Se colocan las minas hasta que haya las especificadas para esa dificultad
        while (minasColocadas < numerominas) {
            // se dan valores aleatorios a la fila y la columna (0 el número mínimo y el máximo el tamaño del tablero)
            val fila = Random.nextInt(tamanoTablero)
            val columna = Random.nextInt(tamanoTablero)

            // Si la casilla está vacía se coloca una mina, si no, se volverá a generar una casilla aleatoria
            if (estadoTablero[fila][columna] != -1) {
                estadoTablero[fila][columna] = -1 // Se da el valor "-1" a esa casilla
                minasColocadas++ // Se añade +1 al contador de minas
            }
        }
    }

    /**
     * Calcula el número de minas adyacentes para cada casilla en el tablero.
     *
     * @param estadoTablero El estado actual del tablero.
     */
    private fun calcularminasAdyacentes(estadoTablero: Array<Array<Int>>) {
        // Recorre todas las filas y columnas del tablero
        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                // Si hay mina, no necesita calcular nada
                if (estadoTablero[fila][columna] != -1) {
                    val contadorminas =
                        contarMinasAdyacentes(estadoTablero, fila, columna) // calcularNumMinas
                    estadoTablero[fila][columna] =
                        contadorminas // Actualizar el valor en la casilla actual
                }
            }
        }
    }

    /**
     * Configuración especial para los botones, se cambia su tamaño, la imagen,
     * el color del texto, y configuración específica del grid
     *
     * @param fila La fila en la que se encuentra el botón.
     * @param columna La columna en la que se encuentra el botón.
     * @param gridLayout El GridLayout que contiene los botones
     * @return El botón configurado.
     */
    private fun configurarBoton(fila: Int, columna: Int, gridLayout: GridLayout): Button {
        val boton = Button(this)

        boton.typeface = Typeface.create(fuenteRetro, Typeface.BOLD) // Fuente personalizada
        // Esto es para ajustar los parámetros del botón (tamaño, etc.)
        boton.layoutParams = GridLayout.LayoutParams().apply {
            // Para que se adapte al padre
            width = 0
            height = 0
            // Columnspec y rowspec se utiliza para que los botones se distribuyan por la pantalla (1f es el peso)
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        }
        boton.setBackgroundResource(R.drawable.bloqueinterrogacion)
        boton.setPadding(
            0, 0, 0, 0
        ) // Padding es para que aparezcan los nums cuando el boton es pequeño

        boton.setOnClickListener {// Listener para cada botón
            revelarCelda(fila, columna, boton)
        }
        // Listener de cuando el usuario mantiene el botón pulsado
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
        }
        */

        boton.setTextColor(ContextCompat.getColor(this, R.color.blanco)) // Color de texto
        gridLayout.addView(boton) // se añade el botón al grid
        return boton
    }

    /**
     * Revela la celda en la posición especificada del tablero de Buscaminas.
     *
     * @param fila La fila de la celda que se va a revelar.
     * @param columna La columna de la celda que se va a revelar.
     * @param boton El botón asociado a la celda que se va a revelar.
     */
    private fun revelarCelda(fila: Int, columna: Int, boton: Button) {

        val valor = estadoTablero[fila][columna]

        when (valor) {
            0 -> {
                revelarCasillasAdyacentes(fila, columna) // revelar las que son 0
            }

            -1 -> {
                boton.setBackgroundResource(imagenesPersonajes[seleccion])
                mostrarFinDelJuego(false)
            }

            -2, -3 -> {
                return
            }
        }
        // para que si hay una mina no me muestre un "-1"
        if (valor != -1) {
            // Si en la casilla hay 0 minas alrededor se deja vacío, si no se muestra el número que hay
            boton.text = if (valor == 0) "" else valor.toString()
            // Cambiar el fondo del botón cuando el valor es 0
            boton.setBackgroundResource(R.drawable.bloquepulsado)
            estadoTablero[fila][columna] = -2 // Sin minas alrededor
        }
    }

    /**
     * Coloca una bandera en la celda especificada
     *
     * @param fila La fila de la celda en la que se va a colocar ola bandera.
     * @param columna La columna de la celda en la que se va a colocarla bandera.
     * @param boton El botón asociado a la celda en la que se va a colocar la bandera.
     */
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
            banderasColocadas++
        } else if (valor == -3) {
            // (Para no poder quitar la bandera)
            return
        }

        // Verificar condición de victoria
        if (valor != -1)
            mostrarFinDelJuego(false)
        else if (banderasColocadas == numeroMinas[dificultadSeleccionada])
            mostrarFinDelJuego(true)
    }


    /**
     * Cuenta el número de minas adyacentes a una celda específica en el tablero
     *
     * @param estadoTablero El estado actual del tablero.
     * @param fila La fila de la celda para la que se cuentan las minas adyacentes.
     * @param columna La columna de la celda para la que se cuentan las minas adyacentes.
     * @return El número de minas adyacentes a la celda especificada.
     */
    private fun contarMinasAdyacentes(
        estadoTablero: Array<Array<Int>>, fila: Int, columna: Int
    ): Int {
        var contadorminas = 0
        // Recorre (arriba, mismo nivel y abajo)
        for (i in -1..1) {
            // Recorre (Izquierda, mismo nivel y derecha)
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
        return contadorminas
    }

    /**
     * Obtiene el botón en la posición especificada dentro del GridLayout.
     *
     * @param fila La fila del botón que se desea obtener.
     * @param columna La columna del botón que se desea obtener.
     * @return El botón en la posición especificada dentro del GridLayout.
     */
    private fun obtenerBoton(fila: Int, columna: Int): Button {
        val gridLayout: GridLayout = findViewById(R.id.grid)
        val indiceBoton = fila * tamanoTablero + columna
        return gridLayout.getChildAt(indiceBoton) as Button
    }

    /**
     * Muestra un cuadro de diálogo que indica el fin del juego, ya sea una victoria o una derrota.
     *
     * @param victoria Indica si el juego se ha ganado (true) o perdido (false).
     */
    private fun mostrarFinDelJuego(victoria: Boolean) {

        val gridLayout: GridLayout = findViewById(R.id.grid)
        mostrarTableroCompleto()

        // Cambiar mensaje según se haya ganado o no
        val mensaje = if (victoria) R.string.ganaste else R.string.perdiste
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.finJuego).setMessage(mensaje)
            // Parametros lambda : DialogInterface, se pone barra baja porque no lo necesito
            .setPositiveButton(R.string.NuevoJuego) { _, _ -> iniciarTablero() }
            .setNegativeButton(R.string.irMenu) { _, _ -> gridLayout.removeAllViews() }
            .setCancelable(false).show()// Que no se pueda cerrar
    }

    /**
     * Revela las casillas adyacentes a la posición especificada del tablero.
     *
     * @param fila La fila de la celda a partir de la cual se revelarán las casillas adyacentes.
     * @param columna La columna de la celda a partir de la cual se revelarán las casillas adyacentes.
     */
    private fun revelarCasillasAdyacentes(fila: Int, columna: Int) {
        // Verificar si la casilla ya está revelada
        if (estadoTablero[fila][columna] == -2) {
            return
        }

        // Marcar la casilla como revelada
        estadoTablero[fila][columna] = -2

        /*
         * Esto es para que no recorra las diagonales, solo arriba, abajo, izquierda y derecha
         *
         * (fila -1 y columna se queda igual, luego fila +1 y columna se queda igual y así...)
         *
         */
        val direcciones = arrayOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)

        // Recorrer las direcciones posibles
        for ((filaDir, colDir) in direcciones) {
            val filaVecina = fila + filaDir
            val columnaVecina = columna + colDir

            // Verificar si la casilla vecina está dentro del tablero
            if (filaVecina in 0 until tamanoTablero && columnaVecina in 0 until tamanoTablero) {
                // Verificar si la casilla vecina no está revelada ni marcada
                if (estadoTablero[filaVecina][columnaVecina] != -2 && estadoTablero[filaVecina][columnaVecina] != -3) {
                    // Obtener el botón correspondiente a la casilla vecina
                    val botonVecino = obtenerBoton(filaVecina, columnaVecina)

                    // Llamar recursivamente a revelarCelda para la casilla vecina
                    revelarCelda(filaVecina, columnaVecina, botonVecino)
                }
            }
        }
    }

    /**
     * Muestra el el valor de todas las casillas, y muestra las bombas
     */
    private fun mostrarTableroCompleto() {
        for (fila in 0 until tamanoTablero) {
            for (columna in 0 until tamanoTablero) {
                val boton = obtenerBoton(fila, columna)
                val valor = estadoTablero[fila][columna]

                // Verificar si la casilla ya está revelada
                if (valor == -2) {
                    continue
                }

                // Marcar la casilla como revelada
                estadoTablero[fila][columna] = -2

                // Establecer el fondo según el valor de la casilla
                when (valor) {
                    -1 -> {
                        // Imagen de la mina
                        boton.setBackgroundResource(imagenesPersonajes[seleccion])
                    }

                    -3 -> {
                        // Imagen de la bandera
                        boton.setBackgroundResource(R.drawable.bandera)
                    }

                    else -> {
                        // Imagen pulsada
                        boton.setBackgroundResource(R.drawable.bloquepulsado)

                        // Mostrar el número
                        boton.text = if (valor == 0) "" else valor.toString()
                    }
                }
            }
        }
    }


    /**
     * Muestra el Spinner de selección de personajes en la interfaz gráfica.
     * Hace que el Spinner sea visible para que el usuario pueda interactuar con él.
     */
    private fun mostrarSpinnerPersonajes() {
        val selectorPersonaje = findViewById<Spinner>(R.id.spinnerPersonajesPrincipal)
        selectorPersonaje.visibility = View.VISIBLE // hace que el spinner se vea
    }

    /**
     * Se activa cuando se selecciona un elemento en el AdapterView, en este caso, el Spinner.
     * Actualiza la variable de clase 'seleccion' con la posición del elemento seleccionado.
     *
     * @param parent El AdapterView del cual se seleccionó un elemento.
     * @param view La vista del elemento seleccionado.
     * @param position La posición del elemento seleccionado en el adaptador.
     * @param id El ID del elemento seleccionado.
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        seleccion = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private inner class AdaptadorPersonalizado(
        context: Context, resource: Int, objects: Array<String>
    ) : ArrayAdapter<String>(context, resource, objects) {

        override fun getDropDownView(
            position: Int, convertView: View?, parent: ViewGroup
        ): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }

        override fun getView(
            position: Int, convertView: View?, parent: ViewGroup
        ): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }

        private fun crearFilaPersonalizada(
            position: Int, convertView: View?, parent: ViewGroup
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
}