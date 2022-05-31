package cn.cqut.compiler.grammar;

import cn.cqut.auto.JFlex.back.Token;
import javafx.scene.control.TextArea;

import java.util.ArrayList;

/**
 * @Author CuriT
 * @Date 2022-5-19 16:23
 */
public class Grammar {
    boolean parserOk = false;

    ArrayList<Token> tokens = new ArrayList<>();

    public Grammar(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public void myParser(TextArea ta2, TextArea ta3) {
        parserOk = false;

        System.out.println("语法分析器开始：");

        ta2.setText("");
        ta3.setText("");

        Parser parser = new Parser(tokens);
        parser.start();
        String errInfo = parser.getErrInfo().toString();

        if (errInfo.length() == 0) {
            ta3.setText(errInfo + "语法分析结束-error:0");
            parserOk = true;
        } else {
            ta3.setText(errInfo+"\n语法分析结束-error:"+errInfo.split("\n").length);
        }
        ta2.setText(parser.getSyntaxTreeInfo().toString());

    }
}
