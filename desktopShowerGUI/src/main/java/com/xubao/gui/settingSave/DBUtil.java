package com.xubao.gui.settingSave;

import java.io.File;
import java.sql.*;

/**
 * @Author xubao
 * @Date 2018/4/18
 */
public class DBUtil {
    public static String settingSaveDBName = "setting";

    public Connection getConn(String dbName) throws ClassNotFoundException, SQLException {
        Connection c = null;
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:"+dbName+".db");
       // c.setAutoCommit(false);
        return c;
    }

    public boolean isDBExist(String dbName){
        File db = new File(dbName+".db");
        if(db.exists()){
            return true;
        }
        return false;
    }

    public void beginTransaction(Connection c) throws SQLException {
        Statement statement = c.createStatement();
        statement.execute("begin transaction;");
    }

    public void commitTransaction(Connection c) throws SQLException {
        Statement statement = c.createStatement();
        statement.execute("commit transaction;");
    }

    public void dropSettingTable(Connection c) throws SQLException {
        String dropServerTableSql = "drop table if exists server_setting;";
        String dropClientTableSql = "drop table if exists client_setting;";

        Statement statement = c.createStatement();
        statement.execute(dropClientTableSql);
        statement.execute(dropServerTableSql);
    }

    public void createSettingTable(Connection c) throws SQLException {
        String createServerTableSql = "create table server_setting(" +
                "id INTEGER primary key autoincrement," +
                "share_theme varchar(100)," +
                "screen_size int," +
                "send_delay int," +
                "send_quality int," +
                "is_need_pwd bool," +
                "pwd varchare(40)" +
                ");";

        String createClientTableSql = "create table client_setting(" +
                "id INTEGER primary key autoincrement," +
                "nick_name varchar(40)," +
                "is_full_screen bool" +
                ");";

        Statement statement = c.createStatement();

        statement.execute(createClientTableSql);
        statement.execute(createServerTableSql);

        statement.close();
    }

    public ClientSetting selectClientSetting(Connection c) throws SQLException {
        ClientSetting clientSetting = null;
        Statement statement = c.createStatement();
        String querySql = "select * from client_setting where id = (select max(id) from client_setting);";
        ResultSet resultSet = statement.executeQuery(querySql);
        if(resultSet.next()){
            int id = resultSet.getInt("id");
            String nickName = resultSet.getString("nick_name");
            boolean isFullScreen = resultSet.getBoolean("is_full_screen");
            clientSetting = new ClientSetting(nickName,isFullScreen);
            clientSetting.setId(id);
        }

        return clientSetting;
    }

    public ServerSetting selectServerSetting(Connection c) throws SQLException {
        ServerSetting serverSetting = null;
        Statement statement = c.createStatement();
        String querySql = "select * from server_setting where id = (select max(id) from server_setting);";
        ResultSet resultSet = statement.executeQuery(querySql);

        if(resultSet.next()){
            int id = resultSet.getInt("id");
            String shareTheme = resultSet.getString("share_theme");
            int screenSize = resultSet.getInt("screen_size");
            int sendDelay = resultSet.getInt("send_delay");
            int sendQuality = resultSet.getInt("send_quality");
            boolean isNeedPwd = resultSet.getBoolean("is_need_pwd");
            String pwd = resultSet.getString("pwd");
            serverSetting = new ServerSetting(shareTheme,screenSize,sendDelay,sendQuality,isNeedPwd,pwd);
            serverSetting.setId(id);
        }

        return serverSetting;
    }

    public void saveClientSetting(Connection c,ClientSetting clientSetting) throws SQLException {
        String saveSql = "insert into client_setting(nick_name,is_full_screen) values(?,?);";

        PreparedStatement preparedStatement = c.prepareStatement(saveSql);
        //preparedStatement.setInt(1,clientSetting.getId());
        preparedStatement.setString(1,clientSetting.getNickName());
        preparedStatement.setBoolean(2,clientSetting.isFullScreen());

        preparedStatement.execute();
        //c.commit();
        preparedStatement.close();
    }

    public void saveServerSetting(Connection c,ServerSetting serverSetting) throws SQLException {
        String saveSql = "insert into server_setting(share_theme,screen_size,send_delay,send_quality,is_need_pwd,pwd) values(?,?,?,?,?,?);";
        PreparedStatement preparedStatement = c.prepareStatement(saveSql);

        //preparedStatement.setInt(1,serverSetting.getId());
        preparedStatement.setString(1,serverSetting.getShareTheme());
        preparedStatement.setInt(2,serverSetting.getScreenSize());
        preparedStatement.setInt(3,serverSetting.getSendDelay());
        preparedStatement.setInt(4,serverSetting.getSendQuality());
        preparedStatement.setBoolean(5,serverSetting.isNeedPwd());
        preparedStatement.setString(6,serverSetting.getPwd());

        preparedStatement.execute();

        //c.commit();

        preparedStatement.close();
    }

    public final static class SimpleDBUtil{
        private static SimpleDBUtil simpleDBUtil = new SimpleDBUtil();
        public static SimpleDBUtil getInstance(){
            return simpleDBUtil;
        }

        private DBUtil dbUtil;
        private Connection conn;
        public SimpleDBUtil(){
            dbUtil = new DBUtil();
            try {
                boolean createTable = false;
                if(!dbUtil.isDBExist(DBUtil.settingSaveDBName)){
                    createTable = true;
                }
                getConn();
                //dbUtil.dropSettingTable(conn);
                if(createTable) {
                    dbUtil.createSettingTable(conn);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void getConn(){
            try {
                conn = dbUtil.getConn(DBUtil.settingSaveDBName);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void closeConn(){
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        public void saveServerSetting(ServerSetting serverSetting) throws SQLException{
            dbUtil.beginTransaction(conn);
            dbUtil.saveServerSetting(conn,serverSetting);
           dbUtil.commitTransaction(conn);
        }
        public void saveClientSetting(ClientSetting clientSetting) throws SQLException{
            //dbUtil.beginTransaction(conn);
            dbUtil.saveClientSetting(conn,clientSetting);
           // dbUtil.commitTransaction(conn);
        }

        public ServerSetting selectServerSetting() throws SQLException{

            ServerSetting serverSetting = dbUtil.selectServerSetting(conn);

            return serverSetting;
        }

        public ClientSetting selectClientSetting() throws SQLException{

            ClientSetting clientSetting = dbUtil.selectClientSetting(conn);

            return clientSetting;
        }

    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ClientSetting c = new ClientSetting("nickName",true);
        c.setId(1);
        SimpleDBUtil.getInstance().saveClientSetting(c);

        ClientSetting clientSetting = SimpleDBUtil.getInstance().selectClientSetting();
        System.out.println(clientSetting);
    }
}
