package cn.cqut.auto.JFlex.back;

import java.util.ArrayList;

public class FunctionTable {
    /**
     * 函数、方法的 名字
     */
    ArrayList<String> functionName = new ArrayList<>();
    /**
     * 函数、方法的 参数列表
     */
    public ArrayList<ArrayList<Integer>> parameterList = new ArrayList<>();//sym.……
    /**
     * 函数、方法的 参数列表
     */
    public ArrayList<ArrayList<Integer>> parameterInd = new ArrayList<>();//sym.……
    /**
     * 函数、方法的 返回值类型
     */
    public ArrayList<Integer> returnType = new ArrayList<>();

    /**
     * 添加函数表
     *
     * @param name      函数名、方法名
     * @param type      返回值类型
     * @param parameter 参数
     * @param parName   参数名
     */
    public void addFunction(String name, int type, ArrayList<Integer> parameter, ArrayList<Integer> parName) {
        functionName.add(name);
        returnType.add(type);
        parameterList.add(parameter);
        parameterInd.add(parName);
    }

    public void addParameter(int nameInd, int ind) {
        parameterInd.get(ind).add(nameInd);
    }

    /**
     * 函数表中是否有 name 函数、方法
     *
     * @param name 函数名、方法名
     * @return 返回name 函数位于函数表的位置、索引。-1代表函数表中不存在名为 name 的函数、方法
     */
    public int haveThis(String name) {
        for (int i = 0; i < functionName.size(); i++)
            if (functionName.get(i).equals(name))
                return i;
        return -1;
    }
}
