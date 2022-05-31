package cn.cqut.compiler.lexical.nfa.ac.transform;

import javafx.scene.control.Label;
import cn.cqut.compiler.lexical.nfa.ac.DO.State;
import cn.cqut.compiler.lexical.nfa.ac.DO.Step;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author CuriT
 * @Date 2022-5-18 09:51
 */
public class DFAToMFA {
    private ArrayList<Character> words = new ArrayList<>();//用于保存所有单词
    private ArrayList<State> statesOfDFA = new ArrayList<>();//用来保存DFAI中所有的状态
    private ArrayList<State> statesOfMFA = new ArrayList<>();//用来保存MFA中所有的状态


    //NFA->DFA
    private ArrayList<Integer> DFAstart = new ArrayList<>();//DFA里的开始状态集合
    private ArrayList<Integer> DFAend = new ArrayList<>();//DFA里的结束状态集合





    public ArrayList<Step> getMFA(Label Lstart, Label Lend) {
        ArrayList<ArrayList<Integer>> MFAS = new ArrayList<>();

        Queue<ArrayList<Integer>> needMFAqueue = new LinkedList<>();

        ArrayList<Integer> end = new ArrayList<>();//终态集合
        ArrayList<Integer> nonEnd = new ArrayList<>();//非终态集合
        for (int i = 0; i < statesOfDFA.size(); i++) {
            if (statesOfDFA.get(i).IsEnd()) {//是终态
                end.add(statesOfDFA.get(i).getNum());//加入终态集合
                continue;
            }
            nonEnd.add(statesOfDFA.get(i).getNum());//加入非终态集合
        }
        //将终态集合和非终态集合加入还需分割的队列中
        if (end.size() != 0) needMFAqueue.add(end);
        if (nonEnd.size() != 0) needMFAqueue.add(nonEnd);
        int size = 0;
        do {
            size = needMFAqueue.size();
            ArrayList<Integer> gather = needMFAqueue.poll();//队头出列
            if (gather.size() == 1) {
                MFAS.add(gather);
                continue;
            }
            for (int i = 0; i < words.size(); i++) {
                ArrayList<State> readCharNextStates = new ArrayList<>();//读了当前字符后的下一个状态们
                for (int j = 0; j < gather.size(); j++) {//得到所有gather读了char的下一状态
                    State now = getStateFromNum_DFA(gather.get(j));//得到gather中的一个状态
                    readCharNextStates.add(now.getNext(words.get(i)));//下一状态,加入状态集合
                }
                int[] visit = new int[readCharNextStates.size()];//得到是否已有集合的数组
                for (int j = 0; j < readCharNextStates.size(); j++) {
                    if (visit[j] != 0 && readCharNextStates.get(j).getNum() != -1)//已加入集合就跳过,当前状态-1不抵达跳过
                        continue;
                    ArrayList<Integer> newGather = new ArrayList<>();//为当前状态相同的状态创造集合
                    newGather.add(gather.get(j));//当前加入
                    visit[j] = 1;
                    for (int k = j + 1; k < readCharNextStates.size(); k++) {//挨个访问之后的状态，是否等价
                        if (haveCommonGather(needMFAqueue, gather, readCharNextStates.get(j), readCharNextStates.get(k))) {
                            //如果当前后继状态相同，加入
                            newGather.add(gather.get(k));
                            visit[k] = 1;
                        }
                    }
                    //判定当前DFA是否是新集合//直接加入
                    if (!isNewGather(needMFAqueue, newGather))
                        needMFAqueue.add(newGather);
                }
            }
        } while (size != needMFAqueue.size() && !needMFAqueue.isEmpty());//队列的个数不在增多,并且队列不为空
        ArrayList<ArrayList<Integer>> needRemove = new ArrayList<>();
        while (!needMFAqueue.isEmpty()) {
            ArrayList<Integer> newGather = needMFAqueue.poll();
            if (newGather.size() == 1) {//若为单个，必定为单一集合，直接加入最终MFAS
                MFAS.add(newGather);
                continue;
            }
            needRemove.add(newGather);
        }
        MFAS = RemoveRepeat(needRemove, MFAS);
        for (int i = 0; i < MFAS.size(); i++) {
            State s = new State(i);//每一个MFAS都是一个状态
            for (int j = 0; j < DFAstart.size(); j++) {
                if (MFAS.get(i).contains(DFAstart.get(j))) {
                    s.setStart();//设为开始
                    Lstart.setText(Lstart.getText() + "\t" + s.getNum());
                    break;
                }
            }
            for (int j = 0; j < DFAend.size(); j++) {
                if (MFAS.get(i).contains(DFAend.get(j))) {
                    s.setEnd();//设为终结
                    Lend.setText(Lend.getText() + "\t" + s.getNum());
                    break;
                }
            }
            statesOfMFA.add(s);
        }
        for (int i = 0; i < statesOfMFA.size(); i++) {//链接
            ArrayList<Integer> g = MFAS.get(i);//当前MFAS对应所包含的所有DFA元素
            State inGState = getStateFromNum_DFA(g.get(0));//选g中的第一个作为代表
            //链接
            for (int j = 0; j < words.size(); j++) {//当前状态读每一个单词，是否有下一状态，选第一个作为代表
                int isend = 0;
                State next = inGState.getNext(words.get(j));//next的下一状态
                if (next.getNum() == -1) {
                    isend++;
                    if (isend == words.size()) {//终态
                        statesOfMFA.get(i).setEnd();
                    }
                    continue;
                }
//				if(next.getNum() != -1) {//读该字符能够到达下一状态
                //查看该状态属于MFAS中的第几个
                int k = 0;
                for (; k < MFAS.size(); k++) {
                    if (MFAS.get(k).contains(next.getNum())) {
                        break;
                    }
                }
                statesOfMFA.get(i).setNext(statesOfMFA.get(k), words.get(j));
//				}
            }
        }

        ArrayList<Step> MFAstep = new ArrayList<>();
        for (int i = 0; i < statesOfMFA.size(); i++) {
            State s = statesOfMFA.get(i);
            for (int j = 0; j < words.size(); j++) {
                char subchar = words.get(j);
                State next = s.getNext(subchar);
                if (next.getNum() != -1) {//有下一状态
                    Step step = new Step("" + s.getNum(), "" + subchar, "" + next.getNum());
                    MFAstep.add(step);
                }
            }
        }
        return MFAstep;

    }

