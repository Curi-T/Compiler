package cn.cqut.auto.JFlex;

import cn.cqut.auto.JFlex.back.*;
import javafx.scene.control.TextArea;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * @Author CuriT
 * @Date 2022-5-19 15:40
 */
public class LexicalAnalyzer {
    VariableTable variableTable;
    ConstantTable constantTable;
    FunctionTable functionTable;
    boolean lexerOk = false;
    ArrayList<Token> tokens = new ArrayList<>();

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public void lexicalAnalyzer(TextArea ta1, TextArea ta2, TextArea ta3) {
        variableTable = null;
        constantTable = null;
        functionTable = null;
        lexerOk = false;
        Lexer lex;
        tokens.clear();
        try {
            ta2.setText("--------------------token表信息...--------------------\nline		val		tokenCode		means\n");
            ta3.setText("----------------------错误信息...----------------------\n");
            lex = new Lexer(new StringReader(ta1.getText()));
            while (true) {
                Token token = lex.yylex();
                if (token == null)
                    break;
                if (token.tokenCode == sym.ERROR) {
                    lexerOk = false;
                    ta3.setText(ta3.getText() + "line " + (token.line + 1) + " ：" + token.val + "\n");
                } else {
                    String str = sym.getMeans(token.tokenCode);
                    if (str == null) {
                        ta2.appendText(String.format("%d:%20s%20d%20s\n", token.line + 1, token.val, token.tokenCode, "符号" + token.val));
                    } else {
                        ta2.appendText(String.format("%d:%20s%20d%20s\n", token.line + 1, token.val, token.tokenCode, str));
                    }
                    tokens.add(token);
                }
            }
//            System.out.println("length:    " + ta3.getText().split("\n").length);
            if (ta3.getText().split("\n").length == 1)
                lexerOk = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
