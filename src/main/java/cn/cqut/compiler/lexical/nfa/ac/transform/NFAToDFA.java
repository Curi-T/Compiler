package cn.cqut.compiler.lexical.nfa.ac.transform;

import cn.cqut.compiler.lexical.nfa.ac.util.Closure;
import javafx.scene.control.Label;
import lombok.Getter;
import cn.cqut.compiler.lexical.nfa.ac.DO.NFA;
import cn.cqut.compiler.lexical.nfa.ac.DO.State;
import cn.cqut.compiler.lexical.nfa.ac.DO.Step;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * @Author CuriT
 * @Date 2022-4-16 18:29
 */
@Getter
public class NFAToDFA {
    private NFA nfa;
    /**
     * 状态的计数器
     */
    private int count = 1;
    /**
     * 用于保存所有单词
     */
    private ArrayList<Character> words = new ArrayList<>();
    /**
     * 用来保存NFA中所有的状态
     */
    private ArrayList<State> statesOfNFA = new ArrayList<>();
    /**
     * DFAI中的状态所包含的NFA中的状态
     */
    private ArrayList<ArrayList<Integer>> DFAI = new ArrayList<>();
    /**
     * 用来保存DFAI中所有的状态
     */
    private ArrayList<State> statesOfDFA = new ArrayList<>();

    public NFAToDFA(NFA nfa, int count, ArrayList<Character> words, ArrayList<State> statesOfNFA) {
        this.nfa = nfa;
        this.count = count;
        this.words = words;
        this.statesOfNFA = statesOfNFA;
    }

    //NFA->DFA
    /**
     * DFA里的开始状态集合
     */
    private ArrayList<Integer> DFAstart = new ArrayList<>();
    /**
     * DFA里的结束状态集合
     */
    private ArrayList<Integer> DFAend = new ArrayList<>();

    public ArrayList<Step> getDFA(Label Lstart, Label Lend) {
        Queue<ArrayList<Integer>> Ique = new LinkedList<>();//定义队列
        ArrayList<ArrayList<Integer>> table = new ArrayList<>();//表
        int row = words.size();//多少个状态一行
        //给NFA加上新的开始符号和结束符号
        State start = new State(count);
        count++;
        State end = new State(count);
        count++;
        start.setNext(nfa.getStart(), '#');
        nfa.getEnd().setNext(end, '#');//连接
        statesOfNFA.add(start);
        statesOfNFA.add(end);//加入现有状态集合
        //开始填——————————/球开始的闭包
        ArrayList<Integer> s = Closure.getEncloure(start);
        DFAI.add(s);//加入已有NFA

        Ique.add(s);//加入队列
        //开始遍历
        while (!Ique.isEmpty()) {
            s = Ique.poll();//出一个状态
            table.add(s);//表格第一列
            //弧转换
            for (int i = 0; i < row; i++) {
                ArrayList<Integer> now = getHuDiversion(s, words.get(i));
                table.add(now);
                if (isNewDFAState(now) && !now.isEmpty()) {//如果是一个新状态
                    DFAI.add(now);//加入状态栏
                    Ique.add(now);//加入队列serty
                }
            }
        }
        //得到状态们
        for (int i = 0; i < DFAI.size(); i++) {//访问定义列的第一个
            State state = new State(i);//创造状态
            if (i == 0) {//那么当前状态为dfa的开始状态
                Lstart.setText(Lstart.getText() + "\t" + state.getNum());
                DFAstart.add(state.getNum());
                state.setStart();
            }
            statesOfDFA.add(state);
        }
        ArrayList<Step> DFAs = new ArrayList<>();//用来保存DFA读取信息
        //链接状态
        //判断第一个状态是不是终态
        ArrayList<Integer> startInfor = table.get(0);//得到状态信息
        State startState = getNextState(startInfor, table);
        if (startInfor.contains(nfa.getEnd().getNum()) && startState.isEnd == false) {//当前状态包含nfa的终态，并且还未被设置成终态过
            Lend.setText(Lend.getText() + "\t" + startState.getNum());
            DFAend.add(startState.getNum());
            startState.setEnd();//设为DFA的终态
        }
        for (int i = 0; i < statesOfDFA.size(); i++) {
            for (int j = 0; j < words.size(); j++) {
                char subchar = words.get(j);
                ArrayList<Integer> nextStateInfor = table.get(i * (words.size() + 1) + j + 1);//得到状态信息
                if (nextStateInfor.size() == 0)
                    continue;
                State nextState = getNextState(nextStateInfor, table);
                if (nextStateInfor.contains(nfa.getEnd().getNum()) && nextState.isEnd == false) {//当前状态包含nfa的终态，并且还未被设置成终态过
                    Lend.setText(Lend.getText() + "\t" + nextState.getNum());
                    DFAend.add(nextState.getNum());
                    nextState.setEnd();//设为DFA的终态
                }

                statesOfDFA.get(i).setNext(nextState, subchar);
                DFAs.add(new Step("" + statesOfDFA.get(i).getNum(), "" + subchar, "" + nextState.getNum()));
            }
        }
        return DFAs;
    }

