package com.example.buscaminas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Menu
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // se crea un objeto toolbar, ¡importante importar el correcto!
        val toolbar : Toolbar = findViewById (R.id.toolbar)
        // hace que la toolbar funcione como actionbar para la activity window actual
        setSupportActionBar(toolbar)
        // Se crea un gridLayout con el que tenemos en el xml
        val gridLayout : GridLayout = findViewById(R.id.grid)

        val rowCount = 8
        val columnCount = 8
        // Para establecer el numero de filas y de columnas
        gridLayout.rowCount = rowCount
        gridLayout.columnCount = columnCount

        // for anidado con el numero de filas y de columnas
        for (fila in 1..rowCount) {
            for (columna in 1..columnCount) {
                // se crea un boton
                val boton = Button(this)

                // Esto es para ajustar los parámetros del boton (tamaño, etc.)
                boton.layoutParams = GridLayout.LayoutParams().apply{
                    width = 0
                    height = 0
                    // Columnspec y rowspec se utiliza para que los botones se distribuyan por la pantalla (1f es el peso)
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED,1f)
                    rowSpec = GridLayout.spec(GridLayout.UNDEFINED,1f)
                    setMargins(0,0,0,0)

                }
                // Se agrega cada boton a su espacio en el grid
                boton.setPadding(0,0,0,0)

                gridLayout.addView(boton)
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