package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:30
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class MinimumDFA{
    private boolean[] newFinalState = null;//由确定化DFA得到
    private ArrayList<Edge> nodeAl = null;//由确定化DFA得到
    private int dfaNode;//确定化DFA节点的个数
    private Set<Character> characterSet = null;//正规式中的字符的集合
    private ArrayList<Set<Integer>> setList = new ArrayList<Set<Integer>>();
    public MinimumDFA(boolean[] newFinalState, ArrayList<Edge> nodeAl, int dfaNode, Set<Character> characterSet) {
        super();
        this.newFinalState = newFinalState;
        this.nodeAl = nodeAl;
        this.dfaNode = dfaNode;
        this.characterSet = characterSet;
    }
    private void init(){//利用分割法将集合分成终态和非终态
        Set<Integer> finalStateSet = new HashSet<Integer>();
        Set<Integer> NofinalStateSet = new HashSet<Integer>();
        for(int i=1; i<=dfaNode; ++i)
            if(newFinalState[i])//终态
                finalStateSet.add(i);
            else
                NofinalStateSet.add(i);
        setList.add(finalStateSet);
        setList.add(NofinalStateSet);
    }

    public void toMinimumDfa(){
        init();
        boolean flag = true;
        ArrayList<Set<Integer>> tmpSetList = new ArrayList<Set<Integer>>();
        while(flag){
            flag = false;
            hjzgg:
            for(int k=0; k<setList.size(); ++k){
                Set<Integer> st = setList.get(k);
                if(st.size()<=1) continue;
                for(Character ch : characterSet){
                    Map<Integer, Integer> mp = new HashMap<Integer, Integer>();
                    for(int i=0; i<nodeAl.size(); ++i){//st集合(也就是map的val值)在 ch这个点对应的集合 {st}a = {...}
                        Edge edge = nodeAl.get(i);
                        if(edge.key == ch && st.contains(edge.u))
                            mp.put(edge.u, edge.v);
                    }

                    for(Integer i : st)
                        if(!mp.containsKey(i))//表明i节点对应的是一条空边
                            mp.put(i, -1);



//将st集合拆分成两个不想交的集合
                    Set<Integer> firstSet = new HashSet<Integer>();
                    Set<Integer> secondSet = new HashSet<Integer>();
                    for(int j=0; j<setList.size(); ++j){
                        firstSet.clear();
                        secondSet.clear();
                        Set<Integer> tmpSt = setList.get(k);
                        for(Entry<Integer, Integer> entry : mp.entrySet()){//返回此映射中包含的映射关系的 set 视图。返回的 set 中的每个元素都是一个 Map.Entry
                            if(tmpSt.contains(entry.getValue()))
                                firstSet.add(entry.getKey());
                            else secondSet.add(entry.getKey());
                        }
                        if(firstSet.size()!=0 && secondSet.size()!=0){
                            flag = true;//如果发现可以拆分的集合，则继续最顶层的while循环
                            for(Integer i : tmpSt){//将firstSet 和 secondSet中都没有的元素添加到firstSet中
                                if(!firstSet.contains(i) && !secondSet.contains(i))
                                    firstSet.add(i);
                            }
                            setList.remove(k);
                            setList.add(firstSet);
                            setList.add(secondSet);
                            break hjzgg;
                        }
                    }
                }
            }
        }
//        for(int k=0; k<setList.size(); ++k)//输出最终的集合划分
//            System.out.println(setList.get(k));
//        System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
        for(int k=0; k<setList.size(); ++k){
            Set<Integer> st = setList.get(k);
            if(st.size() > 1){//看成是一个等价的状态，选择第一个元素当作代表
                int first=0;
                for(Integer i : st){//取得第一个元素
                    first = i;
                    break;
                }
                ArrayList<Edge> tmpList = new ArrayList<Edge>();
                for(int i=0; i<nodeAl.size(); ++i){//遍历所有的边，找到不是first
                    Edge edge = nodeAl.get(i);
                    if(st.contains(edge.u) && edge.u!=first){
                        nodeAl.remove(i);
                        --i;
                    } else if(st.contains(edge.v) && edge.v!=first){
                        nodeAl.remove(i);
                        --i;
                        tmpList.add(new Edge(edge.u, first, edge.key));
                    }
                }
                nodeAl.addAll(tmpList);
            }
        }
    }

    public void outputMinimumDFA(){
//        for(int i=0; i<nodeAl.size(); ++i)//输出未确定化的DFA
//            System.out.println(nodeAl.get(i));
        toMinimumDfa();
        ToMinimumDFA.res="";
        for(int i=0; i<nodeAl.size(); ++i){
            System.out.println(nodeAl.get(i));
            ToMinimumDFA.res=ToMinimumDFA.res+nodeAl.get(i)+"\n";
        }
    }
}

public class ToMinimumDFA {
    String formal_ceremony;
    static String  res="";
    public ToMinimumDFA(String formal_ceremony) {
//        String formal_ceremony = "1(0|1)*101";
        this.formal_ceremony = formal_ceremony;
        NFA nfa = new NFA(formal_ceremony);
        DefinedNFA definedDFA = new DefinedNFA(nfa.getNFAGraphics(), nfa.getCharacterSet(), nfa.getFinalState());
        definedDFA.ToStateMatrix();
        MinimumDFA minimumDFA = new MinimumDFA(definedDFA.getNewFinalState(), definedDFA.getNodeAl(), definedDFA.getDfaNode(), definedDFA.getCharacterSet());
        minimumDFA.outputMinimumDFA();
        NFAViewer.area3.setText(res);
    }

}