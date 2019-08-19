package com.wmh.robotframework.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

/**
 *  * 读取properties文件的内容,并返回map  *   * @author 1  *   
 */
public class ReadProperties {
	/**
	 * 文件路径
	 */
	private String filePath;

	public ReadProperties() {
		// TODO Auto-generated constructor stub
	}

	public ReadProperties(String path) {
		this.filePath = path;
	}

/**
 * 让 Map按key进行排序
 */
public static Map<String, String> sortMapByKey(Map<String, String> map) {
	if (map == null || map.isEmpty()) {
		return null;
	}
	Map<String, String> sortMap = new TreeMap<String, String>(new MapKeyComparator());
	sortMap.putAll(map);
	return sortMap;
}


	/**
	 * 获取properties文件中的内容,并返回map  
	 * 
	 * @return
	 */
	public Map<String, String> getProperties() {
		Map<String, String> map = new HashMap<String, String>();
		InputStream in = null;
		Properties p = new Properties();
		try {
			in = new BufferedInputStream(new FileInputStream(new File(this.filePath)));
			p.load(in);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<Entry<Object, Object>> entrySet = p.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			map.put((String) entry.getKey(), (String) entry.getValue());
		}
		
		
		return sortMapByKey(map);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}

//实现一个比较器类

class MapKeyComparator implements Comparator<String> {

	@Override
	public int compare(String s1, String s2) {
		return s1.compareTo(s2);  //从小到大排序
	}
	
}
