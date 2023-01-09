package org.dawnsql.client.controller;


import com.google.common.base.Strings;
import com.google.gson.Gson;
import org.dawnsql.client.tools.MyRpcDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MyWorkbenchController {
    @Autowired
    private Gson gson;

    @RequestMapping(value = "/work")
    public String work(ModelMap model, HttpServletRequest request, HttpServletResponse response,
                       @RequestParam(value = "work_id", required = false) String work_id) throws UnsupportedEncodingException {

        HttpSession sessoin = request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (Strings.isNullOrEmpty(user_token))
        {
            return "redirect:/login";
        }

        HashMap<String, String> obj = new HashMap<>();
        obj.put("user_token", user_token);
        obj.put("work_id", work_id);
        model.addAttribute("m", obj);
        return "work";
    }

    @RequestMapping(value = "/dawnclient")
    public String dawnclient(ModelMap model, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

        HttpSession sessoin = request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (Strings.isNullOrEmpty(user_token))
        {
            return "redirect:/login";
        }

        HashMap<String, String> obj = new HashMap<>();
        obj.put("user_token", user_token);
        model.addAttribute("m", obj);
        return "dawnclient";
    }

    @RequestMapping(value = "/my_load")
    public @ResponseBody
    String my_load(ModelMap model, HttpServletRequest request,
                   HttpServletResponse response) throws UnsupportedEncodingException {

        HttpSession sessoin = request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (Strings.isNullOrEmpty(user_token))
        {
            return "1";
        }
        return "0";
    }

    /**
     * 展示数据
     * */
    @RequestMapping(value = "/data")
    public
    String data(ModelMap model, HttpServletRequest request,
                HttpServletResponse response,
                @RequestParam(value = "schema", required = false) String schema,
                @RequestParam(value = "table_name", required = false) String table_name) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        HashMap<String, Object> obj = new HashMap<>();
        obj.put("schema", schema);
        obj.put("table_name", table_name);

        StringBuilder columns_name = new StringBuilder();
        StringBuilder columns = new StringBuilder();

        HashMap<String, Object> htobj = MyRpcDb.read_column_meta(user_token, schema, table_name);
        if (htobj.containsKey("success"))
        {
            Map<String, Integer> ht = (Map<String, Integer>) htobj.get("success");
            List<String> lst = new ArrayList<>();
            lst.addAll(ht.keySet());
            for (int i = 0; i < lst.size(); i++)
            {

                if (i != lst.size() - 1)
                {
                    columns_name.append(String.format("'%s',", lst.get(i)));
                    columns.append("{" + String.format("text : '%s', dataIndex : '%s', tooltip: '%s'},", lst.get(i), lst.get(i), lst.get(i)));
                }
                else
                {
                    columns_name.append(String.format("'%s'", lst.get(i)));
                    columns.append("{" + String.format("text : '%s', dataIndex : '%s', tooltip: '%s'}", lst.get(i), lst.get(i), lst.get(i)));
                }
            }
            obj.put("columns_name", "[" + columns_name + "]");
            obj.put("columns", "[" + columns + "]");
            obj.put("vs", lst);

            model.addAttribute("m", obj);
        }

        return "data";
    }

    @RequestMapping(value = "/data_show")
    public @ResponseBody
    String data_show(ModelMap model, HttpServletRequest request,
                     HttpServletResponse response,
                     @RequestParam(value = "schema", required = false) String schema,
                     @RequestParam(value = "table_name", required = false) String table_name,
                     @RequestParam(value = "page", required = false) Integer page,
                     @RequestParam(value = "limit", required = false) Integer limit) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (!Strings.isNullOrEmpty(user_token))
        {
            int totalProperty = MyRpcDb.get_table_count(user_token, schema, table_name);
            String root = MyRpcDb.get_table_row(user_token, schema, table_name, limit * (page - 1), limit);

            return String.format("{'totalProperty': %s, 'root': %s}", String.valueOf(totalProperty), root);
        }
        HashMap<String, Object> ht = new HashMap<>();
        ht.put("totalProperty", 0);
        ht.put("root", new ArrayList<>());
        return gson.toJson(ht);
    }

    @RequestMapping(value = "/tree")
    public @ResponseBody
    String tree(ModelMap model, HttpServletRequest request,
                HttpServletResponse response,
                @RequestParam(value = "id", required = false) String id) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");

        HashMap<String, Object> ht = new HashMap<>();
        if (!Strings.isNullOrEmpty(user_token))
        {
            if (id.equals("root"))
            {
                ht.put("root", MyRpcDb.get_schemas(user_token));
            }
            else if (id.toUpperCase().equals("SYS"))
            {
                ht.put("root", MyRpcDb.get_tables(id));
            }
            else
            {
                ht.put("root", MyRpcDb.get_tables(user_token, id));
            }
        }
        return gson.toJson(ht);
    }

    @RequestMapping(value = "/run_sql")
    public @ResponseBody
    String run_sql(ModelMap model, HttpServletRequest request,
                   HttpServletResponse response,
                   @RequestParam(value = "code", required = false) String code,
                   @RequestParam(value = "select", required = false) Integer select) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        HashMap<String, Object> ht = new HashMap<>();
        if (!Strings.isNullOrEmpty(user_token))
        {
            if (select != null && select == 1) {
                HashMap<String, Object> hashMap = MyRpcDb.run_select_meta(user_token, code);
                if (hashMap.containsKey("success")) {
                    Map<String, Integer> rs = (Map<String, Integer>) hashMap.get("success");
                    List<String> columns_name = new ArrayList<>();
                    List<HashMap<String, String>> columns = new ArrayList<>();
                    for (String k : rs.keySet()) {
                        columns_name.add(k);
                        HashMap<String, String> cm = new HashMap<>();
                        cm.put("text", k);
                        cm.put("dataIndex", k);
                        cm.put("tooltip", k);
                        columns.add(cm);
                    }
                    ht.put("columns_name", columns_name);
                    ht.put("columns", columns);
                }
                else
                {
                    return gson.toJson(hashMap.get("err"));
                }
            }
            else
            {
                HashMap<String, Object> map = MyRpcDb.run_dawn_sql(user_token, code);
                return gson.toJson(map);
            }
        }
        return gson.toJson(ht);
    }

    @RequestMapping(value = "/load_code")
    public @ResponseBody
    String load_code(ModelMap model, HttpServletRequest request,
                     HttpServletResponse response,
                     @RequestParam(value = "code", required = false) String code) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (!Strings.isNullOrEmpty(user_token))
        {
            HashMap<String, Object> hashMap = MyRpcDb.load_code(user_token, code);
            return gson.toJson(hashMap);
        }
        return "";
    }

    /**
     * 运行 sql store
     * */
    @RequestMapping(value = "/run_select_sql")
    public @ResponseBody
    String run_select_sql(ModelMap model, HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "code", required = false) String code,
                          @RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "limit", required = false) Integer limit) throws UnsupportedEncodingException {

        HttpSession sessoin=request.getSession();
        String user_token = (String) sessoin.getAttribute("user_token");
        if (!Strings.isNullOrEmpty(user_token))
        {
            return MyRpcDb.run_select(user_token, code, limit * (page - 1), limit);
        }
        return "";
    }
}

