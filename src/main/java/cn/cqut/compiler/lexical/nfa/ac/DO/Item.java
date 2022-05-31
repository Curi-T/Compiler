package cn.cqut.compiler.lexical.nfa.ac.DO;

/**
 * @Author CuriT
 * @Date 2022-5-16 10:45
 */
public class Item{
    char start;
    String after = "";
    int group = 0;//0代表还没有项目集合
    int index = 0;//属于第几个文法
    int next = 0;//下一个移进文法在文法中的哪一个
    public Item(char start,String after,int index) {
        this.start = start;
        this.after = after;
        this.index = index;
    }
    public int getNext() {
        return this.next;
    }
    public void setNext(int next) {
        this.next = next;
    }
    public void setgroup() {
        group = 1;
    }
    public int getgroup() {
        return group;
    }
    public char getStart() {
        return start;
    }
    public String getAfter() {
        return after;
    }
    public void setAfter(String after) {
        this.after = after;
    }
    public String toString() {
        return String.valueOf(start)+"->"+after;
    }
    public int getIndex() {
        return this.index;
    }

    public char getnextOfPoint() {
        for(int i=0 ; i<after.length() ;i++) {
            if(after.charAt(i)=='.' && i<after.length()-1)
                return after.charAt(i+1);
        }
        return '#';
    }
}