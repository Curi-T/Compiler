package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:31
 */

public class Transform {
    static String[] rwtab = new String[]{"main", "if", "then", "while", "do", "static",
            "int", "double", "struct", "break", "else",
            "long", "switch", "case", "typedef", "char",
            "return", "const", "float", "short", "continue",
            "for", "void", "sizeof"};
    String storage = "";
    String token = "", type = "", sum0 = "";
    char ch;
    int index = 0, flag = 0;
    int syn, sum = 0, row = 0;
    Float sum1;

    public Transform(String storage) {
        this.storage = storage;
    }

    public void run() {
        token = "";
        type = "";
        try {
            ch = storage.charAt(index++);
        } catch (Exception e) {

        }

        while (ch == ' ') {
            ch = storage.charAt(index++);      //去除空格符号
        }
        if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {         //可能是关键字或者自定义的标识符
            while ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_') {
                token += ch;
                ch = storage.charAt(index++);

            }
            index--;      //此次识别的最后一个字符未识别入，需要将标记退原处
            syn = 25;       //默认为识别出的字符串为自定义的标识符，种别码为25
            String s = token.toString();
            for (int i = 0; i < rwtab.length; i++) {
                if (s.equals(rwtab[i])) {
                    syn = i + 1;
                    break;        //识别出是关键字
                }
            }
        } else if ((ch >= '0' && ch <= '9')) {
            sum = 0;
            sum0 = "";
            while ((ch >= '0' && ch <= '9') || ch == '.') {
                if (ch == '.') {
                    flag = 1;
                }
                if (flag == 0) {
                    sum = sum * 10 + ch - '0';
                } else {
                    //sum=sum+ch-'0';
                    sum0 = sum0 + ch;
                    syn = 44;
                }
                ch = storage.charAt(index++);

            }
            index--;
            if (flag == 1) {
                sum1 = sum + Float.parseFloat("0" + sum0);
            } else {
                syn = 26;

            }
            flag = 0;

        } else switch (ch) {

            case '<':
                token += ch;
                ch = storage.charAt(index++);

                if (ch == '=') {
                    token += ch;
                    syn = 35;
                } else if (ch == '>') {
                    token += ch;
                    syn = 34;
                } else {
                    syn = 33;
                    index--;
                }
                break;
            case '>':
                token += ch;
                ch = storage.charAt(index++);

                if (ch == '=') {
                    token += ch;
                    syn = 37;
                } else {
                    syn = 36;
                    index--;
                }
                break;
            case '*':
                token += ch;
                ch = storage.charAt(index++);

                if (ch == '*') {
                    token += ch;
                    syn = 31;
                } else {
                    syn = 13;
                    index--;
                }
                break;
            case '=':
                token += ch;
                ch = storage.charAt(index++);

                if (ch == '=') {
                    syn = 32;
                    token += ch;
                } else {
                    syn = 38;
                    index--;
                }
                break;
            case '/':
                token += ch;
                ch = storage.charAt(index++);

                if (ch == '/') {
                    while (ch != ' ') {
                        ch = storage.charAt(index++);  //忽略掉注释，以空格为界定

                    }
                    syn = -2;
                    break;
                } else {
                    syn = 30;
                    index--;
                }
                break;
            case '+':
                syn = 27;
                token += ch;
                break;
            case '-':
                syn = 28;
                token += ch;
                break;
            case '[':
                syn = 39;
                token += ch;
                break;
            case ']':
                syn = 40;
                token += ch;
                break;
            case ';':
                syn = 41;
                token += ch;
                break;
            case '(':
                syn = 42;
                token += ch;
                break;
            case ')':
                syn = 43;
                token += ch;
                break;
            case '{':
                syn = 44;
                token += ch;
                break;
            case '}':
                syn = 45;
                token += ch;
                break;
            case '#':
                syn = 0;
                token += ch;
                break;
            case '\n':
                syn = -2;
                token += ch;
                break;
            case ',':
                syn = 46;
                token += ch;
                break;
            case '"':
                syn = 47;
                token += ch;
                break;
            case '%':
                syn = 48;
                token += ch;
                break;
            case '.':
                syn = 49;
                token += ch;
                break;
            default:
                token += ch;
                row++;
                syn = -1;
        }
    }

}
/*
 * 单词符号    种别码        单词符号    种别码
main        1            void       23
if          2            sizeof     24
then        3            ID         25
while       4            NUM        26
do          5             +         27
static      6             -         28
int         7             *         29
double      8             /         30
struct      9            **         31
break       10           ==         32
else        11           <          33
long        12           <>         34
switch      13           <=         35
case        14           >          36
typedef     15           >=         37
char        16           =          38
return      17           [          39
const       18           ]          40
float       19           ;          41
short       20           (          42
continue   21            )          43
for         22           #          0
*/

