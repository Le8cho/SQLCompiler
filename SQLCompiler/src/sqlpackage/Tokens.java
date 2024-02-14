package sqlpackage;

import java.util.regex.Pattern;

public class Tokens {
    public static final Pattern SELECT = Pattern.compile("SELECT");
    public static final Pattern FROM = Pattern.compile("FROM");
    public static final Pattern WHERE = Pattern.compile("WHERE");
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

    public static ArrayList<Object[]> lex(String input) {
        ArrayList<Object[]> tokensList = new ArrayList<>();
        int index = 0;

        while (index < input.length()) {

            char ch = input.charAt(index);

            if (ch == ' ' || ch == '\t') {
                index++;
                continue;
            }
            else{

            }

        return tokensList;
    }