    //得到当前编号在NFA中对应状态
    public State getStateFromNum_DFA(int num) {
        State r = new State(-1);
        for (int i = 0; i < statesOfDFA.size(); i++) {
            r = statesOfDFA.get(i);
            if (r.getNum() == num)
                return r;
        }
        return r;
    }

    //判断两个状态是否在同一集合
    public boolean haveCommonGather(Queue<ArrayList<Integer>> que, ArrayList<Integer> nowGther, State s1, State s2) {
        //判断当前在不在nowGther中
        if (nowGther.contains(s1.getNum()) && nowGther.contains(s2.getNum()))
            return true;
        int size = que.size();
        boolean r = false;
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> gather = que.poll();
            if (gather.contains(s2.getNum()) && gather.contains(s1.getNum()))
                r = true;
            que.add(gather);
        }
        return r;
    }

    //判断当前两个状态是不是新的MFAS
    public boolean isNewGather(Queue<ArrayList<Integer>> que, ArrayList<Integer> state) {
        boolean r = false;
        int size = que.size();
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> gather = que.poll();
            que.add(gather);
            if (gather.size() != state.size())
                continue;
            int count = 0;
            for (int j = 0; j < gather.size(); j++) {
                if (gather.contains(state.get(j)))
                    count++;
            }
            if (count == gather.size())
                r = true;
        }
        return r;
    }

    //去重
    public ArrayList<ArrayList<Integer>> RemoveRepeat(ArrayList<ArrayList<Integer>> needRemove, ArrayList<ArrayList<Integer>> MFA) {
        for (int i = 0; i < needRemove.size(); i++) {
            ArrayList<Integer> g = needRemove.get(i);//当前需要判断需不需要去重的集合
            int MFAhas = 0;
            OUT:
            for (int j = 0; j < MFA.size(); j++) {//在每一个MFA中寻找有没有包含g中元素的
                for (int k = 0; k < g.size(); k++) {
                    if (MFA.get(j).contains(g.get(k))) {
                        MFAhas = 1;
                        break OUT;
                    }
                }
            }
            if (MFAhas == 1) //MFA中已包含该状态，该状态为废状态
                continue;
            //判断接下来的集合中有没有包含他的元素，但大小比他小的集合
            int haveContainButSmallG = 0;
            OUT2:
            for (int j = i + 1; j < needRemove.size(); j++) {
                for (int k = 0; k < g.size(); k++) {
                    if (needRemove.get(j).contains(g.get(k)) && needRemove.get(j).size() < g.size()) {
                        haveContainButSmallG = 1;
                        break OUT2;
                    }
                }
            }
            if (haveContainButSmallG == 1)
                continue;
            MFA.add(g);
        }
        return MFA;

    }
}
