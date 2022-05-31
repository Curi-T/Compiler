package cn.cqut.auto.JFlex.back;

import java.util.ArrayList;

public class VariableTable {
    public ArrayList<String> variableName = new ArrayList<>();
    public ArrayList<Integer> variableType = new ArrayList<>();
    public ArrayList<String> value = new ArrayList<>();
    ArrayList<String> layer = new ArrayList<>();

    public int addVariable(String name, int type, String val, String layer) {
        variableName.add(name);
        variableType.add(type);
        value.add(val);
        this.layer.add(layer);
        return variableName.size() - 1;
    }

    /**
     * 获取同层级（同一个作用域内）的变量表中是否有 name 变量
     *
     * @param name  变量名
     * @param layer 传入变量所属层级或作用域
     * @return
     */
    public int haveThisNameInLayer(String name, String layer) {
        for (int i = 0; i < variableName.size(); i++)
            if (variableName.get(i).equals(name) && this.layer.get(i).equals(layer))
                return i;
        return -1;
    }

    /**
     * 获取name在变量表中的位置、索引
     *
     * @param name  变量名
     * @param layer 传入变量所属层级或作用域
     * @return
     */
    public int haveThisNameInUpLayer(String name, String layer) {
        //本层及高层
        String[] str1 = layer.split("/");
        for (int j = str1.length; j >= 0; j--) {
            for (int i = 0; i < variableName.size(); i++) {
                if (!variableName.get(i).equals(name)) {
                    continue;
                }
                String[] str2 = this.layer.get(i).split("/");
                if (str2.length != j) {
                    continue;
                }
                int k;
                for (k = 0; k < j; k++) {
                    if (!str1[k].equals(str2[k]))
                        break;
                }
                if (k == j) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 变量表中是否有此变量名
     *
     * @param name 变量名
     * @return int 返回该变量在变量表中索引
     */
    public int haveThisName(String name) {
        for (int i = 0; i < variableName.size(); i++)
            if (variableName.get(i).equals(name))
                return i;
        return -1;
    }
}
