package cn.cqut.compiler.lexical.nfa.ac.util;

import cn.cqut.compiler.lexical.nfa.ac.DO.State;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @Author CuriT
 * @Date 2022-5-16 18:35
 */
public class Closure {
    //求当前状态的闭包
    public static ArrayList<Integer> getEncloure(State s) {
        ArrayList<Integer> r = new ArrayList<>();
        r.add(s.getNum());
        Stack<State> states = new Stack<>();
        states.add(s);
        while (!states.isEmpty()) {
            s = states.pop();
            ArrayList<Character> subChar = s.getSubChar();//下一个状态
            ArrayList<State> subState = s.getNext();//子状态们
            for (int i = 0; i < subChar.size(); i++) {
                if (subChar.get(i) == '#' && !r.contains(subState.get(i).getNum())) {//当前经过#就能到达下一状态，且r集合里没有包括过
                    r.add(subState.get(i).getNum());
                    states.add(subState.get(i));
                }
            }

        }
        return r;
    }


}
