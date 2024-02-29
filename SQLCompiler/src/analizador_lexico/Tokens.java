package analizador_lexico;

import EDD.Cola;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokens {

    //KEYWORDS
    public static final String SELECT = "SELECT";
    public static final String ASTERISK = "*";
    public static final String FROM = "FROM";
    public static final String WHERE = "WHERE";
    public static final String ON = "ON";
    public static final String AND = "AND";
    public static final String OR = "OR";
    public static final String NOT = "NOT";
    public static final String LIKE = "LIKE";
    public static final String INNER = "INNER";
    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String FULL = "FULL";
    public static final String JOIN = "JOIN";
    public static final String AS = "AS";
    public static final String ORDER = "ORDER";
    public static final String GROUP = "GROUP";
    public static final String BY = "BY";
    public static final String HAVING = "HAVING";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String COUNT = "COUNT";
    public static final String AVG = "AVG";
    public static final String SUM = "SUM";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";

    //Conditional and Arithmetic Operators
    //Conditional Operators
    public static final String EQUAL = "=";
    public static final String LESS = "<";
    public static final String GREATER = ">";
    public static final String OPEN_P = "(";
    public static final String CLOSE_P = ")";
    public static final String[] NOT_EQUAL = {"<>", "!="};
    public static final String LESS_EQUAL = "<=";
    public static final String GREATER_EQUAL = ">=";
    //Arithmetic Operators
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String DIV = "/";

    //Special Symbols
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String PUNTO = ".";

    //ID
    public static final String ID = "ID";
    public static final String NUMBER = "NUMBER";
    public static final String STRING = "STRING";

    public static boolean isDigit(char lexemeChar) {
        return (lexemeChar >= '0' && lexemeChar <= '9');
    }

    public static boolean isAlphabetic(char lexemeChar) {
        //Consideramos como parte del alfabeto al underscore '_' y a la Ñ del español;
        return (lexemeChar >= 'A' && lexemeChar <= 'Z') || (lexemeChar == '_') || (lexemeChar == 'Ñ');
    }

    public static boolean isAWhitespace(char lexemeChar) {

        return Character.isWhitespace(lexemeChar);
    }

    public static boolean isNumber(String lexeme) {
        int indexChar = 0;
        int lexemeLength = lexeme.length();

        while (indexChar < lexemeLength) {
            if (!isDigit(lexeme.charAt(indexChar))) {
                //Uno de los chars no es un digito
                return false;
            }
            indexChar++;
        }
        return true;
    }

    public static boolean isString(String lexeme) {
        int indexChar = 0;
        int lexemeLength = lexeme.length();
        int begin = 0;
        int end = lexemeLength - 1;

        if (lexeme.charAt(begin) == '\'' && lexeme.charAt(end) == '\'') {

            while (indexChar < lexemeLength) {

                //Si cuentra una \' en una posicion diferente del inicio o el final
                if (lexeme.charAt(indexChar) == '\'' && indexChar != begin && indexChar != end) {
                    return false;
                }

                indexChar++;
            }
        }
        return true;
    }

    public static boolean isIdentifier(String lexeme) {

        int indexChar = 0;
        int lexemeLength = lexeme.length();

        if (isDigit(lexeme.charAt(0))) {
            //un identificador nunca empieza con un numero;
            return false;
        }

        while (indexChar < lexemeLength) {
            char lexemeChar = lexeme.charAt(indexChar);
            //Si el char del lexema no es un numero ni letra del alfabeto
            if (!isDigit(lexemeChar) && !isAlphabetic(lexemeChar)) {
                //no es un identificador
                return false;
            }
            //Avanzamos una posicion 
            indexChar++;
        }
        return true;
    }

    public static boolean isKeyword(String lexeme) {

        return lexeme.equals(SELECT)
                || lexeme.equals(ASTERISK)
                || lexeme.equals(FROM)
                || lexeme.equals(WHERE)
                || lexeme.equals(ON)
                || lexeme.equals(AND)
                || lexeme.equals(OR)
                || lexeme.equals(NOT)
                || lexeme.equals(LIKE)
                || lexeme.equals(INNER)
                || lexeme.equals(LEFT)
                || lexeme.equals(RIGHT)
                || lexeme.equals(FULL)
                || lexeme.equals(JOIN)
                || lexeme.equals(AS)
                || lexeme.equals(ORDER)
                || lexeme.equals(GROUP)
                || lexeme.equals(BY)
                || lexeme.equals(HAVING)
                || lexeme.equals(ASC)
                || lexeme.equals(DESC)
                || lexeme.equals(COUNT)
                || lexeme.equals(AVG)
                || lexeme.equals(SUM)
                || lexeme.equals(MIN)
                || lexeme.equals(MAX);

    }

    //Si empieza con uno de estos chars significa que hemos detectado un simbolo
    public static boolean isAOperator(char tokenChar) {

        return tokenChar == EQUAL.charAt(0)
                || tokenChar == LESS.charAt(0)
                || tokenChar == LESS.charAt(0)
                || tokenChar == GREATER.charAt(0)
                || tokenChar == OPEN_P.charAt(0)
                || tokenChar == CLOSE_P.charAt(0)
                || tokenChar == NOT_EQUAL[1].charAt(0) //!
                || tokenChar == PLUS.charAt(0)
                || tokenChar == MINUS.charAt(0)
                || tokenChar == DIV.charAt(0)
                || tokenChar == ASTERISK.charAt(0); //El asterisco es tanto para multiplicar como columnas
    }

    public static boolean isAPunctuation(char tokenChar) {

        return tokenChar == COMMA.charAt(0) || tokenChar == PUNTO.charAt(0) || tokenChar == SEMICOLON.charAt(0);
    }

    public static Cola<Token> lex(String input) throws IllegalArgumentException {

        Cola<Token> tokenList = new Cola<>();
        int index = 0;
        int inputLength = input.length();
        String lexeme = "";
        Token token = null;
        char tokenChar;

        while (index < inputLength) {

            tokenChar = input.charAt(index);

            //mientras el tokenchar no sea un espacio en blanco, una puntuacion, un operador o estemos en el ultimo
            if (!isAWhitespace(tokenChar) && !isAPunctuation(tokenChar) && !isAOperator(tokenChar) && index != inputLength - 1) {
                //Si el token no es un char STOP
                //seguir formando el lexema
                lexeme = lexeme + tokenChar;

            } else {
                //RECONOCIMIENTO DE TOKENS

                //Si estamos al final de la sentencia
                if (index == inputLength - 1) {
                    //agregamos el caracter pendiente que no atrapamos en el primer if (caso especial)
                    //verificamos que no sea un whitespace
                    if (!isAWhitespace(tokenChar)) {
                        lexeme = lexeme + tokenChar;
                    }
                }

                //Quiere decir que hemos encontrado un char STOP: espacio, puntuacion o operador o nos encontramos al final de la cadena
                //analizamos si hay un token pendiente por analizar al momento de hacer STOP
                if (lexeme.length() != 0) {
                    //El lexema es una keyword? //prioridad de palabras    
                    if (isKeyword(lexeme)) {
                        token = new Token(lexeme, lexeme, index);
                    } //Es un identificador?
                    else if (isIdentifier(lexeme)) {
                        token = new Token(ID, lexeme, index);
                    } //Si no es identificador es Numero
                    else if (isNumber(lexeme)) {
                        token = new Token(NUMBER, lexeme, index);
                    } //Si no es un numero es un string
                    else if (isString(lexeme)) {
                        token = new Token(STRING, lexeme, index);
                    }

                    //Token irreconocible (no es ninguno de los anteriores)
                    if (token == null) {
                        throw new IllegalArgumentException("Token irreconocible" + lexeme);
                    }

                    //Agregamos el token generado
                    tokenList.agregar(token);

                    //Vaciar lexema para la siguiente iteracion
                    lexeme = "";
                }

                //Haya token pendiente o no, evaluamos el char STOP tokenChar
                if (isAPunctuation(tokenChar) || isAOperator(tokenChar)) {
                    if (isAOperator(tokenChar)) {
                        switch (tokenChar) {
                            case '+' -> {
                                token = new Token(PLUS, "+", index);
                            }
                            case '-' -> {
                                token = new Token(MINUS, "-", index);
                            }
                            case '/' -> {
                                token = new Token(DIV, "/", index);
                            }
                            case '=' -> {
                                token = new Token(EQUAL, "=", index);
                            }
                            case '(' -> {
                                token = new Token(OPEN_P, "(", index);
                            }
                            case '*' -> {
                                token = new Token(ASTERISK, "*", index);
                            }
                            case ')' -> {
                                token = new Token(CLOSE_P, ")", index);
                            }
                            case '<' -> {
                                int nextIndex = index + 1;
                                if (nextIndex == input.length()) {
                                    token = new Token(LESS, "<", index);
                                } else if (input.charAt(nextIndex) == '>') {
                                    token = new Token(NOT_EQUAL[0], "<>", index);
                                    index = nextIndex;
                                } else if (input.charAt(nextIndex) == '=') {
                                    token = new Token(LESS_EQUAL, "<=", index);
                                    index = nextIndex;
                                }
                            }
                            case '>' -> {
                                int nextIndex = index + 1;
                                if (nextIndex == input.length()) {
                                    token = new Token(GREATER, ">", index);
                                } else if (input.charAt(nextIndex) == '=') {
                                    token = new Token(GREATER_EQUAL, ">=", index);
                                    index = nextIndex;
                                }
                            }
                            case '!' -> {
                                int nextIndex = index + 1;
                                if (nextIndex == input.length()) {
                                    throw new IllegalArgumentException("Token irreconocible" + tokenChar);
                                } else if (input.charAt(nextIndex) == '=') {
                                    token = new Token(NOT_EQUAL[1], "!=", index);
                                    index = nextIndex;
                                }
                            }

                        }

                    } else if (isAPunctuation(tokenChar)) {//Capturamos el token actual puede ser coma punto o espacio o semicolon

                        switch (tokenChar) {
                            case ',' -> {
                                token = new Token(COMMA, ",", index);
                            }
                            case ';' -> {
                                token = new Token(SEMICOLON, ";", index);
                            }
                            case '.' -> {
                                token = new Token(PUNTO, ".", index);
                            }
                        }
                    }

                    //Agregamos el stop tokenChar  detectado a la lista de tokens
                    tokenList.agregar(token);
                }

            }
            //Seguimos iterando en el while
            index++;
        }

        //Retornamos la lista de tokens
        return tokenList;
    }

}
