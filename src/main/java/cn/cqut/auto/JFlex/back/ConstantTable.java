package cn.cqut.auto.JFlex.back;

import java.util.ArrayList;

public class ConstantTable {
    public ArrayList<String> constantName = new ArrayList<>();
    public ArrayList<Integer> constantType = new ArrayList<>();
    public ArrayList<String> value = new ArrayList<>();

    /**
     * 向常量表中添加一个常量
     *
     * @param name 常量名
     * @param type 常量类型
     * @param val  常量值
     */
    public void addConstant(String name, int type, String val) {
        constantName.add(name);
        constantType.add(type);
        value.add(val);
    }

    /**
     * 常量表中是否包含此常量
     *
     * @param name 常量名
     * @return 此常量位于常量表中索引
     */
    public int haveThis(String name) {
        for (int i = 0; i < constantName.size(); i++)
            if (constantName.get(i).equals(name))
                return i;
        return -1;
    }
}