    /**
     * 得到DNF下一个状态
     *
     * @param state
     * @param table
     * @return
     */
    private State getNextState(ArrayList<Integer> state, ArrayList<ArrayList<Integer>> table) {
        for (int i = 0; i < statesOfDFA.size(); i++) {
            int count = 0;
            ArrayList<Integer> s = table.get(i * (words.size() + 1));
            if (s.size() != state.size())
                continue;
            for (int j = 0; j < s.size(); j++)
                if (s.contains(state.get(j)))
                    count++;
            if (count == s.size())
                return statesOfDFA.get(i);
        }
        return new State(-1);

    }

    /**
     * 判断当前状态是否是新状态在dfai中
     *
     * @param state
     * @return
     */
    private boolean isNewDFAState(ArrayList<Integer> state) {
        for (int i = 0; i < DFAI.size(); i++) {
            ArrayList<Integer> s = DFAI.get(i);
            int count = 0;
            if (s.size() != state.size())
                continue;
            for (int j = 0; j < state.size(); j++) {
                if (s.contains(state.get(j)))
                    count++;
            }
            if (count == state.size())
                return false;
        }
        return true;
    }

    /**
     * 得到弧转换
     *
     * @param states
     * @param c
     * @return
     */
    private ArrayList<Integer> getHuDiversion(ArrayList<Integer> states, char c) {
        ArrayList<Integer> r = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {//对states中的每一个状态
            State state = getStateFromNum(states.get(i));//求这个状态
            ArrayList<Integer> move_C = getMove(state, c);//再得到Move的闭包
            for (int j = 0; j < move_C.size(); j++) {
                State s = getStateFromNum(move_C.get(j));//对应状态
                ArrayList<Integer> enclose = Closure.getEncloure(s);
                for (int k = 0; k < enclose.size(); k++) {
                    if (!r.contains(enclose.get(k)))
                        r.add(enclose.get(k));
                }
            }
        }
        return r;
    }

    /**
     * Move
     *
     * @param state
     * @param c
     * @return
     */
    private ArrayList<Integer> getMove(State state, char c) {
        ArrayList<Integer> r = new ArrayList<>();
        Stack<State> stack = new Stack<>();
        stack.add(state);
        while (!stack.isEmpty()) {
            state = stack.pop();
            ArrayList<Character> subChar = state.getSubChar();//下一个状态
            ArrayList<State> subState = state.getNext();//子状态们
            for (int i = 0; i < subChar.size(); i++) {
                if (subChar.get(i) == c && !r.contains(subState.get(i).getNum())) {//当前经过'c'就能到达下一状态，且r集合里没有包括过
                    r.add(subState.get(i).getNum());
                }
            }
        }
        return r;
    }

    /**
     * 得到当前编号在NFA中对应状态
     *
     * @param num
     * @return
     */
    private State getStateFromNum(int num) {
        State r = new State(0);
        for (int i = 0; i < statesOfNFA.size(); i++) {
            r = statesOfNFA.get(i);
            if (r.getNum() == num)
                return r;
        }
        return r;
    }
}
