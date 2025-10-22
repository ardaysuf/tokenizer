package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

class Table {
    JFrame frame;
    JTable table;

    Table(List<String> common_lexeme_list, Tokenizer t) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setTitle("Lexeme-Token Table");

        String[][] data = new String[common_lexeme_list.size()][2];

        for (int i = 0; i < common_lexeme_list.size(); i++) {
            data[i][0] = common_lexeme_list.get(i);
            if (t.getKeyword_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "KEYWORD";
            } else if (t.getOperator_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "OPERATOR";
            } else if (t.getDelimiter_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "DELIMITER";
            } else if (t.getSeparator_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "SEPARATOR";
            } else if (t.getLiteral_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "LITERAL";
            } else if (t.getIdentifier_lexemes().contains(common_lexeme_list.get(i))) {
                data[i][1] = "IDENTIFIER";
            }
        }

        String[] columnNames = {"Lexeme", "Token"};

        table = new JTable(data, columnNames);
        table.setFont(new Font("Arial", Font.PLAIN, 20));
        table.setRowHeight(30);

        JScrollPane sp = new JScrollPane(table);

        frame.add(sp);
        frame.setSize(500, 500);
        frame.setVisible(true);
    }
}

public class Tokenizer {

    private enum State {
        START,
        IN_IDENTIFIER,
        IN_NUMBER,
        IN_STRING,
        IN_CHAR,
        IN_OPERATOR
    }

    private final List<String> keywords;
    private final List<String> operators;
    private final List<String> separators;

    private final List<String> separator_lexemes;
    private final List<String> identifier_lexemes;
    private final List<String> literal_lexemes;
    private final List<String> operator_lexemes;
    private final List<String> keyword_lexemes;
    private final List<String> delimiter_lexemes;

    public Tokenizer() {
        keywords = get_from_sql("SELECT * FROM JavaKeywords");
        operators = get_from_sql("SELECT * FROM JavaOperators");
        separators = get_from_sql("SELECT * FROM JavaSeparators");

        separator_lexemes = new ArrayList<>();
        identifier_lexemes = new ArrayList<>();
        literal_lexemes = new ArrayList<>();
        operator_lexemes = new ArrayList<>();
        keyword_lexemes = new ArrayList<>();
        delimiter_lexemes = new ArrayList<>();
    }

    public List<String> getSeparator_lexemes() {
        return separator_lexemes;
    }

    public void addSeparator_lexemes(String s) {
        if (!separator_lexemes.contains(s))
            separator_lexemes.add(s);
    }

    public List<String> getIdentifier_lexemes() {
        return identifier_lexemes;
    }

    public void addIdentifier_lexemes(String s) {
        if (!identifier_lexemes.contains(s))
            identifier_lexemes.add(s);
    }

    public List<String> getLiteral_lexemes() {
        return literal_lexemes;
    }

    public void addLiteral_lexemes(String s) {
        if (!literal_lexemes.contains(s))
            literal_lexemes.add(s);
    }

    public List<String> getOperator_lexemes() {
        return operator_lexemes;
    }

    public void addOperator_lexemes(String s) {
        if (!operator_lexemes.contains(s))
            operator_lexemes.add(s);
    }

    public List<String> getKeyword_lexemes() {
        return keyword_lexemes;
    }

    public void addKeyword_lexemes(String s) {
        if (!keyword_lexemes.contains(s))
            keyword_lexemes.add(s);
    }

    public List<String> getDelimiter_lexemes() {
        return delimiter_lexemes;
    }

    public void addDelimiter_lexemes(String s) {
        if (!delimiter_lexemes.contains(s))
            delimiter_lexemes.add(s);
    }

    String code(File file) {
        Scanner read;
        String code = "";
        try {
            read = new Scanner(file);
            while (read.hasNextLine()) {
                String line = read.nextLine();
                code += line + "\n";
            }
            read.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    String multi_comment_cleaner(String code) {
        List<Integer> start_indexes = new ArrayList<>();
        List<Integer> end_indexes = new ArrayList<>();
        List<String> multi_comments = new ArrayList<>();
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '/' && code.charAt(i + 1) == '*') {
                start_indexes.add(i);
            } else if (code.charAt(i) == '*' && code.charAt(i + 1) == '/') {
                end_indexes.add(i + 1);
            }
        }
        for (int i = 0; i < start_indexes.size(); i++) {
            multi_comments.add(code.substring(start_indexes.get(i), end_indexes.get(i) + 1));
            code = code.replace(multi_comments.get(i), "");
        }

        return code;
    }

    String single_comment_cleaner(String code) {
        String[] splitted_code = code.split("\n");
        String cleaned_code = "";
        for (int i = 0; i < splitted_code.length; i++) {
            if (splitted_code[i].contains("//")) {
                for (int j = 0; j < splitted_code[i].length(); j++) {
                    if (splitted_code[i].charAt(j) == '/' && splitted_code[i].charAt(j + 1) == '/') {
                        splitted_code[i] = splitted_code[i].substring(0, j);
                    }
                }
            }
        }
        for (String string : splitted_code) {
            cleaned_code += string + "\n";
        }
        return cleaned_code;
    }

