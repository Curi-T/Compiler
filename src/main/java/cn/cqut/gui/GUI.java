package cn.cqut.gui;

import cn.cqut.auto.JFlex.LexicalAnalyzer;
import cn.cqut.auto.JFlex.back.Token;
import cn.cqut.backup.Error;
import cn.cqut.backup.Word;
import cn.cqut.compiler.IntermediateCode.IntermediateCodeGenerator;
import cn.cqut.compiler.grammar.Grammar;
import cn.cqut.compiler.lexical.Lexical;
import cn.cqut.compiler.lexical.LexicalAnalysis;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import cn.cqut.compiler.lexical.nfa.ac.NFA_DFA_MFA_View;

import java.io.*;
import java.util.ArrayList;


/**
 * @author CuriT
 */
public class GUI extends Application {
    static File filePath;
    static String content;
    static boolean model = false;
    static ArrayList<Word> tokenss;

    ArrayList<Token> tokens = new ArrayList<>();

    //  页面
    static NFA_DFA_MFA_View nfa_dfa_mfa_view;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Pane pane = new Pane();
        Scene scene = new Scene(pane);

        primaryStage.setTitle("编译器");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);    //设置窗口最大化
        primaryStage.getIcons().add(new Image("file:" + System.getProperty("user.dir") + "\\src\\main\\resources\\icon\\icon.jpg"));
        primaryStage.setWidth(950);
        primaryStage.setHeight(620);
        primaryStage.show();

        //  背景色
        BackgroundFill backgroundFill = new BackgroundFill(Paint.valueOf("#f4f4f4"), new CornerRadii(0), Insets.EMPTY);
        Background background = new Background(backgroundFill);

        //  文本框
        TextArea code = new TextArea();
        TextArea aCode = new TextArea();
        code.setLayoutX(30);
        code.setLayoutY(40);
        code.maxWidthProperty().bind(primaryStage.widthProperty().divide(2).subtract(60));
        code.minHeightProperty().bind(primaryStage.heightProperty().subtract(100));
        code.maxHeightProperty().bind(primaryStage.heightProperty().subtract(100));
        code.setStyle("-fx-font-size: 20px");
        pane.getChildren().add(code);

        TextArea codeRightAbove = new TextArea();
        codeRightAbove.setEditable(false);
        codeRightAbove.layoutXProperty().bind(primaryStage.widthProperty().divide(2));
        codeRightAbove.setLayoutY(40);
        codeRightAbove.maxWidthProperty().bind(primaryStage.widthProperty().divide(2).subtract(55));
        codeRightAbove.minHeightProperty().bind(primaryStage.heightProperty().divide(3).multiply(2).subtract(70));
        codeRightAbove.maxHeightProperty().bind(primaryStage.heightProperty().divide(3).multiply(2).subtract(60));
        codeRightAbove.setStyle("-fx-font-size: 20px");
        pane.getChildren().add(codeRightAbove);

        TextArea codeRightBelow = new TextArea();
        codeRightBelow.setEditable(false);
        codeRightBelow.layoutXProperty().bind(primaryStage.widthProperty().divide(2));
        codeRightBelow.layoutYProperty().bind(primaryStage.heightProperty().divide(3).multiply(2));
        codeRightBelow.maxWidthProperty().bind(primaryStage.widthProperty().divide(2).subtract(55));
        codeRightBelow.minHeightProperty().bind(primaryStage.heightProperty().divide(3).subtract(60));
        codeRightBelow.maxHeightProperty().bind(primaryStage.heightProperty().divide(3).subtract(80));
        codeRightBelow.setStyle("-fx-font-size: 20px");
        pane.getChildren().add(codeRightBelow);

        Button openFile = new Button("打开");
        openFile.setBackground(background);
        Button saveFile = new Button("保存");
        saveFile.setBackground(background);
        Button saveAsFile = new Button("另存为");
        saveAsFile.setBackground(background);
        GridPane gridPane = new GridPane();
        gridPane.add(openFile, 0, 1);
        gridPane.add(saveFile, 0, 2);
        gridPane.add(saveAsFile, 0, 3);
        TitledPane file = new TitledPane("test", gridPane);

        file.setExpanded(false);
        pane.getChildren().add(file);

        Button buttonFile = new Button("文件");
        buttonFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (file.isExpanded())
                    file.setExpanded(false);
                else
                    file.setExpanded(true);
            }
        });
        buttonFile.setMinSize(80, 30);
        buttonFile.setBackground(background);
        pane.getChildren().add(buttonFile);

        Button lexicalAnalysis = new Button("词法分析");
        lexicalAnalysis.setMinSize(80, 30);
        lexicalAnalysis.setLayoutX(80);
        lexicalAnalysis.setBackground(background);
        pane.getChildren().add(lexicalAnalysis);

        Button syntaxAnalysis = new Button("自动词法");
        syntaxAnalysis.setMinSize(80, 30);
        syntaxAnalysis.setLayoutX(160);
        syntaxAnalysis.setBackground(background);
        pane.getChildren().add(syntaxAnalysis);

        Button nfa = new Button("NFA");
        nfa.setMinSize(80, 30);
        nfa.setLayoutX(240);
        nfa.setBackground(background);
        pane.getChildren().add(nfa);

        Button middleCode = new Button("语法分析");
        middleCode.setMinSize(80, 30);
        middleCode.setLayoutX(325);
        middleCode.setBackground(background);
        pane.getChildren().add(middleCode);

        Button targetCode = new Button("中间代码生成");
        targetCode.setMinSize(80, 30);
        targetCode.setLayoutX(425);
        targetCode.setBackground(background);
        pane.getChildren().add(targetCode);

        //  切换输入模式和读取模式
        Button change = new Button("");
        change.setMinSize(10, 40);
        change.setLayoutX(6);
        change.setLayoutY(180);
        pane.getChildren().add(change);

        Text modelShow = new Text("W");
        modelShow.setLayoutX(8);
        modelShow.setLayoutY(240);
        pane.getChildren().add(modelShow);

        change.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (model) {
                    model = false;
                    modelShow.setText("W");
                } else {
                    model = true;
                    modelShow.setText(" F");
                }
                change.setText("");
            }
        });
        //  文件加载
        openFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                file.setExpanded(false);    //  收起折叠栏
                model = true;
                modelShow.setText("F");

                FileChooser fileChooser = new FileChooser();// 调用资源管理器，打开指定文件
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.c) (*.txt)", "*.c", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                try {
                    fileChooser.setInitialDirectory(new File(new File("").getCanonicalPath() + "\\src\\main\\resources\\testFile"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file1;
                if ((file1 = fileChooser.showOpenDialog(primaryStage)) != null) {
                    String s = "";
                    filePath = file1;
                    try {
                        FileInputStream fis = new FileInputStream(file1);
                        primaryStage.setTitle("编译器    " + file1.getName());
                        InputStreamReader read = new InputStreamReader(fis, "UTF-8");
                        BufferedReader br = new BufferedReader(read);
                        String str = null;
                        while ((str = br.readLine()) != null) {
                            s += str + "\n";
                        }
                        br.close();
                        read.close();
                        fis.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    content = s;
                    aCode.setText(s);
                    String s1 = "";
                    int line = 1;
                    s1 += line + "\t";
                    char ch;
                    for (int i = 0; i < s.length(); i++) {
                        ch = s.charAt(i);
                        if (ch == '\n') {
                            line++;
                            s1 += ch;
                            s1 += line + "\t";
                        } else {
                            s1 += ch;
                        }
                    }
                    code.setText(s1);
                }
            }
        });

        //  文件保存
        saveFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                file.setExpanded(false);    //  收起折叠栏
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName("test.txt");
                Boolean b = false;
                if (filePath != null || (filePath = fileChooser.showSaveDialog(primaryStage)) != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(filePath);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                        osw.write(code.getText());
                        osw.close();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //  文件另存为
        saveAsFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                file.setExpanded(false);    //  收起折叠栏

                FileChooser fileChooser = new FileChooser();// 调用资源管理器
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT、C files (*.c) (*.txt)", "*.c", "*.txt");
                fileChooser.getExtensionFilters().add(extFilter);
                try {
                    fileChooser.setInitialDirectory(new File(new File("").getCanonicalPath() + "\\src\\main\\resources\\testFile"));
                } catch (IOException e) {
                    e.printStackTrace();
                            
                }
                fileChooser.setInitialFileName("test.txt");

                File file1;
                if ((file1 = fileChooser.showSaveDialog(primaryStage)) != null) {
                    try {
                        FileOutputStream fos = new FileOutputStream(file1);
                        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                        osw.write(code.getText());
                        osw.close();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        //  功能实现
        //  词法分析
        lexicalAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                codeRightAbove.setText("");
                codeRightBelow.setText("");
                Lexical la = new LexicalAnalysis();
                if (model) {
                    la.identify(content);
                } else {
                    la.identify(code.getText());
                }
                String s1 = "";
                tokenss = la.getWords();
                for (Word word : la.getWords()) {
                    s1 += "line：" + word.getLine() + "\t\t" + word.getId() + "\t\t" + word.getValue() + "" + "\n";
                }
                String s2 = "";
                for (Error error : la.getErrors()) {
                    s2 += "line：" + error.getLine() + "\t\t" + error.getWord() + "\t\t" + error.getErrorInfo() + "\n";
                }
                codeRightAbove.setText(s1);
                codeRightBelow.setText(s2);
            }
        });

        //  自动词法分析
        syntaxAnalysis.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
                lexicalAnalyzer.lexicalAnalyzer(aCode, codeRightAbove, codeRightBelow);
                tokens = lexicalAnalyzer.getTokens();

//                AutoLexical autoLexical = new AutoLexical(model, content, code);
//                autoLexical.myParse(codeRightAbove,codeRightBelow);
            }
        });


        //  nfa转换
        nfa.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                nfa_dfa_mfa_view = new NFA_DFA_MFA_View();
            }
        });


        /**
         * 语法分析
         */
        middleCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Grammar grammar = new Grammar(tokens);
                grammar.myParser(codeRightAbove, codeRightBelow);
            }
        });

        /**
         * 中间代码生成
         */
        targetCode.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("\n\n\n");
                IntermediateCodeGenerator icg = new IntermediateCodeGenerator(tokens);
                icg.myParse(codeRightAbove, codeRightBelow);
            }
        });


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (nfa_dfa_mfa_view != null) {
                    nfa_dfa_mfa_view.getState().close();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}