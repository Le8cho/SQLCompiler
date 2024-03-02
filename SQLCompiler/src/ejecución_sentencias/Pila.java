package ejecución_sentencias;


public class Pila<T> {
    private Nodo<T> cima;
    private int tamaño;

    // Constructor
    public Pila() {
        cima = null;
        tamaño = 0;
    }

    // Método para verificar si la pila está vacía
    public boolean estaVacia() {
        return tamaño == 0;
    }

    // Método para obtener el tamaño de la pila
    public int tamaño() {
        return tamaño;
    }

    // Método para apilar un elemento en la pila
    public void apilar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        nuevoNodo.siguiente = cima;
        cima = nuevoNodo;
        tamaño++;
    }

    // Método para desapilar un elemento de la pila
    public T desapilar() {
        if (estaVacia()) {
            System.out.println("La pila está vacía");
        }
        T elementoDesapilado = cima.elemento;
        cima = cima.siguiente;
        tamaño--;
        return elementoDesapilado;
    }

    // Método para obtener el elemento en la cima de la pila sin desapilarlo
    public T cima() {
        if (estaVacia()) {
            System.out.println("La pila está vacía");
        }
        return cima.elemento;
    }

    // Clase privada Nodo para representar los elementos de la pila
    private static class Nodo<T> {
        private T elemento;
        private Nodo<T> siguiente;

        // Constructor
        public Nodo(T elemento) {
            this.elemento = elemento;
            this.siguiente = null;
        }
    }
}
