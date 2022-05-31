package cn.cqut.compiler.lexical.nfa.ac.util;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @Author CuriT
 * @Date 2022-5-16 11:55
 */
public class Regular {
    //正则表达式转换为后缀表达式
    public static String getPostfix(String s, ArrayList<Character> words) {
        String r = "";
        Stack<Character> c = new Stack<>();
        for (int i = 0; i < s.length(); i++) {
            if (!words.contains(s.charAt(i)) && !"|()*.".contains("" + s.charAt(i)))
                words.add(s.charAt(i));
            if (s.charAt(i) == '(') {
                c.add(s.charAt(i));
                continue;
            }
            if (s.charAt(i) == ')') {
                char a = c.pop();
                while (a != '(') {
                    r = r + a;
                    a = c.pop();
                }
                //预测括号后面是否有*
                if (i != s.length() - 1 && s.charAt(i + 1) == '*') {
                    r = r + s.charAt(i + 1);
                    i++;
                }
                continue;
            }
            if (s.charAt(i) == '|' || s.charAt(i) == '.' || s.charAt(i) == '*') {
                if (!c.isEmpty() && c.peek() != '(') { //如果不是空栈切栈顶不为(
                    r = r + c.pop();//出栈
                }
                c.add(s.charAt(i));//入栈
                continue;
            }
            r = r + s.charAt(i);
        }
        //把栈内的符号加入到r
        while (!c.isEmpty())
            r = r + c.pop();
        return r;
    }

    //得到未省略'·'的正则表达式
    //转换为不省略'.'的正则表达式
    public static String getRegular(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i == s.length() - 1 || s.charAt(i) == '(') continue;//最后一个就出去
            if (s.charAt(i) == ')' && (s.charAt(i + 1) != '*' && s.charAt(i + 1) != '|')) {
                StringBuilder sb = new StringBuilder(s);
                sb.insert(i + 1, '.');
                s = sb.toString();
                i++;
                continue;
            }
            if (s.charAt(i) != '|' && s.charAt(i) != '*' && s.charAt(i + 1) != '|' && s.charAt(i + 1) != '*' && s.charAt(i + 1) != ')') {//就是操作数了
                StringBuilder sb = new StringBuilder(s);
                sb.insert(i + 1, '.');
                s = sb.toString();
                i++;
                continue;
            }
        }
        return s;
    }
}
