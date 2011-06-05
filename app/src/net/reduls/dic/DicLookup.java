package net.reduls.dic;

import android.app.Application;
import android.util.Log;

public class DicLookup extends Application {
    private net.reduls.diclookup.Dic dic;

    @Override
    public void onCreate() {
        // TODO: configure
        try {
            dic = new net.reduls.diclookup.Dic("/mnt/sdcard/");
        } catch(Exception e) {
            Log.e("REDULS.DIC", e.getMessage());
        }        
    }

    public net.reduls.diclookup.Dic getDic() {
        return dic;
    }
}