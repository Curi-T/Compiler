package cn.cqut.compiler.lexical.nfa.ac.transform;

import lombok.Getter;
import cn.cqut.compiler.lexical.nfa.ac.DO.NFA;
import cn.cqut.compiler.lexical.nfa.ac.DO.State;
import cn.cqut.compiler.lexical.nfa.ac.DO.Step;
import cn.cqut.compiler.lexical.nfa.ac.util.Regular;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @Author CuriT
 * @Date 2022-5-16 17:54
 */
@Getter
public class NormalFormToNFA {
    private String normalForm;
    private int count = 1;//状态的计数器
    private ArrayList<State> statesOfNFA = new ArrayList<>();//用来保存NFA中所有的状态
    private ArrayList<Step> NFAs = new ArrayList<>();//用来保存NFA读取信息

    public NormalFormToNFA(String normalForm, ArrayList<Character> words) {
        this.normalForm = init(normalForm, words);
    }

    //  初始化正则表达式
    private String init(String normalForm, ArrayList<Character> words) {
        //正则化（添加'·'）
        normalForm = Regular.getRegular(normalForm);
        //将正则表达式转换为后缀表达式
        normalForm = Regular.getPostfix(normalForm, words);
        return normalForm;
    }

    public NFA toNfa() {
        Stack<NFA> nfa = new Stack<>();//nfa的栈
        for (int i = 0; i < normalForm.length(); i++) {
            if (normalForm.charAt(i) == '|') {
                State start = new State(count);
                count++;//开始状态
                State end = new State(count);
                count++;//终结状态
                //弹出两个nfa
                NFA now = new NFA(nfa.pop(), nfa.pop(), start, end);
                statesOfNFA.add(now.getStart());
                statesOfNFA.add(now.getEnd());
                nfa.add(now);//加入栈
            } else if (normalForm.charAt(i) == '*') {//取闭包
                State start = new State(count);
                count++;//开始状态
                State end = new State(count);
                count++;//终结状态
                NFA now = new NFA(nfa.pop(), start, end);
                nfa.add(now);
                statesOfNFA.add(now.getStart());
                statesOfNFA.add(now.getEnd());
            } else if (normalForm.charAt(i) == '.') {//取链接
                NFA now = new NFA(nfa.pop(), nfa.pop());
                nfa.add(now);
            } else {//此时单边 O -> O
                State start = new State(count);
                count++;//开始状态
                State end = new State(count);
                count++;//终结状态
                NFA now = new NFA(start, end, normalForm.charAt(i));
                nfa.add(now);//创造一个nfa加入栈
                statesOfNFA.add(now.getStart());
                statesOfNFA.add(now.getEnd());
            }
        }
        return nfa.pop();
    }

    //得到nfa的信息
    public ArrayList<Step> getNFAInfor(State start) {
        //输出当前节点的所有后继
        ArrayList<Character> subChar = start.getSubChar();
        ArrayList<State> subState = start.getNext();
//		stateOfNFA.add(start);
        start.setVisit();//设置为已访问
        for (int i = 0; i < subChar.size(); i++) {
            NFAs.add(new Step("" + start.getNum(), "" + subChar.get(i), "" + subState.get(i).getNum()));//当前状态+接受符号+下状态编号
            if (subState.get(i).getVisit() == 0) {//下一节点未被访问过，递归访问
                getNFAInfor(subState.get(i));
            }
        }
        return NFAs;
    }
}
