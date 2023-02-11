<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>工作台</title>
    <link href="../js/resources/ext-theme-neptune/ext-theme-neptune-all.css" rel="stylesheet"/>
    <script src="../js/ext-all.js" type="text/javascript"></script>
    <script src="../js/locale/ext-lang-zh_CN.js" type="text/javascript"></script>
    <script src="../js/MyUtils.js" type="text/javascript"></script>

    <link href="../codemirror/lib/codemirror.css" rel="stylesheet"/>
    <link href="../codemirror/addon/hint/show-hint.css" rel="stylesheet"/>
    <link href="../codemirror/theme/idea.css" rel="stylesheet"/>

    <script src="../codemirror/lib/codemirror.js" type="text/javascript"></script>
    <script src="../codemirror/mode/sql/sql.js" type="text/javascript"></script>
    <script src="../codemirror/addon/hint/show-hint.js" type="text/javascript"></script>
    <script src="../codemirror/addon/hint/sql-hint.js" type="text/javascript"></script>
    <script src="../codemirror/addon/selection/active-line.js" type="text/javascript"></script>

    <style type="text/css">
        .indexicon {
            background-image: url(../page_img/application_home.png) !important;
        }
        .applicationIcon {
            background-image:url(../page_img/application_home.png) !important;
         }

        .application_doubleIcon {
            background-image:url(../page_img/application_double.png) !important;
         }

        .application_cascadeIcon {
            background-image:url(../page_img/application_cascade.png) !important;
         }

        .tbar_synchronizeIcon {
            background-image:url(../page_img/tbar_synchronize.png) !important;
         }

        .t1{
		background-color:#ebf7ff; width:115px; border-bottom:1px solid #b5e2ff; border-right:1px solid #b5e2ff;
		font-size: 12px;
		}
		.t2{
		width:200px; border-bottom:1px solid #b5e2ff; border-right:1px solid #b5e2ff;
		font-size: 12px;
		}
    </style>

    <script type="text/javascript">
        // 解决Iframe IE加载不完全的问题
        function endIeStatus() { }
        var ShowWin;

        var work_id = '${ m.work_id }';

        var storage = new myLocalStore();
        var editor;

        var code_area_panel = Ext.create('Ext.panel.Panel', {
           anchor: '100%',
           resizable: {
               handles: 's'
           },
           listeners: {
               resize: function(obj, width, height, oldWidth, oldHeight, eOpts){
                   if (typeof(editor) != 'undefined' && editor)
                   {
                       editor.setSize('auto', height + 'px');
                   }
               }
           },
           contentEl: 'code_area'
        });

        var edit_form = Ext.create('Ext.form.Panel', {
           /**
            * 定义 eidt form
            * */
           id: 'edit_form',
           itemId: 'edite_win',
           layout: "form",
           defaults: {
               anchor: '100%'
           },
           buttonAlign: "center",
           labelAlign: "right",
           items: [code_area_panel, {
               id: 'p_grid',
               itemId: 'p_grid',
               //bodyStyle :'overflow-x:hidden;overflow-y:scroll',
               bodyStyle :'overflow-x:scroll;overflow-y:scroll',
               border: true,
               hidden: true
           }, {
               itemId: 'p_error',
               hidden: true
           }],
           listeners: {
               "afterrender": function (m) {
               }
           }
       });

       function run_sql()
       {
          var vs = Ext.String.trim(editor.getSelection());
          if (vs == '') {
              vs = Ext.String.trim(editor.getValue());
          }

          if (vs == '')
          {
              return;
          }
          else
          {
                var p_grid = edit_form.getComponent('p_grid');
                p_grid.hide();
                var p_error = edit_form.getComponent('p_error');
                p_error.removeAll();
          }

            var store_url = '/run_select_sql/?user_token=${ m.user_token }';
            Ext.Msg.wait('正在运行，请稍候...', '信息提示');
            Ext.Ajax.request({
                url: '/run_sql/',
                method: 'POST',
                params: {
                    user_token: '${ m.user_token }',
                    code: vs
                },
                success: function (response) {
                    Ext.Msg.hide();
                    var result = Ext.decode(response.responseText);
                    if (result.hasOwnProperty('err')) {
                        var p_grid = edit_form.getComponent('p_grid');
                        p_grid.hide();
                        var p_error = edit_form.getComponent('p_error');
                        p_error.removeAll();
                        p_error.add(Ext.create('Ext.Component', {
                            html: '<span style="color: red; font-weight: bolder;">' + result.err + '</span>'
                        }));
                        p_error.show();
                    }
                    else if (result.hasOwnProperty('stm'))
                    {
                         var stm = result.stm;
                        if (stm.hasOwnProperty('table') || stm.hasOwnProperty('schema'))
                        {
                            window.parent.my_tree_store();
                        }

                        if (stm.hasOwnProperty('select') && stm.hasOwnProperty('code'))
                        {
                            var store;
                            try
                            {
                                store = new Ext.data.JsonStore({
                                     proxy:{
                                         type: 'ajax',
                                         url: store_url,
                                         actionMethods: {
                                             read: 'POST'
                                         },
                                         reader: {
                                             type: 'json',
                                             root: "root",
                                             totalProperty: "totalProperty"
                                         }
                                     },
                                     pageSize: 50,
                                     //remoteSort: true,
                                     fields: result.columns_name
                                });

                                store.on('beforeload', function (store, operation, eOpts) {
                                     Ext.apply(store.proxy.extraParams, {code: stm.code});
                                });
                            }
                            catch (ex)
                            {
                                var p_grid = edit_form.getComponent('p_grid');
                                p_grid.hide();
                                var p_error = edit_form.getComponent('p_error');
                                p_error.removeAll();
                                p_error.add(Ext.create('Ext.Component', {
                                    html: '<span style="color: red; font-weight: bolder;">select 语句中查询的项要用别名，避免 extjs 转换错误！</span>'
                                }));
                                p_error.show();
                            }

                            store.load({
                                 params: {
                                     code: stm.code,
                                     start: 0,
                                     limit: 50
                                 },
                                 callback: function(records, operation, success) {
                                     if (records.length == 1 && records[0].raw.hasOwnProperty('err'))
                                     {
                                         var err = records[0].raw['err'];
                                         if (err != '') {
                                             var p_grid = edit_form.getComponent('p_grid');
                                             p_grid.hide();
                                             var p_error = edit_form.getComponent('p_error');
                                             p_error.removeAll();
                                             p_error.add(Ext.create('Ext.Component', {
                                                 html: '<span style="color: red; font-weight: bolder;">' + err + '</span>'
                                             }));
                                             p_error.show();
                                         }
                                     }
                                     else
                                     {
                                         var p_error = edit_form.getComponent('p_error');
                                         p_error.removeAll();
                                         p_error.hide();
                                     }
                                 }
                            });

                            var grid = new Ext.grid.GridPanel({
                                 autoHeight: true,
                                 stripeRows: true,
                                 store: store,
                                 columns: result.columns,
                                 //autoScroll: true,
                                 border: true,
                                 viewConfig: {
                                     columnsText: "显示/隐藏列",
                                     sortAscText: "正序排列",
                                     sortDescText: "倒序排列",
                                     forceFit: true
                                 },
                                 loadMask: {
                                     msg: '正在加载数据，请稍等．．．'
                                 },
                                 listeners: {
                                    afterrender: function(obj)
                                    {

                                    }
                                 },
                                 //bbar: bbar,
                                 dockedItems: [{
                                     xtype: 'pagingtoolbar',
                                     store: store,
                                     dock: 'bottom',
                                     displayInfo: true
                                 }]
                             });

                            var p_grid = edit_form.getComponent('p_grid');
                            p_grid.removeAll();
                            p_grid.add(grid);
                            p_grid.show();

                             grid.on('itemdblclick', function (v, record, item, index, e, eOpts) {
                                 var lst = [];
                                 for (var m in record.data)
                                 {
                                     lst.push({name: m, value: record.data[m]});
                                 }
                                 //console.log(lst);
                                 window.parent.show_win({data: lst});
                             });
                        }
                        else
                        {
                              var p_grid = edit_form.getComponent('p_grid');
                              p_grid.hide();
                              var p_error = edit_form.getComponent('p_error');
                              p_error.removeAll();
                              if (result.err != null)
                              {
                                  p_error.add(Ext.create('Ext.Component', {
                                      html: '<span style="color: red; font-weight: bolder;">' + result.err + '</span>'
                                  }));
                              }
                              else if (result.vs.msg != null)
                              {
                                 p_error.add(Ext.create('Ext.Component', {
                                     html: result.vs.msg
                                 }));
                              }
                              p_error.show();
                        }
                    }
                    else
                    {
                          var p_grid = edit_form.getComponent('p_grid');
                          p_grid.hide();
                          var p_error = edit_form.getComponent('p_error');
                          p_error.removeAll();
                          if (result.err != null)
                          {
                              p_error.add(Ext.create('Ext.Component', {
                                  html: '<span style="color: red; font-weight: bolder;">' + result.err + '</span>'
                              }));
                          }
                          else if (result.msg != null)
                          {
                             p_error.add(Ext.create('Ext.Component', {
                                 html: result.msg
                             }));
                          }
                          p_error.show();
                    }
                },
                failure: function (response) {
                    Ext.Msg.hide();

                    var result = Ext.decode(response.responseText);
                    //console.log(response.responseText);
                    Ext.Msg.show({
                        title: '错误提示',
                        msg: result.msg,
                        buttons: Ext.Msg.OK,
                        minWidth: 100
                    });
                }
            });
       }

       function load_to_db()
       {
           var vs = Ext.String.trim(editor.getSelection());
          if (vs == '') {
              vs = Ext.String.trim(editor.getValue());
          }

          if (vs == '')
          {
              return;
          }

          Ext.Msg.wait('正在保存代码，请稍候...', '信息提示');
              Ext.Ajax.request({
                  url: '/load_code/',
                  method: 'POST',
                  params: {
                      user_token: '${ m.user_token }',
                      code: vs
                  },
                  success: function (response) {
                      Ext.Msg.hide();
                      var result = Ext.decode(response.responseText);

                      var p_grid = edit_form.getComponent('p_grid');
                      p_grid.hide();
                      var p_error = edit_form.getComponent('p_error');
                      p_error.removeAll();
                      if (result.msg != null)
                      {
                          p_error.add(Ext.create('Ext.Component', {
                              html: result.msg
                          }));
                      }
                      p_error.show();
                  },
                  failure: function (response) {
                      Ext.Msg.hide();

                      var result = Ext.decode(response.responseText);
                      Ext.Msg.show({
                          title: '错误提示',
                          msg: result.msg,
                          buttons: Ext.Msg.OK,
                          minWidth: 100
                      });
                  }
              });
       }

        Ext.onReady(function () {

             Ext.create('Ext.panel.Panel', {
                id: "panel_panel",
                 layout: 'fit',
                renderTo: Ext.getBody(),
                items: [edit_form],
                   listeners: {
                       afterrender: function (obj) {
                           document.getElementById('code_area').style = '';
                           editor = CodeMirror.fromTextArea(document.getElementById('code_area'), {//根据DOM元素的id构造出一个编辑器
                               lineNumbers: true,
                               styleActiveLine: true,
                               extraKeys: {'Ctrl': 'autocomplete'},
                               theme: "idea",//设置主题
                               //mode: 'text/x-mysql',
                               mode : 'text/x-plsql'
                               //hintOptions: {tables: Ext.JSON.decode(localStore.getItem('tables'))}
                           });
                           //editor.setSize('auto', '227px');
                           editor.on("keyup", function (cm, event) {
                               if (!cm.state.completionActive//所有的字母和'$','{','.'在键按下之后都将触发自动完成
                                   && ((event.keyCode >= 65 && event.keyCode <= 90)
                                       || event.keyCode == 52 || event.keyCode == 219
                                       || event.keyCode == 190)) {
                                   CodeMirror.commands.autocomplete(cm, null, {
                                       completeSingle: false
                                   });
                               }

                               if (work_id == '')
                               {
                                   storage.set_default_work(editor.getValue());
                               }
                               else
                               {
                                   storage.update_my_work_code(work_id, editor.getValue());
                               }
                           });

                           if (work_id == '')
                           {
                               var my_code = storage.get_default_work();
                               if (my_code != null && my_code != '') {
                                   editor.setValue(my_code);
                               }
                           }
                           else
                           {
                               var my_code = storage.get_work(work_id);
                               if (my_code != null && my_code != '') {
                                   editor.setValue(my_code);
                               }
                           }
                       }
                   },
                   bodyStyle :'overflow-x:hidden;overflow-y:scroll',
                   hidden: false
            });
        });
    </script>
</head>
<body id='my_body'>
<textarea id="code_area" style="display: none;">
</textarea>
<a href="javascript:void(0);" onclick="javascript:endIeStatus();" id="endIeStatus" style="display: none;" />
<div id="dawn_sql_show1"></div>
</body>
</html>







































