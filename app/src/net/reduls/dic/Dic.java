package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
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
        String key = ((EditText)v).getText().toString();

        LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();

        for(net.reduls.diclookup.Dic.Entry e : dic.lookup(key, 5)) {
            TextView txt = new TextView(this);
            txt.setText(e.title+"\n"+e.summary);
            resultArea.addView(txt);
        }

        return false;
    }
}
