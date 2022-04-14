package com.imooc.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {
	
	private static Properties props;
	
	static {
		try {
//			InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
			InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("application.yml");
			props = new Properties();
			props.load(in);
			in.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConn() throws Exception{
//		String url = props.getProperty("spring.datasource.url");
//		String username = props.getProperty("spring.datasource.username");
//		String password = props.getProperty("spring.datasource.password");
//		String driver = props.getProperty("spring.datasource.driver-class-name");
		String url = "jdbc:mysql://127.0.0.1:3306/miaosha?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false";
		String username = "root";
		String password = "root123456";
		String driver = "com.mysql.jdbc.Driver";

		Class.forName(driver);
		return DriverManager.getConnection(url,username, password);
	}
}
