package cn.cqut.auto.JFlex.back;

public class sym {
	/** char:	101 */
	public static final int CHAR = 101;
	/** int:	102 */
	public static final int INT = 102;
	/** float:	103 */
	public static final int FLOAT = 103;
	/** break:	104 */
	public static final int BREAK = 104;
	/** 常量 const:	105 */
	public static final int CONST = 105;
	/** return:	106 */
	public static final int RETURN = 106;
	/** void:	107 */
	public static final int VOID = 107;
	/** continue:	108 */
	public static final int CONTINUE = 108;
	/** do:	109 */
	public static final int DO = 109;
	/** while:	110 */
	public static final int WHILE = 110;
	/** if:	111 */
	public static final int IF = 111;
	/** else:	112 */
	public static final int ELSE = 112;
	/** for:	113 */
	public static final int FOR = 113;
	/** main:	114 */
	public static final int MAIN = 114;
//	static final int WRITE = 115;
//	static final int READ = 116;
	/** int类型 integer:	400 */
	public static final int INTEGERVAL = 400;
	/** char类型:	500 */
	public static final int CHARVAL = 500;
	/** string:	600 */
	public static final int STRING = 600;
	/** 标识符:	700 */
	public static final int IDENTIFIER = 700;
	/** 浮点float:	800 */
	public static final int FLOATVAL = 800;
	/** 十六进制:	401 */
	public static final int HEXINTEGERVAL = 401;
	/** 八进制:	402 */
	public static final int OCTINTEGERVAL = 402;
	/** 错误:	-1 */
	public static final int ERROR = -1;
	public static int getCode(String str) {
		switch(str) {
			case "{":return 301;
			case "}":return 302;
			case ";":return 303;
			case ",":return 304;
			case "(":return 201;
			case ")":return 202;
			case "[":return 203;
			case "]":return 204;
			case "!":return 205;
			case "*":return 206;
			case "/":return 207;
			case "%":return 208;
			case "+":return 209;
			case "-":return 210;
			case "<":return 211;
			case "<=":return 212;
			case ">":return 213;
			case ">=":return 214;
			case "==":return 215;
			case "!=":return 216;
			case "&&":return 217;
			case "||":return 218;
			case "=":return  219;
			case "." :return 220;
			case "++" :return 221;
			case "+=":return 222;
			case "--":return 223;
			case "-=":return 224;
		}
		return -1;
	}
	public static String getMeans(int n) {
		switch(n) {
		case CHAR:return "char关键字";
		case INT:return "int关键字";
		case FLOAT:return "float关键字";
		case BREAK:return "break关键字";
		case CONST:return "const关键字";
		case RETURN:return "return关键字";
		case VOID:return "void关键字";
		case CONTINUE:return "continue关键字";
		case DO:return "do关键字";
		case WHILE:return "while关键字";
		case IF:return "if关键字";
		case ELSE:return "else关键字";
		case FOR:return "for关键字";
		case MAIN:return "主函数";
		case INTEGERVAL:return "整型数据";
		case CHARVAL:return "字符型数据";
		case STRING:return "字符串数据";
		case IDENTIFIER:return "标识符";
		case FLOATVAL:return "浮点型数据";
		case HEXINTEGERVAL:return "十六进制整型数据";
		case OCTINTEGERVAL:return "八进制整型数据";
		default:return null;
		}
	}
}
