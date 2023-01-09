/**
 * 本地存储
 * */
function myLocalStore() {
    this.localStore = window.localStorage;

    /**
     * 判断字符是否为空的方法
     * */
    this.isEmpty = function (obj){
        if(typeof obj == "undefined" || obj == null || obj == ""){
            return true;
        }else{
            return false;
        }
    };

    /**
     * 更新 work_id code
     * */
    this.update_work_code = function (works, work_id, work_name, code) {
        var lst = new Array();
        for (var k in works)
        {
            if (works[k]['work_id'] != work_id)
            {
                lst.push(works[k]);
            }
        }

        lst.push({'work_id': work_id, 'work_name': work_name, 'code': code});
        this.localStore.setItem('works', Ext.JSON.encode(lst));
    };

    this.update_my_work_code = function (work_id, code) {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var lst = new Array();
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            for (var k in works)
            {
                if (works[k]['work_id'] == work_id)
                {
                    lst.push({'work_id': work_id, 'work_name': works[k]['work_name'], 'code': code});
                }
                else
                {
                    lst.push({'work_id': works[k]['work_id'], 'work_name': works[k]['work_name'], 'code': works[k]['code']});
                }
            }
            this.localStore.setItem('works', Ext.JSON.encode(lst));
        }
    };

    /**
     * 默认工作台
     * */
    this.set_default_work = function (code) {
        this.localStore.setItem('default_work_code', code);
    };

    this.get_default_work = function () {
        return this.localStore.getItem('default_work_code');
    };

    /**
     * 添加新的工作台和它们的 code
     * */
    this.set_work = function (work_id, work_name, code) {
        if (this.isEmpty(this.localStore.getItem('works')))
        {
            var works = new Array();
            works.push({'work_id': work_id, 'work_name': work_name, 'code': code});
            this.localStore.setItem('works', Ext.JSON.encode(works));
        }
        else
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            this.update_work_code(works, work_id, work_name, code);
        }
    };

    this.get_work = function (work_id) {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            for (var k in works)
            {
                if (works[k]['work_id'] == work_id)
                {
                    return works[k]['code'];
                }
            }
        }
        return "";
    };

    this.get_work_index = function () {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            return works.length + 1;
        }
        return 0;
    };

    this.remove = function (work_id) {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            var lst = new Array();
            for (var k in works)
            {
                if (works[k]['work_id'] != work_id)
                {
                    lst.push(works[k]);
                }
            }

            this.localStore.setItem('works', Ext.JSON.encode(lst));
        }
    };

    this.has_work_name = function (work_name) {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            for (var k in works)
            {
                if (works[k]['work_name'] == work_name)
                {
                    return true;
                }
            }
        }
        return false;
    };

    this.get_work_name = function (work_name) {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            for (var k in works)
            {
                if (works[k]['work_name'] == work_name)
                {
                    return works[k]['code'];
                }
            }
        }
        return "";
    };

    this.get_works = function () {
        if (!this.isEmpty(this.localStore.getItem('works')))
        {
            var works = Ext.JSON.decode(this.localStore.getItem('works'));
            return works;
        }
        return new Array();
    };
}