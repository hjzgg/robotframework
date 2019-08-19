package com.wmh.robotframework.gui;

import com.wmh.robotframework.bean.CaseEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class MenuBarDemo {
    private Frame frame;
    private MenuItem menuItem;
    private MenuItem openFileBtn;
    private MenuItem newProject;
    private FileDialog openFile;
    private TextArea ta;
    private Dialog dialog;
    private JButton ok;
    private JButton cancel;
    private JButton chooseFileBtn;
    private JTextField field;

    private JTextField field2;
    private JTree rootTree;
    private DefaultMutableTreeNode rootNode;
    DefaultTreeModel treeModel;
    private JFileChooser fileChooser;
    private Map<String, Map<String, CaseEntity>> treeData = new HashMap<>();
    private String defaultProjectName;
    private String defaultSavePath;

    public MenuBarDemo() {
        init();
        addEvents();
    }

    public void init() {
        //设置界面风格
        try {
            String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lookAndFeel);
        } catch (Exception e) {
            // TODO
        }

        frame = new JFrame("WMHRobotFWindow");

        //设置图标
        try {
            URL imgUrl = new ClassPathResource("imgs/robot.png").getURL();
            frame.setIconImage(new ImageIcon(imgUrl).getImage());
        } catch (IOException e) {
            // TODO
        }

        frame.setBounds(150, 100, 1000, 600);
        // 菜单栏
        MenuBar menuBar = new MenuBar();
        // 菜单
        Menu menu = new Menu("File");
        // 菜单项
        newProject = new MenuItem("New Project");
        openFileBtn = new MenuItem("open");
        menuItem = new MenuItem("exit");
        // 菜单添加菜单项
        menu.add(newProject);
        menu.add(openFileBtn);
        menu.add(menuItem);

        // 菜单栏添加菜单
        menuBar.add(menu);
        frame.setMenuBar(menuBar);
        // 文本域
        ta = new TextArea();
        frame.add(ta);

        dialog = new Dialog(frame, "New Project Detail", true);// 弹出的对话框
        dialog.setBounds(400, 300, 400, 200);// 设置弹出对话框的位置和大小
        dialog.setLayout(new FlowLayout());// 设置弹出对话框的布局
        JLabel jlb1 = new JLabel("Name:", JLabel.CENTER);
        field = new JTextField(28);
        JLabel jlb2 = new JLabel("fielPath:", JLabel.CENTER);
        field2 = new JTextField(20);
        ok = new JButton("OK");
        cancel = new JButton("Cancel");
        chooseFileBtn = new JButton("choose file");
        dialog.add(jlb1);
        dialog.add(field);
        dialog.add(jlb2);
        dialog.add(field2);
        dialog.add(chooseFileBtn);
        dialog.add(ok, JButton.CENTER_ALIGNMENT);
        dialog.add(cancel, JButton.CENTER_ALIGNMENT);
        rootTree = preDealTrees(false);

        JTree tree = new JTree(new DefaultMutableTreeNode("Test Case Diretory"));

        JTabbedPane jtp = new JTabbedPane();
        JLabel jLabel = new JLabel("Settings");
        JLabel jLabel2 = new JLabel("Scripts");
        JLabel jLabel3 = new JLabel("Run");

        jtp.addTab("Settings", jLabel);
        jtp.addTab("Scripts", jLabel2);
        jtp.addTab("Run", jLabel3);

        JScrollPane js = new JScrollPane(rootTree);
        js.add(tree);
        //new JSplitPane(JSplitPane.WIDTH, js, jtp);
        JSplitPane sp = new JSplitPane(JSplitPane.WIDTH, js, jtp);
        frame.add(sp);
        frame.setVisible(true);

    }

    private JTree preDealTrees(Boolean flag) {
        System.out.println("defaultProjectName：" + defaultProjectName);
        if (StringUtils.isBlank(defaultProjectName)) {
            rootNode = new DefaultMutableTreeNode("Test Case Directory");
            treeModel = new DefaultTreeModel(rootNode);
            System.out.println("treeModel:" + treeModel.getChildCount(rootNode));
            rootTree = new JTree(treeModel);
            return rootTree;
        } else {
            DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(defaultProjectName);
            rootNode.add(newChild);
            Map<String, CaseEntity> caseMap = treeData.get(defaultProjectName);
            for (String caseName : caseMap.keySet()) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(caseName);
                rootNode.add(node);
            }
            System.out.println("rootNode节点数：" + rootNode.getLeafCount());
            if (flag) {
                treeModel.reload();
                System.out.println("重加载treeModel:" + treeModel.getChildCount(rootNode));
            }
            rootTree = new JTree(treeModel);
            return rootTree;
        }
    }

    /**
     *
     */
    public void addEvents() {

        rootTree.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseExited(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseClicked(MouseEvent e) {
                //BUTTON1_MASK（左键），还是BUTTON3_MASK（右键）。


                if (e.getModifiers() == InputEvent.BUTTON1_MASK) {

                }
            }
        });

        rootTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {

                DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();
                if (a.isLeaf()) {
                    // 叶子点击逻辑
                    String projectName = rootNode.getUserObject().toString();
                    CaseEntity caseEntity = treeData.get(projectName).get(a.getUserObject().toString());
                    String browserVersion = caseEntity.getBrowserVersion();
                    String caseScript = caseEntity.getCaseScript();
                } else {
                }
            }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        newProject.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(true);
            }
        });

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String projectName = field.getText();
                defaultProjectName = projectName;
                treeData.put(projectName, new HashMap<>());
                System.out.println("ok：treeData 大小" + treeData.size() + "defaultProjectName:" + defaultProjectName);
                preDealTrees(true);
                System.out.println(treeModel.getChildCount(rootNode));

                dialog.setVisible(false);
            }
        });

        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.setVisible(false);
            }
        });

        // 对话框监听器
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                dialog.setVisible(false);// 设置对话框不可见

            }

        });

        // 菜单点击
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        /* 选择文件 */
        chooseFileBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser("D:\\");

                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.showDialog(new JLabel(), "选择文件夹");
                File selectedFile = fileChooser.getSelectedFile();
                if (null == selectedFile) {
                    return;
                } else {
                    String absolutePath = selectedFile.getAbsolutePath();
                    field2.setText(absolutePath);
                    defaultSavePath = absolutePath;
                    System.out.println(absolutePath);

                }
            }
        });

        // 打开文件
        openFileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                fileChooser = new JFileChooser("D:\\");

                fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fileChooser.showDialog(new JLabel(), "打开文件");

                String absolutePath = fileChooser.getSelectedFile().getAbsolutePath();
                System.out.println(absolutePath);
                File file = new File(absolutePath);
                try {
                    @SuppressWarnings("resource")
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    String line;
                    StringBuilder text = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append("\r\n");
                    }
                    System.out.println(text.toString());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new MenuBarDemo();
    }

}