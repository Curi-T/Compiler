package cn.cqut.compiler.lexical.nfa.te;

/**
 * @Author CuriT
 * @Date 2022-5-12 15:31
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.glass.events.KeyEvent;


public class Viewer extends JFrame implements ActionListener, MouseMotionListener, DocumentListener, MouseListener, ChangeListener {
    private static Viewer viewer;
    JFrame j;
    private static Boolean fsave = false;
    static Point origin = new Point();
    static String text = "";
    JPopupMenu popupMenu;
    JMenuItem m1, m2, m3, m4, m5, m6;
    JLabel lab1, lab2, lab3, lab4, lab5, tu1, tu2, tu3, tu4, tu5, tu6;
    JMenuItem item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, item15, item16;
    JMenu menu5, menu3;
    JTextArea[] area;
    Clipboard clipboard = null;
    private static File fileflag = null;
    private static Boolean flag = true;
    File FileList[], file;
    Boolean SaveList[];
    int active[];
    int array = 0;
    int num = 0;
    JTabbedPane pane;
    JPanel[] panel;
    JScrollPane[] scrollPane;
    JButton[] bu;
    JButton bu1, bu2;
    JLabel[] lab;
    JTextArea area2, area3;

    public Viewer() {
        scrollPane = new JScrollPane[]{null, null, null, null, null};
        bu = new JButton[]{null, null, null, null, null};
        lab = new JLabel[]{null, null, null, null, null};
        panel = new JPanel[]{null, null, null, null, null};
        pane = new JTabbedPane();
        pane.addChangeListener(this);
        area = new JTextArea[]{null, null, null, null, null};
        FileList = new File[]{null, null, null, null, null};
        SaveList = new Boolean[]{false, false, false, false, false};
        active = new int[]{0, 0, 0, 0, 0};
        file = new File("./data.txt");
        setTitle("ff记事本");
        setLayout(null);
        rightMenu();//右击菜单
        init();
        mevent();
        creatTextArea();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(dim);
        setVisible(true);
        Color bgColor = new Color(85, 120, 208);
        ((JPanel) getContentPane()).setBackground(bgColor);
        //((JPanel)getContentPane()).setOpaque(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

    }

    private void init() {
        nav();//快捷图标
        setM();//菜单栏
        addTab();//选项卡

    }

    //窗口拖动和鼠标事件
    private void mevent() {
        addMouseListener(new MouseAdapter() {
            // 按下（mousePressed 不是点击，而是鼠标被按下没有抬起）
            public void mousePressed(MouseEvent e) {
                // 当鼠标按下的时候获得窗口当前的位置
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            // 拖动（mouseDragged 指的不是鼠标在窗口中移动，而是用鼠标拖动）
            public void mouseDragged(MouseEvent e) {
                // 当鼠标拖动时获取窗口当前位置
                Point p = getLocation();
                // 设置窗口的位置
                // 窗口当前的位置 + 鼠标当前在窗口的位置 - 鼠标按下的时候在窗口的位置
                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y);
            }
        });
        addMouseListener(new MouseAdapter() {
            // 点击鼠标
            public void mousePressed(MouseEvent event) {
                // 调用triggerEvent方法处理事件
                triggerEvent(event);
            }

            // 释放鼠标
            public void mouseReleased(MouseEvent event) {
                triggerEvent(event);
            }

            private void triggerEvent(MouseEvent event) { // 处理事件
                // isPopupTrigger():返回此鼠标事件是否为该平台的弹出菜单触发事件。

            }

            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    if (area[array].getSelectedText() == null) m6.setEnabled(false);
                    else m6.setEnabled(true);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    //右击菜单
    public void rightMenu() {
        popupMenu = new JPopupMenu();
        m1 = new JMenuItem("复制");
        m2 = new JMenuItem("粘贴");
        ImageIcon icon1 = new ImageIcon("image/f.ico");
        icon1.setImage(icon1.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        m3 = new JMenuItem("新建", icon1);
        icon1 = new ImageIcon("image/s.ico");
        icon1.setImage(icon1.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        m4 = new JMenuItem("保存", icon1);
        icon1 = new ImageIcon("image/s2.ico");
        icon1.setImage(icon1.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        m5 = new JMenuItem("保存为", icon1);
        m6 = new JMenuItem("剪切");
        m1.addActionListener(this);
        m2.addActionListener(this);
        m3.addActionListener(this);
        m4.addActionListener(this);
        m5.addActionListener(this);
        m6.addActionListener(this);
        popupMenu.add(m6);
        popupMenu.add(m1);
        popupMenu.add(m2);
        popupMenu.add(m3);
        popupMenu.add(m4);
        popupMenu.add(m5);

    }

    public String getArea() {
        return this.area[this.array].getText();

    }

    //快捷栏图标
    public void nav() {

        JPanel pan = new JPanel();
        ImageIcon icon1 = new ImageIcon("image/f.ico");
        icon1.setImage(icon1.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu1 = new JLabel(icon1);
        tu1.addMouseListener(this);
        ImageIcon icon2 = new ImageIcon("image/op.ico");
        icon2.setImage(icon2.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu2 = new JLabel(icon2);
        tu2.addMouseListener(this);
        ImageIcon icon3 = new ImageIcon("image/s.ico");
        icon3.setImage(icon3.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu3 = new JLabel(icon3);
        tu3.addMouseListener(this);
        ImageIcon icon4 = new ImageIcon("image/s2.ico");
        icon4.setImage(icon4.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu4 = new JLabel(icon4);
        tu4.addMouseListener(this);
        ImageIcon icon5 = new ImageIcon("image/h.ico");
        icon5.setImage(icon5.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu5 = new JLabel(icon5);
        tu5.addMouseListener(this);
        ImageIcon icon6 = new ImageIcon("image/by.png");
        icon6.setImage(icon6.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        tu6 = new JLabel(icon6);
        tu6.addMouseListener(this);
        pan.add(tu1);
        pan.add(new JLabel(" "));
        pan.add(tu2);
        pan.add(new JLabel(" "));
        pan.add(tu3);
        pan.add(new JLabel(" "));
        pan.add(tu4);
        pan.add(new JLabel(" "));
        pan.add(tu5);
        pan.add(new JLabel("        "));
        pan.add(tu6);
        pan.setBounds(-390, 0, 2580, 50);
        pan.add(new JLabel("                                     "));
        pan.add(new JLabel("                                     "));
        pan.add(new JLabel("                                     "));
        //pan.setOpaque(false);
        add(pan);
    }

    //菜单栏
    public void setM() {
        ImageIcon icon = new ImageIcon("image/f2.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        lab1 = new JLabel(icon);
        icon = new ImageIcon("image/bj.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        lab2 = new JLabel(icon);
        icon = new ImageIcon("image/g.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        lab3 = new JLabel(icon);
        icon = new ImageIcon("image/h.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        lab4 = new JLabel(icon);
        icon = new ImageIcon("image/q.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        lab5 = new JLabel(icon);
        lab1.addMouseMotionListener(this);
        lab2.addMouseMotionListener(this);
        lab3.addMouseMotionListener(this);
        lab4.addMouseMotionListener(this);
        lab5.addMouseMotionListener(this);
        JMenuBar menubar = new JMenuBar();
        Color bgColor = new Color(85, 120, 208);
        //menubar.setBackground(bgColor);
        boolean t = true;
        menubar.setBorderPainted(t);
        setJMenuBar(menubar);
        icon = new ImageIcon("image/f2.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        JMenu menu1 = new JMenu("文件(F)");
        menu1.setFont(new Font("宋体", Font.BOLD, 23));
        menu1.setMnemonic(KeyEvent.VK_F);
        //menu1.setForeground(new Color(207,46,125));
        JMenu menu2 = new JMenu("编辑-词法分析(E)");
        menu2.setFont(new Font("宋体", Font.BOLD, 23));
        menu2.setMnemonic(KeyEvent.VK_E);
        menu3 = new JMenu("格式(G)");
        menu3.setFont(new Font("宋体", Font.BOLD, 23));
        menu3.setMnemonic(KeyEvent.VK_G);
        JMenu menu4 = new JMenu("帮助(H)");
        menu4.setFont(new Font("宋体", Font.BOLD, 23));
        menu4.setMnemonic(KeyEvent.VK_H);
        menu5 = new JMenu("退出(Q)");
        menu5.setFont(new Font("宋体", Font.BOLD, 23));
        menu5.setMnemonic(KeyEvent.VK_Q);
        menu5.addMouseListener(this);
        menubar.add(new JLabel("   "));
        menubar.add(lab1);
        menubar.add(menu1);
        menubar.add(new JLabel("    "));
        menubar.add(lab2);
        menubar.add(menu2);
        menubar.add(new JLabel("    "));
        menubar.add(lab3);
        menubar.add(menu3);
        menubar.add(new JLabel("    "));
        menubar.add(lab4);
        menubar.add(menu4);
        menubar.add(new JLabel("    "));
        menubar.add(lab5);
        menubar.add(menu5);
        icon = new ImageIcon("image/f.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item1 = new JMenuItem("新建(alt+N)", icon);
        item1.addActionListener(this);
        item1.setMnemonic(KeyEvent.VK_N);
        icon = new ImageIcon("image/S.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item2 = new JMenuItem("保存(alt+S)", icon);
        item2.addActionListener(this);
        item2.setMnemonic(KeyEvent.VK_S);
        icon = new ImageIcon("image/s2.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item3 = new JMenuItem("另存为(alt+A)", icon);
        item3.addActionListener(this);
        item3.setMnemonic(KeyEvent.VK_A);
        icon = new ImageIcon("image/x.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item4 = new JMenuItem("关闭(alt+X)", icon);
        item4.addActionListener(this);
        item4.setMnemonic(KeyEvent.VK_X);
        item5 = new JMenuItem("剪切(alt+x)");
        item5.addActionListener(this);
        item5.setMnemonic(KeyEvent.VK_M);
        item6 = new JMenuItem("复制(alt+c)");
        item6.addActionListener(this);
        item6.setMnemonic(KeyEvent.VK_M);
        icon = new ImageIcon("image/op.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item7 = new JMenuItem("打开(alt+O)", icon);
        item7.addActionListener(this);
        item7.setMnemonic(KeyEvent.VK_O);
        item8 = new JMenuItem("粘贴(alt+v)");
        item8.addActionListener(this);
        item8.setMnemonic(KeyEvent.VK_T);
        item9 = new JMenuItem("关于(alt+B)");
        item9.addActionListener(this);
        item9.setMnemonic(KeyEvent.VK_B);
        item10 = new JMenuItem("大字号");
        item10.addActionListener(this);
        icon = new ImageIcon("image/yes.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        item11 = new JMenuItem("中等字号", icon);
        item11.addActionListener(this);
        item12 = new JMenuItem("小字号");
        item12.addActionListener(this);
        item13 = new JMenuItem("文件记录");
        item13.addActionListener(this);
        item14 = new JMenuItem("帮助");
        item14.addActionListener(this);
        item15 = new JMenuItem("我得账户");
        item15.addActionListener(this);
        item16 = new JMenuItem("DFA-NFA-MFA");
        item16.addActionListener(this);
        menu1.add(item1);
        menu1.add(item2);
        menu1.add(item3);
        menu1.addSeparator();
        menu1.add(item7);
        menu1.addSeparator();
        menu1.add(item13);
        menu1.addSeparator();
        menu1.add(item4);
        menu2.add(item5);
        menu2.add(item6);
        menu2.add(item8);
        menu2.add(item16);
        menu3.add(item10);
        menu3.add(item11);
        menu3.add(item12);
        menu4.add(item9);
        menu4.add(item14);
    }

    public void creatTextArea() {
        //*********************************
        JScrollPane scrollPane = new JScrollPane();
        area2 = new JTextArea(100, 200);
        area2.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    m3.setEnabled(false);
                    m4.setEnabled(false);
                    m5.setEnabled(false);
                    if (area2.getSelectedText() == null) {
                        m6.setEnabled(false);
                        m1.setEnabled(false);
                    } else {
                        m6.setEnabled(true);
                        m1.setEnabled(true);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        area2.getSelectedText();

        area2.setText("编译结果(键值对)\n");
        area2.getDocument().addDocumentListener(this);
        area2.setLineWrap(true);
        area2.setFont(new Font("宋体", Font.BOLD, 25));
        Color bgColor = new Color(207, 46, 125);
        area2.setCaretColor(bgColor);
        area2.setForeground(Color.white);
        area2.setMargin(new Insets(15, 15, 15, 15));
        bgColor = new Color(32, 35, 42);
        area2.setBackground(bgColor);
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 8, false);
        scrollPane.setBorder(border);
        scrollPane.setViewportView(area2);
        scrollPane.setVisible(true);
        scrollPane.setBounds(0, 0, 890, 750);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel pane = new JPanel();
        pane.setLayout(null);
        pane.add(scrollPane);
        pane.setFont(new Font("宋体", Font.BOLD, 20));
        pane.setBounds(1000, 87, 890, 713);
        //*****************
        JScrollPane scrollPane3 = new JScrollPane();
        area3 = new JTextArea(100, 200);
        area3.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    m3.setEnabled(false);
                    m4.setEnabled(false);
                    m5.setEnabled(false);
                    if (area3.getSelectedText() == null) {
                        m6.setEnabled(false);
                        m1.setEnabled(false);
                    } else {
                        m6.setEnabled(true);
                        m1.setEnabled(true);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }

            }
        });
        area3.getSelectedText();
        area3.setText("错误提示\n");
        area3.getDocument().addDocumentListener(this);
        area3.setLineWrap(true);
        area3.setFont(new Font("宋体", Font.BOLD, 25));
        bgColor = new Color(207, 46, 125);
        area3.setCaretColor(bgColor);
        area3.setForeground(Color.white);
        area3.setMargin(new Insets(15, 15, 15, 15));
        bgColor = new Color(32, 35, 42);
        area3.setBackground(bgColor);
        border = BorderFactory.createLineBorder(new Color(0, 0, 0), 8, false);
        scrollPane3.setBorder(border);
        scrollPane3.setViewportView(area3);
        scrollPane3.setVisible(true);
        scrollPane3.setBounds(0, 0, 1890, 200);
        scrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel pane1 = new JPanel();
        pane1.setLayout(null);
        pane1.add(scrollPane3);
        pane1.setFont(new Font("宋体", Font.BOLD, 20));
        pane1.setBounds(5, 815, 1890, 180);
        //*****************

        add(pane);
        add(pane1);
    }

    //选项卡
    public void addTab() {
        fileflag = FileList[array];
        fsave = SaveList[array];
        //*********************************
        scrollPane[array] = new JScrollPane();
        area[array] = new JTextArea(100, 200);
        area[array].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    m3.setEnabled(true);
                    m4.setEnabled(true);
                    m5.setEnabled(true);
                    if (area[array].getSelectedText() == null) {
                        m6.setEnabled(false);
                        m1.setEnabled(false);
                    } else {
                        m6.setEnabled(true);
                        m1.setEnabled(true);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        area[array].getSelectedText();
        area[array].setText("输入c语言源程序\n");
        area[array].getDocument().addDocumentListener(this);
        area[array].setLineWrap(true);
        area[array].setFont(new Font("宋体", Font.BOLD, 25));
        Color bgColor = new Color(207, 46, 125);
        area[array].setCaretColor(bgColor);
        area[array].setForeground(Color.white);
        area[array].setMargin(new Insets(15, 15, 15, 15));
        bgColor = new Color(32, 35, 42);
        area[array].setBackground(bgColor);
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 8, false);
        scrollPane[array].setBorder(border);
        scrollPane[array].setViewportView(area[array]);
        scrollPane[array].setVisible(true);
        scrollPane[array].setBounds(140, 60, 800, 600);
        scrollPane[array].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane[array].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //*****************
        panel[array] = new JPanel();
        bu[array] = new JButton();
        lab[array] = new JLabel("未命名文件");
        lab[array].setFont(new Font("宋体", Font.BOLD, 15));
        ImageIcon icon = new ImageIcon("image/close.ico");
        icon.setImage(icon.getImage().getScaledInstance(12, 12, Image.SCALE_DEFAULT));
        bu[array].setSize(30, 30);
        bu[array].setIcon(icon);
        bu[array].setBorder(null);
        bu[array].setBorderPainted(false);
        bu[array].setFocusPainted(false);
        bu[array].setPressedIcon(new ImageIcon("image/close.ico"));
        panel[array].add(new JLabel("            "));
        panel[array].add(lab[array]);
        panel[array].add(new JLabel("            "));
        //panel[array].add(bu[array]);
        panel[array].setBorder(null);
        panel[array].setOpaque(false);
        pane.setFont(new Font("宋体", Font.BOLD, 20));
        pane.addTab("", scrollPane[array]);
        pane.setTabComponentAt(pane.indexOfComponent(scrollPane[array]), panel[array]);
        num++;
        int a = pane.indexOfComponent(scrollPane[array]);
        active[a] = 1;
        lab[array].setText(String.valueOf(a + 1 + ",未命名*"));
        pane.setBounds(5, 50, 980, 750);
        add(pane);
    }

    public void creatNewTab(String sum) {
        fileflag = FileList[array];
        fsave = SaveList[array];
        scrollPane[array] = new JScrollPane();
        area[array] = new JTextArea(100, 200);
        area[array].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isMetaDown()) {
                    m3.setEnabled(true);
                    m4.setEnabled(true);
                    m5.setEnabled(true);
                    if (area[array].getSelectedText() == null) {
                        m6.setEnabled(false);
                        m1.setEnabled(false);
                    } else {
                        m6.setEnabled(true);
                        m1.setEnabled(true);
                    }
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        area[array].getSelectedText();
        area[array].setText(sum);
        area[array].getDocument().addDocumentListener(this);
        area[array].setLineWrap(true);
        area[array].setFont(new Font("宋体", Font.BOLD, 25));
        Color bgColor = new Color(207, 46, 125);
        area[array].setCaretColor(bgColor);
        area[array].setForeground(Color.white);
        area[array].setMargin(new Insets(15, 15, 15, 15));
        bgColor = new Color(32, 35, 42);
        area[array].setBackground(bgColor);
        Border border = BorderFactory.createLineBorder(new Color(0, 0, 0), 8, false);
        scrollPane[array].setBorder(border);
        scrollPane[array].setViewportView(area[array]);
        scrollPane[array].setVisible(true);
        scrollPane[array].setBounds(140, 60, 800, 600);
        scrollPane[array].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane[array].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        //*****************
        panel[array] = new JPanel();
        bu[array] = new JButton();
        lab[array] = new JLabel("未命名*");
        lab[array].setFont(new Font("宋体", Font.BOLD, 15));
        ImageIcon icon = new ImageIcon("image/close.ico");
        icon.setImage(icon.getImage().getScaledInstance(12, 12, Image.SCALE_DEFAULT));
        bu[array].setSize(30, 30);
        bu[array].setIcon(icon);
        bu[array].setBorder(null);
        bu[array].setBorderPainted(false);
        bu[array].setFocusPainted(false);
        bu[array].setPressedIcon(new ImageIcon("image/close.ico"));
        panel[array].add(new JLabel("            "));
        panel[array].add(lab[array]);
        panel[array].add(new JLabel("            "));
        panel[array].add(bu[array]);
        panel[array].setBorder(null);
        panel[array].setOpaque(false);
        pane.setFont(new Font("宋体", Font.BOLD, 20));
        pane.addTab("", scrollPane[array]);
        pane.setTabComponentAt(pane.indexOfComponent(scrollPane[array]), panel[array]);
        if (num == 5) {
            pane.setTabComponentAt(0, new JLabel(""));
        }
        if (num < 5) {
            pane.setTabComponentAt(pane.indexOfComponent(scrollPane[array]), panel[array]);
        }
        int a = pane.indexOfComponent(scrollPane[array]);
        active[a] = 1;
        lab[array].setText(String.valueOf(a + 1 + ",未命名*"));
        pane.setSelectedIndex(array);
        bu[array].addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object o = e.getSource();
                int x = pane.getTabCount();
                if (x == 1) {

                } else {
                    for (int j = 0; j < x; j++) {
                        if (o == bu[j]) {
                            close(j);
                            pane.setTabComponentAt(0, panel[0]);
                            int index = pane.getSelectedIndex();
                            array = index;
                            //pane.setSelectedIndex(array);
                            fileflag = FileList[array];
                            fsave = SaveList[array];
                        }
                    }
                }


            }

        });

    }

    public void recents() {
        j = new JFrame();
        Box box0 = Box.createVerticalBox();
        Box box1 = Box.createVerticalBox();
        Box box2 = Box.createHorizontalBox();
        bu1 = new JButton("清除纪录");
        ImageIcon icon = new ImageIcon("image/f2.ico");
        icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
        bu2 = new JButton("关闭");
        bu1.addActionListener(this);
        bu2.addActionListener(this);
        try {
            String encoding = "GBK";
            File f = file;
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(f), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    String record[] = lineTxt.split("#@end#");
                    int x = record.length - 1;
                    for (int i = record.length - 1; i > 0; i--) {
                        if (i < record.length - 6) {
                            break;
                        }
                        String name = "名称：" + record[i].split("#@path#")[0];
                        if (name.length() > 18) {
                            name = name.substring(0, 19);
                        }

                        String path = record[i].split("#@path#")[1];
                        JLabel lab0 = new JLabel(icon);
                        JLabel lab1 = new JLabel(name);
                        JLabel labx = new JLabel(path);
                        labx.addMouseListener(new MouseAdapter() {
                            public void mouseEntered(MouseEvent e) //鼠标进入
                            {
                                j.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            }

                            public void mouseExited(MouseEvent e) //鼠标移除
                            {
                                j.setCursor(Cursor.getDefaultCursor());
                            }

                            public void mouseClicked(MouseEvent e) {
                                Object o = e.getSource();
                                if (o == labx) {
                                    String p = labx.getText();
                                    if (num < 5) {
                                        File getPath;
                                        getPath = new File(p);
                                        File fp = getPath;
                                        fileflag = fp;
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

                                            for (int j = 0; j < 5; j++) {
                                                if (active[j] == 0) {
                                                    array = j;
                                                    break;
                                                }
                                            }
                                            FileList[array] = fileflag;
                                            SaveList[array] = true;
                                            num++;
                                            creatNewTab(sum);
                                            String str = FileList[array].getName();
                                            lab[array].setText(str.substring(0, str.lastIndexOf(".")));
                                            reader.close();
                                            j.dispose();
                                        } catch (FileNotFoundException e1) {
                                            JOptionPane.showMessageDialog(null, "文件位置发生改变", "错误", JOptionPane.ERROR_MESSAGE);
                                        } catch (IOException e2) {
                                            JOptionPane.showMessageDialog(null, "发生错误", "错误", JOptionPane.ERROR_MESSAGE);
                                        } finally {
                                            if (reader != null) {
                                                try {
                                                    reader.close();
                                                } catch (IOException e3) {
                                                    JOptionPane.showMessageDialog(null, "发生错误", "错误", JOptionPane.ERROR_MESSAGE);
                                                }
                                            }
                                        }

                                    } else {
                                        JOptionPane.showMessageDialog(null, "最多同时打开5个文档", "错误", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            }
                        });
                        Panel pan = new Panel();
                        GridBagLayout gbl = new GridBagLayout();
                        GridBagConstraints gbs = new GridBagConstraints();
                        pan.setLayout(gbl);
                        pan.add(lab1);
                        pan.add(labx);
                        gbs.fill = GridBagConstraints.BOTH;
                        gbs.gridwidth = 2;
                        gbs.gridheight = 2;
                        gbs.insets = new Insets(5, 5, 5, 5);
                        gbs.weightx = 1;
                        gbs.weighty = 1;
                        gbs.gridx = 0;
                        gbs.gridy = 3;
                        gbl.setConstraints(lab1, gbs);
                        gbs.fill = GridBagConstraints.BOTH;
                        gbs.gridwidth = 2;
                        gbs.gridheight = 2;
                        gbs.insets = new Insets(5, 5, 5, 5);
                        gbs.weightx = 1;
                        gbs.weighty = 1;
                        gbs.gridx = 0;
                        gbs.gridy = 5;
                        gbl.setConstraints(labx, gbs);
                        pan.add(new JLabel("---------------------------------------"));
                        box1.add(pan);
                    }
                }
                read.close();
            } else {

            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        box2.add(bu1);
        box2.add(new JLabel("     "));
        box2.add(bu2);
        box2.add(new JLabel("     "));
        box0.add(box1);
        box0.add(new JLabel("     "));
        box0.add(box2);
        j.add(box0);
        j.setBounds(750, 320, 550, 550);
        j.setVisible(true);
        j.setTitle("最近文件");
        j.setLayout(new FlowLayout());
    }

    public void close(int j) {
        if (SaveList[j] == false) {
            int a = JOptionPane.showConfirmDialog(null, "文件未保存是否保存", "询问", JOptionPane.YES_OPTION);
            if (a == JOptionPane.YES_OPTION) {
                saveallfile(j);
                pane.remove(j);
                lab[j] = null;
                panel[j] = null;
                bu[j] = null;
                FileList[j] = null;
                SaveList[j] = false;
                active[j] = 0;
                num--;
                sort(j);
            }
            if (a == JOptionPane.NO_OPTION) {
                pane.remove(j);
                bu[j] = null;
                FileList[j] = null;
                SaveList[j] = false;
                active[j] = 0;
                num--;
                sort(j);
            } else {

            }

        } else {
            pane.remove(j);
            bu[j] = null;
            FileList[j] = null;
            SaveList[j] = false;
            active[j] = 0;
            num--;
            sort(j);
        }
    }

    public void sort(int index) {
        for (int i = index; i + 1 < 5; i++) {
            FileList[i] = FileList[i + 1];
            active[i] = active[i + 1];
            SaveList[i] = SaveList[i + 1];
            panel[i] = panel[i + 1];
            scrollPane[i] = scrollPane[i + 1];
            bu[i] = bu[i + 1];
            lab[i] = lab[i + 1];
        }
    }

    public void writeToFile(String data, String path) {
        file = new File("./data.txt");
        String content = data + "#@path#" + path + "#@end#";
        try (FileOutputStream fop = new FileOutputStream(file, true)) {
            if (!file.exists()) {
                file.createNewFile();
            }
            byte[] contentInBytes = content.getBytes();
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getViewer() {
        if (viewer == null) {
            viewer = new Viewer();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {    //菜单项的事件处理
        Object o = e.getSource();
        clipboard = getToolkit().getSystemClipboard();//获取系统剪贴板
        if (o == item1 || o == m3) {
            if (num == 5) {
                JOptionPane.showMessageDialog(null, "最多同时打开5个文档", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                FileList[array] = fileflag;
                SaveList[array] = false;
                num++;
                array = pane.getTabCount();
                active[array] = 1;
                creatNewTab("记录你的每件事");
            }

        }

        if (o == item2 || o == m4) {
            saveallfile(array);
        }
        if (o == item3 || o == m5) {
            FileDialog savedia;
            fileflag = null;
            savedia = new FileDialog(viewer, "保存", FileDialog.SAVE);
            if (fileflag == null) {
                savedia.setVisible(true);
                String dirPath = savedia.getDirectory();
                String fileName = savedia.getFile();
                fileName = fileName + ".txt";
                if (dirPath == null || fileName == null) return;
                fileflag = new File(dirPath, fileName);
                try {
                    BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                    String t = area[array].getText();
                    bfwt.write(t);
                    bfwt.close();
                    fsave = true;
                    FileList[array] = fileflag;
                    String str = FileList[array].getName();
                    lab[array].setText(str.substring(0, str.lastIndexOf(".")));
                    SaveList[array] = fsave;
                    active[array] = 1;
                    writeToFile(fileflag.getName(), fileflag.getPath());
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }

        }
        if (o == item4) {
            int a = 0;
            for (int j = 0; j < 5; j++) {
                if (SaveList[j] == false && active[j] == 1) {
                    a = 1;
                }
            }
            if (a == 1) {
                int que = JOptionPane.showConfirmDialog(null, "存在未保存的文档，是否退出", "是否关闭", JOptionPane.YES_NO_OPTION);
                if (que == JOptionPane.YES_OPTION) {
                    System.exit(0);

                } else if (que == JOptionPane.NO_OPTION) {

                } else {


                }
            }
            if (a == 0) {
                System.exit(0);
            }
        }
        if (o == item13) {
            recents();
        }
        if (o == bu1) {
            file = new File("./data.txt");
            String content = "";
            try (FileOutputStream fop = new FileOutputStream(file)) {
                if (!file.exists()) {
                    file.createNewFile();
                }
                byte[] contentInBytes = content.getBytes();
                fop.write(contentInBytes);
                fop.flush();
                fop.close();
            } catch (IOException e5) {
                e5.printStackTrace();
            }
            j.dispose();
            JOptionPane.showMessageDialog(null, "删除成功", "完成", JOptionPane.INFORMATION_MESSAGE);
        }
        if (o == bu2) {
            j.dispose();
        }
        if (o == item7) {
            if (num < 5) {
                File getPath;
                JFileChooser jf = new JFileChooser("../");
                FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文档(*.txt)", "txt");
                jf.setFileFilter(filter);
                int value = jf.showOpenDialog(viewer);
                if (value == JFileChooser.APPROVE_OPTION) {
                    getPath = jf.getSelectedFile();
                    File fp = getPath;
                    fileflag = fp;
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

                        for (int j = 0; j < 5; j++) {
                            if (active[j] == 0) {
                                array = j;
                                break;
                            }
                        }
                        FileList[array] = fileflag;
                        SaveList[array] = true;
                        num++;
                        creatNewTab(sum);
                        String str = FileList[array].getName();
                        lab[array].setText(str.substring(0, str.lastIndexOf(".")));
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
            } else {
                JOptionPane.showMessageDialog(null, "最多同时打开5个文档", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (o == m1 || o == item6) {
            text = area[array].getSelectedText();
            StringSelection temp = new StringSelection(text);
            clipboard.setContents(temp, null);

        }
        if (o == m2 || o == item8) {
            Transferable contents = clipboard.getContents(this);
            DataFlavor flavor = DataFlavor.stringFlavor;
            if (contents.isDataFlavorSupported(flavor)) try {
                String str;
                str = (String) contents.getTransferData(flavor);
                String a = area[array].getSelectedText();
                if (a == null) {
                    int cur = area[array].getCaretPosition();
                    // 得到光标之后的字符串
                    String tailString = area[array].getText().substring(cur);
                    // 得到光标之前的字符串
                    String headString = area[array].getText().substring(0, cur);
                    // 拼接字符串 并输出
                    if (str != null) area[array].setText(headString + str + tailString);
                } else {
                    int cur = area[array].getCaretPosition();
                    int s = area[array].getText().indexOf(a);
                    int end = s + a.length();
                    String tail = area[array].getText().substring(end);
                    // 得到光标之前的字符串
                    String head = area[array].getText().substring(0, s);
                    if (str != null) area[array].setText(head + str + tail);
                }
            } catch (Exception ee) {
            }
        }
        if (o == m6 || o == item5) {
            text = area[array].getSelectedText();// 复制
            StringSelection temp = new StringSelection(text);
            clipboard.setContents(temp, null);
            //删除选中
            String a0 = area[array].getSelectedText();
            int cur0 = area[array].getCaretPosition();
            int s0 = area[array].getText().indexOf(a0);
            int end0 = s0 + a0.length();
            String tail0 = area[array].getText().substring(end0);
            String head0 = area[array].getText().substring(0, s0);
            area[array].setText(head0 + tail0);


        }
        if (o == item10 || o == item11 || o == item12) {
            if (o == item10) {
                area[array].setFont(new Font("宋体", Font.BOLD, 35));
                menu3.removeAll();
                ImageIcon icon = new ImageIcon("image/yes.ico");
                icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
                item10 = new JMenuItem("大字号", icon);
                item10.addActionListener(this);
                item11 = new JMenuItem("中等字号");
                item11.addActionListener(this);
                item12 = new JMenuItem("小字号");
                item12.addActionListener(this);
                menu3.add(item10);
                menu3.add(item11);
                menu3.add(item12);
            } else if (o == item12) {
                area[array].setFont(new Font("宋体", Font.BOLD, 15));
                menu3.removeAll();
                ImageIcon icon = new ImageIcon("image/yes.ico");
                icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
                item10 = new JMenuItem("大字号");
                item10.addActionListener(this);
                item11 = new JMenuItem("中等字号");
                item11.addActionListener(this);
                item12 = new JMenuItem("小字号", icon);
                item12.addActionListener(this);
                menu3.add(item10);
                menu3.add(item11);
                menu3.add(item12);
            } else {
                area[array].setFont(new Font("宋体", Font.BOLD, 25));
                menu3.removeAll();
                ImageIcon icon = new ImageIcon("image/yes.ico");
                icon.setImage(icon.getImage().getScaledInstance(32, 32, Image.SCALE_DEFAULT));
                item10 = new JMenuItem("大字号");
                item10.addActionListener(this);
                item11 = new JMenuItem("中等字号", icon);
                item11.addActionListener(this);
                item12 = new JMenuItem("小字号");
                item12.addActionListener(this);
                menu3.add(item10);
                menu3.add(item11);
                menu3.add(item12);
            }
            flag = false;

        }

        if (o == item9) {
            JOptionPane.showMessageDialog(this, "作者是17111205114谢先锋");
        }
        if (o == item14) {
            new Viewer2();
        }
        if (o == item16) {
            new NFAViewer();
        }


    }

    public void saveallfile(int array) {
        FileDialog savedia;
        savedia = new FileDialog(viewer, "保存", FileDialog.SAVE);
        if (fileflag == null) {
            savedia.setVisible(true);
            String dirPath = savedia.getDirectory();
            String fileName = savedia.getFile();
            fileName = fileName + ".txt";
            if (dirPath == null || fileName == null) return;
            fileflag = new File(dirPath, fileName);
            try {
                BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                String t = area[array].getText();
                bfwt.write(t);
                bfwt.close();
                fsave = true;
                FileList[array] = fileflag;
                SaveList[array] = fsave;
                active[array] = 1;
                String str = FileList[array].getName();
                lab[array].setText(str.substring(0, str.lastIndexOf(".")));
                writeToFile(fileflag.getName(), fileflag.getPath());

            } catch (IOException ex) {
                throw new RuntimeException();
            }
        } else {
            try {
                BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                String t = area[array].getText();
                bfwt.write(t);
                bfwt.close();
                fsave = true;
                FileList[array] = fileflag;
                SaveList[array] = fsave;
                active[array] = 1;
                String str = FileList[array].getName();
                lab[array].setText(str.substring(0, str.lastIndexOf(".")));
            } catch (IOException ex) {
                throw new RuntimeException();
            }
        }

    }

    @Override
    public void mouseDragged(MouseEvent e) {


    }

    @Override
    public void mouseMoved(MouseEvent e) {


    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        fsave = false;

    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        fsave = false;

    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        fsave = false;

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object o = e.getSource();
        if (o == menu5) {
            if (fsave == false) {
                int que = JOptionPane.showConfirmDialog(null, "是否保存", "是否继续", JOptionPane.YES_NO_OPTION);
                if (que == JOptionPane.YES_OPTION) {
                    saveallfile(array);
                } else if (que == JOptionPane.NO_OPTION) {
                    System.exit(0);
                } else {


                }
            }
            if (fileflag != null) {
                fileflag = null;
                System.exit(0);
            }
        }
        if (o == tu1) {
            if (num == 5) {
                JOptionPane.showMessageDialog(null, "最多同时打开5个文档", "错误", JOptionPane.ERROR_MESSAGE);
            } else {
                FileList[array] = fileflag;
                SaveList[array] = false;
                num++;
                array = pane.getTabCount();
                active[array] = 1;
                creatNewTab("记录你的每件事");
            }
        }

        if (o == tu2) {
            File getPath;
            JFileChooser jf = new JFileChooser("../");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文档(*.txt)", "txt");
            jf.setFileFilter(filter);
            int value = jf.showOpenDialog(viewer);
            if (value == JFileChooser.APPROVE_OPTION) {
                getPath = jf.getSelectedFile();
                File fp = getPath;
                fileflag = fp;
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
                    area[array].setText(sum);
                    String str = FileList[array].getName();
                    lab[array].setText(str.substring(0, str.lastIndexOf(".")));
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
        if (o == tu3) {
            saveallfile(array);
        }
        if (o == tu4) {
            FileDialog savedia;
            fileflag = null;
            savedia = new FileDialog(viewer, "保存", FileDialog.SAVE);
            if (fileflag == null) {
                savedia.setVisible(true);
                String dirPath = savedia.getDirectory();
                String fileName = savedia.getFile();
                fileName = fileName + ".txt";
                if (dirPath == null || fileName == null) return;
                fileflag = new File(dirPath, fileName);
                try {
                    BufferedWriter bfwt = new BufferedWriter(new FileWriter(fileflag));
                    String t = area[array].getText();
                    bfwt.write(t);
                    bfwt.close();
                    fsave = true;
                    FileList[array] = fileflag;
                    String str = FileList[array].getName();
                    lab[array].setText(str.substring(0, str.lastIndexOf(".")));
                    SaveList[array] = fsave;
                    active[array] = 1;
                    writeToFile(fileflag.getName(), fileflag.getPath());
                } catch (IOException ex) {
                    throw new RuntimeException();
                }
            }
        }
        if (o == tu5) {
            JOptionPane.showMessageDialog(this, "作者是17111205114谢先锋");
        }
        if (o == tu6) {
            Transform trans = new Transform(getArea());
            int leng = getArea().length();
            String st = "", st0 = "";
            area2.setText("编译结果(键值对)\n");
            do {
                trans.run();
                switch (trans.syn) {
                    case 26:
                        area2.setText(area2.getText() + "(" + trans.syn + " , \'" + trans.sum + "\')\n");
                        break;
                    case 44:
                        area2.setText(area2.getText() + "(" + trans.syn + " , \'" + trans.sum1 + "\')\n");
                        break;
                    case -1:
                        st = "There are " + trans.row + "errors!\n";
                        st0 = st0 + "错误字符:" + trans.token + "   ";
                        area3.setText(st + st0);
                        break;
                    case -2:
                        break;
                    default:
                        area2.setText(area2.getText() + "(" + trans.syn + " , \'" + trans.token + "\')\n");
                }
            } while (trans.index <= leng);

        }

    }

    @Override
    public void mousePressed(MouseEvent e) {


    }

    @Override
    public void mouseReleased(MouseEvent e) {


    }

    @Override
    public void mouseEntered(MouseEvent e) {


    }

    @Override
    public void mouseExited(MouseEvent e) {


    }

    @Override
    public void stateChanged(ChangeEvent e) {

        if (((JTabbedPane) e.getSource()).getSelectedIndex() == 0) {
            array = 0;
            fileflag = FileList[array];
            fsave = SaveList[array];

        } else if (((JTabbedPane) e.getSource()).getSelectedIndex() == 1) {
            array = 1;
            fileflag = FileList[array];
            fsave = SaveList[array];
        } else if (((JTabbedPane) e.getSource()).getSelectedIndex() == 2) {
            array = 2;
            fileflag = FileList[array];
            fsave = SaveList[array];
        } else if (((JTabbedPane) e.getSource()).getSelectedIndex() == 3) {
            array = 3;
            fileflag = FileList[array];
            fsave = SaveList[array];
        } else {
            array = 4;
            fileflag = FileList[array];
            fsave = SaveList[array];
        }
    }

}



