package com.example.buscaminas
import android.content.Context
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog

class JuegoBuscaminas(private val context: Context, private val tableroMinas: TableroMinas) {

    fun manejarClickCelda(fila: Int, columna: Int, boton: Button) {
        val resultado = tableroMinas.revelarCelda(fila, columna)

        when (resultado) {
            TableroMinas.ResultadoJuego.GANADO -> mostrarFinDelJuego(true)
            TableroMinas.ResultadoJuego.PERDIDO -> mostrarFinDelJuego(false)
            else -> {
                // Puedes agregar más lógica según sea necesario
            }
        }
    }

    private fun mostrarFinDelJuego(victoria: Boolean) {
        val mensaje = if (victoria) "¡Ganaste!" else "¡Perdiste!"
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Fin del juego")
            .setMessage(mensaje)
            .setPositiveButton("Nuevo Juego") { _, _ -> reiniciarJuego() }
            .setNegativeButton("Salir") { _, _ -> finalizarJuego() }
            .show()
    }

    private fun reiniciarJuego() {
        // Lógica para reiniciar el juego según tus necesidades
        tableroMinas.iniciarNuevoJuego()
        // Puedes reiniciar el diseño de la interfaz, etc.
    }

    private fun finalizarJuego() {
        // Lógica para finalizar el juego según tus necesidades
        // Puedes cerrar la aplicación, volver a la pantalla principal, etc.
    }

    fun reiniciarJuegoDesdeBoton(vista: View) {
        reiniciarJuego()
    }

    fun finalizarJuegoDesdeBoton(vista: View) {
        finalizarJuego()
    }

    fun obtenerBoton(fila: Int, columna: Int): Button {
        // Puedes implementar este método según la estructura de tu interfaz
        // Aquí simplemente devuelvo un nuevo botón para fines demostrativos
        return Button(context)
    }
}