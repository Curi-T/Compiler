package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:28
 */

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;


public class NFAViewer implements ActionListener {
    JTextField text1;
    private JFrame frame;
    static JTextArea area1;
    static JTextArea area2;
    static JTextArea area3;
    private JButton be, exit, get1, born1, save1, get2, born2, save2, get3, born3, save3;
    JLabel label1_s, label1_e, label2_s, label2_e, label3_s, label3_e;
    Box box0;

    public NFAViewer() {
        frame = new JFrame();
        frame.setLayout(null);
        frame.setTitle("NFA-DFA-MFA");
        init();
        frame.setBounds(200, 100, 1170, 800);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void init() {
        JPanel panel = new JPanel();
        text1 = new JTextField(20);
        JLabel label = new JLabel("请输入正规式:");
        be = new JButton("验证正规式");
        exit = new JButton("退出");
        be.addActionListener(this);
        exit.addActionListener(this);
        panel.add(label);
        panel.add(text1);
        panel.add(be);
        panel.add(exit);
        panel.setBounds(10, 10, 1150, 90);
        frame.add(panel);
        //***********************
        box0 = Box.createHorizontalBox();
        box0.setBounds(10, 110, 1150, 600);
        frame.add(box0);
        JPanel panel1 = new JPanel();
        panel1.setBorder(BorderFactory.createLineBorder(Color.black));
        panel1.setLayout(null);
        JLabel label1 = new JLabel("正规式-NFA");
        label1.setFont(new Font("宋体", Font.BOLD, 18));
        JPanel panel1_title = new JPanel();
        JLabel label1_a = new JLabel("起始状态");
        label1_a.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label1_b = new JLabel("接收符号");
        label1_b.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label1_c = new JLabel("到达状态");
        label1_c.setBorder(BorderFactory.createLineBorder(Color.black));
        panel1_title.add(label1_a);
        panel1_title.add(new JLabel("   "));
        panel1_title.add(label1_b);
        panel1_title.add(new JLabel("   "));
        panel1_title.add(label1_c);
        label1_a.setFont(new Font("宋体", Font.BOLD, 18));
        label1_b.setFont(new Font("宋体", Font.BOLD, 18));
        label1_c.setFont(new Font("宋体", Font.BOLD, 18));
        area1 = new JTextArea(20, 90);
        area1.setLineWrap(true);
        label1_s = new JLabel("开始状态集");
        label1_e = new JLabel("终结状态集");
        label1_s.setFont(new Font("宋体", Font.BOLD, 18));
        label1_e.setFont(new Font("宋体", Font.BOLD, 18));
        Box box1 = Box.createHorizontalBox();
        get1 = new JButton("读入NFA文件");
        born1 = new JButton("生成NFA");
        save1 = new JButton("保存NFA文件");
        box1.add(get1);
        box1.add(born1);
        box1.add(save1);
        label1.setBounds(5, 0, 300, 50);
        panel1.add(label1);
        panel1_title.setBounds(5, 60, 300, 30);
        panel1.add(panel1_title);
        area1.setFont(new Font("宋体", Font.BOLD, 32));
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 4, false);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBorder(border);
        scrollPane.setViewportView(area1);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(border);
        scrollPane.setBounds(5, 92, 300, 350);
        panel1.add(scrollPane);
        label1_s.setBounds(5, 445, 300, 30);
        panel1.add(label1_s);
        label1_e.setBounds(5, 475, 300, 30);
        panel1.add(label1_e);
        box1.setBounds(5, 500, 300, 90);
        panel1.add(box1);
        box0.add(panel1);
        //**************
        JPanel panel2 = new JPanel();
        panel2.setBorder(BorderFactory.createLineBorder(Color.black));
        panel2.setLayout(null);
        JLabel label2 = new JLabel("NFA-DFA");
        label2.setFont(new Font("宋体", Font.BOLD, 18));
        JPanel panel2_title = new JPanel();
        JLabel label2_a = new JLabel("起始状态");
        label2_a.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label2_b = new JLabel("接收符号");
        label2_b.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label2_c = new JLabel("到达状态");
        label2_c.setBorder(BorderFactory.createLineBorder(Color.black));
        panel2_title.add(label2_a);
        panel2_title.add(new JLabel("   "));
        panel2_title.add(label2_b);
        panel2_title.add(new JLabel("   "));
        panel2_title.add(label2_c);
        label2_a.setFont(new Font("宋体", Font.BOLD, 18));
        label2_b.setFont(new Font("宋体", Font.BOLD, 18));
        label2_c.setFont(new Font("宋体", Font.BOLD, 18));
        area2 = new JTextArea(20, 90);
        area2.setLineWrap(true);
        label2_s = new JLabel("开始状态集");
        label2_e = new JLabel("终结状态集");
        label2_s.setFont(new Font("宋体", Font.BOLD, 18));
        label2_e.setFont(new Font("宋体", Font.BOLD, 18));
        Box box2 = Box.createHorizontalBox();
        get2 = new JButton("读入DFA文件");
        born2 = new JButton("生成DFA");
        save2 = new JButton("保存DFA文件");
        box2.add(get2);
        box2.add(born2);
        box2.add(save2);
        label2.setBounds(5, 0, 300, 50);
        panel2.add(label2);
        panel2_title.setBounds(5, 60, 300, 30);
        panel2.add(panel2_title);
        area2.setFont(new Font("宋体", Font.BOLD, 32));
        Border border2 = BorderFactory.createLineBorder(new Color(0, 0, 0), 4, false);
        JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setBorder(border2);
        scrollPane2.setViewportView(area2);
        scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane2.setBorder(border2);
        scrollPane2.setBounds(5, 92, 300, 350);
        panel2.add(scrollPane2);
        label2_s.setBounds(5, 445, 300, 30);
        panel2.add(label2_s);
        label2_e.setBounds(5, 475, 300, 30);
        panel2.add(label2_e);
        box2.setBounds(5, 500, 300, 90);
        panel2.add(box2);
        box0.add(panel2);
        //*************
        JPanel panel3 = new JPanel();
        panel3.setBorder(BorderFactory.createLineBorder(Color.black));
        panel3.setLayout(null);
        JLabel label3 = new JLabel("DFA-MFA");
        label3.setFont(new Font("宋体", Font.BOLD, 18));
        JPanel panel3_title = new JPanel();
        JLabel label3_a = new JLabel("起始状态");
        label3_a.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label3_b = new JLabel("接收符号");
        label3_b.setBorder(BorderFactory.createLineBorder(Color.black));
        JLabel label3_c = new JLabel("到达状态");
        label3_c.setBorder(BorderFactory.createLineBorder(Color.black));
        panel3_title.add(label3_a);
        panel3_title.add(new JLabel("   "));
        panel3_title.add(label3_b);
        panel3_title.add(new JLabel("   "));
        panel3_title.add(label3_c);
        label3_a.setFont(new Font("宋体", Font.BOLD, 18));
        label3_b.setFont(new Font("宋体", Font.BOLD, 18));
        label3_c.setFont(new Font("宋体", Font.BOLD, 18));
        area3 = new JTextArea(20, 90);
        area3.setLineWrap(true);
        label3_s = new JLabel("开始状态集");
        label3_e = new JLabel("终结状态集");
        label3_s.setFont(new Font("宋体", Font.BOLD, 18));
        label3_e.setFont(new Font("宋体", Font.BOLD, 18));
        Box box3 = Box.createHorizontalBox();
        get3 = new JButton("读入MFA文件");
        born3 = new JButton("生成MFA");
        save3 = new JButton("保存MFA文件");
        box3.add(get3);
        box3.add(born3);
        box3.add(save3);
        label3.setBounds(5, 0, 300, 50);
        panel3.add(label3);
        panel3_title.setBounds(5, 60, 300, 30);
        panel3.add(panel3_title);
        area3.setFont(new Font("宋体", Font.BOLD, 32));
        Border border3 = BorderFactory.createLineBorder(new Color(0, 0, 0), 4, false);
        JScrollPane scrollPane3 = new JScrollPane();
        scrollPane3.setBorder(border3);
        scrollPane3.setViewportView(area3);
        scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane3.setBorder(border3);
        scrollPane3.setBounds(5, 92, 300, 350);
        panel3.add(scrollPane3);
        label3_s.setBounds(5, 445, 300, 30);
        panel3.add(label3_s);
        label3_e.setBounds(5, 475, 300, 30);
        panel3.add(label3_e);
        box3.setBounds(5, 500, 300, 90);
        panel3.add(box3);
        box0.add(panel3);


        get1.addActionListener(this);
        born1.addActionListener(this);
        save1.addActionListener(this);

        get2.addActionListener(this);
        born2.addActionListener(this);
        save2.addActionListener(this);

        get3.addActionListener(this);
        born3.addActionListener(this);
        save3.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == be) {
            String a = text1.getText().trim();
            String message = "";
            new ToNFA(a, message);
            if ((!NFA.message.equals("")) || a.equals("")) {
                if (a.equals("")) {
                    JOptionPane.showMessageDialog(null, "输入为空", "错误", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, NFA.message, "错误", JOptionPane.ERROR_MESSAGE);
                    NFA.message = "";
                }

            } else {
                JOptionPane.showMessageDialog(null, "所输入为正规式", "成功", JOptionPane.PLAIN_MESSAGE);
            }

        }
        if (o == exit) {
            frame.setVisible(false);
        }
        if (o == get1) {
            File getPath;
            JFileChooser jf = new JFileChooser("../");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文档(*.txt)", "txt");
            jf.setFileFilter(filter);
            int value = jf.showOpenDialog(frame);
            if (value == JFileChooser.APPROVE_OPTION) {
                getPath = jf.getSelectedFile();
                File fp = getPath;
                File fileflag = fp;
                BufferedReader reader = null;
                String tempString = null;
                String sum = "";
                int line = 1;
                try {
                    reader = new BufferedReader(new FileReader(fp));
                    while ((tempString = reader.readLine()) != null) {
                        sum = sum + tempString;
                        line++;
                    }

                    this.area1.setText(sum);
                    reader.close();
                } catch (FileNotFoundException e1) {

                } catch (IOException e2) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e3) {

                        }
                    }
                }
            } else {
            }
        }
        if (o == born1) {
            String a = text1.getText().trim();
            new ToNFA(a);
        }
        if (o == save1) {
            FileDialog savedia;
            File fileflag = null;
            savedia = new FileDialog(frame, "保存", FileDialog.SAVE);
            if (fileflag == null) {
                savedia.setVisible(true);
                String dirPath = savedia.getDirectory();
                String fileName = savedia.getFile();
                fileName = fileName + ".txt";
                if (dirPath == null || fileName == null) return;
                fileflag = new File(dirPath, fileName);
                try {
                    BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                    String t = area1.getText();
                    bfwt.write(t);
                    bfwt.close();

                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
        if (o == get2) {
            File getPath;
            JFileChooser jf = new JFileChooser("../");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文档(*.txt)", "txt");
            jf.setFileFilter(filter);
            int value = jf.showOpenDialog(frame);
            if (value == JFileChooser.APPROVE_OPTION) {
                getPath = jf.getSelectedFile();
                File fp = getPath;
                File fileflag = fp;
                BufferedReader reader = null;
                String tempString = null;
                String sum = "";
                int line = 1;
                try {
                    reader = new BufferedReader(new FileReader(fp));
                    while ((tempString = reader.readLine()) != null) {
                        sum = sum + tempString;
                        line++;
                    }

                    this.area2.setText(sum);
                    reader.close();
                } catch (FileNotFoundException e1) {

                } catch (IOException e2) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e3) {

                        }
                    }
                }
            } else {
            }
        }
        if (o == born2) {
            String a = text1.getText().trim();
            new ToDefinedDFA(a);
        }
        if (o == save2) {
            FileDialog savedia;
            File fileflag = null;
            savedia = new FileDialog(frame, "保存", FileDialog.SAVE);
            if (fileflag == null) {
                savedia.setVisible(true);
                String dirPath = savedia.getDirectory();
                String fileName = savedia.getFile();
                fileName = fileName + ".txt";
                if (dirPath == null || fileName == null) return;
                fileflag = new File(dirPath, fileName);
                try {
                    BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                    String t = area2.getText();
                    bfwt.write(t);
                    bfwt.close();

                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
        if (o == get3) {
            File getPath;
            JFileChooser jf = new JFileChooser("../");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文档(*.txt)", "txt");
            jf.setFileFilter(filter);
            int value = jf.showOpenDialog(frame);
            if (value == JFileChooser.APPROVE_OPTION) {
                getPath = jf.getSelectedFile();
                File fp = getPath;
                File fileflag = fp;
                BufferedReader reader = null;
                String tempString = null;
                String sum = "";
                int line = 1;
                try {
                    reader = new BufferedReader(new FileReader(fp));
                    while ((tempString = reader.readLine()) != null) {
                        sum = sum + tempString;
                        line++;
                    }

                    this.area3.setText(sum);
                    reader.close();
                } catch (FileNotFoundException e1) {

                } catch (IOException e2) {

                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e3) {

                        }
                    }
                }
            } else {
            }
        }
        if (o == born3) {
            String a = text1.getText().trim();
            new ToMinimumDFA(a);
        }
        if (o == save3) {
            FileDialog savedia;
            File fileflag = null;
            savedia = new FileDialog(frame, "保存", FileDialog.SAVE);
            if (fileflag == null) {
                savedia.setVisible(true);
                String dirPath = savedia.getDirectory();
                String fileName = savedia.getFile();
                fileName = fileName + ".txt";
                if (dirPath == null || fileName == null) return;
                fileflag = new File(dirPath, fileName);
                try {
                    BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                    String t = area3.getText();
                    bfwt.write(t);
                    bfwt.close();

                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
    }
}
