package cn.cqut.compiler.IntermediateCode;

import cn.cqut.auto.JFlex.back.*;

import java.util.ArrayList;

/**
 * 中间代码生成
 */
public class Parse {
    /**
     * token表
     */
    private final ArrayList<Token> list;
    /**
     * token表索引，标识当前token位置
     */
    private int index = 0;
    /**
     * 当前token信息
     */
    private Token token;
    /**
     * 当前token的行信息
     */
    private int line;
    /**
     * 错误信息
     */
    private StringBuilder errorInfo = new StringBuilder();
    /**
     * 错误标志位：false为未发生错误，true为发生错误
     */
    private boolean err = false;

    /**
     * 大括号的开始与结束 如：for语句的开始与结束，main函数的开始与结束
     */
    private final ArrayList<String> now = new ArrayList<>();

    private final ArrayList<Integer> lay = new ArrayList<>();

    private int nowLay = 0;
    /**
     * 中间代码、四元式
     */
    private final ArrayList<String[]> code = new ArrayList<>();
    /**
     * 常量表
     */
    private final ConstantTable constantTable = new ConstantTable();
    /**
     * 变量表
     */
    private final VariableTable variableTable = new VariableTable();
    /**
     * 函数表、方法表
     */
    private final FunctionTable functionTable = new FunctionTable();
    /**
     * 已经产生四元式的条数
     */
    private int NXQ = 0;
    /**
     * 临时变量命名变量
     */
    private int varName = 0;//
    /**
     * 参数值信息存放
     */
    private ArrayList<String[]> canShuValue = new ArrayList<>();

    /**
     * 中间代码生成构造器：
     *
     * @param list token表
     */
    public Parse(ArrayList<Token> list) {
        this.list = list;
        if (hasNextToken()) {
            getNextToken();
        }
        lay.add(0);
        ArrayList<Integer> a = new ArrayList<>();
        a.add(sym.FLOAT);
        functionTable.addFunction("write", sym.VOID, a, new ArrayList<>());
        functionTable.addFunction("read", sym.FLOAT, new ArrayList<>(), new ArrayList<>());
    }

    /**
     * 获取中间代码
     *
     * @return ArrayList<String [ ]>
     */
    public ArrayList<String[]> getCode() {
        return code;
    }

    /**
     * 获取错误信息
     *
     * @return String
     */
    public String getErrorInfo() {
        return errorInfo.toString();
    }

    /**
     * 开始生成中间代码
     */
    public void start() {
        if (token == null) {
            return;
        }
        program();
        if (hasNextToken()) {
            err = true;
        }
    }

    /**
     * 获取下一个token
     */
    private void getNextToken() {
        if (hasNextToken()) {
            token = list.get(index);
            line = token.line;
            index++;
        } else {
            line = token.line;
            token = null;
        }
    }

    /**
     * 是否有下一个token
     *
     * @return boolean
     */
    private boolean hasNextToken() {
        if (index >= list.size()) {
            token = null;
            return false;
        }
        return true;
    }

