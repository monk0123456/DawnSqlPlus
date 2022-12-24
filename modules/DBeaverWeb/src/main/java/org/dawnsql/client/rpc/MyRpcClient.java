package org.dawnsql.client.rpc;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.gridgain.dawn.rpc.MyRpcService;

public class MyRpcClient {
    private TTransport transport = new TFramedTransport(new TSocket("127.0.0.1", 8091));
    private TProtocol protocol = new TCompactProtocol(transport);

    private MyRpcService.Client client = new MyRpcService.Client(protocol);

    public String executeSqlQuery(String userToken, String sql, String ps)
    {
        try {
            transport.open();
            return client.executeSqlQuery(userToken, sql, ps);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            transport.close();
        }
        return null;
    }
}
