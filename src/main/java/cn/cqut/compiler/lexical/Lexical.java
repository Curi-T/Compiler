package cn.cqut.compiler.lexical;

import cn.cqut.backup.Error;
import cn.cqut.backup.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Lexical {
    public String keyWords[] = {"char", "int", "float", "break", "const", "return", "void", "continue", "do", "while", "if", "else", "for"};
    public char[] special = {'=', '\t', ',', ' ', '\n', ';', '{', '}', '(', ')', '+', '-', '*', '/', '%', '[', ']', '!', '>', '<'};
    public char[] operator1 = {'+', '-', '*', '/', '%', '>', '<', '='};
    public char[] separator1 = {'\n', ' ', ';', '{', '}', '(', ')', '[', ']'};


    public Map<String, Integer> token = new HashMap<>();
    public ArrayList<Character> separator = new ArrayList<>();
    public ArrayList<Character> operator = new ArrayList<>();
    public ArrayList<Word> words = new ArrayList<>();
    public ArrayList<Error> errors = new ArrayList<>();
    public int line = 1;

    {
        token.put("char", 101);
        token.put("int", 102);
        token.put("float", 103);
        token.put("break", 104);
        token.put("const", 105);
        token.put("return", 106);
        token.put("void", 107);
        token.put("continue", 108);
        token.put("do", 109);
        token.put("while", 110);
        token.put("if", 111);
        token.put("else", 112);
        token.put("for", 113);
        token.put("main", 114);
        token.put("{", 301);
        token.put("}", 302);
        token.put(";", 303);
        token.put(",", 304);
        token.put("(", 201);
        token.put(")", 202);
        token.put("[", 203);
        token.put("]", 204);
        token.put("!", 205);
        token.put("*", 206);
        token.put("/", 207);
        token.put("%", 208);
        token.put("+", 209);
        token.put("-", 210);
        token.put("<", 211);
        token.put("<=", 212);
        token.put(">", 213);
        token.put(">=", 214);
        token.put("==", 215);
        token.put("!=", 216);
        token.put("&&", 217);
        token.put("||", 218);
        token.put("=", 219);
        token.put(".", 220);
        token.put("++", 221);
        token.put("--", 222);
        token.put("+=", 223);
        token.put("*=", 224);
        token.put("/=", 225);
        token.put("-=", 226);
        token.put("&", 227);
        token.put("|", 228);
        token.put("@", 229);
        token.put("#", 230);
        token.put("^",231);
        token.put("<<",231);


        separator.add('\n');
        separator.add(' ');
        separator.add('\t');
        separator.add(',');
        separator.add(';');
        separator.add('[');
        separator.add(']');
        separator.add('{');
        separator.add('}');
        separator.add('(');
        separator.add(')');
        separator.add('#');
        separator.add('@');

        operator.add('+');
        operator.add('-');
        operator.add('*');
        operator.add('/');
        operator.add('%');
        operator.add('>');
        operator.add('<');
        operator.add('=');
        operator.add('&');
        operator.add('|');
        operator.add('#');
        operator.add('@');
        operator.add('^');
        operator.add('!');
        operator.add('<');
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public ArrayList<Error> getErrors() {
        return errors;
    }

    /**
      词法分析
     */
    public Boolean identify(String s) {
        s += " ";
        for (int front = 0; front < s.length(); ) {
            char ch = s.charAt(front);
            if (Character.isLetter(ch) || ch == '_') {
                //  字母或下划线  识别标识符和关键字
                front = identifier(s, front);
            } else if (Character.isDigit(ch)) {
                //  数字  识别数值型常数
                front = integerIdentify(s, front);
            } else if (ch == '/') {
                //  /   处理注释或符号
                front = annotation(s, front);
            } else if (ch == '\'') {
                //  ‘   识别字符常数
                front = characterIdentify(s, front);
            } else if (ch == '"') {
                //  "   识别字符串常数
                front = stringIdentify(s, front);
            } else {
                //  其他  识别其他界符和运算符
                front = other(s, front);
            }
        }
        return true;
    }

    public int characterIdentify(String s, int front) {
        return 0;
    }

    public int stringIdentify(String s, int front) {
        return 0;
    }

    public int annotation(String s, int front) {

        return 0;
    }

    public int other(String s, int front) {
        return 0;
    }

    public int integerIdentify(String s, int front) {
        return 0;
    }

    public int identifier(String s, int front) {
        return 0;
    }
}
