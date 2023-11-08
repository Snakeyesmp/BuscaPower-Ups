package com.example.buscaminas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // se crea un objeto toolbar, ¡importante importar el correcto!
        val toolbar : Toolbar = findViewById (R.id.toolbar)
        // hace que la toolbar funcione como actionbar para la activity window actual
        setSupportActionBar(toolbar)

        val gridLayout : GridLayout = findViewById(R.id.grid)

        val rowCount = 16
        val columnCount = 16

        // Esta manera de obtener cuanto mide la pantalla está deprecated, pero al estar usando una API antigua no queda otra
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // el ancho del botón será el ancho de la pantalla dividido por el numero de columnas
        val cellWidth = screenWidth / columnCount
        val cellHeight = screenHeight / rowCount

        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        for (i in 1..rowCount) {
            for (j in 1..columnCount) {
                var cell = Button(this)
                // Esto es para poder cambiar los parámetros del boton ( tamaño, margenes, etc.)
                cell.layoutParams = GridLayout.LayoutParams().apply {
                    width = cellWidth
                    height = cellHeight
                }

                // Agrega la celda al GridLayout.
                gridLayout.addView(cell)
            }
        }



    }

    /**
     *Metodo que se usa para inflar un menu dentro de la toolbar y que aparezca
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

}