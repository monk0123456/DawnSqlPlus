package org.dawn.trans.log;

import org.gridgain.internal.h2.mvstore.MVStore;
import org.gridgain.smart.recovery.MyRecovery;

public class AppRecovery {

    public static void main( String[] args )
    {
        String path = "/Users/chenfei/temp/data/my.data";
        MyRecovery myRecovery = new MyRecovery(MVStore.open(path), "", "");
    }
}
