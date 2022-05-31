package cn.cqut.auto.JFlex.back;

import lombok.ToString;

@ToString
public class Token {
	
	public int tokenCode;
	public String val;
	public int line;
	public int column;
	public Token(int tokenCode, String val, int line, int column) {
		this.tokenCode = tokenCode;
		this.val = val;
		this.column = column;
		this.line = line;
	}

	public int getTokenCode() {
		return tokenCode;
	}
	
	public String getValue() {
		return val;
	}
}
