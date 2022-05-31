package cn.cqut.compiler.grammar;

import cn.cqut.auto.JFlex.back.Token;
import cn.cqut.auto.JFlex.back.sym;

import java.util.ArrayList;

public class Parser {
    /**
     * 当前token表，由类创建者提供者提供
     */
    final private ArrayList<Token> list;
    /**
     * 当前从token表中取出来的token，包含token的所有信息
     */
    private Token token;
    /**
     * 当前token位置
     */
    private int index = 0;

    final private int high = 50;
    /**
     * 语法树信息存储
     */
    private final StringBuffer syntaxTreeInfo = new StringBuffer();
    /**
     * 语法分析错误信息存储
     */
    private final StringBuffer errInfo = new StringBuffer();
    /**
     * 标志上一个单词的行数，用下一个单词的行数减去上一个单词的行数，即得到是否应该换行
     */
    private int ta2row = 0;
    /**
     * 标识程序识别过程中 是否出错；false代表未出错
     */
    private boolean err = false;
    /**
     *
     */
    private int line;
    /**
     * 装入一对大括号的开始和结束
     */
    private final ArrayList<String> now = new ArrayList<>();
    /**
     * 层级
     */
    private int layer0 = 0;

    public Parser(ArrayList<Token> list) {
        this.list = list;
        if (hasNextToken()) {
            getNextToken();
            line = token.line;
        }
    }

    public StringBuffer getSyntaxTreeInfo() {
        return syntaxTreeInfo;
    }

    public StringBuffer getErrInfo() {
        return errInfo;
    }

    /**
     * 开始语法分析
     */
    public void start() {
        if (token == null) {
            return;
        }
        program();
        if (hasNextToken()) {
            err = true;
        }
        System.out.println(err);
    }

    private void getNextToken() {
        if (hasNextToken()) {
            token = list.get(index);
            index++;
        } else {
            token = null;
        }
        assert token != null;
        line = token.line;
    }

    /**
     * 后续是否还有token
     *
     * @return true：还有；false：后面没有
     */
    private boolean hasNextToken() {
        if (index >= list.size()) {
            token = null;
            return false;
        }
        return true;
    }

