package util;

import model.CrawlerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;

/**
 * Created by Bin on 2015/3/11.
 * 爬取信息单元类
 * 对应数据库表t_crawler_data(title, extra_imfo, content, comment, memo, oper_time, source_url)
 */
public class CrawlerDataDao {
    public static final Logger log = LoggerFactory.getLogger(CrawlerData.class);
    private final static String CLASS_FOR_NAME = "com.mysql.jdbc.Driver";
    private final static String DB_URL = "jdbc:mysql://localhost:3306/crawler";
    private final static String DB_NAME = "root";
    private final static String DB_PASSWORD = "sa";

    public static void insertData(CrawlerData crawlerData) {
        Connection conn=null;
        PreparedStatement st=null;
        java.util.Date date = new java.util.Date();//获得系统时间.
        String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);//将时间格式转换成符合Timestamp要求的格式.
        Timestamp newdate = Timestamp.valueOf(nowTime);//把时间转换
        try {
            Class.forName(CLASS_FOR_NAME);
            conn=(Connection) DriverManager.getConnection(DB_URL, DB_NAME, DB_PASSWORD);
            String sql="insert into t_crawler_cnblogs(title, extra_imfo, content_html, content_text, comment, memo, oper_time, source_url, keywords) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            st=conn.prepareStatement(sql);
            st.setString(1, crawlerData.getTitle());
            st.setString(2, crawlerData.getExtra_imfo());
            st.setString(3, crawlerData.getContent_html());
            st.setString(4, crawlerData.getContent_text());
            st.setString(5, crawlerData.getComment());
            st.setString(6, crawlerData.getMemo());
            st.setTimestamp(7, newdate);
            st.setString(8, crawlerData.getSource_url());
            st.setString(9, crawlerData.getKeywords());

            int flage = st.executeUpdate();
            if(flage == 1){
                log.info("insert data successfully.");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(st!=null)
            {
                try {
                    st.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null)
            {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
