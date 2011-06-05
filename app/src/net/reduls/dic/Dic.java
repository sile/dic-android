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
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import java.util.List;

public class Dic extends Activity implements OnKeyListener, OnClickListener
{
    private static final int SEARCH_RESULT_LIMIT=16;
    public net.reduls.diclookup.Dic dic;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
        dic = ((DicLookup)this.getApplication()).getDic();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View searchBar = findViewById(R.id.search_bar);
        searchBar.setOnKeyListener(this);

        searchBar.setFocusable(true);
    }

    public void onClick (View v) {
        final int entryId = v.getId();
        TextView txt = (TextView)v;
        Intent i = new Intent(this, Entry.class);
        i.putExtra("entry.id", entryId);
        startActivity(i);
    }
    
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        String key = ((EditText)v).getText().toString();

        LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();
        
        TextView tt = new TextView(this);
        tt.setText("#"+key+":"+event.getAction());
        resultArea.addView(tt);      
  
        if(event.getAction() == KeyEvent.ACTION_UP && 
           key.length() > 0) {
            //            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);


            for(net.reduls.diclookup.Dic.Entry e : dic.lookup(key, SEARCH_RESULT_LIMIT)) {
                TextView txt = new TextView(this);
                String title = String.format(getText(R.string.result_title).toString(), e.title);
                String summary = formatEntry(e.summary);
                txt.setText(Html.fromHtml(title+"<br />"+summary));
                txt.setOnClickListener(this);
                txt.setId(e.id);
                resultArea.addView(txt);
            }
            return true; // XXX: ???
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
