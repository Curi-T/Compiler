package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:30
 */
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

class Edge{
    public int u, v;
    public char key;

    public Edge(int u, int v, char key) {
        super();
        this.u = u;
        this.v = v;
        this.key = key;
    }
    @Override
    public String toString() {
        return "   "+u + "   "+key +"   "+ v +"   "  ;
    }

    @Override
    public boolean equals(Object arg0) {
        Edge tmp = (Edge)arg0;
        return tmp.u==this.u && tmp.v==this.v && tmp.key==this.key;
    }
    @Override
    public int hashCode() {
        return u+v+key;
    }
}

class NFA{
    public static String message="";
    public static final int MAX_NODE = 100;
    private boolean finalState[] = new boolean[MAX_NODE];//记录每一个节点是否为终态
    private String formal_ceremony;//正规式字符串
    private int cnt_node=1;//记录节点的个数
    private Map<Integer, Integer> endNode = new TreeMap<Integer, Integer>();//每一个开始节点对应的终端节点
    private ArrayList<Edge> nodeAl = new ArrayList<Edge>();
    private Vector<Pair>[] g = new Vector[MAX_NODE];//NFA图
    private Set<Character> st = new TreeSet<Character>();//正规式中出现的字符的集合
    public NFA(String formal_ceremony) {
        super();
        this.formal_ceremony = formal_ceremony;
    }

    private void addEdge(int u, int v, char ch){
        nodeAl.add(new Edge(u, v, ch));
        if(g[u] == null)
            g[u] = new Vector<Pair>();
        g[u].add(new Pair(v, ch));
        if(ch!='$')
            st.add(ch);
    }

    public boolean    kernel_way(int fa, int ld, int rd, boolean isClosure){//fa表示区间的开始点，正规式的区间[ld, rd], isClosure表示这段区间查是否存在闭包
        if(ld < 0 || rd >= formal_ceremony.length()){
            message="正规式不正确---发生数组越界!";
            return false;
        }
        int pre_node = fa;
        int inBracket = 0;//判断'|'是否在括弧内
        for(int i=ld; i<=rd; ++i){
            if(formal_ceremony.charAt(i)=='(') ++inBracket;
            else if(formal_ceremony.charAt(i)==')') --inBracket;
            else if(formal_ceremony.charAt(i)=='|' && 0==inBracket){
                if(!kernel_way(fa, ld, i-1, isClosure)) return false;
                if(!kernel_way(fa, i+1, rd, isClosure)) return false;
                return true;
            }
        }
        for(int i=ld; i<=rd; ++i){
            if(formal_ceremony.charAt(i)=='('){//又是一个子区间
                //寻找和 该 '('相匹配的')'
                int cntLeftBracket = 0;//统计遍历过程中'('出现的次数，遇到')'减去1
                int posRightBracket = -1;//记录相匹配的')'的位置
                int posLeftBracket = i;
                for(int j=i+1; j<=rd; ++j){
                    if(formal_ceremony.charAt(j)=='(')
                        ++cntLeftBracket;
                    else if(formal_ceremony.charAt(j)==')'){
                        if(cntLeftBracket == 0){
                            posRightBracket = j;
                            break;
                        }
                        --cntLeftBracket;
                    }
                }
                if(posRightBracket == -1){//出错
                    message="正规式出错----括弧不匹配!";
                    return false;
                }
                int nodeFather = 0;//括弧内正则式的开始节点
                if(posRightBracket+1 <= rd && formal_ceremony.charAt(posRightBracket+1)=='*'){
                    i = posRightBracket+1;//过滤掉"()*"
                    addEdge(pre_node, ++cnt_node, '$');//表示这一条边为空
                    pre_node = cnt_node;
                    nodeFather = cnt_node;
                    addEdge(pre_node, ++cnt_node, '$');//表示这一条边为空
                    pre_node = cnt_node;
                    //处理()*括弧内的正规式
                    if(!kernel_way(nodeFather, posLeftBracket+1, posRightBracket-1, true)) return false;
                } else {
                    nodeFather = pre_node;
                    if(!kernel_way(nodeFather, posLeftBracket+1, posRightBracket-1, false))//对于"(101)"， 看成101
                        return false;
                    i = posRightBracket;
                }

            } else {//单个字符
                if(formal_ceremony.charAt(i)==')') continue;
                if(i+1 <= rd && formal_ceremony.charAt(i+1)=='*'){
                    addEdge(pre_node, ++cnt_node, '$');//表示这一条边为空
                    pre_node = cnt_node;
                    addEdge(pre_node, pre_node, formal_ceremony.charAt(i));
                    if(i+1==rd  && isClosure)
                        addEdge(pre_node, fa, '$');//表示这一条边为空并且是连接到父亲节点
                    else{
                        if(endNode.containsKey(fa))
                            addEdge(pre_node, endNode.get(fa), '$');
                        else{
                            addEdge(pre_node, ++cnt_node, '$');//表示这一条边为空
                            if(i==rd) endNode.put(fa, cnt_node);//记录非闭包状态下 第一个节点对应的最后一个节点
                        }
                    }
                    pre_node = cnt_node;
                    ++i;//过滤*
                } else {
                    if(i==rd && isClosure){//是闭包的情况
                        addEdge(pre_node, fa, formal_ceremony.charAt(i));
                    } else{
                        if(endNode.containsKey(fa))
                            addEdge(pre_node, endNode.get(fa), formal_ceremony.charAt(i));
                        else{
                            addEdge(pre_node, ++cnt_node, formal_ceremony.charAt(i));
                            if(i==rd) endNode.put(fa, cnt_node);//记录非闭包状态下 第一个节点对应的最后一个节点
                        }
                    }
                    pre_node = cnt_node;
                }
            }
        }
        return true;
    }

    private void checkFinalState(){//检查哪一个节点是终态
        for(int i=1; i<=cnt_node; ++i){
            int cc = 0;
            if(g[i] == null){//表明是终态
                finalState[i] = true;
                continue;
            }
            for(int j=0; j<g[i].size(); ++j)
                if(g[i].elementAt(j).v != i)
                    ++cc;
            if(cc == 0)//表明是终态
                finalState[i] = true;
        }
    }

    public boolean[] getFinalState(){
        return finalState;
    }

    public Vector<Pair>[] getNFAGraphics(){
        if(kernel_way(1, 0, formal_ceremony.length()-1, false)){
//            for(Edge e : nodeAl)//打印NFA
//                System.out.println(e);
            checkFinalState();
            return g;
        }
        return null;
    }

    public Set<Character> getCharacterSet(){
        return st;
    }

    public void outputNFA(){
        if(kernel_way(1, 0, formal_ceremony.length()-1, false)){
            checkFinalState();
            ToNFA.res="";
            for(Edge e : nodeAl){
                System.out.println(e);
                ToNFA.res=ToNFA.res+e+"\n";
            }
        }
    }
}


/*
 * 将正规式转换成NFA
 * */
public class ToNFA {
    String formal_ceremony;
    static String  res="";
    /*public static void main(String[] args){
        String formal_ceremony = "0*(100*)*0*";
        NFA nfa = new NFA(formal_ceremony);
        nfa.outputNFA();
    }*/
    public ToNFA(String formal_ceremony){
        this.formal_ceremony = formal_ceremony;
        NFA nfa = new NFA(formal_ceremony);
        nfa.outputNFA();
        NFAViewer.area1.setText(res);
    }
    public ToNFA(String formal_ceremony,String a){
        this.formal_ceremony = formal_ceremony;
        NFA nfa = new NFA(formal_ceremony);
        nfa.outputNFA();
    }
}