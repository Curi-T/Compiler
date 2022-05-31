package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:31
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import com.sun.glass.events.KeyEvent;
public class Viewer2 implements ActionListener  {
    JTextField text;
    private JFrame frame;
    JTextArea area;
    private JButton be,exit;
    public Viewer2() {
        go();

    }
    private void go() {
        frame = new JFrame();
        frame.setLayout(null);
        /*frame.getContentPane().add(be,BorderLayout.EAST);
        frame.getContentPane().add(bn,BorderLayout.NORTH);*/
        init();
        frame.setBounds(720,320,770,550);
        frame.setUndecorated(true);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }
    public void init(){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("记事本程序");
        DefaultMutableTreeNode rootFirst1 = new DefaultMutableTreeNode("文件");
        DefaultMutableTreeNode rootFirst2 = new DefaultMutableTreeNode("编辑");
        DefaultMutableTreeNode rootFirst3 = new DefaultMutableTreeNode("格式");
        DefaultMutableTreeNode rootFirst4 = new DefaultMutableTreeNode("帮助");
        DefaultMutableTreeNode rootFirst5 = new DefaultMutableTreeNode("退出");
        DefaultMutableTreeNode rootSecond1 = new DefaultMutableTreeNode("新建",false);
        DefaultMutableTreeNode rootSecond2 = new DefaultMutableTreeNode("保存",false);
        DefaultMutableTreeNode rootSecond3 = new DefaultMutableTreeNode("另存为",false);
        DefaultMutableTreeNode rootSecond4 = new DefaultMutableTreeNode("打开",false);
        DefaultMutableTreeNode rootSecond5 = new DefaultMutableTreeNode("文件记录",false);
        DefaultMutableTreeNode rootSecond6 = new DefaultMutableTreeNode("交易",false);
        DefaultMutableTreeNode rootSecond7 = new DefaultMutableTreeNode("剪切",false);
        DefaultMutableTreeNode rootSecond8 = new DefaultMutableTreeNode("复制",false);
        DefaultMutableTreeNode rootSecond9 = new DefaultMutableTreeNode("粘贴",false);
        DefaultMutableTreeNode rootSecond10 = new DefaultMutableTreeNode("大字号",false);
        DefaultMutableTreeNode rootSecond11 = new DefaultMutableTreeNode("中等字号",false);
        DefaultMutableTreeNode rootSecond12 = new DefaultMutableTreeNode("小字号",false);
        DefaultMutableTreeNode rootSecond13 = new DefaultMutableTreeNode("帮助",false);
        DefaultMutableTreeNode rootSecond15 = new DefaultMutableTreeNode("关于",false);
        root.add(rootFirst1);
        root.add(rootFirst2);
        root.add(rootFirst3);
        root.add(rootFirst4);
        root.add(rootFirst5);
        rootFirst1.add(rootSecond1);
        rootFirst1.add(rootSecond2);
        rootFirst1.add(rootSecond3);
        rootFirst1.add(rootSecond4);
        rootFirst1.add(rootSecond5);
        rootFirst1.add(rootSecond6);
        rootFirst2.add(rootSecond7);
        rootFirst2.add(rootSecond8);
        rootFirst2.add(rootSecond9);
        rootFirst3.add(rootSecond10);
        rootFirst3.add(rootSecond11);
        rootFirst3.add(rootSecond12);
        rootFirst4.add(rootSecond13);
        rootFirst4.add(rootSecond15);
        //*****************

        JScrollPane scrollPane=new JScrollPane();
        area=new JTextArea(100,200);
        //area.enable(false);
        area.setLineWrap(true);
        area.setFont(new Font("宋体",Font.BOLD,18));
        area.setBorder(null);
        area.setForeground(Color.black);
        scrollPane.setViewportView(area);
        scrollPane.setVisible(true);
        scrollPane.setBounds(120,30, 490, 450);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        //frame.getContentPane().add(scrollPane,BorderLayout.CENTER);
        frame.add(scrollPane);
        //*****************
        Box box1=Box.createVerticalBox();
        text=new JTextField(12);
        JLabel lab=new JLabel("搜索你想要的功能:");
        text.setFont(new Font("宋体",Font.BOLD,15));
        be=new JButton("搜索");
        be.addActionListener(this);
        exit=new JButton("退出");
        exit.addActionListener(this);
        box1.add(lab);
        box1.add(text);
        box1.add(be);
        box1.add(new JLabel("  "));
        box1.add(exit);
        frame.add(box1);
        box1.setBounds(620, 30, 120, 150);
        //*****************
        DefaultTreeModel treeModel = new DefaultTreeModel(root);//采用树模型
        JTree tree = new JTree(treeModel);
        frame.getContentPane().add(tree,BorderLayout.WEST);//tree.setBackground(Color.black);tree.setOpaque(true);
        frame.add(tree);
        tree.setBounds(10, 30, 100, 450);
        TreeSelectionModel treeSelect;
        treeSelect = tree.getSelectionModel();//获得树的选择模式
        treeSelect.setSelectionMode(4);//设置树的选择模式为多选
        tree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {

                if(!tree.isSelectionEmpty()) {//判断节点是否被选中，被选中为0，没被选中为1
                    String sum="";
                    TreePath[] selectionPath =tree.getSelectionPaths();//获取所有被选中节点的路径
                    for(int i = 0; i < selectionPath.length; i++) {
                        TreePath path = selectionPath[i];
                        Object[] obj = path.getPath();//以Object数组的形式返回该路径中所有节点的对象
                        for(int j = 0; j < obj.length; j++) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj[j];// 获得节点
                            String st=(String) node.getUserObject();
                            sum=sum+node.getUserObject();
                        }

                    }
                    area.setText(sum);
                }

            }
        });

        frame.add(tree);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o=e.getSource();
        if(o==be){
            String st=text.getText();
            String sum="";
            if(st.equals("新建")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->新建";
            }
            else if(st.equals("保存")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->保存";
            }
            else if(st.equals("另存为")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->另存为";
            }
            else if(st.equals("打开")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->打开";
            }
            else if(st.equals("文件记录")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->文件记录";
            }
            else if(st.equals("关闭")){
                sum=area.getText();
                sum=sum+"\n记事本程序->文件->关闭";
            }
            else if(st.equals("复制")){
                sum=area.getText();
                sum=sum+"\n记事本程序->编辑->复制";
            }
            else if(st.equals("剪切")){
                sum=area.getText();
                sum=sum+"\n记事本程序->编辑->剪切";
            }
            else if(st.equals("粘贴")){
                sum=area.getText();
                sum=sum+"\n记事本程序->编辑->粘贴";
            }
            else if(st.equals("大字号")){
                sum=area.getText();
                sum=sum+"\n记事本程序->格式->大字号";
            }
            else if(st.equals("小字号")){
                sum=area.getText();
                sum=sum+"\n记事本程序->格式->小字号";
            }
            else if(st.equals("中等字号")){
                sum=area.getText();
                sum=sum+"\n记事本程序->格式->中等字号";
            }
            else if(st.equals("帮助")){
                sum=area.getText();
                sum=sum+"\n记事本程序->帮助->帮助";
            }
            else if(st.equals("关于")){
                sum=area.getText();
                sum=sum+"\n记事本程序->帮助->关于";
            }
            else{
                sum="搜索的功能不存在，尝试搜索左侧目录中的内容";
            }
            area.setText(sum);
        }
        if(o==exit){
            frame.dispose();
        }

    }


}
