package org.dawnsql.log;

import cn.smart.service.IMyLogTrans;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.dawn.trans.log.MyLogTransService;
import org.tools.MyConvertUtil;

public class MyLogTransactionClient implements IMyLogTrans {
    private TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8090));
    private TProtocol protocol = new TCompactProtocol(transport);
    private MyLogTransService.Client client = new MyLogTransService.Client(protocol);

    public MyLogTransactionClient() throws TTransportException {
        transport.open();
    }

    @Override
    public void createSession(String tranSession) {
        try {
            client.createSession(tranSession);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveTo(String tranSession, byte[] bytes) {
        try {
            client.saveTo(tranSession, MyConvertUtil.byteToBuffer(bytes));
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void commit(String tranSession) {
        try {
            client.commit(tranSession);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void rollback(String tranSession) {
        try {
            client.rollback(tranSession);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

}