    List<String> get_from_sql(String sql_query) {
        List<String> keys = new ArrayList<>();
        String url = "jdbc:sqlserver://DESKTOP-897CFOQ\\SQLEXPRESS;" + "database=JavaKeywords;" + "encrypt=true;" + "trustServerCertificate=true;" + "integratedSecurity=true;";

        ResultSet resultSet;

        try (Connection connection = DriverManager.getConnection(url); Statement statement = connection.createStatement()) {

            resultSet = statement.executeQuery(sql_query);

            while (resultSet.next()) {
                keys.add(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return keys;
    }

    public static void print_lexeme_list(String title, List<String> lexemes) {
        System.out.println("\n" + title.toUpperCase() + ":");
        System.out.println("------------------");
        for (String lexeme : lexemes) {
            System.out.print(lexeme + ", ");
        }
        System.out.println();
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void processIdentifierOrKeyword(String lexeme) {
        if (this.keywords.contains(lexeme)) {
            this.addKeyword_lexemes(lexeme);
        } else {
            this.addIdentifier_lexemes(lexeme);
        }
    }

    public void tokenize(String code) {
        Set<String> operatorSet = new HashSet<>(this.operators);
        Set<String> separatorSet = new HashSet<>(this.separators);

        State currentState = State.START;
        StringBuilder currentLexeme = new StringBuilder();

        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            String s = String.valueOf(c);

            switch (currentState) {
                case START:
                    if (Character.isWhitespace(c)) {
                        continue;
                    } else if (Character.isLetter(c) || c == '_') {
                        currentState = State.IN_IDENTIFIER;
                        currentLexeme.append(c);
                    } else if (Character.isDigit(c)) {
                        currentState = State.IN_NUMBER;
                        currentLexeme.append(c);
                    } else if (c == '"') {
                        currentState = State.IN_STRING;
                        currentLexeme.append(c);
                        this.addDelimiter_lexemes(s);
                    } else if (c == '\'') {
                        currentState = State.IN_CHAR;
                        currentLexeme.append(c);
                        this.addDelimiter_lexemes(s);
                    } else if (separatorSet.contains(s)) {
                        this.addSeparator_lexemes(s);
                    } else if (operatorSet.contains(s)) {
                        currentState = State.IN_OPERATOR;
                        currentLexeme.append(c);
                    }
                    break;

                case IN_IDENTIFIER:
                    if (Character.isLetterOrDigit(c) || c == '_') {
                        currentLexeme.append(c);
                    } else {
                        processIdentifierOrKeyword(currentLexeme.toString());
                        currentLexeme.setLength(0);
                        currentState = State.START;
                        i--;
                    }
                    break;

                case IN_NUMBER:
                    if (Character.isDigit(c) || c == '.') {
                        currentLexeme.append(c);
                    } else {
                        if (isNumeric(currentLexeme.toString())) {
                            this.addLiteral_lexemes(currentLexeme.toString());
                        }
                        currentLexeme.setLength(0);
                        currentState = State.START;
                        i--;
                    }
                    break;

                case IN_OPERATOR:
                    String potentialOp = currentLexeme.toString() + c;
                    if (operatorSet.contains(potentialOp)) {
                        currentLexeme.append(c);
                    } else {
                        this.addOperator_lexemes(currentLexeme.toString());
                        currentLexeme.setLength(0);
                        currentState = State.START;
                        i--;
                    }
                    break;

                case IN_STRING:
                    currentLexeme.append(c);
                    if (c == '"' && currentLexeme.length() > 1) {
                        if (currentLexeme.charAt(currentLexeme.length() - 2) != '\\') {
                            this.addLiteral_lexemes(currentLexeme.toString());
                            currentLexeme.setLength(0);
                            currentState = State.START;
                        }
                    }
                    break;

                case IN_CHAR:
                    currentLexeme.append(c);
                    if (c == '\'' && currentLexeme.length() > 1) {
                        if (currentLexeme.charAt(currentLexeme.length() - 2) != '\\') {
                            this.addLiteral_lexemes(currentLexeme.toString());
                            currentLexeme.setLength(0);
                            currentState = State.START;
                        }
                    }
                    break;
            }
        }

        if (!currentLexeme.isEmpty()) {
            String lexeme = currentLexeme.toString();
            if (currentState == State.IN_IDENTIFIER) {
                processIdentifierOrKeyword(lexeme);
            } else if (currentState == State.IN_NUMBER) {
                if (isNumeric(lexeme)) {
                    this.addLiteral_lexemes(lexeme);
                }
            } else if (currentState == State.IN_OPERATOR) {
                this.addOperator_lexemes(lexeme);
            }
        }
    }

    public static void main(String[] args) {
        String path_name = "C:\\Users\\yusuf\\OneDrive\\Desktop\\Yazılım\\Java\\örnek_kod.txt";
        File file = new File(path_name);
        Tokenizer t = new Tokenizer();
        String code = t.code(file);

        String single_comment_cleaned_code = t.single_comment_cleaner(code);
        String comment_cleaned_code = t.multi_comment_cleaner(single_comment_cleaned_code);

        t.tokenize(comment_cleaned_code);

        print_lexeme_list("IDENTIFIERS", t.getIdentifier_lexemes());
        print_lexeme_list("SEPARATORS", t.getSeparator_lexemes());
        print_lexeme_list("OPERATORS", t.getOperator_lexemes());
        print_lexeme_list("KEYWORDS", t.getKeyword_lexemes());
        print_lexeme_list("LITERALS", t.getLiteral_lexemes());
        print_lexeme_list("DELIMITERS", t.getDelimiter_lexemes());

        List<String> common_lexeme_list = new ArrayList<>();
        common_lexeme_list.addAll(t.getIdentifier_lexemes());
        common_lexeme_list.addAll(t.getSeparator_lexemes());
        common_lexeme_list.addAll(t.getDelimiter_lexemes());
        common_lexeme_list.addAll(t.getLiteral_lexemes());
        common_lexeme_list.addAll(t.getOperator_lexemes());
        common_lexeme_list.addAll(t.getKeyword_lexemes());

        new Table(common_lexeme_list, t);
    }
}