    /**
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
     * 判断是否是 直到找到一个 str，从这里开始
     *
     * @param str 开头参数
     */
    private void nextSymbol(String str) {
        if (token == null) {
            err = true;
            return;
        }
        //  直到找到一个，从这里开始
        while (!token.val.equals(str)) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                return;
            }
        }
    }

    /**
     * 语法分析主程序
     */
    private void program() {
        /*程序*/
        if (token == null)
            return;
        System.out.println("程序");
        /*声明*/
        statement();
        if (!prepareInToMain())
            return;

        /*程序段*/
        programSegment();
        if (err)
            return;

        while (token != null)
            functionDefinition();/*函数定义*/
    }

    /**
     * 准备进入main前判断main(){
     *
     * @return boolean
     */
    private boolean prepareInToMain() {
        nextSymbol("main");
        //  err=false，未找到main，分析失败，否则从main开始分析
        if (err)
            return true;
        System.out.println(token.val + " and " + (token.tokenCode == sym.MAIN));

        if (token.tokenCode == sym.MAIN) {/*main*/
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":main函数缺少括号！ \n");
                err = true;
                return true;
            }
        } else {
            errInfo.append(line + 1).append(":找不到程序入口！? \n");
            err = true;
            return true;
        }
        if (token.tokenCode == sym.getCode("(")) {/*(*/
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少) \n");
            }
        } else {
            errInfo.append(line + 1).append(":main后面接括号！\n");
        }
        if (token.tokenCode == sym.getCode(")")) {/*)*/
            syntaxTreeFormat();

            syntaxTreeInfo.append("(main函数)");
            now.add("main");
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少函数主体\n");
                err = true;
                return true;
            }
        } else {
            errInfo.append(line + 1).append(":缺少)\n");
        }

        nextSymbol("{");
        return true;
    }

    /**
     * 声明分析
     */
    private void statement() {
        System.out.println("声明");
        if (token == null)
            return;
        System.out.println(token.val);
        if (token.tokenCode == sym.CONST) {
            //  常量分析
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的const关键字\n");
                err = true;
                return;
            }
            variableDefinition(true);
            if (err)
                return;
            statement();
            return;
        }
        int type;
        if (token.tokenCode == sym.VOID || token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            type = token.tokenCode;
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的数据类型！\n");
                err = true;
                return;
            }
            if (token.tokenCode == sym.IDENTIFIER) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":函数或变量声明不完整\n");
                    err = true;
                    return;
                }
            } else {
                errInfo.append(line + 1).append(":函数或变量声明错误！\n");
                nextLine();
                if (err)
                    return;

                statement();
                return;
            }

            if (token.tokenCode == sym.getCode("=")) {
                variableDeclaration(type);
            } else if (token.tokenCode == sym.getCode("(")) {
                functionDeclaration();
            } else if (token.tokenCode == sym.getCode(";")) {
                syntaxTreeFormat();
                syntaxTreeInfo.append("(变量声明)");
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    return;
                }
                statement();
            } else {
                errInfo.append(line + 1).append(":缺少分号;\n");
                nextLine();
            }
        }
    }

    /**
     * 函数声明 分析
     */
    private void functionDeclaration() {
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":函数声明不完整！\n");
            err = true;
            return;
        }
        //  形参判断分析
        formalParameterList();
        if (err)
            return;

        if (token.tokenCode == sym.getCode(")")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少分号;\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":函数声明错误！\n");
            nextLine();
            if (err)
                return;
            statement();
            return;
        }
        if (token.tokenCode == sym.getCode(";")) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(函数声明)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少分号;\n");
        }
        statement();
    }

    /**
     * 开头全局变量声明 与赋初值
     *
     * @param type
     */
    private void variableDeclaration(int type) {
        if (type == sym.VOID) {
            errInfo.append(line + 1).append(":变量类型不能为void\n");
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":变量声明不完整\n");
            err = true;
            return;
        }

        B(0, 0, 0, 0);
        if (err)
            return;

        if (token.tokenCode == sym.getCode(";")) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(变量声明)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少分号;\n");
        }
        //  继续声明
        statement();
    }


    /**
     * 程序段分析
     */
    private void programSegment() {
        /*程序段*/
        System.out.println("程序段");
        if (token.tokenCode == sym.getCode("{")) {
            syntaxTreeFormat();
            if (now.size() == 0) {
                errInfo.append(line + 1).append(":程序结构错误！\n");
                err = false;
                return;
            }
            syntaxTreeInfo.append("(").append(now.get(now.size() - 1)).append("开始)");
            layer0++;
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":不完整的程序段\n");
                err = true;
                return;
            }

            //  进入程序内部，进行内部语句的分析，程序段必须用{}包裹
            while (token != null && token.tokenCode != sym.getCode("}")) {
                if (someSentence())
                    break;
                if (err) {
                    errInfo.append(line + 1).append(":缺少}\n");
                    return;
                }
            }
            nextSymbol("}");
            //  结束main内部语句的分析
            if (err) {
                errInfo.append(line + 1).append(":缺少}\n");
                err = false;
                return;
            }

            layer0--;
            syntaxTreeFormat();
            if (now.size() == 0) {
                errInfo.append(line + 1).append(":程序结构错误！\n");
                err = false;
                return;
            }
            syntaxTreeInfo.append("(").append(now.remove(now.size() - 1)).append("结束)");
            if (hasNextToken()) {
                getNextToken();
            }
        } else {
            errInfo.append(line + 1).append(":缺少{\n");
        }
    }

    /**
     * 函数定义分析
     */
    private void functionDefinition() {
        System.out.println("函数定义");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR || token.tokenCode == sym.VOID) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":函数定义不完整\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":函数返回值错误\n");
        }
        if (token.tokenCode == sym.IDENTIFIER) {
            now.add(token.val);
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":函数定义不完整\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":函数定义错误\n");
        }
        nextSymbol("(");
        if (err)
            return;

        if (token.tokenCode != sym.getCode("(")) {
            errInfo.append(line + 1).append(":函数定义缺少括号()\n");
            nextSymbol("(");
            if (err)
                return;

        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":缺少)\n");
            err = true;
            return;
        }
        parameterList();
        if (err)
            return;

        if (token.tokenCode == sym.getCode(")")) {
            syntaxTreeFormat();
            if (now.size() == 0) {
                errInfo.append(line + 1).append(":程序结构错误！\n");
                err = false;
                return;
            }
            syntaxTreeInfo.append("(").append(now.get(now.size() - 1)).append("函数定义)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少函数体\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少)\n");
            nextSymbol("{");
            if (err)
                return;
        }
        programSegment();
    }

    /**
     * 变量定义声明
     *
     * @param isConst 是否是常量声明，false代表否
     */
    private void variableDefinition(boolean isConst) {
        /*变量定义*/
        System.out.println("变量定义");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            syntaxTreeFormat();
            if (isConst)
                syntaxTreeInfo.append("(常量定义)");
            else
                syntaxTreeInfo.append("(变量定义)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":变量定义不完整\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":变量类型错误\n");
            if (!nextLine())
                return;
            return;
        }
        while (true) {
            if (token.tokenCode == sym.IDENTIFIER) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":变量定义不完整\n");
                    err = true;
                    return;
                }
            } else {
                errInfo.append(line + 1).append(":错误的变量定义\n");
                //			err = true;
                nextLine();
                if (err)
                    return;

                return;
            }
            initialValue();
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
                errInfo.append(line + 1).append(":缺少分号;\n");
                nextLine();
                if (err)
                    return;
                return;
            }
        }
    }

    /**
     * 各种语句的分析汇总
     *
     * @return boolean 返回分析是否出错，true为分析成功
     */
    private boolean someSentence() {
        System.out.println("各种语句");
        if (token == null) {
            err = true;
            return false;
        }
        //  标识符分析
        if (token.tokenCode == sym.IDENTIFIER) {
            if (identifierAnalysis())
                return false;
        } else if (token.tokenCode == sym.IF) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(if语句)");
            now.add(token.val);
            ifSentence();
        } else if (token.tokenCode == sym.FOR) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(for语句)");
            now.add(token.val);
            forSentence();
        } else if (token.tokenCode == sym.WHILE) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(while语句)");
            now.add(token.val);
            whileSentence();
        } else if (token.tokenCode == sym.DO) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(do-while语句)");
            now.add("do-while");
            doWhileSentence();
        } else if (token.tokenCode == sym.RETURN) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(return语句)");
            returnSentence();
        } else if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            variableDefinition(false);
        } else if (token.tokenCode == sym.BREAK) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                err = true;
                return false;
            }
            if (token.tokenCode == sym.getCode(";")) {
                syntaxTreeFormat();
                syntaxTreeInfo.append("(break语句)");
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                return false;
            }
        } else if (token.tokenCode == sym.CONTINUE) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                err = true;
                return false;
            }
            if (token.tokenCode == sym.getCode(";")) {
                syntaxTreeFormat();
                syntaxTreeInfo.append("(continue语句)");
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return false;
                }
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                return false;
            }
        } else if (token.tokenCode == sym.getCode("{")) {
            now.add("子程序段");
            programSegment();
        } else {
            return false;
        }
        return err;
    }

    /**
     * 标识符开头
     *
     * @return boolean false代表失败
     */
    private boolean identifierAnalysis() {
        if (hasNextToken()) {
            getNextToken();
        } else {
            err = true;
            errInfo.append(line + 1).append(":多余的变量！\n");
            return true;
        }

        if (token.tokenCode == sym.getCode("=")) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(赋值语句)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
                errInfo.append(line + 1).append(":赋值语句不完整！\n");
                return true;
            }
            B(0, 0, 0, 0);  //  进入赋值语句
            if (err) {
                errInfo.append(line + 1).append(":缺少;\n");
                return true;
            }
            System.out.println("err      now:   " + token.val);
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return true;
                }
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                nextLine();
                if (err)
                    return true;

                return true;
            }
        } else if (token.tokenCode == sym.getCode("(")) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(函数调用)");
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":函数调用不完整\n");
                err = true;
                return true;
            }

            actualParametersList();
            if (err) {
                errInfo.append(line + 1).append(":缺少)\n");
                return true;
            }
            System.out.println("is )?  " + token.val);
            if (token.tokenCode == sym.getCode(")")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":缺少;\n");
                    err = true;
                    return true;
                }
            } else {
                errInfo.append(line + 1).append(":缺少)\n");
                nextLine();
                if (err)
                    return true;
                return true;
            }
            //  结束当前语句
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return true;
                }
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                return true;
            }

        } else {
            errInfo.append(line + 1).append(":多余的变量！\n");
            nextLine();
            if (err)
                return true;
            return true;
        }
        return false;
    }

    private void parameterList() {
        /*参数列表*/
        System.out.println("参数列表");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":形参列表不完整！\n");
                err = true;
                return;
            }
            if (token.tokenCode == sym.IDENTIFIER) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
            } else {
                errInfo.append(line + 1).append(":形参列表缺少变量名！\n");
                return;
            }
            parameterList0();
        }
    }

    private void parameterList0() {
        /*参数列表0*/
        System.out.println("参数列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的,\n");
                err = true;
                return;
            }
            parameterList();
        }
    }

    /**
     * 形参列表分析
     */
    private void formalParameterList() {
        System.out.println("形参列表");
        if (token == null)
            return;
        if (token.tokenCode == sym.INT || token.tokenCode == sym.FLOAT || token.tokenCode == sym.CHAR) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少)\n");
                err = true;
                return;
            }
            formalParameterList0();
        }
    }

    /**
     * 多个形参分析
     */
    private void formalParameterList0() {
        /*形参列表0*/
        System.out.println("形参列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的,\n");
                err = true;
                return;
            }
            formalParameterList();
        }
    }

    /**
     * 变量的初始化
     */
    private void initialValue() {
        /*初值*/
        System.out.println("初值");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode("=")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的=\n");
                err = true;
                return;
            }
            B(0, 0, 0, 0);
        }
    }

    /**
     * 方法调用 实参
     */
    private void actualParametersList() {
        /*实参列表*/
        System.out.println("实参列表");
        if (token == null)
            return;
        if (token.tokenCode == sym.IDENTIFIER || token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.FLOATVAL || token.tokenCode == sym.CHARVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.STRING) {
            B(0, 0, 0, 0);
            if (err)
                return;
            actualParametersList0();
        }
    }
    //

    /**
     * 多个实参分析
     */
    private void actualParametersList0() {
        /*实参列表0*/
        System.out.println("实参列表0");
        if (token == null)
            return;
        if (token.tokenCode == sym.getCode(",")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的，\n");
                err = true;
                return;
            }
            actualParametersList();
        }
    }

    /**
     * if 语句分析
     */
    private void ifSentence() {
        /*if语句*/
        System.out.println("if语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.IF) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的if关键字\n");
                err = true;
                return;
            }
        } else {
            return;
        }
        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少if条件\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少(\n");
        }
        J(0);
        if (err) {
            errInfo.append(line + 1).append(":缺少)\n");
            return;
        }
        if (token.tokenCode != sym.getCode(")")) {
            errInfo.append(line + 1).append(":缺少)\n");
            nextSymbol(")");
            if (err)
                return;
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":缺少if程序体\n");
            err = true;
            return;
        }
        if (token.tokenCode != sym.getCode("{")) {
            errInfo.append(line + 1).append(":多余的符号").append(token.val).append("\n");
            nextSymbol("{");
            if (err) {
                return;
            }
        }
        programSegment();
        if (err)
            return;
        elseSentence();
    }

    /**
     * else 语句分析
     */
    private void elseSentence() {
        /*else语句*/
        System.out.println("else语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.ELSE) {
            syntaxTreeFormat();
            syntaxTreeInfo.append("(else语句)");
            now.add("else");
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少else程序体\n");
                err = true;
                return;
            }
            programSegment();
        }
    }

    /**
     * for 语句分析
     */
    private void forSentence() {
        /*for语句*/
        System.out.println("for语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.FOR) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的for关键字\n");
                err = true;
                return;
            }
        } else {
            return;
        }
        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少循环条件！\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少(\n");
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                err = true;
                return;
            }
        } else if (token.tokenCode == sym.IDENTIFIER) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                err = true;
                return;
            }
            if (token.tokenCode == sym.getCode("=")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":for语句不完整\n");
                    err = true;
                    return;
                }
                B(0, 0, 0, 0);
                if (err)
                    return;
                if (token.tokenCode == sym.getCode(";")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        errInfo.append(line + 1).append(":for语句不完整\n");
                        err = true;
                        return;
                    }
                } else {
                    errInfo.append(line + 1).append(":for语句缺少;\n");
                    nextSymbol(";");
                    if (err)
                        return;
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return;
                    }
                }
            } else {
                errInfo.append(line + 1).append(":for语句初始化语句不完整\n");
                nextSymbol(";");
                if (err)
                    return;
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                    return;
                }
            }
        } else {
            errInfo.append(line + 1).append(":for语句结构错误\n");
        }
        //  for中第二个 条件判断表达式
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                err = true;
                return;
            }
        } else {
            J(0);
            if (err) {
                errInfo.append(line + 1).append(":for语句不完整\n");
                return;
            }
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":for语句不完整\n");
                    err = true;
                    return;
                }
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                nextSymbol(")");
                if (err)
                    return;
            }
        }
        //  for中第三个 表达式、赋值语句
        if (token.tokenCode == sym.getCode(")")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                err = true;
                return;
            }
        } else if (token.tokenCode == sym.IDENTIFIER) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句不完整\n");
                err = true;
                return;
            }// 开始赋值递增
            if (token.tokenCode == sym.getCode("=")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    errInfo.append(line + 1).append(":for语句不完整\n");
                    err = true;
                    return;
                }
                B(0, 0, 0, 0);
                if (err)
                    return;
                System.out.println(token.val);//    结束for语句开头，以 ) 结尾
                if (token.tokenCode == sym.getCode(")")) {
                    if (hasNextToken()) {
                        getNextToken();
                    } else {
                        err = true;
                        return;
                    }
                } else {
                    return;
                }
            } else {
                errInfo.append(line + 1).append(":for语句自增部分错误\n");
            }
        } else {
            errInfo.append(line + 1).append(":for语句自增部分错误\n");
            nextSymbol(")");
            if (err)
                return;
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":for语句程序体部分不存在\n");
                err = true;
                return;
            }
        }
        // for语句中是程序段
        programSegment();
    }

    /**
     * while 语句分析
     */
    private void whileSentence() {
        /*while语句*/
        System.out.println("while语句");
        if (token == null)
            return;
        int whileLine;
        if (token.tokenCode == sym.WHILE) {
            whileLine = token.line;
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":多余的while关键字\n");
                err = true;
                return;
            }
        } else {
            return;
        }
        if (token.tokenCode != sym.getCode("(")) {
            errInfo.append(whileLine).append(":while后应该接条件\n");
            nextSymbol("(");
            if (err)
                return;
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":while语句不完整\n");
            err = true;
            return;
        }
        J(0);
        if (err) {
            errInfo.append(line + 1).append(":while语句不完整\n");
            return;
        }
        if (token.tokenCode != sym.getCode(")")) {
            errInfo.append(line + 1).append(":while语句不完整\n");
            nextSymbol(")");
            if (err)
                return;
        }
        if (hasNextToken()) {
            getNextToken();
        } else {
            errInfo.append(line + 1).append(":for语句程序体部分不存在\n");
            err = true;
            return;
        }
        programSegment();
    }

    /**
     * doWhile 语句分析
     */
    private void doWhileSentence() {
        /*doWhile语句*/
        System.out.println("do-while语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.DO) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":do-while语句不完整\n");
                errInfo.append(line + 1).append(":do-while语句不完整\n");
                err = true;
                return;
            }
        } else {
            return;
        }
        programSegment();
        if (err) {
            errInfo.append(line + 1).append(":do-while语句不完整\n");
            return;
        }
        if (token.tokenCode == sym.WHILE) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":do-while语句不完整\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少while关键字\n");
            nextSymbol("(");
            if (err)
                return;
        }

        if (token.tokenCode == sym.getCode("(")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少循环条件\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少循环条件\n");
            nextLine();
            if (err)
                return;
            return;
        }
        J(0);
        if (err) {
            errInfo.append(line + 1).append(":缺少)\n");
            return;
        }
        if (token.tokenCode == sym.getCode(")")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                err = true;
                return;
            }
        } else {
            errInfo.append(line + 1).append(":缺少)\n");
            nextLine();
            if (err)
                return;
            return;
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
            }
        } else {
            errInfo.append(line + 1).append(":缺少;\n");
        }
    }

    /**
     * return 语句分析
     */
    private void returnSentence() {
        /*return语句*/
        System.out.println("return语句");
        if (token == null)
            return;
        if (token.tokenCode == sym.RETURN) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
                err = true;
                return;
            }
        } else {
            return;
        }
        if (token.tokenCode == sym.getCode(";")) {
            if (hasNextToken()) {
                getNextToken();
            } else {
                err = true;
            }
        } else {
            B(0, 0, 0, 0);
            if (err) {
                errInfo.append(line + 1).append(":缺少;\n");
                return;
            }
            if (token.tokenCode == sym.getCode(";")) {
                if (hasNextToken()) {
                    getNextToken();
                } else {
                    err = true;
                }
            } else {
                errInfo.append(line + 1).append(":缺少;\n");
            }
        }
    }

    /**
     * 算术表达式
     *
     * @param layer 相对层级
     */
    private void B(int i, int j, int len, int layer) {
        System.out.println("B");
        if (token == null) {
            System.out.println("表达式不完整");
            errInfo.append(line + 1).append(":表达式不完整\n");
            err = true;
            return;
        }
        C(i - len / 2, j + high, len * 3 / 4, layer + 1);
        if (err)
            return;
        B0(i + len / 2, j + high, len * 3 / 4, layer + 1);

    }

    /**
     * + -
     *
     * @param layer 相对层级
     */
    private void B0(int i, int j, int len, int layer) {
        System.out.println("B0");
        if (token == null) {
            return;
        }
        System.out.println(token.val);
        if (token.tokenCode == sym.getCode("+")) {
            System.out.println("+");
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'+'后面缺少参与运算的数据");
                errInfo.append(line + 1).append(":'+'后面缺少参与运算的数据\n");
                err = true;
                return;
            }
            B(i + len / 2, j + high, len * 3 / 4, layer + 1);
        } else if (token.tokenCode == sym.getCode("-")) {
            System.out.println("-");
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'-'后面缺少参与运算的数据");
                errInfo.append(line + 1).append(":'-'后面缺少参与运算的数据\n");
                err = true;
                return;
            }
            B(i + len / 2, j + high, len * 3 / 4, layer + 1);
        }
    }

    /**
     * 项
     *
     * @param layer 相对层级
     */
    private void C(int i, int j, int len, int layer) {
        System.out.println("C");    //  共同的标识符开头
        if (token == null) {
            System.out.println("表达式不完整");
            errInfo.append(line + 1).append(":表达式不完整\n");
            err = true;
            return;
        }
        D(i - len / 2, j + high, len * 3 / 4, layer + 1);
        if (err)
            return;
        C0(i + len / 2, j + high, len * 3 / 4, layer + 1);
    }

    /**
     * * / %
     *
     * @param layer 相对层级
     */
    private void C0(int i, int j, int len, int layer) {
        System.out.println("C0");
        if (token == null) {
            return;
        }
        if (token.tokenCode == sym.getCode("*")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'*'后面缺少参与运算的数据");
                errInfo.append(line + 1).append(":'*'后面缺少参与运算的数据\n");
                err = true;
                return;
            }
            C(i + len / 2, j + high, len * 3 / 4, layer + 1);
        } else if (token.tokenCode == sym.getCode("/")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'/'后面缺少参与运算的数据");
                errInfo.append(line + 1).append(":'/'后面缺少参与运算的数据\n");
                err = true;
                return;
            }
            C(i + len / 2, j + high, len * 3 / 4, layer + 1);
        } else if (token.tokenCode == sym.getCode("%")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("'%'后面缺少参与运算的数据");
                errInfo.append(line + 1).append(":'%'后面缺少参与运算的数据\n");
                err = true;
                return;
            }
            C(i + len / 2, j + high, len * 3 / 4, layer + 1);
        }
    }

    /**
     * 标识符开头、因子
     *
     * @param layer 相对层级
     */
    private void D(int i, int j, int len, int layer) {
        System.out.println("D");
        if (token == null) {
            System.out.println("表达式不完整");
            errInfo.append(line + 1).append(":表达式不完整\n");
            err = true;
            return;
        }
        if (token.tokenCode == sym.getCode("(")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("括号内缺少数据");
                errInfo.append(line + 1).append(":括号内缺少数据\n");
                err = true;
                return;
            }
            if (token.tokenCode == sym.getCode("-")) {
                if (hasNextToken())
                    getNextToken();
                else {
                    System.out.println("括号内缺少数据");
                    errInfo.append(line + 1).append(":括号内缺少数据\n");
                    err = true;
                    return;
                }
                if (token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.FLOATVAL) {
                    if (hasNextToken())
                        getNextToken();
                    else {
                        System.out.println("括号内缺少数据");
                        errInfo.append(line + 1).append(":括号内缺少数据\n");
                        err = true;
                        return;
                    }
                }
            } else {
                B(i, j + high, len * 3 / 4, layer + 1);
                if (err)
                    return;
                if (token == null) {
                    System.out.println("表达式不完整***");
                    errInfo.append(line + 1).append(":表达式不完整\n");
                    err = true;
                    return;
                }
            }
            if (token.tokenCode == sym.getCode(")")) {
                System.out.println(token.val);
                if (hasNextToken())
                    getNextToken();
            } else {
                System.out.println("缺少)");
                errInfo.append(line + 1).append(":缺少)\n");
            }
        } else if (token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.HEXINTEGERVAL) {
            if (hasNextToken())
                getNextToken();
        } else if (token.tokenCode == sym.FLOATVAL) {
            if (hasNextToken())
                getNextToken();
        } else if (token.tokenCode == sym.CHARVAL) {
            if (hasNextToken())
                getNextToken();
        } else if (token.tokenCode == sym.IDENTIFIER) {
            F();
        } else {
            System.out.println("运算符过多或参与运算的不是表达式、常量、变量或函数调用");
            errInfo.append(line + 1).append(":运算符过多或参与运算的不是表达式、常量、变量或函数调用\n");
        }
    }

    /**
     * 函数调用
     */
    private void F() {
        System.out.println("F");
        if (token == null) {
            System.out.println("表达式不完整");
            errInfo.append(line + 1).append(":表达式不完整\n");
            err = true;
            return;
        }
        if (token.tokenCode == sym.IDENTIFIER) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else
                return;
            G();
        } else {
            System.out.println("缺少数据");
            errInfo.append(line + 1).append(":缺少数据\n");
        }
    }

    /**
     * 函数调用
     */
    private void G() {
        System.out.println("G");
        if (token == null) {
            err = true;
            return;
        }
        if (token.tokenCode == sym.getCode("(")) {
            System.out.println(token.val);
            if (hasNextToken())
                getNextToken();
            else {
                System.out.println("函数缺少参数");
                errInfo.append(line + 1).append(":函数缺少参数\n");
                err = true;
                return;
            }
            actualParametersList();
            if (err)
                return;
            if (token == null) {
                System.out.println("表达式不完整");
                errInfo.append(line + 1).append(":表达式不完整\n");
                err = true;
                return;
            }
            if (token.tokenCode == sym.getCode(")")) {
                System.out.println(token.val);
                if (hasNextToken())
                    getNextToken();
            } else {
                System.out.println("缺少)");
                errInfo.append(line + 1).append(":缺少)\n");
                getNextToken();
            }
        }
    }

    private void I(int i, int j, int len, int layer) {
        System.out.println("I");
        if (token == null) {
            System.out.println("表达式不完整");
            errInfo.append(line + 1).append(":表达式不完整\n");
            err = true;
            return;
        }
        B(i - len / 2, j + high, len * 3 / 4, layer + 1);
        if (err)
            return;
        I0(i + len / 2, j + high, len * 3 / 4, layer + 1);
    }

    private void I0(int i, int j, int len, int layer) {
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
                errInfo.append(line + 1).append(":多余的','\n");
                err = true;
                return;
            }
            I(i + len / 2, j + high, len * 3 / 4, layer + 1);
        }
    }

    /**
     * 条件判断，布尔表达式
     *
     * @param layer 相对层级
     */
    private void J(int layer) {
        System.out.println("J");
        if (token == null) {
            err = true;
            return;
        }
        K(layer + 1);
        if (err)
            return;
        J0(layer + 1);
    }

    /**
     * 或 判断
     *
     * @param layer 相对层级
     */
    private void J0(int layer) {
        System.out.println("J0");
        if (token == null) {
            return;
        }
        if (token.tokenCode == sym.getCode("||")) {
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return;
            }
            J(layer + 1);
        }
    }

    /**
     * 布尔项
     *
     * @param layer 相对层级
     */
    private void K(int layer) {
        System.out.println("K");
        if (token == null) {
            err = true;
            return;
        }
        L(layer + 1);
        if (err)
            return;
        K0(layer + 1);
    }

    /**
     * 与 判断
     *
     * @param layer 相对层级
     */
    private void K0(int layer) {
        System.out.println("K0");
        if (token == null) {
            return;
        }
        if (token.tokenCode == sym.getCode("&&")) {
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return;
            }
            K(layer + 1);
        }
    }

    /**
     * 布尔因子
     *
     * @param layer 相对层级
     */
    private void L(int layer) {
        System.out.println("L");
        if (token == null) {
            err = true;
            return;
        }
        if (token.tokenCode == sym.getCode("!")) {
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return;
            }
            if (token.tokenCode == sym.getCode("(")) {
                if (hasNextToken())
                    getNextToken();
                else {
                    err = true;
                    return;
                }
            } else {
                System.out.println("！后应接（布尔表达式）");
                errInfo.append(line + 1).append(":！后应接（布尔表达式）\n");
            }
            J(layer + 1);
            if (token.tokenCode == sym.getCode(")")) {
                if (hasNextToken())
                    getNextToken();
                else {
                    err = true;
                }
            } else {
                System.out.println("缺少)");
                errInfo.append(line + 1).append(":缺少）\n");
            }
        } else if (token.tokenCode == sym.getCode("(") || token.tokenCode == sym.IDENTIFIER || token.tokenCode == sym.HEXINTEGERVAL || token.tokenCode == sym.OCTINTEGERVAL || token.tokenCode == sym.FLOATVAL || token.tokenCode == sym.INTEGERVAL || token.tokenCode == sym.CHARVAL) {
            B(0, 0, 0, layer + 1);
            if (err)
                return;
            L0(layer + 1);
        }
    }

    /**
     * 关系表达式
     *
     * @param layer 相对层级
     */
    private void L0(int layer) {
        System.out.println("L0");
        if (token == null) {
            return;
        }
        if (token.tokenCode == sym.getCode(">") || token.tokenCode == sym.getCode("<") || token.tokenCode == sym.getCode(">=") || token.tokenCode == sym.getCode("<=") || token.tokenCode == sym.getCode("==") || token.tokenCode == sym.getCode("!=")) {
            if (hasNextToken())
                getNextToken();
            else {
                err = true;
                return;
            }
            B(0, 0, 0, layer + 1);
        }
    }

    private void syntaxTreeFormat() {
        for (int p = 0; p < token.line - ta2row; p++)
            syntaxTreeInfo.append("\n");
        if (ta2row != token.line)
            for (int p = 0; p < layer0; p++)
                syntaxTreeInfo.append("\t");
        ta2row = token.line;
    }
}


//函数开始判断token==null?
//如果有空产生式,函数开始判断token==null的时候string加入->$,如果不是空产生式,则令err=true
//如果有空产生式,不能匹配任何一个的时候string加入->$,如果不是空产生式,则令err=true
//调用完判断err是不是true
//识别到一个元素就读取下一个token
//不是所有没有next都将err=true,如果是识别了最后一个终结符后的读则不用给err赋值
//a  = 1+a*3+d/b;
//(a>1+3)(b<1-4)&&(d/5)




