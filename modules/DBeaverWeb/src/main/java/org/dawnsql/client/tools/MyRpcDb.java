package org.dawnsql.client.tools;


import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.dawnsql.client.rpc.MyRpcClient;

import java.util.*;

public class MyRpcDb {

    private static Gson gson = new GsonBuilder()
            .enableComplexMapKeySerialization()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private static MyRpcClient instance = new MyRpcClient();

    public static HashMap<String, Object> getHm(final String rs)
    {
        HashMap<String, Object> my_rs = null;
        try
        {
            my_rs = gson.fromJson(rs, new TypeToken<HashMap<String, Object>>() {}.getType());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return my_rs;
    }

    /**
     * 读取所有的 schema
     * */
    public static List<HashMap<String, String>> get_schemas(String user_token)
    {
        List<HashMap<String, String>> root = new ArrayList<>();
        String rs = instance.executeSqlQuery(user_token, "SELECT SCHEMA_NAME FROM sys.SCHEMAS", "schema");
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            List<List<?>> my_rs = (List<List<?>>) ht.get("vs");
            for (List<?> r : my_rs) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", r.get(0).toString());
                hashMap.put("text", r.get(0).toString());
                root.add(hashMap);
            }
        }
        else {
            List<List<?>> my_rs = gson.fromJson(rs, new TypeToken<List<List<?>>>() {
            }.getType());
            for (List<?> r : my_rs) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", r.get(0).toString());
                hashMap.put("text", r.get(0).toString());
                root.add(hashMap);
            }
        }
        return root;
    }

    /**
     * 读取所有的 schema
     * */
    public static List<HashMap<String, Object>> get_tables(String sys_table)
    {
        List<HashMap<String, Object>> root = new ArrayList<>();
        if (sys_table.toUpperCase().equals("SYS")) {
            String[] my_rs = new String[]{"BASELINE_NODES", "CACHES", "CACHE_GROUPS", "CACHE_GROUP_PAGE_LISTS", "CLIENT_CONNECTIONS", "CONTINUOUS_QUERIES", "DATASTREAM_THREADPOOL_QUEUE", "DATA_REGION_PAGE_LISTS", "DS_ATOMICLONGS", "DS_ATOMICREFERENCES", "DS_ATOMICSEQUENCES", "DS_ATOMICSTAMPED", "DS_COUNTDOWNLATCHES", "DS_QUEUES", "DS_REENTRANTLOCKS", "DS_SEMAPHORES", "DS_SETS", "INDEXES", "JOBS", "LOCAL_CACHE_GROUPS_IO", "METRICS", "NODES", "NODE_ATTRIBUTES", "NODE_METRICS", "SCAN_QUERIES", "SCHEMAS", "SQL_QUERIES", "SQL_QUERIES_HISTORY", "STATISTICS_CONFIGURATION", "STATISTICS_LOCAL_DATA", "STATISTICS_PARTITION_DATA", "STRIPED_THREADPOOL_QUEUE", "TABLES", "TABLE_COLUMNS", "TASKS", "TRANSACTIONS", "VIEWS", "VIEW_COLUMNS"};
            for (String r : my_rs) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", r);
                hashMap.put("text", r);
                hashMap.put("schema", "sys");
                hashMap.put("leaf", true);
                root.add(hashMap);
            }
        }

        return root;
    }

    /**
     * 读取所有的 schema
     * */
    public static List<HashMap<String, Object>> get_tables(String user_token, String schema_name)
    {
        List<HashMap<String, Object>> root = new ArrayList<>();
        String rs = instance.executeSqlQuery(user_token, String.format("SELECT TABLE_NAME FROM sys.TABLES WHERE SCHEMA_NAME = '%s'", schema_name), "schema");
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            List<List<?>> my_rs = (List<List<?>>) ht.get("vs");
            for (List<?> r : my_rs) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", r.get(0).toString());
                hashMap.put("text", r.get(0).toString());
                hashMap.put("schema", schema_name);
                hashMap.put("leaf", true);
                root.add(hashMap);
            }
        }
        else {
            List<List<?>> my_rs = gson.fromJson(rs, new TypeToken<List<List<?>>>() {
            }.getType());
            for (List<?> r : my_rs) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", r.get(0).toString());
                hashMap.put("text", r.get(0).toString());
                hashMap.put("schema", schema_name);
                hashMap.put("leaf", true);
                root.add(hashMap);
            }
        }

        return root;
    }

    /**
     * 读取表的列
     * */
    public static HashMap<String, Object> read_column_meta(String user_token, String schema_name, String table_name)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            String rs = instance.executeSqlQuery(user_token, String.format("SELECT * FROM %s.%s", schema_name, table_name), "meta");
            HashMap<String, Object> ht = getHm(rs);
            if (ht != null && ht.containsKey("vs"))
            {
                Map<String, Integer> my_rs = (Map<String, Integer>) ht.get("vs");
                hashMap.put("success", my_rs);
            }
            else {
                Map<String, Integer> my_rs = gson.fromJson(rs, new TypeToken<Map<String, Integer>>() {
                }.getType());
                hashMap.put("success", my_rs);
            }
        } catch (Exception e)
        {
            String rs = instance.executeSqlQuery(user_token, String.format("SELECT * FROM %s.%s", schema_name, table_name), "meta");
            HashMap<String, Object> ht = getHm(rs);
            if (ht != null && ht.containsKey("vs"))
            {
                Map<String, String> my_rs = (Map<String, String>) ht.get("vs");
                hashMap.put("err", my_rs);
            }
            else {
                Map<String, String> my_rs = gson.fromJson(rs, new TypeToken<Map<String, Integer>>() {
                }.getType());
                hashMap.put("err", my_rs);
            }
        }
        return hashMap;
    }

    /**
     * 判断是否注册
     * */
    public static List<List<?>> re_register(String group_name)
    {
        String rs = instance.executeSqlQuery("", String.format("SELECT m.id FROM MY_META.MY_USERS_GROUP m WHERE m.GROUP_NAME = '%s'", group_name), "schema");
        List<List<?>> my_rs;
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            my_rs = (List<List<?>>) ht.get("vs");
        }
        else
        {
            my_rs = gson.fromJson(rs, new TypeToken<List<List<?>>>(){}.getType());
        }
        return my_rs;
    }

    /**
     * 登录
     * */
    public static List<List<?>> login(String user_token)
    {
        String rs = instance.executeSqlQuery("", String.format("SELECT m.id FROM MY_META.MY_USERS_GROUP m WHERE m.USER_TOKEN = '%s'", user_token), "schema");
        List<List<?>> my_rs;
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            my_rs = (List<List<?>>) ht.get("vs");
        }
        else
        {
            my_rs = gson.fromJson(rs, new TypeToken<List<List<?>>>(){}.getType());
        }
        return my_rs;
    }

    /**
     * 登录
     * */
    public static int register_db(String group_name, String user_token)
    {
        String rs = instance.executeSqlQuery("", String.format("create schema %s;add_user_group('%s', '%s', 'all', '%s');", group_name, group_name + user_token), "my_meta");
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            if (!Strings.isNullOrEmpty(String.valueOf(ht.get("vs"))))
                return 1;
        }
        else {
            if (!Strings.isNullOrEmpty(rs))
                return 1;
        }
        return 0;
    }

    /**
     * 读取表的 count
     * */
    public static Integer get_table_count(String user_token, String schema, String table_name)
    {
        String rs = instance.executeSqlQuery(user_token, String.format("SELECT count(*) FROM %s.%s", schema, table_name), "count");
        Integer my_rs = 0;
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            my_rs = ((Double)ht.get("vs")).intValue();
        }
        else {
            my_rs = gson.fromJson(rs, new TypeToken<Integer>() {}.getType());
        }
        return my_rs;
    }

    /**
     * 读取表的 row
     * */
    public static String get_table_row(String user_token, String schema, String table_name, Integer start, Integer limit)
    {
        HashMap<String, Integer> ps = new HashMap<>();
        ps.put("start", start);
        ps.put("limit", limit);
        ps.put("row", 1);
        ps.put("data", 1);
        String rs = instance.executeSqlQuery(user_token, String.format("SELECT * FROM %s.%s", schema, table_name), gson.toJson(ps));
        HashMap<String, Object> ht = getHm(rs);
        if (ht != null && ht.containsKey("vs"))
        {
            return ht.get("vs").toString();
        }
        return rs;
    }

    public static Boolean isSelect(Map<String, String> stm)
    {
        if (stm != null && stm.containsKey("select"))
            return true;
        return false;
    }

    /**
     * 执行 select 语句
     * */
    public static HashMap<String, Object> run_select_meta(String user_token, String sql)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            String rs = instance.executeSqlQuery(user_token, sql, "meta");
            HashMap<String, Object> ht = getHm(rs);
            if (ht != null && ht.containsKey("vs"))
            {
                if (isSelect((Map<String, String>) ht.get("stm")))
                {
                    Map<String, Integer> my_rs = (Map<String, Integer>) ht.get("vs");
                    hashMap.put("success", my_rs);
                }
                else
                {
                    hashMap.put("success", ht.get("vs").toString());
                }
            }
            else {
                Map<String, Integer> my_rs = gson.fromJson(rs, new TypeToken<Map<String, Integer>>() {}.getType());
                hashMap.put("success", my_rs);
            }
        } catch (Exception e) {
            String rs = instance.executeSqlQuery(user_token, sql, "meta");
            HashMap<String, Object> ht = getHm(rs);
            if (ht != null && ht.containsKey("vs")) {
                if (isSelect((Map<String, String>) ht.get("stm")))
                {
                    Map<String, String> my_rs = (Map<String, String>) ht.get("vs");
                    hashMap.put("err", my_rs);
                }
                else {
                    hashMap.put("err", ht.get("vs").toString());
                }
            } else {
                Map<String, String> my_rs = gson.fromJson(rs, new TypeToken<Map<String, String>>() {
                }.getType());
                hashMap.put("err", my_rs);
            }
        }
        return hashMap;
    }

    /**
     * 执行 Dawn Sql 语句
     * */
    public static HashMap<String, Object> run_dawn_sql(String user_token, String sql) {
        String rs = instance.executeSqlQuery(user_token, sql, "");
        try {
            HashMap<String, Object> my_rs = gson.fromJson(rs, new TypeToken<HashMap<String, Object>>() {
            }.getType());
            if (my_rs.containsKey("err")) {
                return my_rs;
            } else if (my_rs.containsKey("vs")) {
                if (isSelect((Map<String, String>) my_rs.get("stm")))
                {
                    HashMap<String, Object> ht = new HashMap<>();
                    ht.put("msg", my_rs.get("vs"));
                    return ht;
                }
                else {
                    HashMap<String, Object> ht = new HashMap<>();
                    ht.put("msg", my_rs.get("vs"));
                    return ht;
                }
            } else {
                HashMap<String, Object> ht = new HashMap<>();
                ht.put("msg", rs);
                return ht;
            }
        } catch (Exception e) {
            HashMap<String, Object> ht = new HashMap<>();
            ht.put("msg", rs);
            return ht;
        }
    }

    /**
     * 执行 Dawn Sql 语句
     * */
    public static String run_my_dawn_sql(String user_token, String sql) {
        String rs = instance.executeSqlQuery(user_token, sql, "");
        try {
            HashMap<String, Object> ht = getHm(rs);
            if (ht != null && ht.containsKey("stm"))
            {
                if (isSelect((Map<String, String>) ht.get("stm")))
                {
                    // 是否返回的是 meta
                    Map<String, String> mcode = (Map<String, String>) ht.get("stm");
                    if (mcode.containsKey("code"))
                    {
                        Map<String, Integer> rs_meta = (Map<String, Integer>) ht.get("vs");
                        HashMap<String, Object> ht_rs = new HashMap<>();
                        List<String> columns_name = new ArrayList<>();
                        List<HashMap<String, String>> columns = new ArrayList<>();
                        for (String k : rs_meta.keySet()) {
                            columns_name.add(k);
                            HashMap<String, String> cm = new HashMap<>();
                            cm.put("text", k);
                            cm.put("dataIndex", k);
                            cm.put("tooltip", k);
                            columns.add(cm);
                        }
                        ht_rs.put("columns_name", columns_name);
                        ht_rs.put("columns", columns);
                        //ht_rs.put("code", mcode.get("code"));
                        ht_rs.put("stm", ht.get("stm"));
                        return gson.toJson(ht_rs);
                    }
                }
                else
                {
                    HashMap<String, Object> ht_1 = new HashMap<>();
                    ht_1.put("msg", ht.get("vs").toString());
                    return gson.toJson(ht_1);
                }
            }
            else if (ht != null && ht.containsKey("err"))
            {
                HashMap<String, Object> ht_1 = new HashMap<>();
                ht_1.put("msg", ht.get("err").toString());
                return gson.toJson(ht_1);
            }
            else
            {
                HashMap<String, Object> ht_1 = new HashMap<>();
                ht_1.put("msg", rs);
                return  gson.toJson(ht_1);
            }
        } catch (Exception e) {
            HashMap<String, Object> ht = new HashMap<>();
            ht.put("msg", rs);
            return  gson.toJson(ht);
        }
        return rs;
    }

    /**
     * 执行 select 语句
     * */
    public static String run_select(String user_token, String sql, Integer start, Integer limit)
    {
        HashMap<String, Integer> ps = new HashMap<>();
        ps.put("start", start);
        ps.put("limit", limit);
        ps.put("select", 1);
        ps.put("row", 1);
        ps.put("data", 1);
        String rs = instance.executeSqlQuery(user_token, sql, gson.toJson(ps));
        HashMap<String, Object> my_rs = getHm(rs);
        if (my_rs != null) {
            if (my_rs.containsKey("vs")) {
                String myvs = my_rs.get("vs").toString();
                return myvs;
            } else if (my_rs.containsKey("err")) {
                List<HashMap<String, String>> lstrs = new ArrayList<>();
                HashMap<String, String> map = new HashMap<>();
                map.put("err", my_rs.get("err").toString());
                lstrs.add(map);
                return gson.toJson(lstrs);
            }
        }
        //List<List<?>> my_rs = gson.fromJson(rs, new TypeToken<List<List<?>>>(){}.getType());
        return rs;
    }

    /**
     * load code to db
     * */
    public static HashMap<String, Object> load_code(String user_token, String sql)
    {
        String rs = instance.executeSqlQuery(user_token, sql, "load");
        if (Strings.isNullOrEmpty(rs))
        {
            HashMap<String, Object> ht = new HashMap<>();
            ht.put("msg", "保存成功！");
            return ht;
        }
        else
        {
            try {
                HashMap<String, Object> my_rs = gson.fromJson(rs, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                HashMap<String, Object> ht = new HashMap<>();
                ht.put("msg", my_rs.get("err"));
                return ht;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        HashMap<String, Object> ht = new HashMap<>();
        ht.put("msg", "保存失败！");
        return ht;
    }
}
