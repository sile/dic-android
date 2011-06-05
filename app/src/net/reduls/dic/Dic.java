package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.util.Log;
import android.text.Html;
import java.util.List;

public class Dic extends Activity implements OnKeyListener, OnClickListener
{
    private static final int SEARCH_RESULT_LIMIT=20;
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

    public void onClick (View v) {
        final int entryId = v.getId();
        TextView txt = (TextView)v;
        txt.setText("ID: "+entryId);
    }
    
    public boolean onKey(View v, int arg1, KeyEvent event) {
        String key = ((EditText)v).getText().toString();

        LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();
        
        if(key.length() > 0) {
            for(net.reduls.diclookup.Dic.Entry e : dic.lookup(key, SEARCH_RESULT_LIMIT)) {
                TextView txt = new TextView(this);
                String title = String.format(getText(R.string.result_title).toString(), e.title);
                String summary = formatEntry(e.summary);
                txt.setText(Html.fromHtml(title+"<br />"+summary));
                txt.setOnClickListener(this);
                txt.setId(e.id);
                resultArea.addView(txt);
            }
        }

        return false;
    }

    private String formatEntry(String text) {
        return
            text.replaceAll("`([^`]*)`","<b>$1</b>")
            .replaceAll("\\{([^}]*)\\}","<small><i><b><font color=\"red\">$1</font></b></i></small>")
            .replaceAll("\\[([^\\]]*)\\]","<small><u><b><font color=\"blue\">$1</font></b></u></small>");
    }
}