    /**
     * 寻找到下一行
     *
     * @return boolean
     */
    private boolean nextLine() {
        if (token == null) {
            err = true;
            return false;
        }
        int now = line;
        while (token.line == now) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return false;
            }
        }
        return true;
    }

    /**
     * 寻找至 str 开头的 token
     *
     * @param str 字符串
     */
    private void nextSymbol(String str) {
        if (token == null) {
            err = true;
            return;
        }
        while (!token.val.equals(str)) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
    }

    //p:6 q:5
    //6:(,,,4)
    //5:(,,,2)
    //4:(,,,3)
    //3:(,,,0)
    //2:(,,,0)
    private int merge(int p, int q) {
        int r = p;
        if (p == -1)
            return q;
        for (; ; ) {
            int ind = Integer.valueOf(code.get(p)[3]);
            if (ind == 0)
                break;
            p = ind;
        }
        code.get(p)[3] = q + "";
        return r;
    }
    //  第p条四元式的出口为val，即应跳转至val条语句

    /**
     * 回填四元式，第p条四元式的出口为val，即应跳转至val条语句
     *
     * @param p   要回填的四元式的位置
     * @param val 应填入的跳转位置
     */
    private void backPatch(int p, int val) {
        for (; ; ) {
            int ind = Integer.parseInt(code.get(p)[3]);
            code.get(p)[3] = val + "";
            if (ind == 0)
                break;
            p = ind;
        }
    }

    /**
     * 生成四元式
     *
     * @param op     运算符
     * @param agr1   参数 1
     * @param arg2   参数 2
     * @param result 结果
     */
    private void getQuaternionCode(String op, String agr1, String arg2, String result) {
        String[] arr;
        if (op.equals("call")) {
            arr = new String[6];//  0, 1, 2, 3返回值存放的变量, 4层级, 5参数格式
            generateCodeNode(op, agr1, arg2, result, arr);
            arr[5] = functionTable.parameterList.get(functionTable.haveThis(agr1)).size() + "";
        } else {
            arr = new String[5];
            generateCodeNode(op, agr1, arg2, result, arr);
        }
        code.add(arr);
        NXQ++;
    }

    /**
     * 产生四元式节点
     *
     * @param op     运算符
     * @param agr1   参数 1
     * @param arg2   参数 2
     * @param result 结果
     * @param arr    节点存放于数组
     */
    private void generateCodeNode(String op, String agr1, String arg2, String result, String[] arr) {
        arr[0] = op;
        System.out.print("(" + op + ",");
        arr[1] = agr1;
        System.out.print(agr1 + ",");
        arr[2] = arg2;
        System.out.print(arg2 + ",");
        arr[3] = result;
        System.out.println(result + ")");
        arr[4] = getLay();
    }

    /**
     * 新建临时变量，并加入变量表中，命名为：_var(N)_
     *
     * @param type 变量的类型，种别码
     * @return int 返回临时变量在变量表中的位置，即索引
     */
    private int newTemp(int type) {
        int i = variableTable.addVariable("_var" + varName + "_", type, "", "0");
        varName++;
        return i;
    }

    /**
     * 计算获得层级
     *
     * @return String 返回层级表示字符串形式
     */
    private String getLay() {
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < lay.size() - 1; i++)
            str.append(lay.get(i) + "/");
        str.append(lay.get(lay.size() - 1));
        return str.toString();

    }

    /**
     * 语法分析主程序
     */
    private void program() {
        if (token == null)
            return;
        System.out.println("程序");
        statement();/*声明*/
        nextSymbol("main");
        if (err)
            return;
        if (token.tokenCode == sym.MAIN) {/*main*/
            getQuaternionCode("main", "", "", "");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        } else {
            err = true;
            return;
        }
        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            }
        }
        if (token.tokenCode == sym.getCode(")")) {
            now.add("main");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
        nextSymbol("{");
        if (err)
            return;
        int[] returnChain = {-1};
        //程序段
        programSegment(null, null, returnChain);
        getQuaternionCode("sys", "", "", "");
        if (err)
            return;

        //函数
        while (token != null)
            functionDefinition();
    }

    /**
     * 声明分析函数
     */
    private void statement() {
        String name;//当前
        int type;//当前token类型
        String val; //  当前token的值
        System.out.println("声明");
        if (token == null)
            return;
        if (token.tokenCode == sym.CONST) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
            variableDefinition(true);
            if (err)
                return;
            statement();
            return;
        }
        if (token.tokenCode == sym.VOID || token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            type = token.tokenCode;
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
            if (token.tokenCode == sym.IDENTIFIER) {
                name = token.val;
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
            } else {
                nextLine();
                if (err)
                    return;
                statement();
                return;
            }

            if (token.tokenCode == sym.getCode("=")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
                val = token.val;
                if (constantTable.haveThis(name) == -1 && variableTable.haveThisNameInLayer(name, getLay()) == -1 && functionTable.haveThis(name) == -1) {
                    variableTable.addVariable(name, type, val, getLay());
                } else {
                    if (variableTable.haveThisNameInLayer(name, getLay()) != -1)
                        errorInforPrint(":error5：变量重复定义\n");
                    else
                        errorInforPrint(":error31：变量与常量或函数重名\n");

                }
                String[] pp = B();
                int ind = variableTable.haveThisNameInUpLayer(name, getLay());
                if (pp[0].equals("0")) {
                    getQuaternionCode("=", variableTable.variableName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, variableTable.value.get(Integer.parseInt(pp[1])));
                } else if (pp[0].equals("2")) {
                    getQuaternionCode("=", constantTable.constantName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, constantTable.value.get(Integer.parseInt(pp[1])));
                } else {
                    getQuaternionCode("=", pp[1], "", name);
                    variableTable.value.set(ind, pp[1]);
                }
                if (err)
                    return;
                if (token.tokenCode == sym.getCode(";")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        return;
                    }
                }
                statement();
            } else if (token.tokenCode == sym.getCode("(")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
                ArrayList<Integer> parameter = new ArrayList<>();
                formalParameterList(parameter);
                if (constantTable.haveThis(name) == -1 && variableTable.haveThisNameInLayer(name, getLay()) == -1 && functionTable.haveThis(name) == -1)
                    functionTable.addFunction(name, type, parameter, new ArrayList<Integer>());
                else {
                    if (functionTable.haveThis(name) != -1)
                        errorInforPrint(":error3：函数重复声明\n");
                    else
                        errorInforPrint(":error32：函数与变量或常量重名\n");
                }
                if (err)
                    return;

                if (token.tokenCode == sym.getCode(")")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return;
                    }
                } else {
                    nextLine();
                    if (err)
                        return;
                    statement();
                    return;
                }
                if (token.tokenCode == sym.getCode(";")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        return;
                    }
                }
                statement();
            } else if (token.tokenCode == sym.getCode(";")) {
                if (constantTable.haveThis(name) == -1 && variableTable.haveThisNameInLayer(name, getLay()) == -1 && functionTable.haveThis(name) == -1)
                    variableTable.addVariable(name, type, null, getLay());
                else {
                    if (variableTable.haveThisNameInLayer(name, getLay()) != -1)
                        errorInforPrint(":error4：变量重复定义\n");
                    else
                        errorInforPrint(":error33：变量与常量或函数重名\n");
                }
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    return;
                }
                statement();
            } else {
                nextLine();
            }
        } else {
        }
    }

    /**
     * 程序段
     *
     * @param breakChain    break 链
     * @param continueChain continent 链
     * @param returnChain   return 链
     * @return
     */
    private int programSegment(int[] breakChain, int[] continueChain, int[] returnChain) {
        System.out.println("程序段");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.getCode("{")) {
            if (now.size() == 0) {
                err = false;
                return -1;
            }
            lay.add(nowLay);

            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
            //记录该条语句是否有退出的链
            int[] chain = {-1};
            //记录这个代码段内需要退出的链
            int c = -1;
            while (token != null && token.tokenCode != sym.getCode("}")) {
                //
                if (!someSentence(chain, breakChain, continueChain, returnChain))
                    break;
                if (chain[0] != -1) {
                    // 回填，将NXQ即当前新语句回填至假出口
                    backPatch(chain[0], NXQ);
                    chain[0] = -1;
                }
                if (err) {
                    return c;
                }
            }
            nextSymbol("}");
            if (err) {
                err = false;
                return -1;
            }
            nowLay = lay.remove(lay.size() - 1) + 1;
            if (now.size() == 0) {
                return -1;
            }
            if (hasNextToken()) {
                getNextToken();
            } else {
                return c;
            }
            return c;
        } else {
            return -1;
        }
    }

    /**
     * 函数定义
     */
    private void functionDefinition() {
        int index = -1;
        int type = -1;
        System.out.println("函数定义");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR || token.tokenCode == sym.VOID) {
            type = token.tokenCode;
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
        if (token.tokenCode == sym.IDENTIFIER) {
            getQuaternionCode(token.val, "", "", "");
            index = functionTable.haveThis(token.val);
            if (index == -1)
                errorInforPrint(":error6：函数未声明\n");
            if (index != -1 && functionTable.returnType.get(index) != type)
                errorInforPrint(":error8：函数声明与定义返回值类型不一致\n");
            now.add(token.val);
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
        nextSymbol("(");
        if (err)
            return;

        if (token.tokenCode != sym.getCode("(")) {
            nextSymbol("(");
            if (err)
                return;
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            err = true;
            return;
        }
        parameterList(index);
        if (err)
            return;

        if (token.tokenCode == sym.getCode(")")) {
            if (now.size() == 0) {
                err = false;
                return;
            }
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        } else {
            nextSymbol("{");
            if (err)
                return;
        }
        int[] returnChain = {-1};
        programSegment(null, null, returnChain);
        getQuaternionCode("ret", "", "", "");
    }

    /**
     * 变量定义
     *
     * @param isConst 是否是常量的定义
     */
    private void variableDefinition(boolean isConst) {
        int type;
        String name;//变量名
        String val; //变量的值
        System.out.println("变量定义");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            type = token.tokenCode;
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        } else {
            nextLine();
            if (err)
                return;
            return;
        }
        while (true) {
            if (token.tokenCode == sym.IDENTIFIER) {
                name = token.val;
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
            } else {
                nextLine();
                if (err)
                    return;
                return;
            }
            val = initialValue(name);
            if (isConst)
                if (val != null && constantTable.haveThis(name) == -1 && variableTable.haveThisName(name) == -1 && functionTable.haveThis(name) == -1) {
                    constantTable.addConstant(name, type, val);
                } else {
                    if (val == null)
                        errorInforPrint(":error28：定义常量需赋初值\n");
                    else if (constantTable.haveThis(name) != -1)
                        errorInforPrint(":error29:常量重复定义\n");
                    else
                        errorInforPrint(":error1：常量与变量或函数同名\n");
                }//不是常量、同一个作用域的变量表中没有该变量、方法、函数表中也没有该标识符 name，将该变量加入变量表
            else if (constantTable.haveThis(name) == -1 && variableTable.haveThisNameInLayer(name, getLay()) == -1 && functionTable.haveThis(name) == -1) {
                variableTable.addVariable(name, type, val, getLay());
            } else {
                if (variableTable.haveThisNameInLayer(name, getLay()) != -1)
                    errorInforPrint(":error2：变量重复定义\n");
                else
                    errorInforPrint(":error30：变量与常量或函数同名\n");
            }
            if (err)
                return;
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    return;
                }
                break;
            } else if (token.tokenCode == sym.getCode(",")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    return;
                }
            } else {
                nextLine();
                if (err)
                    return;
                return;
            }
        }
    }

    /**
     * 各种语句
     *
     * @param chain
     * @param breakChain
     * @param continueChain
     * @param returnChain
     * @return
     */
    private boolean someSentence(int[] chain, int[] breakChain, int[] continueChain, int[] returnChain) {
        String name;//暂存标识符的名字
        System.out.println("各种语句");
        if (token == null) {
            err = true;
            return false;
        }
        if (token.tokenCode == sym.IDENTIFIER) {
            name = token.val;//保存标识符待用
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return false;
            }

            if (token.tokenCode == sym.getCode("=")) {
                if (constantTable.haveThis(name) != -1)
                    errorInforPrint(":error34：赋值语句左边不可为常量\n");
                else if (variableTable.haveThisNameInUpLayer(name, getLay()) == -1)
                    errorInforPrint(":error9：赋值语句左边标识符" + name + "未定义\n");
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }
                String[] pp = B();//等号右边值
                int ind = variableTable.haveThisNameInUpLayer(name, getLay());//name：被赋值变量的名字，ind其在变量表中的索引
                if (pp[0].equals("0")) {//  变量表中取值
                    getQuaternionCode("=", variableTable.variableName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, variableTable.value.get(Integer.parseInt(pp[1])));
                } else if (pp[0].equals("2")) {
                    getQuaternionCode("=", constantTable.constantName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, constantTable.value.get(Integer.parseInt(pp[1])));
                } else {
                    getQuaternionCode("=", pp[1], "", name);
                    variableTable.value.set(ind, pp[1]);
                }
                if (err) {
                    return false;
                }
                if (token.tokenCode == sym.getCode(";")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return false;
                    }
                } else {
                    nextLine();
                    if (err)
                        return false;

                    return false;
                }
            } else if (token.tokenCode == sym.getCode("(")) {
                // 查找函数表，name函数
                int index = functionTable.haveThis(name);
                if (index == -1)
                    errorInforPrint(":error10：该函数未声明\n");

                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }

                actualParametersList(index, name.equals("write") || name.equals("read"));
                if (err) {
                    return false;
                }
                int rettype = functionTable.returnType.get(index);
                if (rettype == sym.VOID) {
                    getQuaternionCode("call", name, "", "");
                } else {
                    int pp = newTemp(rettype);
                    getQuaternionCode("call", name, "", variableTable.variableName.get(pp));
                }
                if (token.tokenCode == sym.getCode(")")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return false;
                    }
                } else {
                    nextLine();
                    if (err)
                        return false;
                    return false;
                }

                if (token.tokenCode == sym.getCode(";")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return false;
                    }
                } else {
                    return false;
                }

            } else {
                nextLine();
                if (err)
                    return false;
                return false;
            }
        } else if (token.tokenCode == sym.IF) {
            now.add(token.val);
            chain[0] = ifSentence(breakChain, continueChain, returnChain);
        } else if (token.tokenCode == sym.FOR) {
            now.add(token.val);
            chain[0] = forSentence(returnChain);
        } else if (token.tokenCode == sym.WHILE) {
            now.add(token.val);
            chain[0] = whileSentence(returnChain);
        } else if (token.tokenCode == sym.DO) {
            now.add("do-while");
            chain[0] = doWhileSentence(returnChain);
        } else if (token.tokenCode == sym.RETURN) {
            returnChain[0] = NXQ;
            returnSentence();
        } else if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            variableDefinition(false);
        } else if (token.tokenCode == sym.BREAK) {
            int i;
            for (i = now.size() - 1; i >= 0; i--)
                if (now.get(i).equals("for") || now.get(i).equals("while") || now.get(i).equals("do-while"))
                    break;
            if (i < 0)
                errorInforPrint(":error11：break语句应出现在循环内\n");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return false;
            }
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }
            } else {
                return false;
            }
            breakChain[0] = NXQ;
            getQuaternionCode("j", "", "", "0");
        } else if (token.tokenCode == sym.CONTINUE) {
            int i;
            for (i = now.size() - 1; i >= 0; i--)
                if (now.get(i).equals("for") || now.get(i).equals("while") || now.get(i).equals("do-while"))
                    break;
            if (i < 0)
                errorInforPrint(":error12：continue语句应出现在循环内\n");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return false;
            }
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }
            } else {
                return false;
            }
            continueChain[0] = NXQ;
            getQuaternionCode("j", "", "", "0");
        } else if (token.tokenCode == sym.getCode("{")) {
            now.add("子程序段");
            int[] returnChain0 = {-1};
            int[] breakChain0 = {-1};
            int[] continueChain0 = {-1};
            programSegment(breakChain0, continueChain0, returnChain0);
            if (returnChain != null) {
                if (returnChain[0] != -1 && returnChain0[0] != -1)
                    returnChain[0] = merge(returnChain[0], returnChain0[0]);
                else if (returnChain[0] == -1)
                    returnChain[0] = returnChain0[0];
            }
            if (breakChain != null) {
                if (breakChain[0] != -1 && breakChain0[0] != -1)
                    breakChain[0] = merge(breakChain[0], breakChain0[0]);
                else if (breakChain[0] == -1)
                    breakChain[0] = breakChain0[0];
                if (continueChain[0] != -1 && continueChain0[0] != -1)
                    continueChain[0] = merge(continueChain[0], continueChain0[0]);
                else if (continueChain[0] == -1)
                    continueChain[0] = continueChain0[0];
            }
        } else {
            return false;
        }
        return !err;
    }

    /**
     * 参数列表
     *
     * @param index 函数、方法的在函数表中的 索引
     */
    private void parameterList(int index) {
        ArrayList<Integer> list = null;
        int i = 0;
        if (index != -1)
            list = functionTable.parameterList.get(index);
        System.out.println("参数列表");
        if (token == null)
            return;
        int type;
        int ind = canShuValue.size() - list.size();
        while (token.tokenCode != sym.getCode(")")) {
            if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
                type = token.tokenCode;
                if (list != null && list.get(i) == token.tokenCode)
                    i++;
                else
                    errorInforPrint(":error7：定义与声明中函数参数不一致\n");
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
                if (token.tokenCode == sym.IDENTIFIER) {
                    if (constantTable.haveThis(token.val) == -1 && variableTable.haveThisNameInLayer(token.val, getLay() + "/" + nowLay) == -1 && functionTable.haveThis(token.val) == -1) {
                        String val;
                        if (canShuValue.get(ind)[0].equals("0")) {
                            val = variableTable.value.get(Integer.parseInt(canShuValue.get(ind)[1]));
                        } else if (canShuValue.get(ind)[0].equals("1")) {
                            val = canShuValue.get(ind)[1];
                        } else {
                            val = constantTable.value.get(Integer.parseInt(canShuValue.get(ind)[1]));
                        }
                        functionTable.parameterInd.get(index).add(variableTable.value.size());
                        variableTable.addVariable(token.val, type, val, getLay() + "/" + nowLay);
                        canShuValue.remove(ind);
                    } else {
                        if (variableTable.haveThisNameInLayer(token.val, getLay() + "/" + nowLay) != -1) {
                            errorInforPrint(":error21：参数名重复\n");
                        } else {
                            errorInforPrint(":error20：参数名与变量或常量名重复\n");
                        }
                    }
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return;
                    }
                } else {
                    return;
                }
                parameterList0(index);
            } else {
                return;
            }
        }
    }

    /**
     * 参数列表，多个参数时
     *
     * @param index 函数、方法的在函数表中的 索引
     */
    private void parameterList0(int index) {
        System.out.println("参数列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        } else {
        }
    }

    /**
     * 形参列表
     *
     * @param formalList 形参
     */
    private void formalParameterList(ArrayList<Integer> formalList) {
        System.out.println("形参列表");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            formalList.add(token.tokenCode);
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
            formalParameterList0(formalList);
        }
    }

    /**
     * 形参列表0
     *
     * @param formalList 参数
     */
    private void formalParameterList0(ArrayList<Integer> formalList) {
        System.out.println("形参列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
            formalParameterList(formalList);
        }
    }

    /**
     * 初始化变量
     *
     * @param name 变量名
     * @return 直接赋值。。。
     */
    private String initialValue(String name) {
        System.out.println("初值");
        if (token == null)
            return null;
        if (token.tokenCode == sym.getCode("=")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return null;
            }
            String[] pp = B();
            if (pp[0].equals("0")) {
                getQuaternionCode("=", variableTable.variableName.get(Integer.parseInt(pp[1])), "", name);
                return variableTable.value.get(Integer.parseInt(pp[1]));
            } else if (pp[0].equals("2")) {
                getQuaternionCode("=", constantTable.constantName.get(Integer.parseInt(pp[1])), "", name);
                return constantTable.value.get(Integer.parseInt(pp[1]));
            } else {
                //  直接赋值操作
                getQuaternionCode("=", pp[1], "", name);
                return pp[1];
            }
        } else {
            return null;
        }
    }

    /**
     * 方法的实参列表验证，即调用者传入的参数列表和类型是否和方法表中存储的该方法的参数列表一致
     *
     * @param index 当前方法在方法、函数表中的 索引
     * @param ts
     */
    private void actualParametersList(int index, boolean ts) {
        ArrayList<Integer> list = null;
        if (index != -1)//获取当前方法index在方法表中的 参数列表，以便进行匹配验证参数是否正确
            list = functionTable.parameterList.get(index);
        int i = 0;
        System.out.println("实参列表");
        if (token == null)
            return;
        int type0;
        while (token.tokenCode != sym.getCode(")")) {
            String[] str = B();
            int type1 = -1;
            String val;
            if (str[0].equals("0")) {
                type1 = variableTable.variableType.get(Integer.parseInt(str[1]));
                val = variableTable.variableName.get(Integer.parseInt(str[1]));
            } else if (str[0].equals("1")) {
                type1 = Integer.parseInt(str[2]);
                val = str[1];
            } else {
                type1 = constantTable.constantType.get(Integer.parseInt(str[1]));
                val = constantTable.constantName.get(Integer.parseInt(str[1]));
            }
            canShuValue.add(str);
            if (!ts) {
                if (list != null) {
                    type0 = list.get(i);
                    i++;
                    if (type0 != type1)
                        errorInforPrint(":error17：实参类型与形参不一致\n");
                } else
                    errorInforPrint(":error17：实参类型与形参不一致\n");
            }
            getQuaternionCode("para", val, "", "");
            if (token.tokenCode == sym.getCode(",")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
            } else {
                break;
            }
        }
    }

    /**
     * 实参列表0
     *
     * @param index
     */
    private void actualParametersList0(int index) {
        System.out.println("实参列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
    }

    /**
     * if语句
     *
     * @param breakChain
     * @param continueChain
     * @param returnChain
     * @return
     */
    private int ifSentence(int[] breakChain, int[] continueChain, int[] returnChain) {
        System.out.println("if语句");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.IF) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            return -1;
        }
        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        }
        int[] if_tf = J();//if语句的真假出口
        if (err) {
            return -1;
        }
        if (token.tokenCode != sym.getCode(")")) {
            nextSymbol(")");
            if (err)
                return -1;
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            err = true;
            return -1;
        }
        backPatch(if_tf[0], NXQ);
        if (token.tokenCode != sym.getCode("{")) {
            nextSymbol("{");
            if (err)
                return -1;

        }
        int[] returnChain0 = {-1};
        int[] breakChain0 = {-1};
        int[] continueChain0 = {-1};
        programSegment(breakChain0, continueChain0, returnChain0);
        if (returnChain != null) {
            if (returnChain[0] != -1 && returnChain0[0] != -1)
                returnChain[0] = merge(returnChain[0], returnChain0[0]);
            else if (returnChain[0] == -1)
                returnChain[0] = returnChain0[0];
        }
        if (breakChain != null) {
            if (breakChain[0] != -1 && breakChain0[0] != -1)
                breakChain[0] = merge(breakChain[0], breakChain0[0]);
            else if (breakChain[0] == -1)
                breakChain[0] = breakChain0[0];
            if (continueChain[0] != -1 && continueChain0[0] != -1)
                continueChain[0] = merge(continueChain[0], continueChain0[0]);
            else if (continueChain[0] == -1)
                continueChain[0] = continueChain0[0];
        }
        if (err)
            return -1;
        return elseSentence(if_tf, breakChain, continueChain, returnChain);

    }

    /**
     * else语句
     *
     * @param if_tf
     * @param breakChain
     * @param continueChain
     * @param returnChain
     * @return
     */
    private int elseSentence(int[] if_tf, int[] breakChain, int[] continueChain, int[] returnChain) {
        System.out.println("else语句");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.ELSE) {
            int q = NXQ;
            getQuaternionCode("j", "", "", "0");
            backPatch(if_tf[1], NXQ);//回填else假出口
            int returnInd = q;
            now.add("else");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
            int[] returnChain0 = {-1};
            int[] breakChain0 = {-1};
            int[] continueChain0 = {-1};
            programSegment(breakChain0, continueChain0, returnChain0);
            if (returnChain != null) {
                if (returnChain[0] != -1 && returnChain0[0] != -1) {
                } else if (returnChain[0] == -1) {
                    returnChain[0] = returnChain0[0];
                }
            }
            if (breakChain != null) {
                if (breakChain[0] != -1 && breakChain0[0] != -1)
                    breakChain[0] = merge(breakChain[0], breakChain0[0]);
                else if (breakChain[0] == -1)
                    breakChain[0] = breakChain0[0];
                if (continueChain[0] != -1 && continueChain0[0] != -1)
                    continueChain[0] = merge(continueChain[0], continueChain0[0]);
                else if (continueChain[0] == -1)
                    continueChain[0] = continueChain0[0];
            }
            return returnInd;
        } else {
            return if_tf[1];
        }
    }

    /**
     * for语句
     *
     * @param returnChain
     * @return
     */
    private int forSentence(int[] returnChain) {
        int test = -1;
        int chain = -1;
        int inc = -1;
        int[] tf = {-1, -1};
        System.out.println("for语句");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.FOR) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            return -1;
        }
        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else if (token.tokenCode == sym.IDENTIFIER) {
            String name = token.val;
            if (constantTable.haveThis(token.val) != -1)
                errorInforPrint(":error34：赋值语句左边不可为常量\n");
            else if (variableTable.haveThisNameInUpLayer(token.val, getLay()) == -1)
                errorInforPrint(":error18：变量" + token.val + "未定义\n");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
            if (token.tokenCode == sym.getCode("=")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return -1;
                }
                String[] pp = B();
                int ind = variableTable.haveThisNameInUpLayer(name, getLay());
                if (pp[0].equals("0")) {
                    getQuaternionCode("=", variableTable.variableName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, variableTable.value.get(Integer.parseInt(pp[1])));
                } else if (pp[0].equals("2")) {
                    getQuaternionCode("=", constantTable.constantName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, constantTable.value.get(Integer.parseInt(pp[1])));
                } else {
                    //  1是指简单赋值语句
                    getQuaternionCode("=", pp[1], "", name);
                    variableTable.value.set(ind, pp[1]);
                }
                if (err)
                    return -1;
                if (token.tokenCode != sym.getCode(";")) {
                    nextSymbol(";");
                    if (err)
                        return -1;
                }
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return -1;
                }
            } else {
                nextSymbol(";");
                if (err)
                    return -1;

                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return -1;
                }
            }
        }

        test = NXQ;
        if (token.tokenCode == sym.getCode(";")) {
            getQuaternionCode("j", "", "", "0");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            // tf 为真出口和假出口在中间代码的位置、索引
            tf = J();
            if (err) {
                return -1;
            }
            chain = tf[1];
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return -1;
                }
            } else {
                nextSymbol(")");
                if (err)
                    return -1;
            }
        }
        //后面还要给赋值语句中变量的值写到变量表里
        //记录循环开头的四元表达式的 索引
        inc = NXQ;
        if (token.tokenCode == sym.getCode(")")) {
            getQuaternionCode("j", "", "", test + "");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else if (token.tokenCode == sym.IDENTIFIER) {
            String name = token.val;
            if (constantTable.haveThis(token.val) != -1)
                errorInforPrint(":error34：赋值语句左边不可为常量\n");
            else if (variableTable.haveThisNameInUpLayer(token.val, getLay()) == -1 && constantTable.haveThis(token.val) == -1)
                errorInforPrint(":error22：变量" + token.val + "未定义\n");

            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
            if (token.tokenCode == sym.getCode("=")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return -1;
                }
                String[] pp = B();
                int ind = variableTable.haveThisNameInUpLayer(name, getLay());
                if (pp[0].equals("0")) {
                    getQuaternionCode("=", variableTable.variableName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, variableTable.value.get(Integer.parseInt(pp[1])));
                } else if (pp[0].equals("2")) {
                    getQuaternionCode("=", constantTable.constantName.get(Integer.parseInt(pp[1])), "", name);
                    variableTable.value.set(ind, constantTable.value.get(Integer.parseInt(pp[1])));
                } else {
                    getQuaternionCode("=", pp[1], "", name);
                    variableTable.value.set(ind, pp[1]);
                }
                if (err)
                    return -1;
                getQuaternionCode("j", "", "", test + "");
                if (token.tokenCode == sym.getCode(")")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        } else {
            nextSymbol(")");
            if (err)
                return -1;

            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        }
        if (tf[0] != -1)
            backPatch(tf[0], NXQ);
        else
            backPatch(test, NXQ);
        int[] breakChain = {-1};
        int[] continueChain = {-1};
        int[] returnChain0 = {-1};
        programSegment(breakChain, continueChain, returnChain0);
        if (returnChain[0] != -1 && returnChain0[0] != -1)
            returnChain[0] = merge(returnChain[0], returnChain0[0]);
        else if (returnChain[0] == -1)
            returnChain[0] = returnChain0[0];
        getQuaternionCode("j", "", "", inc + "");
        if (continueChain[0] != -1)
            backPatch(continueChain[0], test);
        int c = chain;
        if (breakChain[0] != -1 && c != -1)
            return merge(c, breakChain[0]);
        else if (breakChain[0] == -1 && c != -1)
            return c;
        else if (breakChain[0] != -1 && c == -1)
            return breakChain[0];
        else
            return -1;
    }

    /**
     * while语句
     *
     * @param returnChain
     * @return
     */
    private int whileSentence(int[] returnChain) {
        int test = -1;
        System.out.println("while语句");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.WHILE) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            return -1;
        }
        if (token.tokenCode != sym.getCode("(")) {
            nextSymbol("(");
            if (err)
                return -1;

        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            err = true;
            return -1;
        }
        test = NXQ; //保存当前四元式的最新位置
        int[] tf = J();
        if (err) {
            return -1;
        }
        if (token.tokenCode != sym.getCode(")")) {
            nextSymbol(")");
            if (err)
                return -1;

        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            err = true;
            return -1;
        }
        backPatch(tf[0], NXQ);
        int[] breakChain = {-1};
        int[] continueChain = {-1};
        int[] returnChain0 = {-1};
        programSegment(breakChain, continueChain, returnChain0);
        if (returnChain[0] != -1 && returnChain0[0] != -1)
            returnChain[0] = merge(returnChain[0], returnChain0[0]);
        else if (returnChain[0] == -1)
            returnChain[0] = returnChain0[0];
        getQuaternionCode("j", "", "", test + "");
        if (continueChain[0] != -1)
            backPatch(continueChain[0], test);
        int chain = tf[1];
        if (breakChain[0] != -1)
            chain = merge(breakChain[0], chain);
        return chain;
    }

    /**
     * doWhile语句
     *
     * @param returnChain
     * @return
     */
    private int doWhileSentence(int[] returnChain) {
        System.out.println("do-while语句");
        if (token == null)
            return -1;
        if (token.tokenCode == sym.DO) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            return -1;
        }
        int head = NXQ;
        int[] breakChain = {-1};
        int[] continueChain = {-1};
        int[] returnChain0 = {-1};
        programSegment(breakChain, continueChain, returnChain0);
        if (returnChain[0] != -1 && returnChain0[0] != -1)
            returnChain[0] = merge(returnChain[0], returnChain0[0]);
        else if (returnChain[0] == -1)
            returnChain[0] = returnChain0[0];

        if (err) {
            return -1;
        }
        if (token.tokenCode == sym.WHILE) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            nextSymbol("(");
            if (err)
                return -1;
        }

        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            nextLine();
            if (err)
                return -1;
            return -1;
        }
        if (continueChain[0] != -1)
            backPatch(continueChain[0], NXQ);
        //  tf  真出口和假出口
        int[] tf = J();
        int chain = tf[1];
        if (breakChain[0] != -1)
            chain = merge(breakChain[0], chain);
        backPatch(tf[0], head);
        if (err) {
            return -1;
        }
        if (token.tokenCode == sym.getCode(")")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        } else {
            nextLine();
            if (err)
                return -1;
            return -1;
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return -1;
            }
        }
        return chain;
    }

    /**
     * return语句
     */
    private void returnSentence() {
        System.out.println("return语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.RETURN) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        } else {
            return;
        }
        String name = null;
        for (int i = now.size() - 1; i >= 0; i--) {
            if (!(now.get(i).equals("do-while") || now.get(i).equals("while") || now.get(i).equals("for") || now.get(i).equals("子程序段") || now.get(i).equals("else") || now.get(i).equals("if"))) {
                name = now.get(i);
                break;
            }
        }
        int type = sym.ERROR;
        if (name == null) {
            errorInforPrint(":error13：找不到return 对应的函数\n");
        } else {
            int index = functionTable.haveThis(name);
            if (index == -1)
                errorInforPrint(":error14:找不到函数？？\n");
            type = functionTable.returnType.get(index);
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (type != sym.VOID)
                errorInforPrint(":error15：缺少返回值\n");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
            }
        } else {
            String[] pp = B();
            System.out.println("B:" + pp[0] + "," + pp[1] + "," + pp[2]);
            if (pp[0].equals("0")) {
                if (variableTable.variableType.get(Integer.parseInt(pp[1])) != type) {
                    System.out.println("error16：返回数据类型与函数返回值类型不符\n");
                    return;
                }
                getQuaternionCode("ret", variableTable.variableName.get(Integer.parseInt(pp[1])), "", "");
            } else if (pp[0].equals("2")) {
                if (constantTable.constantType.get(Integer.parseInt(pp[1])) != type) {
                    System.out.println("error16：返回数据类型与函数返回值类型不符\n");
                    return;
                }
                getQuaternionCode("ret", constantTable.constantName.get(Integer.parseInt(pp[1])), "", "");
            } else {
                if (Integer.parseInt(pp[2]) != type) {
                    System.out.println("error16：返回数据类型与函数返回值类型不符\n");
                    return;
                }
                getQuaternionCode("ret", pp[1], "", "");
            }
            if (err) {
                return;
            }
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                }
            }
        }
    }

    private String[] B() {
        System.out.println("B");
        if (token == null) {
            System.out.println("表达式不完整");
            err = true;
            return null;
        }
        String[] n = C(false);
        if (err)
            return null;
        int m = B0(n);
        if (m == -1)
            return n;
        return new String[]{"0", m + "", ""};
    }

    //  运算表达式
    private int B0(String[] p) {
        System.out.println("B0");
        if (token == null) {
            return -1;
        }
        System.out.println(token.val);
        if (token.tokenCode == sym.getCode("+")) {
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'+'后面缺少参与运算的数据");
                err = true;
                return -1;
            }
            String[] re = C(false);
            int ind = -1;
            String sp;
            int tp;
            if (p[0].equals("0")) {
                sp = variableTable.variableName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            } else if (p[0].equals("1")) {
                sp = p[1];
                tp = Integer.parseInt(p[1]);
            } else {
                sp = constantTable.constantName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            }
            String sr;
            int tr;
            if (re[0].equals("0")) {
                sr = variableTable.variableName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            } else if (re[0].equals("1")) {
                sr = re[1];
                tr = Integer.parseInt(re[1]);
            } else {
                sr = constantTable.constantName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            }
            // ind是新临时变量在变量表中的位置索引
            ind = newTemp(Math.max(tr, tp));
            getQuaternionCode("+", sp, sr, variableTable.variableName.get(ind));

            if (err)
                return -1;
            int n = B0(new String[]{"0", ind + "", ""});
            if (n == -1)
                return ind;
            return n;
        } else if (token.tokenCode == sym.getCode("-")) {
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'-'后面缺少参与运算的数据");
                err = true;
                return -1;
            }
            String[] re = C(false);
            int ind = -1;
            String sp;
            int tp;
            if (p[0].equals("0")) {
                sp = variableTable.variableName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            } else if (p[0].equals("1")) {
                sp = p[1];
                tp = Integer.parseInt(p[1]);
            } else {
                sp = constantTable.constantName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            }
            String sr;
            int tr;
            if (re[0].equals("0")) {
                sr = variableTable.variableName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            } else if (re[0].equals("1")) {
                sr = re[1];
                tr = Integer.parseInt(re[1]);
            } else {
                sr = constantTable.constantName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            }
            ind = newTemp(Math.max(tr, tp));
            getQuaternionCode("-", sp, sr, variableTable.variableName.get(ind));

            if (err)
                return -1;
            int n = B0(new String[]{"0", ind + "", ""});
            if (n == -1)
                return ind;
            return n;
        } else {
            return -1;
        }

    }

    private String[] C(boolean chu) {
        System.out.println("C");
        if (token == null) {
            System.out.println("表达式不完整");
            err = true;
            return null;
        }// 表达式求值后的 值信息 各种。值类型或标识变量，值或临时变量的索引，类型种别码
        String[] re = D();
        if (err)
            return re;
        int n = C0(re);
        if (n == -1)
            return re;
        re[0] = "0";
        re[1] = n + "";
        return re;
    }

    private int C0(String[] p) {
        System.out.println("C0");
        if (token == null) {
            return -1;
        }
        if (token.tokenCode == sym.getCode("*")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'*'后面缺少参与运算的数据");
                err = true;
                return -1;
            }
            String[] re = D();
            int ind = -1;
            String sp;
            int tp;
            if (p[0].equals("0")) {
                sp = variableTable.variableName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            } else if (p[0].equals("1")) {
                sp = p[1];
                tp = Integer.parseInt(p[1]);
            } else {
                sp = constantTable.constantName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            }
            String sr;
            int tr;
            if (re[0].equals("0")) {
                sr = variableTable.variableName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            } else if (re[0].equals("1")) {
                sr = re[1];
                tr = Integer.parseInt(re[1]);
            } else {
                sr = constantTable.constantName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            }
            ind = newTemp(Math.max(tr, tp));
            getQuaternionCode("*", sp, sr, variableTable.variableName.get(ind));

            if (err)
                return -1;
            int n = C0(new String[]{"0", ind + "", ""});
            if (n == -1)
                return ind;
            return n;
        } else if (token.tokenCode == sym.getCode("/")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'/'后面缺少参与运算的数据");
                err = true;
                return -1;
            }
            String[] re = D();
            int ind = -1;
            String sp;
            int tp;
            if (p[0].equals("0")) {
                sp = variableTable.variableName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            } else if (p[0].equals("1")) {
                sp = p[1];
                tp = Integer.parseInt(p[1]);
            } else {
                sp = constantTable.constantName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            }
            String sr;
            int tr;
            if (re[0].equals("0")) {
                sr = variableTable.variableName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            } else if (re[0].equals("1")) {
                sr = re[1];
                tr = Integer.parseInt(re[1]);
            } else {
                sr = constantTable.constantName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            }
            ind = newTemp(Math.max(tr, tp));
            getQuaternionCode("/", sp, sr, variableTable.variableName.get(ind));

            if (err)
                return -1;
            int n = C0(new String[]{"0", ind + "", ""});
            if (n == -1)
                return ind;
            return n;
        } else if (token.tokenCode == sym.getCode("%")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'%'后面缺少参与运算的数据");
                err = true;
                return -1;
            }
            String[] re = D();
            int ind = -1;
            String sp;
            int tp;
            if (p[0].equals("0")) {
                sp = variableTable.variableName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            } else if (p[0].equals("1")) {
                sp = p[1];
                tp = Integer.parseInt(p[1]);
            } else {
                sp = constantTable.constantName.get(Integer.parseInt(p[1]));
                tp = variableTable.variableType.get(Integer.parseInt(p[1]));
            }
            String sr;
            int tr;
            if (re[0].equals("0")) {
                sr = variableTable.variableName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            } else if (re[0].equals("1")) {
                sr = re[1];
                tr = Integer.parseInt(re[1]);
            } else {
                sr = constantTable.constantName.get(Integer.parseInt(re[1]));
                tr = variableTable.variableType.get(Integer.parseInt(re[1]));
            }
            ind = newTemp(Math.max(tr, tp));
            getQuaternionCode("%", sp, sr, variableTable.variableName.get(ind));

            if (err)
                return -1;
            int n = C0(new String[]{"0", ind + "", ""});
            if (n == -1)
                return ind;
            return n;
        } else {
            return -1;
        }
    }

    //returnVal[1为常量；0为变量 , 常量值或变量表的下标 , 常量或变量类型]TODO
    private String[] D() {
        String[] returnVal = {"-1", "-1", "-1"};
        System.out.println("D");
        if (token == null) {
            System.out.println("表达式不完整");
            err = true;
            return null;
        }
        if (token.tokenCode == sym.getCode("(")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("括号内缺少数据");
                err = true;
                return null;
            }
            if (token.tokenCode == sym.getCode("-")) {
                if (hasNextToken())
                    getNextToken();
                else {
                    err = true;
                    return null;
                }
                if (token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.FLOATVAL) {
                    returnVal[1] = "-" + token.val;
                    if (token.tokenCode == sym.FLOATVAL)
                        returnVal[2] = sym.FLOAT + "";
                    else
                        returnVal[2] = sym.INT + "";
                    if (hasNextToken())
                        getNextToken();
                    else {
                        return returnVal;
                    }
                    returnVal[0] = "1";
                } else {
                    return null;
                }
            } else {
                returnVal = B();
                if (err)
                    return null;
                if (token == null) {
                    err = true;
                    return null;
                }
            }
            if (token == null) {
                System.out.println("表达式不完整***");
                err = true;
                return null;
            }
            if (token.tokenCode == sym.getCode(")")) {
                System.out.println(token.val);
                if (hasNextToken())
                    getNextToken();
                else
                    return null;
            } else {
                System.out.println("缺少)");
                return null;
            }
            return returnVal;
        } else if (token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.HEXINTEGERVAL) {
            returnVal[1] = token.val;
            returnVal[2] = sym.INT + "";
            if (hasNextToken())
                getNextToken();
            else {
                return returnVal;
            }
            returnVal[0] = "1";
            return returnVal;
        } else if (token.tokenCode == sym.FLOATVAL) {
            returnVal[1] = token.val;
            returnVal[2] = sym.FLOAT + "";
            if (hasNextToken())
                getNextToken();
            else {
                return returnVal;
            }
            returnVal[0] = "1";
            return returnVal;
        } else if (token.tokenCode == sym.CHARVAL) {
            returnVal[1] = token.val;
            returnVal[2] = sym.CHAR + "";
            ;
            if (hasNextToken())
                getNextToken();
            else {
                return returnVal;
            }
            returnVal[0] = "1";
            return returnVal;
        } else if (token.tokenCode == sym.IDENTIFIER) {
            int n = 3;
            int ind1 = constantTable.haveThis(token.val);
            int ind2 = variableTable.haveThisNameInUpLayer(token.val, getLay());
            int ind3 = functionTable.haveThis(token.val);
            if (ind1 != -1) {
                n = ind1;
                if (constantTable.value.get(ind1) == null)
                    errorInforPrint(":error26：常量没有赋值？\n");
            } else if (ind2 != -1) {
                n = ind2;// 变量在变量表中的索引
                if (variableTable.value.get(ind2) == null)
                    errorInforPrint(":error27：参与运算的变量未赋值\n");
            } else if (ind3 == -1 && !(token.val.equals("write") || token.val.equals("read"))) {
                errorInforPrint(":error24：标识符" + token.val + "未定义\n");
            }
            String name = token.val;//当前变量的名字或函数的名字
            getNextToken();
            int m = G(name, n);//函数返回值 临时变量 的索引
            if (err)
                return null;
            if (m == -1)
                return null;
            if (ind1 == -1)//返回值不是常量，返回的第一个字为0，变量
                returnVal[0] = "0";
            else
                returnVal[0] = "2";
            returnVal[1] = m + "";
            returnVal[2] = "";
            return returnVal;
            //这里可能有函数调用或者普通变量
        } else {
            System.out.println("运算符过多或参与运算的不是表达式、常量、变量或函数调用");
            return returnVal;
        }
    }

    private int G(String name, int n) {
        System.out.println("G");
        if (token == null) {
            err = true;
            return -1;
        }
        if (token.tokenCode == sym.getCode("(")) {
            int ind = functionTable.haveThis(name);//当前方法在方法表中的位置、索引
            if (ind == -1 && !(name.equals("write") || name.equals("read")))
                errorInforPrint(":error25：函数" + token.val + "未定义\n");
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("函数缺少参数");
                err = true;
                return -1;
            }
            actualParametersList(ind, name.equals("write") || name.equals("read"));
            if (err)
                return -1;
            int rettype = functionTable.returnType.get(ind);//方法的返回值类型
            int returnVal = -2;
            if (rettype == sym.VOID) {
                getQuaternionCode("call", name, "", "");
            } else {//pp临时变量在变量表中的所索引   tempVariableIndex
                int pp = newTemp(rettype);
                returnVal = pp;
                getQuaternionCode("call", name, "", variableTable.variableName.get(pp));
            }
            if (token == null) {
                System.out.println("表达式不完整");
                err = true;
                return -1;
            }
            if (token.tokenCode == sym.getCode(")")) {
                System.out.println(token.val);
                if (hasNextToken())
                    getNextToken();
                else
                    return -1;
                //函数返回值 临时变量 的索引
                return returnVal;
            } else {
                System.out.println("缺少)");
                getNextToken();
                return -1;
            }
        } else {
            return n;
        }
    }

    private void H() {
        System.out.println("H");
        if (token == null) {
            return;
        }
        if (token.tokenCode == sym.getCode("(") || token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.IDENTIFIER || token.tokenCode == sym.CHARVAL || token.tokenCode == sym.FLOATVAL || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL) {
            I();
        }
    }

    private void I() {
        System.out.println("I");
        if (token == null) {
            System.out.println("表达式不完整");
            err = true;
            return;
        }
        B();
        if (err)
            return;
        I0();
    }

    private void I0() {
        System.out.println("I0");
        if (token == null) {
            return;
        }

        if (token.tokenCode == sym.getCode(",")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("多余的','");
                err = true;
                return;
            }
            I();
        }
    }

    private int[] J() {
        System.out.println("J");
        if (token == null) {
            err = true;
            return null;
        }
        int[] backFill_tf = K();//真假出口应该回填的语句
        if (err)
            return null;
        int[] nn = J0(backFill_tf);
        if (nn[0] == -1) {
            return backFill_tf;
        }
        return nn;
    }

    private int[] J0(int[] mm) {
        int[] returnVal = {-1, -1};
        System.out.println("J0");
        if (token == null) {
            return returnVal;
        }
        if (token.tokenCode == sym.getCode("||")) {
            backPatch(mm[1], NXQ);
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return returnVal;
            }
            int[] nn = J();
            returnVal[1] = nn[1];
            returnVal[0] = merge(mm[0], nn[0]);
        }
        return returnVal;
    }

    private int[] K() {
        System.out.println("K");
        if (token == null) {
            err = true;
            return null;
        }
        int[] backFill_tf = L();
        if (err)
            return null;
        int[] nn = K0(backFill_tf);
        if (nn[0] == -1)
            return backFill_tf;
        return nn;
    }

    private int[] K0(int[] mm) {
        int[] returnVal = {-1, -1};
        System.out.println("K0");
        if (token == null) {
            return returnVal;
        }
        if (token.tokenCode == sym.getCode("&&")) {
            backPatch(mm[0], NXQ);
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return returnVal;
            }
            int[] nn = K();
            returnVal[0] = nn[0];
            returnVal[1] = merge(mm[1], nn[1]);
        }
        return returnVal;
    }

    private int[] L() {
        System.out.println("L");
        if (token == null) {
            err = true;
            return null;
        }
        if (token.tokenCode == sym.getCode("!")) {
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return null;
            }
            if (token.tokenCode == sym.getCode("(")) {
                if (hasNextToken())
                    getNextToken();
                else {
                    err = true;
                    return null;
                }
                int[] mm = J();
                if (token.tokenCode == sym.getCode(")")) {
                    if (hasNextToken())
                        getNextToken();
                    else {
                        err = true;
                        return null;
                    }
                }
                int i = mm[0];
                mm[0] = mm[1];
                mm[1] = i;
                return mm;
            }
            return null;
        } else if (token.tokenCode == sym.getCode("(") || token.tokenCode == sym.IDENTIFIER || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.FLOATVAL || token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.CHARVAL) {
            String[] leftVal = B();//returnVal[ , , ],leftVal
            if (err)
                return null;
            int[] returnVal = new int[]{NXQ, NXQ + 1};//当前的中间代码行和其下一行
            int[] tf_out = L0(leftVal);//leftVal为比较符号左值,nn 判断语句的中间代码，当前条和下一条
            if (tf_out[0] == -1) {
                if (leftVal[0].equals("1")) {
                    getQuaternionCode("jnz", leftVal[1], "", "0");
                    getQuaternionCode("j", "", "", "0");
                } else if (leftVal[0].equals("0")) {
                    getQuaternionCode("jnz", variableTable.variableName.get(Integer.parseInt(leftVal[1])), "", "0");
                    getQuaternionCode("j", "", "", "0");
                } else {
                    getQuaternionCode("jnz", constantTable.constantName.get(Integer.parseInt(leftVal[1])), "", "0");
                    getQuaternionCode("j", "", "", "0");
                }
                return returnVal;
            }
            return tf_out;
        } else {
            return null;
        }
    }

    private int[] L0(String[] leftVal) {
        int[] returnVal = {-1, -1};
        System.out.println("L0");
        if (token == null) {
            return returnVal;
        }
        if (token.tokenCode == sym.getCode(">") || token.tokenCode == sym.getCode("<") || token.tokenCode == sym.getCode(">=") || token.tokenCode == sym.getCode("<=") || token.tokenCode == sym.getCode("==") || token.tokenCode == sym.getCode("!=")) {
            String p = token.val;//当前token的值，即比较的符号
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return returnVal;
            }
            String[] rightVal = B();//leftVal是比较符号左边的值或者变量下标，rightVal是比较符号右边的值
            returnVal[0] = NXQ;
            returnVal[1] = NXQ + 1;

            String leftTrueVal;
            if (leftVal[0].equals("0")) {//左边是变量，取出变量值给sp
                leftTrueVal = variableTable.variableName.get(Integer.parseInt(leftVal[1]));
            } else if (leftVal[0].equals("1")) {//左边是直接值，取出值给sp
                leftTrueVal = leftVal[1];
            } else {//左边是常量，取出常量值给sp
                leftTrueVal = constantTable.constantName.get(Integer.parseInt(leftVal[1]));
            }
            String rightTrueVal;
            if (rightVal[0].equals("0")) {//右边是变量，取出变量值给sr
                rightTrueVal = variableTable.variableName.get(Integer.parseInt(rightVal[1]));
            } else if (rightVal[0].equals("1")) {//右边是直接值，取出值给sr
                rightTrueVal = rightVal[1];
            } else {//右边是常量，取出常量值给sr
                rightTrueVal = constantTable.constantName.get(Integer.parseInt(rightVal[1]));
            }
            getQuaternionCode("j" + p, leftTrueVal, rightTrueVal, "0");//生成比较的四元式
            getQuaternionCode("j", "", "", "0");//假值中间代码
            return returnVal;// 真假值出口
        } else {
            return returnVal;
        }
    }

    /**
     * 错误打印和输出
     *
     * @param errorInfo 错误信息
     */
    private void errorInforPrint(String errorInfo) {
        this.errorInfo.append(line).append(errorInfo);
        System.out.println();
    }
}




/*
main(){
	int a = 0;
	int b = 0;
	if(!1&&8>3){
		int c = 0;
	}
}
 */
//函数开始判断token==null?
//如果有空产生式,函数开始判断token==null的时候string加入->$,如果不是空产生式,则令err=true
//如果有空产生式,不能匹配任何一个的时候string加入->$,如果不是空产生式,则令err=true
//调用完判断err是不是true
//识别到一个元素就读取下一个token
//不是所有没有next都将err=true,如果是识别了最后一个终结符后的读则不用给err赋值
//a  = 1+a*3+d/b;
//(a>1+3)(b<1-4)&&(d/5)
/*
main()
{
        int  a = 1, b = 1, c=2, d = 1;
        int  x = 1;
        if  (a>b+4  &&  a<c  ||  x>2)  {
	        x=a+b*6/c-d;
	        if (a>100) { 
			a=c+10;
		} else  {
	        	x=a-b*6/d+c;
          	}
	}
}
*/