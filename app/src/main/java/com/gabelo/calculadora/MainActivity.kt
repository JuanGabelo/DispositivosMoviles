package com.gabelo.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gabelo.calculadora.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var concatenatedNumbers = "" // Variable para mantener la concatenación
    private lateinit var textViewResult: TextView // Referencia al TextView
    private var firstNumber = ""
    private var secondNumber = ""
    private var currentOperation = ""
    private var currentExpression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el TextView
        textViewResult = findViewById(R.id.btnViewOperations)

        binding.btnDeleteAll.setOnClickListener(this)
        binding.btnDivision.setOnClickListener(this)
        binding.btnMultiplicacion.setOnClickListener(this)
        binding.btnDeleteNumber.setOnClickListener(this)
        binding.btnNumberEight.setOnClickListener(this)
        binding.btnNumberFive.setOnClickListener(this)
        binding.btnNumberFowr.setOnClickListener(this)
        binding.btnNumberNine.setOnClickListener(this)
        binding.btnNumberOne.setOnClickListener(this)
        binding.btnNumberSeven.setOnClickListener(this)
        binding.btnNumberSix.setOnClickListener(this)
        binding.btnNumberThree.setOnClickListener(this)
        binding.btnNumberTwo.setOnClickListener(this)
        binding.btnNumberZero.setOnClickListener(this)
        binding.btnParentesisLeft.setOnClickListener(this)
        binding.btnParentesisRight.setOnClickListener(this)
        binding.btnResta.setOnClickListener(this)
        binding.btnResultadoTotal.setOnClickListener(this)
        binding.btnSuma.setOnClickListener(this)
    }

    // Función onClick
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btnNumberZero -> {
                recibirNumero("0")
            }

            R.id.btnNumberOne -> {
                recibirNumero("1")
            }

            R.id.btnNumberTwo -> {
                recibirNumero("2")
            }

            R.id.btnNumberThree -> {
                recibirNumero("3")
            }

            R.id.btnNumberFowr -> {
                recibirNumero("4")
            }

            R.id.btnNumberFive -> {
                recibirNumero("5")
            }

            R.id.btnNumberSix -> {
                recibirNumero("6")
            }

            R.id.btnNumberSeven -> {
                recibirNumero("7")
            }

            R.id.btnNumberEight -> {
                recibirNumero("8")
            }

            R.id.btnNumberNine -> {
                recibirNumero("9")
            }

            R.id.btnSuma -> {
                recibirOperador("+")
            }

            R.id.btnResta -> {
                recibirOperador("-")
            }

            R.id.btnMultiplicacion -> {
                recibirOperador("*")
            }

            R.id.btnDivision -> {
                recibirOperador("/")
            }

            R.id.btnResultadoTotal -> {
                val expr = binding.btnViewOperations.text.toString()
                val resultado = evaluar(expr)
                binding.btnViewOperations.text = resultado.toString()
            }

            R.id.btnParentesisLeft -> {
                recibirOperador("(")
            }

            R.id.btnParentesisRight -> {
                recibirOperador(")")
            }

            R.id.btnResultadoTotal -> {
                val expr = binding.btnViewOperations.text.toString()
                val resultado = evaluar(expr)
                binding.btnViewOperations.text = resultado.toString()
            }

            R.id.btnDeleteNumber -> {
                deleteNumber()
            }

            R.id.btnDeleteAll -> {
                deleteAll()
            }

        }
    }
    private fun recibirNumero(numero: String) {
        binding.btnViewOperations.append(numero)
    }

    private fun evaluar(expr: String): Double {
        val numeros = mutableListOf<Double>()
        val operadores = mutableListOf<Char>()

        var i = 0
        while (i < expr.length) {
            if (expr[i].isWhitespace()) {
                i++
                continue
            }

            if (expr[i].isDigit() || expr[i] == '.') {
                var buffer = ""
                while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                    buffer += expr[i]
                    i++
                }
                numeros.add(buffer.toDouble())
            }

            else if (expr[i] == '(') {
                if (!verifyOperatorBeforeParentesis(expr, i)) {
                    return Double.NaN
                }
                operadores.add(expr[i])
                i++
            }
            else if (expr[i] == ')') {
                if (!verifyParentesis(operadores)) {
                    return Double.NaN
                }
                while (operadores.last() != '(') {
                    processOperation(numeros, operadores)
                }
                operadores.removeAt(operadores.size - 1) // remove '('
                i++
            }
            else if (expr[i] == '+' || expr[i] == '-' || expr[i] == '*' || expr[i] == '/') {
                if (!verifyOperator(expr, i)) {
                    return Double.NaN
                }
                while (operadores.isNotEmpty() && operadores.last() != '(' && priority(expr[i]) <= priority(
                        operadores.last()
                    )
                ) {
                    processOperation(numeros, operadores)
                }
                operadores.add(expr[i])
                i++
            }
        }
        if (!verifyParejaParentesis(operadores)) {
            return Double.NaN
        }

        while (operadores.isNotEmpty()) {
            processOperation(numeros, operadores)
        }

        return numeros.last()
    }
    private fun priority(op: Char): Int {
        return when (op) {
            '+' -> 1
            '-' -> 1
            '*' -> 2
            '/' -> 2
            else -> -1
        }
    }
    private fun processOperation(numeros: MutableList<Double>, operadores: MutableList<Char>) {
        if (numeros.size < 2 || operadores.isEmpty()) {
            Toast.makeText(this, "Error: Operación inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val num1 = numeros.removeAt(numeros.size - 1)
        val num2 = numeros.removeAt(numeros.size - 1)

        val result = when (operadores.removeAt(operadores.size - 1)) {
            '+' -> num2 + num1
            '-' -> num2 - num1
            '*' -> num2 * num1
            '/' -> {
                if (num1 == 0.0) {
                    Toast.makeText(this, "Error: División por cero", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    num2 / num1
                }
            }

            else -> 0.0
        }

        numeros.add(result)
    }
    private fun verifyOperatorBeforeParentesis(expr: String, i: Int): Boolean {
        if (i > 0 && expr[i - 1].isDigit() && expr[i + 1] != '-' && expr[i + 1] != '+') {
            Toast.makeText(
                this,
                "Error: Número seguido de paréntesis de apertura sin operador",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
    private fun verifyParentesis(operadores: MutableList<Char>): Boolean {
        if (operadores.isEmpty() || !operadores.contains('(')) {
            Toast.makeText(
                this,
                "Error: Paréntesis de cierre sin paréntesis de apertura correspondiente",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
    private fun verifyParejaParentesis(operadores: MutableList<Char>): Boolean {
        if (operadores.contains('(')) {
            Toast.makeText(
                this,
                "Error: Paréntesis de apertura sin paréntesis de cierre correspondiente",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }
    private fun verifyOperator(expr: String, i: Int): Boolean {
        if (i > 0 && "+-*/".contains(expr[i - 1])) {
            Toast.makeText(this, "Error: Dos operadores consecutivos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun recibirOperador(operador: String) {
        binding.btnViewOperations.append(operador)
    }
    private fun deleteNumber() {
        val text = binding.btnViewOperations.text.toString()
        if (text.isNotEmpty()) {
            binding.btnViewOperations.text = text.substring(0, text.length - 1)
        }
    }

    private fun deleteAll() {
        binding.btnViewOperations.text = ""
    }
}