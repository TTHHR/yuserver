package cn.qingyuyu.yuserver.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    String token, data;

    public User(String token) {
        this.token = token;
    }

    public boolean checkUser() {
        boolean b = false;
        String sql = "Select * from yuuser where token=?";
        String parameters[] = { token };
        ResultSet rs = SqlHelper.executeQuery(sql, parameters);
        try {
            if (rs.next())
                b = true;
        } catch (Exception e) {
            Log.getInstance().e("checkUser", e.toString());
            b = false;
        } finally {
            SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getCt());
        }
        return b;
    }
    public boolean insertIntoDatabase(String data)
    {
        boolean b = false;
        String sql = "Replace into yudata(token,data)values(?,?)";
        String parameters[] = { token,data };

        try {
            SqlHelper.executeUpdate(sql, parameters);
            b = true;
        } catch (SQLException e) {
            Log.getInstance().e("Insert", e.toString());
            b = false;
        }
        finally{
            SqlHelper.close(SqlHelper.getRs(), SqlHelper.getPs(), SqlHelper.getCt());
        }

        return b;
    }
    public String getDataFromBase() {
        String data = null;
        String sql = "Select * from yudata where token=?";
        String parameters[] = { token };
        ResultSet rs = SqlHelper.executeQuery(sql, parameters);
        try {
            if (rs.next())
                data=rs.getString(2);
        } catch (Exception e) {
            Log.getInstance().e("getData", e.toString());
            data="error in database";
        } finally {
            SqlHelper.close(rs, SqlHelper.getPs(), SqlHelper.getCt());
        }
        return data;
    }
}