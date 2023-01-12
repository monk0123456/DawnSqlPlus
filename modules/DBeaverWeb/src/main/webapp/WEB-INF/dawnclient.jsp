<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>DawnSql编辑器</title>
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

        //var storage = new myLocalStore();
                // 解决Iframe IE加载不完全的问题
                function endIeStatus() { }
                var ShowWin;
                var my_tab_id = 'work_panel';

                var tree_store = Ext.create('Ext.data.TreeStore', {
                                                   autoLoad: false,
                                                   clearOnLoad: true,
                                                   clearRemovedOnLoad: true,
                                                   nodeParam:'id',
                                                   proxy: {
                                                       type: 'ajax',
                                                       //url: '/extjs_case/get_tree_store/',
                                                       url: '/tree/?user_token=${ m.user_token }',
                                                       reader: {
                                                           type: 'json',
                                                           root: 'root'
                                                       }
                                                   },
                                                   root: {
                                                       expanded: true,
                                                       text: '根节点'
                                                   }
                                               });

                function my_tree_store() {
                            tree_store.reload();
                        }

                var center = Ext.create('Ext.tab.Panel', {
                            //距两边间距
                            style: "padding:0 5px 0 5px",
                            region: "center",
                            //默认选中第一个
                            activeItem: 0,
                            //如果Tab过多会出现滚动条
                            enableTabScroll: true,
                            //加载时渲染所有
                            deferredRender: false,
                            layoutOnTabChange: true,
                            listeners: {
                                afterrender: function (obj) {

                                }
                            },
                            items: [{
                                xtype: "panel",
                                id: "work_panel",
                                iconCls: "indexicon",
                                title: "工作台",
                                layout: 'fit',
                                html: '<iframe id="win_work_panel" scrolling="auto" frameborder="0" width="100%" height="100%" src="/work/"></iframe>'
                            }]
                        });

                Ext.application({
                    name: 'MyEdit',
                    launch: function() {

                        // 右导航
                       var tbar = Ext.create('Ext.toolbar.Toolbar', {
                           enableOverflow: true,
                           items: [{
                               text: '运行',
                               handler: function () {
                                   document.getElementById("win_" + my_tab_id).contentWindow.run_sql();
                               }
                           }, '-', {
                               text: '保存到数据库',
                               handler: function () {
                                   document.getElementById("win_" + my_tab_id).contentWindow.load_to_db();
                               }
                           }, '-', {
                               text: '退出',
                               handler: function () {
                                   //document.getElementById("win_" + my_tab_id).contentWindow.my_refresh();
                                   //window.loaction.href = '<c:url value="/login_out/" />';
                                   document.getElementById("from_out").submit();
                               }
                           }]
                       });

                        Ext.define('cn.plus.tree.Panel', {
                           extend: 'Ext.tree.Panel',
                           region: 'west',
                           itemId: 'west_panel',
                           title: '数据库导航',
                           width: 200,
                           split: true,
                           collapsible: true,
                           floatable: true,
                           scroll: 'both',
                           autoScroll: true,
                           url: '',
                           listeners: {
                               "itemexpand": function (node, event) {
                                   if (node.isLeaf()) {
                                        event.stopEvent();
                                        node.toggle();
                                    }
                               },
                               'itemclick': function (node, record, item, index, e, eOpts) {
                                 //e.stopEvent();
                                   if (record.data.leaf)
                                   {
                                       e.stopEvent();

                                       var id = "tab_id_" + record.data.id;
                                        var n = center.getComponent(id);
                                        if (!n) {
                                            var endIeStatus = document.getElementById("endIeStatus");
                                            if (document.createEvent) {
                                                var ev = document.createEvent('HTMLEvents');
                                                ev.initEvent('click', false, true);
                                                endIeStatus.dispatchEvent(ev);
                                            }
                                            else {
                                                endIeStatus.click();
                                            }
                                        }

                                        if (Ext.isEmpty(Ext.getCmp(id))) {
                                            var url = "/data/?schema=" + record.data.parentId + "&table_name=" + record.data.id;
                                            //console.log(url);
                                            n = center.add({
                                                id: id,
                                                closable: true,
                                                layout: 'fit',
                                                title: record.data.text,
                                                // listeners: { activate: function() { Ext.getCmp('centerPanel').setTitle(node.text) } },
                                                html: '<iframe scrolling="auto" frameborder="0" width="100%" height="100%" src='+ url +'></iframe>'
                                            });
                                        }
                                        center.setActiveTab(n);
                                   }
                                },
                               'afterlayout': function(treePanel, eOpts){
                                    var emBody = document.getElementById(treePanel.body.id);
                                    var emView = document.getElementById(treePanel.getView().id);
                                    var emTable = document.getElementById(treePanel.getView().id).childNodes[0];
                                    if(emTable != undefined){
                                        var emTableW = emTable.style.width;
                                        var newEmTableW = parseInt(emTable.style.width.substr(0,emTableW.length-2))-20;
                                        emTable.style.width = newEmTableW+"px";
                                        emTable.style.tableLayout = 'auto';
                                        emTable.style.borderCollapse = 'separate';
                                    }
                                    emView.style.overflow = 'visible';
                                    if(treePanel.treeGrid != 'Y'){
                                        emView.style.overflowX = 'auto';
                                    }else{
                                        emView.style.overflowX = 'hidden';
                                    }
                                    emView.style.overflowY = 'auto';
                                    emView.style.position = 'relative';
                                    var emDivArr = emBody.parentNode.getElementsByTagName("div");
                                       for(var i = 0; i < emDivArr.length; i++){
                                           var emDiv = emDivArr[i];
                                           if(emDiv.id.indexOf("gridscroller") == 0 && emDiv.className.indexOf("x-scroller-vertical") > 0){
                                               /*if(emDiv.parentNode != null){
                                                   emDiv.parentNode.removeChild(emDiv);
                                               }*/
                                               emDiv.style.width = "0px";
                                               emView.style.width = treePanel.getWidth()+"px";
                                               emBody.style.width = treePanel.getWidth()+"px";
                                           }
                                       }
                                 }
                           },
                           //rootVisible: false,
                           initComponent: function () {
                               var me = this;

                               var base_config = {
                                   store: tree_store
                               };

                               Ext.apply(this, base_config);
                               this.callParent();
                           }
                       });

                        ShowWin = new Ext.Window({
                                    title: '详情',
                                    width: 730,
                                    height: 468,
                                    autoScroll: true,
                                    maximizable: false,
                                    resizable: true,
                                    collapsible: true,
                                    closeAction: 'hide',
                                    closable: true,
                                    modal: 'true',
                                    buttonAlign: "center",
                                    layout: 'fit',
                                    bodyStyle: "padding:8px 8px 8px 8px",
                                    bodyCfg: {
                                        tag: 'center',
                                        cls: 'x-panel-body',
                                    },
                                    contentEl: 'dawn_sql_show1'
                                }).show();
                        ShowWin.hide();

                        Ext.create('Ext.Viewport', {
                           layout: 'border',
                           title: 'DawnSqlWeb',
                           defaults: {
                               //collapsible: true,
                               split: true
                           },
                            listeners: {
                                afterrender: function (obj) {

                                }
                            },
                           items: [{
                               xtype: 'panel',
                               region: 'north',
                               border: false,
                               tbar: tbar
                           }, Ext.create('cn.plus.tree.Panel', {url: '/tree/?user_token=${ m.user_token }'}), center]
                       });
                    }
                });

                function show_win(data) {
                    var tpl = new Ext.XTemplate(
        				        '<table cellpadding="0" cellspacing="0" style="border:1px solid #b5e2ff;" width="630">',
                                '<tpl for="data">',
                                '<tr style="height:25px;">',
                                '<td class="t1" align="right">{name}：</td><td class="t2">&nbsp;&nbsp;<span>{value}</span></td>',
                                '</tr></tpl>',
                                '</table>'
        				     );
        				     tpl.overwrite(Ext.get("dawn_sql_show1"), data);

                     ShowWin.show();
                }
    </script>
</head>
<body>
<a href="javascript:void(0);" onclick="javascript:endIeStatus();" id="endIeStatus" style="display: none;" />
<div id="dawn_sql_show1"></div>
<form id="from_out" action="<c:url value="/login_out/" />" method="get" style="display: none;"></form>
</body>
</html>







































