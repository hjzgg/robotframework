package com.wmh.robotframework.gui;

import com.alibaba.fastjson.JSONObject;
import com.wmh.robotframework.bean.CaseEntity;
import com.wmh.robotframework.browser.DriverManagerType;
import com.wmh.robotframework.browser.WebDriverManagerException;
import com.wmh.robotframework.config.ReadProperties;
import com.wmh.robotframework.service.TestProperties;
import com.wmh.robotframework.service.TestService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class GUIDemo {
	Logger LOGGER = LoggerFactory.getLogger(GUIDemo.class);

	@Autowired
	private TestService testService;

	private static Boolean initFlag = false;
	private Frame frame;
	private MenuItem menuItem;
	private MenuItem openFileBtn;
	private MenuItem newProject;
	private Dialog dialog;
	private JButton ok;
	private JButton cancel;
	private JButton chooseFileBtn;
	private JTextField field;
	private JTextField field2;
	private JTextField version;
	public static JTextArea logTextArea = new JTextArea("测试日志", 10, 10);
	private JTextArea scripts;
	private JTree rootTree;
	private JButton runButton;
	private JButton rtnButton;

	private DefaultMutableTreeNode rootNode;
	DefaultTreeModel treeModel;
	private JFileChooser fileChooser;
	private JComboBox<String> jcb1;
	private List<String> browserVersion;
	private static Map<String, CaseEntity> treeData;

	private Map<String, String> versionMap = new ReadProperties("src/main/resources/versions.properties")
			.getProperties();

	public GUIDemo(TestService testService) {
		super();
		this.testService = testService;
	}

	private void initTreeMapData() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File("src/main/resources/treeMapData")));
			String content;
			while ((content = br.readLine()) != null) {
				LOGGER.info("初始化数据：" + content);
				JSONObject jsonObject = JSONObject.parseObject(content);
				for (Entry<String, Object> entry : jsonObject.entrySet()) {
					if (null == treeData) {
						treeData = new HashMap<>();
					}
					treeData.put(entry.getKey(), JSONObject.parseObject(entry.getValue().toString(), CaseEntity.class));
					initFlag = true;
					LOGGER.info("初始化后jtree数据：" + JSONObject.toJSONString(treeData));
				}
			}
		} catch (Exception e) {
			initFlag = false;
			if (null == treeData) {
				treeData = new HashMap<>();
			}
			LOGGER.info("未找到指定文件");
		} finally {
			try {
				br.close();
			} catch (Exception e) {
				LOGGER.info("初始化失败");
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
		field = new JTextField(55);
		JLabel jlb2 = new JLabel("fielPath:", JLabel.CENTER);
		field2 = new JTextField(30);
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
			Map<String ,String > temp = new HashMap<>();
			for (Entry<String, String> entry : versionMap.entrySet()) {
				
				String browserName = entry.getKey().substring(0, entry.getKey().length() - 2);
				if(!temp.containsKey(browserName)) {
					temp.put(browserName, "1");
					browserVersion.add(browserName);
				}
				
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
		rtnButton = new JButton("View the results");
		runPanel.add(runButton);
		runPanel.add(rtnButton);
		logTextArea.setLineWrap(true);
		JScrollPane logPanel = new JScrollPane(logTextArea);

		JSplitPane jpanel3 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, runPanel, logPanel);

		jtp.addTab("Run", jpanel3);

		return jtp;
	}

	private JTree preDealTrees(Boolean flag) {
		LOGGER.info("jtree数据：" + JSONObject.toJSONString(treeData));
		rootNode = new DefaultMutableTreeNode("Test Case Directory");
		treeModel = new DefaultTreeModel(rootNode);
		LOGGER.info("treeModel:" + treeModel.getChildCount(rootNode));
		rootTree = new JTree(treeModel);

		if (initFlag) {

			for (Entry<String, CaseEntity> entry : treeData.entrySet()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getKey());
				rootNode.add(node);
			}
			LOGGER.info("rootNode节点数：" + rootNode.getLeafCount());
			if (flag) {
				treeModel.reload();
				LOGGER.info("重加载treeModel:" + treeModel.getChildCount(rootNode));
			}
		}
		return rootTree;
	}

	private void reloadTrees(CaseEntity caseEntity) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(caseEntity.getCaseName());
		rootNode.add(node);
		LOGGER.info("rootNode节点数：" + rootNode.getLeafCount());
		treeModel.reload();
		LOGGER.info("重加载treeModel:" + treeModel.getChildCount(rootNode));
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
				LOGGER.error("saveData error...", e);
			}
		}

		LOGGER.info("实例化 treeData成功");
	}

	/**
	 *
	 */
	public void addEvents() {

		rtnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();
				if (null == a) {
					JOptionPane.showConfirmDialog(null, "Please choose a test case", "Warning",
							JOptionPane.YES_NO_OPTION);
					return;
				} else {
					CaseEntity caseEntity = treeData.get(a.getUserObject().toString());
					String saveDirecotry = caseEntity.getSaveDirecotry();
					String filePath = "file:///" +  (saveDirecotry + "/log.html").replace('\\', '/');
					openBorwser(filePath);
				}

			}
		});
		runButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				TestProperties tp = new TestProperties();

				String versionText = version.getText();
				String scriptText = scripts.getText();
				DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();

				if (null == a || null == treeData.get(a.getUserObject().toString()) || StringUtils.isBlank(versionText)
						|| StringUtils.isBlank(scriptText)) {
					JOptionPane.showConfirmDialog(null, "Please create a test case", "Warning",
							JOptionPane.YES_NO_OPTION);
					return;
				}

				String projectName = a.getUserObject().toString();
				CaseEntity caseEntity = treeData.get(a.getUserObject().toString());

				caseEntity.setBrowserVersion(versionText);
				caseEntity.setCaseScript(scriptText);
				saveFile(caseEntity);
				treeData.put(projectName, caseEntity);
				saveData();

				for (Entry<String, CaseEntity> string : treeData.entrySet()) {
					LOGGER.info(string.getValue().toString());
				}

				tp.setDriverManagerType(getDriverType(versionText.toUpperCase()));
				tp.setOutputDirectory(caseEntity.getSaveDirecotry());
				tp.setTestCaseId(caseEntity.getCaseName());
				tp.setTestCasesDirectory(caseEntity.getSaveDirecotry());

				testService.execute(tp);

			}
		});

		rootTree.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// BUTTON1_MASK（左键），还是BUTTON3_MASK（右键）。

				if (e.getModifiers() == InputEvent.BUTTON1_MASK) {
					DefaultMutableTreeNode a = (DefaultMutableTreeNode) rootTree.getLastSelectedPathComponent();
					if (a.isLeaf()) {
						String projectName = a.getUserObject().toString();
						LOGGER.info("projectName" + projectName);
						for (Entry<String, CaseEntity> entry : treeData.entrySet()) {
							LOGGER.info(entry.getValue().toString());
						}
						CaseEntity caseEntity = treeData.get(projectName);
						String browserVersion = caseEntity.getBrowserVersion();
						String caseScript = caseEntity.getCaseScript();

						if (StringUtils.isNotBlank(caseScript)) {
							scripts.setText(caseScript);
						}
						if (StringUtils.isNotBlank(browserVersion)) {
							jcb1.setSelectedItem(browserVersion);
						}

						LOGGER.info("caseScript" + caseScript);
						LOGGER.info("savePath" + caseEntity.getSaveDirecotry());
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
				String savaPath = field2.getText();
				if (StringUtils.isBlank(projectName) || StringUtils.isBlank(savaPath)) {
					JOptionPane.showConfirmDialog(null, "Name or filePaht can't be null", "Warning",
							JOptionPane.YES_NO_OPTION);
					return;
				}

				CaseEntity caseEntity = new CaseEntity();

				caseEntity.setCaseName(projectName);
				caseEntity.setSaveDirecotry(savaPath);
				treeData.put(projectName, caseEntity);
				saveData();
				LOGGER.info("ok：treeData 大小" + treeData.size() + "defaultProjectName:" + projectName);
				reloadTrees(caseEntity);
				LOGGER.info("treeModel大小：" + treeModel.getChildCount(rootNode));

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
					LOGGER.info(absolutePath);

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
				LOGGER.info(absolutePath);
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
					LOGGER.info(text.toString());
				} catch (Exception e1) {
					LOGGER.info(e1.getMessage());
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

	private void saveFile(CaseEntity caseEntity) {
		BufferedWriter br = null;
		try {
			String saveDirecotry = caseEntity.getSaveDirecotry();
			String caseName = caseEntity.getCaseName();
			String caseScript = caseEntity.getCaseScript();
			String filePath = saveDirecotry + "/" + caseName + ".robot";
			br = new BufferedWriter(new FileWriter(new File(filePath)));
			br.write(caseScript);
			br.flush();

		} catch (IOException e) {
		} finally {
			try {
				br.close();
			} catch (IOException e) {
			}
		}
	}

	public static void openBorwser(String url) {
		// 判断当前系统是否支持Java AWT Desktop扩展
		if (java.awt.Desktop.isDesktopSupported()) {
			try {
				// 创建一个URI实例
				java.net.URI uri = java.net.URI.create(url);
				// 获取当前系统桌面扩展
				java.awt.Desktop dp = java.awt.Desktop.getDesktop();
				// 判断系统桌面是否支持要执行的功能
				if (dp.isSupported(java.awt.Desktop.Action.BROWSE)) {
					// 获取系统默认浏览器打开链接
					dp.browse(uri);
				}
			} catch (java.io.IOException e) {
				// 此为无法获取系统默认浏览器
			}
		}
	}
}