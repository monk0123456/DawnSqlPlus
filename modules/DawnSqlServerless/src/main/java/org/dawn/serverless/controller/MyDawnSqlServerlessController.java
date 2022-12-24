package org.dawn.serverless.controller;

import org.apache.ignite.internal.jdbc.thin.JdbcThinConnection;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

@Controller
public class MyDawnSqlServerlessController {

    @RequestMapping(value = "/serverless")
    public @ResponseBody
    String serverless(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                      @RequestParam(value = "user_token", required = false) String user_token,
                      @RequestParam(value = "func", required = false) String func,
                      @RequestParam(value = "params", required = false) String params) throws SQLException, ClassNotFoundException {

        Class.forName("org.apache.ignite.IgniteJdbcDriver");
        String url = "jdbc:ignite:thin://127.0.0.1:10800/public?lazy=true";
        JdbcThinConnection conn = (JdbcThinConnection) DriverManager.getConnection(url);
        conn.setUserToken(user_token);

        String funcLine = func + "(" + params + ")";

        Object ob = null;
        PreparedStatement stmt = conn.prepareStatement(funcLine);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            ob = rs.getObject(1);
        }
        rs.close();
        stmt.close();
        conn.close();
        return ob.toString();
    }
}
