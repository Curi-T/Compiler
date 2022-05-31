package cn.cqut.auto;

import javafx.scene.control.TextArea;

/**
 * @Author CuriT
 * @Date 2022-5-22 23:24
 */
public class AutoLexical {
    boolean model;
    String content;
    TextArea code;

    public AutoLexical(boolean model, String content, TextArea code) {
        this.model = model;
        this.content = content;
        this.code = code;
    }

    public void myParse(TextArea codeRightAbove, TextArea codeRightBelow) {
        codeRightAbove.setText("");
        codeRightBelow.setText("");
        String s;
        if (model) {
            s = Pattern.lexical(content);
        } else {
            s = Pattern.lexical(code.getText());
        }
        String[] split = s.split("---");
        codeRightAbove.setText(split[0]);
        codeRightBelow.setText(split[1]);
    }
}
