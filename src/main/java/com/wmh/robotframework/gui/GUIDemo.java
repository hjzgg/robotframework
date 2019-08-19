package com.wmh.robotframework.gui;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wmh.robotframework.bean.CaseEntity;
import com.wmh.robotframework.browser.DriverManagerType;
import com.wmh.robotframework.browser.WebDriverManagerException;
import com.wmh.robotframework.config.ReadProperties;
import com.wmh.robotframework.service.TestProperties;
import com.wmh.robotframework.service.TestService;

@Component
public class GUIDemo {

	@Autowired
	private TestService testService;

	private static Boolean initFlag = false;
	private Frame frame;
	private MenuItem menuItem;
	private MenuItem openFileBtn;
	private MenuItem newProject;
	private TextArea ta;
	private Dialog dialog;
	private JButton ok;
	private JButton cancel;
	private JButton chooseFileBtn;
	private JTextField field;
	private JTextField field2;
	private JTextField version;
	public static JTextArea logTextArea;
	private JTextArea scripts;
	private JTree rootTree;
	private JButton runButton;
	private DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;
	private JFileChooser fileChooser;
	private JComboBox<String> jcb1;
	private List<String> browserVersion;
	private static Map<String, CaseEntity> treeData;
	/*
	 * private String defaultProjectName; private String defaultSavePath;
	 */
	private Map<String, String> versionMap = new ReadProperties("src/main/resources/versions.properties")
			.getProperties();

