package cn.cqut.compiler.lexical.nfa.ac.DO;

/**
 * @Author CuriT
 * @Date 2022-5-16 16:22
 */
public class NFA{
    State start;
    State end;
    //  最初构造NFA
    public NFA(State s,State e,char c) {//输入两个节点，直接创造 ● → ●
        start = s;
        end = e;
        start.setNext(end, c);//开始连接终结
    }
    public NFA(NFA a,NFA b,State s,State e) {// 或操作
        start = s;
        end = e;//两个新开始结束状态
        start.setNext(a.getStart(), '#');//当前新起始状态读#，到a的开始状态
        start.setNext(b.getStart(), '#');//当前新起始状态读#,到b的开始状态
        a.getEnd().setNext(end, '#');//a的结束状态读‘#’进入到新的结束状态
        b.getEnd().setNext(end, '#');//b的结束状态读‘#’进入到新的#
    }

    public NFA(NFA a,State s,State e) {//闭包操作
        start = s;
        end = e;//两个新开始结束状态
        start.setNext(a.getStart(), '#');//当前新起始状态读#，到a的开始状态
        start.setNext(end, '#');//当前新起始状态读'#',到当前nfa的结束状态
        a.getEnd().setNext(end, '#');//a的结束状态读‘#’进入到新的结束状态
        a.getEnd().setNext(a.getStart(), '#');//a的结束状态读‘#’，进入a原先的开始状态
    }
    public NFA(NFA latter,NFA former) {//链接
        start = former.getStart();//NFA开始就是前者的开始
        end = latter.getEnd();//NFA的结束就是后者的结束
        former.getEnd().setNext(latter.getStart(), '#');
    }

    public void setEnd(State e) {
        end = e;
    }
    public State getStart() {
        return start;
    }
    public State getEnd() {
        return end;
    }
}