package sqlpackage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//Clase que lee los archivos SQL y les da el formato adecuado
public class SQLFileReader {

    public static String readFile(String ruta) {

        String sqlString = "";

        try {
            BufferedReader sqlBuffer = new BufferedReader(new FileReader(ruta, StandardCharsets.UTF_8));
            String linea;

            while ((linea = sqlBuffer.readLine()) != null) {
                int P = -1;
                //Encontraremos la primera ocurrencia de '#' en la linea recuperada
                P = posSimbolo(linea, 0, '#', 1);
                //Es decir se encontro el char en la linea
                if (P != -1) {
                    //Solo recuperamos lo que está antes del char '#'
                    linea = linea.substring(0, P);
                }

                P = -2;
                P = posSimbolo(linea, 0, '-', 2);
                
                if (P != -1) {
                    //Solo recuperamos lo que está antes del char '#'
                    linea = linea.substring(0, P);
                }
                
                //Borramos cualquier espacio en blanco al inicio o final
                linea = linea.trim();
                //Si la linea leida no esta vacia o en blanco
                if ((linea.length() != 0)) {
                    //Concatenamos esa linea a TEXT
                    sqlString = sqlString.concat(linea).concat(" ");
                }
            }

        } catch (IOException E) {

        }
        return sqlString;
    }

    //----------------------------------------
    //posSimbolo: Funcion que halla un char especifico y devuelve la posicion de la primera ocurrencia del simbolo
    //String cadena = cadena fuente 
    //indiceInicial = posicion Inicial desde donde buscar 
    //simbolo = char a buscar 
    //ordenOcurrencia = primera, segunda o tercera...
    private static int posSimbolo(String cadena, int indiceInicial, char simbolo, int ordenOcurrencia) {
        //i empieza en indiceInicial
        int indexCadena = indiceInicial;
        //Contador de ocurrencias del simbolo
        int contadorOcurrencias = 0;
        //posSimbolo donde se halle el simbolo de la cadena, 0 por default
        int posSimbolo = -1;
        //longCadena longitudde la cadena
        int longCadena = cadena.length();

        //Recorrer la cadena
        while ((indexCadena <= longCadena - 1) && (posSimbolo == -1)) {
            //Si el char en el indice i es el char E que buscamos
            if (cadena.charAt(indexCadena) == simbolo) {
                contadorOcurrencias++;
                //Si contador llego al orden de ocurrencias que buscamos
                if (contadorOcurrencias == ordenOcurrencia) {
                    //Actualizar P
                    switch (ordenOcurrencia) {
                        case 1 -> {
                            posSimbolo = indexCadena;
                        }
                        case 2 -> {
                            //El simbolo es --
                            if (cadena.charAt(indexCadena + 1) == ' ') {
                                posSimbolo = indexCadena - 1;
                            }
                        }
                    }
                }
            }
            indexCadena++;
        }
        //Retorna P que es el indice donde se encuentra la primera ocurrencia
        return posSimbolo;
    }
    //----------------------------------------

    //Funcion Locate 3 que busca la halla la posicion inicila de un cadena T en una cadena S, F es el numero de ocurrencia
    public static int Locate3(String S, String T, int F) {
        int i, C, k, N, P;
        //Cadena temporal E
        String E;
        // k longitud de la cadena T
        k = T.length();
        // N longitud de la cadena S - 1
        N = S.length() - 1;
        // P Posicion inicializado en 0
        P = 0;
        // C contador
        C = 0;
        //i Indice inicializado en 1
        i = 0;
        while (i <= N - k + 1) {
            //E es una cadena de longitud K
            E = S.substring(i, i + k);
            //Es la cadena T a buscar igual a la cadena E extraida?
            if (T.equals(E) == true) {
                //Incrementar contador
                C++;
                //Contador llego a la ocurrencia deseada?
                if (C == F) {
                    //Devolver la posicion
                    P = i;
                }
                //System.out.println(i  + "\t" + E);
            }
            //Seguir buscando
            i++;
        }
        return P;
    }
    //----------------------------------------

}
