package com.dabskutz.kalkulator.core.calculator

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Robust recursive-descent expression parser.
 * Handles operators (+, -, *, /, %, parenthesis), decimals, and negative numbers.
 */
internal class ExpressionParser(rawExpression: String) {

    private val rawExpression: String = NumberFormatter.stripSeparators(rawExpression)

    private enum class TokenType {
        NUMBER, PLUS, MINUS, MULTIPLY, DIVIDE, PERCENT, LEFT_PAREN, RIGHT_PAREN, END
    }

    private data class Token(val type: TokenType, val value: String, val position: Int)

    private val tokens: MutableList<Token> = ArrayList()
    private var index = 0

    fun parse(): Result<BigDecimal> {
        return try {
            tokenize()
            if (tokens.isEmpty() || (tokens.size == 1 && tokens[0].type == TokenType.END)) {
                return Result.success(BigDecimal.ZERO)
            }
            val value = parseExpression()
            if (index < tokens.size && tokens[index].type != TokenType.END) {
                return Result.failure(CalculatorEngine.InvalidExpressionException(
                    "Unexpected token '${tokens[index].value}' at position ${tokens[index].position}"
                ))
            }
            Result.success(value)
        } catch (e: CalculatorEngine.DivisionByZeroException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(CalculatorEngine.InvalidExpressionException(e.message ?: "Syntax error"))
        }
    }

    private fun parseExpression(): BigDecimal {
        var left = parseTerm()
        while (index < tokens.size) {
            val token = tokens[index]
            if (token.type != TokenType.PLUS && token.type != TokenType.MINUS) break
            index++
            val right = parseTerm()
            left = when (token.type) {
                TokenType.PLUS -> left.add(right)
                TokenType.MINUS -> left.subtract(right)
                else -> left
            }
        }
        return left
    }

    private fun parseTerm(): BigDecimal {
        var left = parseFactor()
        while (index < tokens.size) {
            val token = tokens[index]
            if (token.type != TokenType.MULTIPLY && token.type != TokenType.DIVIDE) break
            index++
            val right = parseFactor()
            left = when (token.type) {
                TokenType.MULTIPLY -> left.multiply(right)
                TokenType.DIVIDE -> {
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        throw CalculatorEngine.DivisionByZeroException()
                    }
                    left.divide(right, CalculatorEngine.DEFAULT_SCALE, CalculatorEngine.DECIMAL_MATH)
                }
                else -> left
            }
        }
        return left
    }

    private fun parseFactor(): BigDecimal {
        var value = parseUnary()
        while (index < tokens.size && tokens[index].type == TokenType.PERCENT) {
            index++
            value = value.divide(BigDecimal("100"), CalculatorEngine.DEFAULT_SCALE, CalculatorEngine.DECIMAL_MATH)
        }
        return value
    }

    private fun parseUnary(): BigDecimal {
        if (index < tokens.size && tokens[index].type == TokenType.MINUS) {
            index++
            return parseUnary().negate()
        }
        if (index < tokens.size && tokens[index].type == TokenType.PLUS) {
            index++
            return parseUnary()
        }
        return parsePrimary()
    }

    private fun parsePrimary(): BigDecimal {
        if (index >= tokens.size) {
            throw CalculatorEngine.InvalidExpressionException("Unexpected end of expression")
        }
        val token = tokens[index++]
        return when (token.type) {
            TokenType.NUMBER -> {
                try {
                    BigDecimal(token.value)
                } catch (e: Exception) {
                    throw CalculatorEngine.InvalidExpressionException("Invalid number format: ${token.value}")
                }
            }
            TokenType.LEFT_PAREN -> {
                val value = parseExpression()
                if (index >= tokens.size || tokens[index].type != TokenType.RIGHT_PAREN) {
                    throw CalculatorEngine.InvalidExpressionException("Missing closing parenthesis ')'")
                }
                index++ // Consume ')'
                value
            }
            else -> {
                throw CalculatorEngine.InvalidExpressionException("Unexpected token '${token.value}' at position ${token.position}")
            }
        }
    }

    private fun tokenize() {
        val s = rawExpression
        var i = 0
        while (i < s.length) {
            val c = s[i]
            when {
                c.isWhitespace() -> {
                    i++
                }
                c.isDigit() || c == '.' -> {
                    val start = i
                    var dotCount = if (c == '.') 1 else 0
                    i++
                    while (i < s.length && (s[i].isDigit() || s[i] == '.')) {
                        if (s[i] == '.') {
                            dotCount++
                            if (dotCount > 1) break
                        }
                        i++
                    }
                    val numStr = s.substring(start, i)
                    val normalized = if (numStr == ".") "0" else numStr
                    tokens.add(Token(TokenType.NUMBER, normalized, start))
                }
                c == '+' -> {
                    tokens.add(Token(TokenType.PLUS, "+", i))
                    i++
                }
                c == '-' || c == '−' -> {
                    tokens.add(Token(TokenType.MINUS, "-", i))
                    i++
                }
                c == '*' || c == '×' -> {
                    tokens.add(Token(TokenType.MULTIPLY, "×", i))
                    i++
                }
                c == '/' || c == '÷' -> {
                    tokens.add(Token(TokenType.DIVIDE, "÷", i))
                    i++
                }
                c == '%' -> {
                    tokens.add(Token(TokenType.PERCENT, "%", i))
                    i++
                }
                c == '(' -> {
                    tokens.add(Token(TokenType.LEFT_PAREN, "(", i))
                    i++
                }
                c == ')' -> {
                    tokens.add(Token(TokenType.RIGHT_PAREN, ")", i))
                    i++
                }
                else -> {
                    i++
                }
            }
        }
        tokens.add(Token(TokenType.END, "", s.length))
    }
}
