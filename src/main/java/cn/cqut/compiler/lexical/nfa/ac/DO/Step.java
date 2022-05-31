package cn.cqut.compiler.lexical.nfa.ac.DO;

import javafx.beans.property.SimpleStringProperty;

public class Step {
	private SimpleStringProperty num = new SimpleStringProperty("1 ");//序号
	private SimpleStringProperty stateStack = new SimpleStringProperty(" 1");//状态栈
	private SimpleStringProperty charStack = new SimpleStringProperty("1 ");//符号栈
	private SimpleStringProperty shizi = new SimpleStringProperty("1 ");//产生式
	private SimpleStringProperty input = new SimpleStringProperty("1 ");//输入串
	private SimpleStringProperty infor = new SimpleStringProperty(" 1");//信息
	public Step(String num, String stateStack, String charStack, String shizi, String input, String infor) {//SLR
		this.num = new SimpleStringProperty(num);
		this.stateStack = new SimpleStringProperty(stateStack);
		this.charStack = new SimpleStringProperty(charStack);
		this.shizi = new SimpleStringProperty(shizi);
		this.input = new SimpleStringProperty(input);
		this.infor = new SimpleStringProperty(infor);
	}
	public Step(String num, String charStack, String input, String infor) {//LL
		this.num = new SimpleStringProperty(num);
		this.charStack = new SimpleStringProperty(charStack);
		this.input = new SimpleStringProperty(input);
		this.infor = new SimpleStringProperty(infor);
	}
	public Step(String num, String input, String infor) {//NFADFAMFA
		this.num = new SimpleStringProperty(num);
		this.input = new SimpleStringProperty(input);
		this.infor = new SimpleStringProperty(infor);
	}
	public String toString() {
		return num.get() +"\t" + stateStack.get() +"\t" + charStack.get() +"\t" + shizi.get() +"\t" + input.get() +"\t" + infor.get();
	}
	public String getNum() {
		return num.get();
	}
	public String getStateStack() {
		return stateStack.get();
	}
	public String getCharStack() {
		return charStack.get();
	}
	public String getShizi() {
		return shizi.get();
	}
	public String getInput() {
		return input.get();
	}
	public String getInfor() {
		return infor.get();
	}
	
}
