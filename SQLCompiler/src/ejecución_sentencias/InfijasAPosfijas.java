/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ejecución_sentencias;
/**
 *
 * @author utente
 */
public class InfijasAPosfijas {
    
     // Función para verificar si un carácter es un operador
    static boolean esOperador(char caracter) {
        return (caracter == '+' || caracter == '-' || caracter == '*' || caracter == '/');
    }

    // Función para devolver la precedencia de un operador
    static int precedencia(char operador) {
        switch (operador) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    // Función para convertir notación infija a posfija
    static String infijoAPosfijo(String expresion) {
        StringBuilder resultado = new StringBuilder();
        Pila<Character> pila = new Pila<>();

        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);

            // Si el caracter es un dígito o letra, lo agregamos directamente al resultado
            if (Character.isLetterOrDigit(caracter)) {
                resultado.append(caracter);
            }
            // Si el caracter es un paréntesis izquierdo, lo agregamos a la pila
            else if (caracter == '(') {
                pila.apilar(caracter);
            }
            // Si el caracter es un paréntesis derecho, desapilamos y agregamos al resultado hasta encontrar el paréntesis izquierdo correspondiente
            else if (caracter == ')') {
                while (!pila.estaVacia() && pila.cima() != '(') {
                    resultado.append(pila.desapilar());
                }
                pila.desapilar(); // Quitamos el paréntesis izquierdo de la pila
            }
            // Si el caracter es un operador
            else if (esOperador(caracter)) {
                while (!pila.estaVacia() && precedencia(caracter) <= precedencia(pila.cima())) {
                    resultado.append(pila.desapilar());
                }
                pila.apilar(caracter);
            }
        }

        // Vaciamos la pila al resultado
        while (!pila.estaVacia()) {
            resultado.append(pila.desapilar());
        }

        return resultado.toString();
    }
    
    static int operarExpresionPosfija(String expresion) {
        Pila<Integer> pila = new Pila<>();

        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);

            // Si el caracter es un dígito, lo convertimos a entero y lo apilamos en la pila
            if (Character.isDigit(caracter)) {
                pila.apilar(caracter - '0'); // Convertimos el caracter a su valor numérico
            }
            // Si el caracter es un operador, desapilamos los dos últimos números de la pila,
            // realizamos la operación y apilamos el resultado nuevamente en la pila
            else {
                int segundoNumero = pila.desapilar();
                int primerNumero = pila.desapilar();

                switch (caracter) {
                    case '+':
                        pila.apilar(primerNumero + segundoNumero);
                        break;
                    case '-':
                        pila.apilar(primerNumero - segundoNumero);
                        break;
                    case '*':
                        pila.apilar(primerNumero * segundoNumero);
                        break;
                    case '/':
                        pila.apilar(primerNumero / segundoNumero);
                        break;
                }
            }
        }

        // Al final, el resultado estará en la cima de la pila
        return pila.desapilar();
    }

    public static int operarExpresion(String expresionInfija) {
        String expresionPosfija = infijoAPosfijo(expresionInfija);
        System.out.println("Expresión infija: " + expresionInfija);
        System.out.println("Expresión posfija: " + expresionPosfija);
        int resultado = operarExpresionPosfija(expresionPosfija);
        System.out.println("El resultado es: " + resultado);
        return resultado;
    }
}
