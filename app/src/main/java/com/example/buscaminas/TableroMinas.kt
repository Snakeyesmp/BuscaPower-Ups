package com.example.buscaminas

import kotlin.random.Random

class TableroMinas(private val tamano: Int, private val numeroMinas: Int) {

    enum class EstadoCelda {
        NO_REVELADA,
        REVELADA,
        CON_BANDERA
    }

    enum class ResultadoJuego {
        EN_CURSO,
        GANADO,
        PERDIDO
    }

    private val estadoCeldas: Array<Array<EstadoCelda>> = Array(tamano) { Array(tamano) { EstadoCelda.NO_REVELADA } }
    private val tieneMina: Array<Array<Boolean>> = Array(tamano) { Array(tamano) { false } }

    init {
        colocarMinas()
        // No necesitas calcular las minas adyacentes aquí, puedes hacerlo cuando se revele la primera celda
    }

    private fun colocarMinas() {
        // Colocar minas de manera aleatoria
        val random = Random
        var minasColocadas = 0

        while (minasColocadas < numeroMinas) {
            val fila = random.nextInt(tamano)
            val columna = random.nextInt(tamano)

            if (!tieneMina[fila][columna]) {
                tieneMina[fila][columna] = true
                minasColocadas++
            }
        }
    }

    fun revelarCelda(fila: Int, columna: Int): ResultadoJuego {
        // Lógica para revelar una celda
        if (tieneMina[fila][columna]) {
            // ¡Has perdido!
            return ResultadoJuego.PERDIDO
        }

        // Puedes agregar más lógica aquí según sea necesario

        // Verificar si todas las celdas no minadas han sido reveladas
        if (todasLasCeldasNoMinadasReveladas()) {
            // ¡Has ganado!
            return ResultadoJuego.GANADO
        }

        // El juego aún está en curso
        return ResultadoJuego.EN_CURSO
    }

    private fun todasLasCeldasNoMinadasReveladas(): Boolean {
        for (fila in 0 until tamano) {
            for (columna in 0 until tamano) {
                if (!tieneMina[fila][columna] && estadoCeldas[fila][columna] != EstadoCelda.REVELADA) {
                    return false
                }
            }
        }
        return true
    }

    fun obtenerEstadoCelda(fila: Int, columna: Int): EstadoCelda {
        return estadoCeldas[fila][columna]
    }

    fun cambiarEstadoCelda(fila: Int, columna: Int, nuevoEstado: EstadoCelda) {
        estadoCeldas[fila][columna] = nuevoEstado
    }

    fun iniciarNuevoJuego() {
        // Lógica para reiniciar el juego según sea necesario
        // En este ejemplo, simplemente reiniciamos los estados y las minas
        for (i in 0 until tamano) {
            for (j in 0 until tamano) {
                estadoCeldas[i][j] = EstadoCelda.NO_REVELADA
                tieneMina[i][j] = false
            }
        }
        colocarMinas()
    }
}