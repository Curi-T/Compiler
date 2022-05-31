package cn.cqut.auto.JFlex.back;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class Symbol {
	HashMap<Integer, String> map = new HashMap<>();
	public Symbol() {
		try {
			Scanner input = new Scanner(new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\tokenCode\\tokenCode.txt"));
			while(input.hasNextInt()) {
				input.nextInt();
				String str = input.next();
				int n = input.nextInt();
				map.put(n, str);
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public Token symbol(int type, int yyline, int yycolumn, Object value) {
//		System.out.println(type+","+value);
		if(type == sym.IDENTIFIER && value.equals("main")) {
			return new Token(sym.MAIN,value.toString(),yyline,yycolumn);			
		}
		return new Token(type,value.toString(),yyline,yycolumn);
	}
	public Token symbol(int type, int yyline, int yycolumn) {
//		System.out.println(type+","+map.get(type));
		return new Token(type,map.get(type),yyline,yycolumn);
	}
}
