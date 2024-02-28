package EDD;

import java.io.Serializable;

//Clase pendiente por implementar (No obligatorio)
//Implementar o adaptar metodos .get(index) .add() y .size() para reemplazarlo por Arraylist
public class ListaEnlazadaSimple<E extends Comparable> implements Serializable {

    public class Nodo<E extends Comparable> implements Serializable {

        E dato;
        Nodo<E> siguiente;

        public Nodo(E dato) {
            this.dato = dato;
            this.siguiente = null;
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

    private Nodo<E> inicio;

    public ListaEnlazadaSimple() {
        this.inicio = null;
    }

    public void insertarAlInicio(E valor) {
        Nodo<E> nuevoNodo = new Nodo<>(valor);
        nuevoNodo.siguiente = inicio;
        inicio = nuevoNodo;
    }

    public void insertarAlFinal(E valor) {
        Nodo<E> nuevoNodo = new Nodo(valor);
        if (inicio == null) {
            inicio = nuevoNodo;
        } else {
            Nodo<E> actual = inicio;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
    }

    public void insertarPorPosicion(E valor, int posicion) {
        if (posicion < 0) {
            throw new IndexOutOfBoundsException("Posición inválida para inserción");
        }
        if (posicion == 0) {
            insertarAlInicio(valor);
        } else {
            Nodo<E> nuevoNodo = new Nodo<>(valor);
            Nodo<E> actual = inicio;
            int contador = 0;
            while (actual != null && contador < posicion - 1) {
                actual = actual.siguiente;
                contador++;
            }
            if (actual != null) {
                nuevoNodo.siguiente = actual.siguiente;
                actual.siguiente = nuevoNodo;
            } else {
                throw new IndexOutOfBoundsException("Posición inválida para inserción");
            }
        }
    }

    public void insertarDespuesDeValor(E valorBuscado, E valorInsertar) {
        Nodo<E> actual = inicio;
        while (actual != null && actual.dato != valorBuscado) {
            actual = actual.siguiente;
        }
        if (actual != null) {
            Nodo nuevoNodo = new Nodo(valorInsertar);
            nuevoNodo.siguiente = actual.siguiente;
            actual.siguiente = nuevoNodo;
        }
    }

    public void eliminarDespuesDe(E valorBuscado) {
        Nodo<E> actual = inicio;
        while (actual != null && actual.dato != valorBuscado) {
            actual = actual.siguiente;
        }
        if (actual != null && actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
        }
    }

    public void eliminarPorPosicion(int posicion) {
        if (posicion < 0) {
            throw new IndexOutOfBoundsException("Posición inválida para eliminación");
        }
        if (posicion == 0) {
            inicio = inicio.siguiente;
        } else {
            Nodo<E> actual = inicio;
            int contador = 0;
            while (actual != null && contador < posicion - 1) {
                actual = actual.siguiente;
                contador++;
            }
            if (actual != null && actual.siguiente != null) {
                actual.siguiente = actual.siguiente.siguiente;
            } else {
                throw new IndexOutOfBoundsException("Posición inválida para eliminación");
            }
        }
    }

    public void mostrarLista() {
        Nodo<E> actual = inicio;
        while (actual != null) {
            System.out.print(actual.dato + " ");
            actual = actual.siguiente;
        }
        System.out.println();
    }

}
