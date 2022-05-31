package cn.cqut.auto;

import cn.cqut.backup.Error;
import cn.cqut.backup.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class Pattern {
    public static Map<String, Integer> token = new HashMap<>();
    public static ArrayList<Character> separator = new ArrayList<>();
    public static ArrayList<Character> operator = new ArrayList<>();
    public static ArrayList<Word> words = new ArrayList<>();
    public static ArrayList<Error> errors = new ArrayList<>();
    public static int line = 1;

    public static String lexical(String s) {
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
        token.put("#", 300);

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
//        注释
        String annotation = "(?<annotation>\\/((\\*(.|\\n)*\\*\\/)|(\\/.*\\n)))";
//        关键字
        String keywords = "(?<keywords>char|int|float|break|const|return|void|continue|do|while|if|else|for)";
//        运算符
        String operator = "(?<operator>\\+\\+|\\+=|\\+|--|-=|-|\\*=|==|/|%=|%|>=|<=|=)";
//        分隔符
        String separator = "(?<separator>[,:\\{}:)(])";
//        字符串
        String words = "(?<words>\"[a-zA-Z_0-9]*\")";
//        字符
        String letter = "(?<letter>\'[a-zA-Z_0-9]\')";
//        标识符
        String identifier = "(?<identifier>[a-zA-Z_][a-zA-Z_0-9_]+)";
//        数字：小数、指数
        String regex0 = "(?<digit1>[0-9]+\\.[0-9]+([e|E][0-9]*[-+]?[0-9]+)?)";
//        整数
        String regex1 = "(?<digit2>[0-9]+[1-9]*)";
//        十六进制数
        String sixty = "(?<digit3>0[x|X][a-f_A-F_1-9]+)";
//        八进制数
        String eight = "(?<digit4>0[1-7][0-7]*)";

        String error1 = "(?<error1>0([0-7]*[8-9]+[0-7]*)+)";
        String error2 = "(?<error2>(0x|0X)([0-9a-fA-F]*[g-zG-Z]+[0-9a-fA-F]*)+)";
        String error3 = "(?<error3>(0|[1-9]+[0-9]*).[^0-9]|(0|[1-9]+[0-9]*).([0-9]+.[0-9]*)+)";
        String error4 = "(?<error4>[0-9]+([a-wA-WyzWZ]+[0-9]*)+)";
        String error5 = "(?<error5>[>|<][=][=])";
        String error6 = "(?<error6>([0][0-7]+[.][0-9eE.+-]+)|([0][0-9]*[89]+[0-9]*|[0][0][0-9]*))";
        String error7 = "(?<error7>[1-9][0-9]*.)";
        String error8 = "(?<error8>[@$~]+[a-zA-Z_0-9]*)";
        String error9 = "(?<error9>[\'][a-zA-Z][;]*)";
        String error10 = "(?<error10>[\"][a-zA-Z_0-9]+[;]*)";


        String all = annotation + "|" + keywords + "|" + operator + "|" + separator + "|" + words + "|" + letter + "|" + identifier + "|" + regex0 + "|" + regex1 + "|" + sixty + "|" + eight;
        all += "|" + error1 + "|" + error2 + "|" + error3 + "|" + error4 + "|" + error5 + "|" + error6 + "|" + error7 + "|" + error8 + "|" + error9 + "|" + error10;

        String test = "(?<test>char|int|float|break|const|return|void|continue|do|while|if|else|for)";
        String test2 = "(?<digit>[0-9]+[1-9]*)";
        java.util.regex.Pattern compile = java.util.regex.Pattern.compile(all);

        Matcher matcher = compile.matcher(s);

        StringBuffer stringBuffer = new StringBuffer();
        StringBuffer stringBufferError = new StringBuffer();
        String str;
        while (matcher.find()) {
            str = matcher.group();
            if (matcher.group("annotation") != null)
                stringBuffer.append("注释\t\t" + str + "\n");
            else if (matcher.group("keywords") != null)
                stringBuffer.append(token.get(str) + "\t\t" + str + "\n");
            else if (matcher.group("operator") != null)
                stringBuffer.append(token.get(str) + "\t\t" + str + "\n");
            else if (matcher.group("separator") != null)
                stringBuffer.append(token.get(str) + "\t\t" + str + "\n");
            else if (matcher.group("words") != null)
                stringBuffer.append(600 + "\t\t" + str + "\n");
            else if (matcher.group("letter") != null)
                stringBuffer.append(500 + "\t\t" + str + "\n");
            else if (matcher.group("identifier") != null)
                stringBuffer.append(700 + "\t\t" + str + "\n");
            else if (matcher.group("digit1") != null)
                stringBuffer.append(800 + "\t\t" + str + "\n");
            else if (matcher.group("digit2") != null)
                stringBuffer.append(400 + "\t\t" + str + "\n");
            else if (matcher.group("digit3") != null)
                stringBuffer.append(400 + "\t\t" + str + "\n");
            else if (matcher.group("error1") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error2") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error3") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error4") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error5") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error6") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error7") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error8") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error9") != null)
                stringBufferError.append(str + "\n");
            else if (matcher.group("error10") != null)
                stringBufferError.append(str + "\n");

        }
        return stringBuffer.append("---").append(stringBufferError).toString();
    }
}