	public void initTreeMapData() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("src/main/resources/treeMapData")));
			String content;
			while ((content = br.readLine()) != null) {
				System.out.println("初始化数据：" + content);
				JSONObject jsonObject = JSONObject.parseObject(content);
				for (Entry<String, Object> entry : jsonObject.entrySet()) {
					if (null == treeData) {
						treeData = new HashMap<>();
					}
					treeData.put(entry.getKey(), JSONObject.parseObject(entry.getValue().toString(), CaseEntity.class));
					initFlag = true;
					System.out.println("初始化后jtree数据：" + JSONObject.toJSONString(treeData));
				}
			}
		} catch (Exception e) {
			initFlag = false;
			System.out.println("未找到指定文件");
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				System.out.println("初始化失败");
				initFlag = false;

			}
		}
	}

	@PostConstruct
	public void init() {
		initTreeMapData();

		// 设置界面风格
		try {
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		} catch (Exception e) {
			// TODO
		}

		frame = new JFrame("WMHRobotFWindow");

		// 设置图标
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
		/*
		 * ta = new TextArea(); frame.add(ta);
		 */

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
		rootTree = preDealTrees(initFlag);

		JTree tree = new JTree(new DefaultMutableTreeNode("Test Case Diretory"));

		JTabbedPane jtp = initTabbedPane();

		JScrollPane js = new JScrollPane(rootTree);
		js.add(tree);
		JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, js, jtp);
		sp.setDividerLocation(200);
		sp.setEnabled(false);
		frame.add(sp);
		frame.setVisible(true);

		addEvents();

	}

	private JTabbedPane initTabbedPane() {
		JTabbedPane jtp = new JTabbedPane();
		if (null == browserVersion) {
			browserVersion = new ArrayList<>();
			for (Entry<String, String> entry : versionMap.entrySet()) {
				browserVersion.add(entry.getKey().substring(0, entry.getKey().length() - 2) + "---" + entry.getValue());
			}
		}

		JPanel jpanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("                浏览器:               ");
		version = new JTextField(20);
		JLabel label2 = new JLabel("          ");

		String[] arr = new String[browserVersion.size()];
		String[] browser = browserVersion.toArray(arr);

		jcb1 = new JComboBox<>(browser);
		jcb1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				version.setText(jcb1.getSelectedItem().toString());
			}
		});
		jpanel.add(label);

		jpanel.add(version);
		jpanel.add(label2);

		jpanel.add(jcb1);

		scripts = new JTextArea("请输入测试脚本", 8, 10);
		scripts.setLineWrap(true);
		JScrollPane jpanel2 = new JScrollPane(scripts);
		jtp.addTab("Settings", jpanel);
		jtp.addTab("Scripts", jpanel2);

		JPanel runPanel = new JPanel();
		runButton = new JButton("Run");
		runPanel.add(runButton);

		logTextArea = new JTextArea("测试日志", 10, 10);
		logTextArea.setLineWrap(true);
		JScrollPane logPanel = new JScrollPane(logTextArea);

		JSplitPane jpanel3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, runPanel, logPanel);

		jtp.addTab("Run", jpanel3);

		return jtp;
	}

	private JTree preDealTrees(Boolean flag) {
		System.out.println("jtree数据：" + JSONObject.toJSONString(treeData));
		rootNode = new DefaultMutableTreeNode("Test Case Directory");
		treeModel = new DefaultTreeModel(rootNode);
		System.out.println("treeModel:" + treeModel.getChildCount(rootNode));
		rootTree = new JTree(treeModel);

		if (initFlag) {

			for (Entry<String, CaseEntity> entry : treeData.entrySet()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
				rootNode.add(node);
			}
			System.out.println("rootNode节点数：" + rootNode.getLeafCount());
			if (flag) {
				treeModel.reload();
				System.out.println("重加载treeModel:" + treeModel.getChildCount(rootNode));
			}
		}
		return rootTree;
	}

	private void reloadTrees(CaseEntity caseEntity) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(caseEntity.getCaseName());
		rootNode.add(node);
		System.out.println("rootNode节点数：" + rootNode.getLeafCount());
		treeModel.reload();
		System.out.println("重加载treeModel:" + treeModel.getChildCount(rootNode));
	}

	private void saveData() {
		// 持久化 treeData
				File file = new File("src/main/resources/treeMapData");
				BufferedWriter br = null;
				try {
					br = new BufferedWriter(new FileWriter(file));
					br.write(JSONObject.toJSONString(treeData));
					br.flush();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				System.out.println("实例化 treeData成功");
	}

	
	
	
	/**
	 * 
	 */
	public void addEvents() {

		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TestProperties tp = new TestProperties();

				String versionText = version.getText();
				String scriptText = scripts.getText();
				DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();

				String projectName = a.getUserObject().toString();
				CaseEntity caseEntity = treeData.get(projectName);
				if (StringUtils.isBlank(versionText) || StringUtils.isBlank(scriptText) || null == caseEntity) {

					JOptionPane.showConfirmDialog(null, "Please create a test case", "Warning",
							JOptionPane.YES_NO_OPTION);

					return;
				}

				caseEntity.setBrowserVersion(versionText);
				caseEntity.setCaseScript(scriptText);
				treeData.put(projectName, caseEntity);
				saveData();
				
				for (Entry<String, CaseEntity> string : treeData.entrySet()) {
					System.out.println(string.getValue().toString());
				}
				System.out.println();
				String[] split = versionText.split("---");

				tp.setDriverManagerType(getDriverType(split[0].toUpperCase()));
				tp.setDriverVersion(split[1]);
				tp.setOutputDirectory(caseEntity.getSaveDirecotry());
				tp.setTestCaseId(caseEntity.getCaseName());
				tp.setTestCasesDirectory(caseEntity.getSaveDirecotry());

				testService.execute(tp);

			}
		});

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
				// BUTTON1_MASK（左键），还是BUTTON3_MASK（右键）。

				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();

					if (a.isLeaf()) {
						String projectName = a.getUserObject().toString();
						System.out.println("projectName" + projectName);
						for (Entry<String, CaseEntity> entry : treeData.entrySet()) {
							System.out.println(entry.getValue().toString());
						}
						CaseEntity caseEntity = treeData.get(projectName);
						String browserVersion = caseEntity.getBrowserVersion();
						String caseScript = caseEntity.getCaseScript();

						if (StringUtils.isNotBlank(caseScript)) {
							scripts.setText(caseScript);
						}
						if (StringUtils.isNotBlank(browserVersion)) {
							jcb1.setSelectedItem(browserVersion);
							// ddfaf
						}

						System.out.println("caseScript" + caseScript);
						System.out.println("savePath" + caseEntity.getSaveDirecotry());
					}
				}
			}
		});

		rootTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {

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
				CaseEntity caseEntity = new CaseEntity();

				caseEntity.setCaseName(projectName);
				caseEntity.setSaveDirecotry(field2.getText());
				treeData.put(projectName, caseEntity);
				saveData();
				System.out.println("ok：treeData 大小" + treeData.size() + "defaultProjectName:" + projectName);
				reloadTrees(caseEntity);
				System.out.println("treeModel大小：" + treeModel.getChildCount(rootNode));

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
					System.out.println(e1.getMessage());
				}
			}
		});
	}

	public DriverManagerType getDriverType(String type) {
		switch (type) {
		case "CHROME":
			return DriverManagerType.CHROME;
		case "FIREFOX":
			return DriverManagerType.FIREFOX;
		case "OPERA":
			return DriverManagerType.OPERA;
		case "EDGE":
			return DriverManagerType.EDGE;
		case "PHANTOMJS":
			return DriverManagerType.PHANTOMJS;
		case "IEXPLORER":
			return DriverManagerType.IEXPLORER;
		case "SELENIUM_SERVER_STANDALONE":
			return DriverManagerType.SELENIUM_SERVER_STANDALONE;
		default:
			throw new WebDriverManagerException("Invalid driver manager type: " + type);
		}
	}

}