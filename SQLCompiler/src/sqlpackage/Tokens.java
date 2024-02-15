package sqlpackage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokens {

    public static final Pattern SELECT = Pattern.compile("SELECT");
    public static final Pattern FROM = Pattern.compile("FROM");
    public static final Pattern WHERE = Pattern.compile("WHERE");
    public static final Pattern LIKE = Pattern.compile("LIKE");
    public static final Pattern COMMA = Pattern.compile(",");
    public static final Pattern ASTERISK = Pattern.compile("\\*");
    public static final Pattern ID = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");
    public static final Pattern EQUAL = Pattern.compile("=");
    public static final Pattern NOT_EQUAL = Pattern.compile("<>");
    public static final Pattern LESS = Pattern.compile("<");
    public static final Pattern LESS_EQUAL = Pattern.compile("<=");
    public static final Pattern GREATER = Pattern.compile(">");
    public static final Pattern GREATER_EQUAL = Pattern.compile(">=");
    public static final Pattern AND = Pattern.compile("AND");
    public static final Pattern OR = Pattern.compile("OR");
    public static final Pattern NUMBER = Pattern.compile("\\d+");
    public static final Pattern STRING = Pattern.compile("'[^']*'");

    //Diccionario de clave tipoToken y Pattern que guarda los regex
    //Linked Hash Map respeta el orden de insercion de los elementos
    private static final Map<String, Pattern> tokensDictionary = new LinkedHashMap<String, Pattern>();

    public static ArrayList<Token> lex(String input) {

        //Linked Hash map donde clave es el tipoToken y el valor es el objeto Pattern
        //Lista de clave y valor
        tokensDictionary.put("SELECT", SELECT);
        tokensDictionary.put("FROM", FROM);
        tokensDictionary.put("WHERE", WHERE);
        tokensDictionary.put("LIKE", LIKE);
        tokensDictionary.put("COMMA", COMMA);
        tokensDictionary.put("ASTERISK", ASTERISK);
        tokensDictionary.put("ID", ID);
        tokensDictionary.put("EQUAL", EQUAL);
        tokensDictionary.put("NOT_EQUAL", NOT_EQUAL);
        tokensDictionary.put("LESS", LESS);
        tokensDictionary.put("LESS_EQUAL", LESS_EQUAL);
        tokensDictionary.put("GREATER", GREATER);
        tokensDictionary.put("GREATER_EQUAL", GREATER_EQUAL);
        tokensDictionary.put("AND", AND);
        tokensDictionary.put("OR", OR);
        tokensDictionary.put("NUMBER", NUMBER);
        tokensDictionary.put("STRING", STRING);

        ArrayList<Token> tokensList = new ArrayList<>();

        int index = 0;

        while (index < input.length()) {
            
            char ch = input.charAt(index);
            //Ojo, puede que sea un whitespace character diferente
            if (ch == ' ' || ch == '\t') {
                index++;
            } else {
                boolean match = false;
                //Recorremos cada clave valor del diccionario
                //Entry entry representa un par clave valor del diccionario
                for (Map.Entry<String, Pattern> entry : tokensDictionary.entrySet()) {
                    Pattern pattern = entry.getValue();
                    Matcher matcher = pattern.matcher(input);
                    
                    //Empezamos a buscar el siguiente patron empezando del indice y considerando el char de la posicion del indice
                    if (matcher.find(index) && matcher.group().charAt(0) == input.charAt(index)) {
                        //Hubo match
                        match = true;
                        //Creamos la variable tokenValor para guardar la coincidencia encontrada
                        String tokenValor = matcher.group();
                        //Creamos un token temporal
                        Token token = new Token(entry.getKey(), tokenValor, index);
                        //Añadimos el token a la lista de tokens
                        tokensList.add(token);
                        //Actualizar index
                        index += tokenValor.length();
                        break;
                    }
                }
                //Si no hay match quiere decir que no se encontro un tipo de Token en el diccionario
                if (match == false) {
                    System.out.println("Char no identificado {" + ch + "}");
                }

            }
        }
        return tokensList;
    }
}
