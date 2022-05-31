package cn.cqut.compiler.lexical.nfa.ac.DO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;

public class State {
    int num = 0;//当前项目编号
    ArrayList<Item> item = new ArrayList<>();//该状态所包含的所有项目
    boolean isterminstate = false;//当前状态是否为终结状态
    ArrayList<Integer> SubState = new ArrayList<>();//后继状态的编号
    ArrayList<Character> SubChar = new ArrayList<>();//读到某个字符转换状态
    ArrayList<State> sub = new ArrayList<>();//NFA图中的后序状态
    int visit = 0;//代表当前未被访问过
    public boolean isEnd = false;//初始化不是终态//用于DFA
    boolean isStart = false;//是不是开始状态
    /***********************
     * 用于绑定表格信息
     */
    private SimpleIntegerProperty a;//状态编号
    private SimpleStringProperty b = new SimpleStringProperty(" 1");//当前状态下的所有项目信息
    /**********************************************
     * goto表的信息！！很多！先生成10个备选。初始化为空格！
     */
    private SimpleStringProperty c1 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c2 = new SimpleStringProperty(" 1");
    private SimpleStringProperty c3 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c4 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c5 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c6 = new SimpleStringProperty(" 1");
    private SimpleStringProperty c7 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c8 = new SimpleStringProperty("1 ");
    private SimpleStringProperty c9 = new SimpleStringProperty(" 1");
    private SimpleStringProperty c0 = new SimpleStringProperty("1 ");
    /*********************************************
     * 文法   和   非终结符
     */
    private SimpleStringProperty nonterminals = new SimpleStringProperty(" 1 ");
    private SimpleStringProperty grammar = new SimpleStringProperty(" 1 ");

    public State(int num) {
        this.num = num;
        this.a = new SimpleIntegerProperty(num);
//		System.out.println(a.get()+b.get());
    }

    public void setNext(State s, char c) {//图中链接下一个
        SubChar.add(c);
        sub.add(s);
    }

    public State getNext(char c) {
        for (int i = 0; i < SubChar.size(); i++) {
            if (SubChar.get(i) == c)
                return sub.get(i);
        }
        return new State(-1);
    }

    public void setEnd() {
        isEnd = true;
    }

    public boolean IsEnd() {
        return isEnd;
    }

    public void setStart() {
        isStart = true;
    }

    public boolean IsStart() {
        return isStart;
    }

    //	public ArrayList<Integer> getSubStateDFA(){
//		return SubState;
//	}
//	public void setSubStateDFA(ArrayList<Integer> a) {
//		SubState = a;
//	}
    public State(String nonterminals) {
        this.nonterminals = new SimpleStringProperty(nonterminals);
    }

    public State(String grammar, int i) {//文法
        this.grammar = new SimpleStringProperty(grammar);
    }

    public void setGrammar(String g) {
        this.grammar = new SimpleStringProperty(g);
    }

    public String getGrammar() {
        return this.grammar.get();
    }

    public void setNonterminals(String t) {
        this.nonterminals = new SimpleStringProperty(t);
    }

    public String getNonterminals() {
        return this.nonterminals.get();
    }

    public ArrayList<Character> getSubChar() {
        return this.SubChar;
    }

    public ArrayList<Integer> getSubState() {
        return this.SubState;
    }

    public ArrayList<State> getNext() {
        return sub;
    }

    public ArrayList<Item> getItems() {
        return item;
    }

    public void setVisit() {
        visit = 1;
    }

    public int getVisit() {
        return visit;
    }

    public void setSubsequnt(int index, char c) {
        SubState.add(index);
        SubChar.add(c);
    }

    public boolean isHaveSub() {
        if (SubChar.size() != 0) return true;
        return false;
    }

    public String getSubsequnt() {
        if (SubChar.size() == 0)
            return "null";
        String result = "";
        for (int i = 0; i < SubChar.size(); i++) {
            result = result + String.valueOf(SubChar.get(i)) + "→" + SubState.get(i) + "\n";
        }
        return result;
    }

    public int getA() {
        return a.get();
    }

    public String getB() {

        return b.get();
    }

    public String getC1() {
        return c1.get();
    }

    public String getC2() {
        return c2.get();
    }

    public String getC3() {
        return c3.get();
    }

    public String getC4() {
        return c4.get();
    }

    public String getC5() {
        return c5.get();
    }

    public String getC6() {
        return c6.get();
    }

    public String getC7() {
        return c7.get();
    }

    public String getC8() {
        return c8.get();
    }

    public String getC9() {
        return c9.get();
    }

    public String getC0() {
        return c0.get();
    }

    public void setC(String c, int num) {
//		this.c10 = new SimpleStringProperty(c);
        switch (num) {
            case 1:
                this.c1 = new SimpleStringProperty(c);
                break;
            case 2:
                this.c2 = new SimpleStringProperty(c);
                break;
            case 3:
                this.c3 = new SimpleStringProperty(c);
                break;
            case 4:
                this.c4 = new SimpleStringProperty(c);
                break;
            case 5:
                this.c5 = new SimpleStringProperty(c);
                break;
            case 6:
                this.c6 = new SimpleStringProperty(c);
                break;
            case 7:
                this.c7 = new SimpleStringProperty(c);
                break;
            case 8:
                this.c8 = new SimpleStringProperty(c);
                break;
            case 9:
                this.c9 = new SimpleStringProperty(c);
                break;
            case 0:
                this.c0 = new SimpleStringProperty(c);
                break;
        }
    }

    public void addItem(Item item) {
        this.item.add(item);//在当前状态下加入一个新状态
        this.b = new SimpleStringProperty(b.get() + item.toString());
    }

    public int getNum() {
        return this.num;
    }

    public String toString() {
        String result = String.valueOf(num) + "：\n";
        for (int i = 0; i < item.size(); i++) {
            result = result + item.get(i).toString() + "\n";
        }
        return result;
    }


}
