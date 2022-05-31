package cn.cqut.compiler.lexical;

import cn.cqut.backup.Error;
import cn.cqut.backup.Word;

public class LexicalAnalysis extends Lexical {


    /**
     * 数字识别
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int integerIdentify(String s, int front) {
        int state = 0;
        int i = 0;
        for (i = front; i < s.length() && state < 13 && state != 4 && state != 7; i++) {
            char ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (ch == '0') {
                        state = 2;
                    } else {
                        state = 1;
                    }
                    break;
                case 1:
                    if (Character.isDigit(ch)) {
                        state = 1;
                    } else if (ch == '.') {
                        state = 8;
                    } else if (ch == 'E' || ch == 'e') {
                        state = 10;
                    } else {
                        //  整数
                        for (int j = 0; j < special.length; j++) {
                            if (ch == special[j]) {
                                state = 15;
                                break;
                            }
                        }
                        if (state != 15) {
                            //  数字中含其他字符
                            state = 16;
                        }
                    }
                    break;
                case 2:
                    if (ch == '.') {
                        state = 8;
                    } else if (ch <= '7' && ch >= '0') {
                        state = 3;
                    } else if (ch == 'X' || ch == 'x') {
                        state = 5;
                    } else if (separator.contains(ch)) {
                        state = 17;
                    }
                    break;
                case 3:
                    if (ch <= '7' && ch >= '0') {
                        state = 3;
                    } else {
                        state = 4;
                    }
                case 5:
                    if (Character.isDigit(ch) || (ch <= 'f' && ch >= 'a') || (ch <= 'F' && ch >= 'A')) {
                        state = 6;
                    }
                case 6:
                    if (Character.isDigit(ch) || (ch <= 'f' && ch >= 'a') || (ch <= 'F' && ch >= 'A')) {
                        state = 6;
                    } else {
                        state = 7;
                    }
                case 8:
                    if (Character.isDigit(ch)) {
                        state = 9;
                    } else {
                        //  小数点后错误
                        state = 16;
                    }
                    break;
                case 9:
                    if (Character.isDigit(ch)) {
                        state = 9;
                    } else if (ch == 'E' || ch == 'e') {
                        state = 10;
                    } else {
                        //  为小数
                        for (int j = 0; j < special.length; j++) {
                            if (ch == special[j]) {
                                state = 14;
                                break;
                            }
                        }
                        if (state != 14) {
                            //  数字中不应含其他字符
                            state = 16;
                        }
                    }
                    break;
                case 10:
                    if (ch == '+' || ch == '-') {
                        state = 11;
                    } else if (Character.isDigit(ch)) {
                        state = 12;
                    } else {
                        state = 16;
                    }
                    break;
                case 11:
                    if (Character.isDigit(ch)) {
                        state = 12;
                    } else {
                        state = 16;
                    }
                    break;
                case 12:
                    if (Character.isDigit(ch)) {
                        state = 12;
                    } else {
                        //  为指数
                        for (int j = 0; j < special.length; j++) {
                            if (ch == special[j]) {
                                state = 13;
                                break;
                            }
                        }
                        if (state != 13) {
                            state = 16;
                        }
                    }
                    break;
            }
        }
        if (state == 16) {
            boolean flag = false;
            for (int j = i; j < s.length(); j++) {
                char ch = s.charAt(j);
                for (int k = 0; k < special.length; k++) {
                    if (ch == special[k]) {
                        i = j;
                        flag = true;
                        errors.add(new Error(s.substring(front, j), 900, line, "数字格式错误，建议不含其他字符"));
                        break;
                    }
                }
                if (flag)
                    break;
            }
        }
        switch (state) {
            case 13:
                words.add(new Word(s.substring(front, --i), 900, line, "指数", ""));
                break;
            case 14:
                words.add(new Word(s.substring(front, --i), 800, line, "float", ""));
                break;
            case 15:
                words.add(new Word(s.substring(front, --i), 400, line, "int", ""));
                break;
        }
        return i;
    }

    /**
     * 标识符识别
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int identifier(String s, int front) {
        int state = 0;
        int i = 0;
        for (i = front; i < s.length() && state != 2; i++) {
            char ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (Character.isLetter(ch) || ch == '_') {
                        state = 1;
                    }
                    break;
                case 1:
                    if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_') {
                        state = 1;
                    } else {
                        state = 2;
                    }
                    break;
            }
        }
        String res = s.substring(front, --i);
        boolean b = token.containsKey(res);
        if (b) {
            words.add(new Word(res, token.get(res), line, "关键字", ""));
        } else {
            words.add(new Word(res, 700, line, "标识符", "identifier"));
        }
        return i;
    }

    /**
     * 注释和除号
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int annotation(String s, int front) {
        int state = 0;
        int i;
        char ch;
        for (i = front; i < s.length() && state < 5; i++) {
            ch = s.charAt(i);
            if (ch == '\n')
                line++;
            switch (state) {
                case 0:
                    if (ch == '/') {
                        state = 1;
                    }
                    break;
                case 1:
                    if (ch == '*') {
                        state = 2;
                    } else if (ch == '/') {
                        state = 4;
                    } else {
                        state = 7;
                    }
                    break;
                case 2:
                    if (ch != '*') {
                        state = 2;
                    } else if (ch == '*') {
                        state = 3;
                    }
                    break;
                case 3:
                    if (ch == '*') {
                        state = 3;
                    } else if (ch != '/') {
                        state = 2;
                    } else if (ch == '/') {
                        state = 5;
                    }
                    break;
                case 4:
                    if (ch != '\n') {
                        state = 4;
                    } else {
                        state = 6;
                    }
                    break;
            }
        }
        if (i == s.length()) {
            errors.add(new Error(s.substring(front, i), 505, line, "寻至文件末尾，未找到注释结束符号"));
        } else if (state == 7) {
            String res = s.substring(front, --i);
            words.add(new Word(res, token.get(res), line, "运算符", ""));
        }
        return i;
    }

    /**
     * 单个字符识别
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int characterIdentify(String s, int front) {
        int state = 0;
        int i = 0;
        for (i = front; i < s.length() && state < 4; i++) {
            char ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (ch == '\'') {
                        state = 1;
                    }
                    break;
                case 1:
                    if (ch == '\\') {
                        state = 1;
                    } else if (ch != '\'') {
                        state = 2;
                    }
                    break;
                case 2:
                    if (ch == '\'') {
                        state = 4;
                    } else {
                        state = 5;
                    }
                    break;
            }
        }
        if (state == 5) {
            errors.add(new Error(s.substring(front, i), 503, line, "字符未结束"));
        } else {
            String res = s.substring(front + 1, i - 1);
            words.add(new Word(res, 500, line, "字符", ""));
        }
        return i;
    }

    /**
     * 识别字符串
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int stringIdentify(String s, int front) {
        int state = 0;
        int i;
        for (i = front; i < s.length() && state < 2; i++) {
            char ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (ch == '"') {
                        state = 1;
                    }
                    break;
                case 1:
                    if (ch != '"') {

                        state = 1;
                    } else if (ch == '"') {
                        state = 2;
                    }
                    if (ch == ';') {
                        state = 3;
                    }
                    break;
            }
        }
        if (state == 3) {
            errors.add(new Error(s.substring(front, i), 504, line, "字符串未结束"));
        } else {
            String res = s.substring(front + 1, i - 1);
            words.add(new Word(res, 600, line, "字符串", ""));
        }
        return i;
    }

    /**
     * 其他情况
     *
     * @param s
     * @param front
     * @return
     */
    @Override
    public int other(String s, int front) {
        int state = 0;
        int i;
        int count = 0;
        char ch = s.charAt(front);
        if (ch == '\n') {
            line++;
            return ++front;
        } else if (ch == ' ' || ch == '\t') {
            return ++front;
        }

        for (i = front; i < s.length() && state < 5; i++) {
            ch = s.charAt(i);
            switch (state) {
                case 0:
                    if (separator.contains(ch)) {
                        state = 6;  //  分隔符
                    } else if (operator.contains(ch)) {
                        state = 2;
                    }
                    break;
                case 2:
                    if (operator.contains(ch)) {
                        state = 3;
                    } else {
                        state = 8;  //单个运算符
                    }
                    break;
                case 3:
                    if (operator.contains(ch)) {
                        state = 4;
                    } else {
                        state = 7;    //两个运算符 >=0
                    }
                    break;
                case 4:
                    if (operator.contains(ch)) {
                        state = 4;
                    } else {
                        state = 5;    //运算符错误
                    }
                    break;
            }
        }
        if (state > 6)
            --i;
        if (state == 5) {
            --i;
        }
        String res = s.substring(front, i);

        if (token.containsKey(res)) {
            words.add(new Word(res, token.get(res), line, "其他", ""));
        } else {
            errors.add(new Error(res, 501, line, "运算符号错误"));
        }

        return i;
    }
}
