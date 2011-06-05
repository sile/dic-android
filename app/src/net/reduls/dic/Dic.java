package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import java.util.List;

public class Dic extends Activity implements OnKeyListener
{
    private net.reduls.diclookup.Dic dic;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // TODO: configure
        try {
            dic = new net.reduls.diclookup.Dic("/mnt/sdcard/");
        } catch(Exception e) {
            Log.e("REDULS.DIC", e.getMessage());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View searchBar = findViewById(R.id.search_bar);
        searchBar.setOnKeyListener(this);
    }
    
    public boolean onKey(View v, int arg1, KeyEvent event) {
        TextView result = (TextView)findViewById(R.id.search_result);
        String key = ((EditText)v).getText().toString();
        List<net.reduls.diclookup.Dic.Entry> entrys = dic.lookup(key, 1);
        if(entrys.isEmpty())
            result.setText("Not Found!");
        else
            result.setText(entrys.get(0).title+"\n"+entrys.get(0).summary);
        return false;
    }
}
