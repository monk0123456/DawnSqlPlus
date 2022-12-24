package org.dawn.trans.log.app;

public class AppService {

    public static void main( String[] args )
    {
        MyLogTransactionService myLogTransactionService = new MyLogTransactionService();
        myLogTransactionService.start();
    }
}
