package EDD;

import java.io.Serializable;

public class Pila<E> {

    public class Nodo<E>  {

        E dato;
        Nodo<E> siguiente;

        public Nodo() {
            this.dato = null;
            this.siguiente = null;
        }

        public Nodo(E dato) {
            this.dato = dato;
        }

        public E getDato() {
            return dato;
        }

        public void setDato(E dato) {
            this.dato = dato;
        }

        public Nodo<E> getSiguiente() {
            return siguiente;
        }

        public void setSiguiente(Nodo<E> siguiente) {
            this.siguiente = siguiente;
        }

    }

    Nodo<E> tope;

    public Pila() {
        this.tope = null;
    }

    public Pila(Nodo<E> tope) {
        this.tope = tope;
    }

    public Nodo<E> getTope() {
        return tope;
    }

    public boolean isEmpty() {
        return tope == null;
    }

    public void vaciarPila() {
        tope = null;
    }

    public void push(E dato) {
        Nodo<E> nuevoNodo = new Nodo(dato);
        nuevoNodo.siguiente = tope;
        tope = nuevoNodo;
    }

    public Nodo pop() {
        if (isEmpty()) {
            System.err.println(" La pila esta vacia");
        }
        Nodo<E> prov = tope;
        tope = tope.siguiente;
        return prov;
    }

    public void mostrarPila() {
        Nodo<E> temp = tope;
        System.out.print("Elementos de la pila: ");
        while (temp != null) {
            System.out.print(temp.getDato() + " ");
            temp = temp.siguiente;
        }
        System.out.println();
    }

}
