package org.dawn.trans.log.impl;

import org.apache.thrift.TException;
import org.dawn.trans.log.MyLogTransService;
import org.gridgain.smart.backup.MyLogStore;
import org.tools.MyConvertUtil;

import java.nio.ByteBuffer;

public class MyLogTransactionImpl implements MyLogTransService.Iface {

    private String path;
    private MyLogStore myStore;

    private static class InstanceHolder {
        public static MyLogTransactionImpl instance = new MyLogTransactionImpl();
    }

    public static MyLogTransactionImpl getInstance() {
        return MyLogTransactionImpl.InstanceHolder.instance;
    }

    public void setMyStore(String path) {
        this.path = path;
        this.myStore = MyLogStore.getInstance();
        this.myStore.setMvStore(path);
    }

    private MyLogTransactionImpl()
    {}

    @Override
    public void createSession(String tranSession) throws TException {
        this.myStore.createSession(tranSession);
    }

    @Override
    public void saveTo(String tranSession, ByteBuffer data) throws TException {
        this.myStore.saveTo(tranSession, MyConvertUtil.bufferToByte(data));
    }

    @Override
    public void commit(String tranSession) throws TException {
        this.myStore.commit(tranSession);
    }

    @Override
    public void rollback(String tranSession) throws TException {
        this.myStore.rollback(tranSession);
    }
}
