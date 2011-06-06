package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.util.Log;
import android.text.Html;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.List;

public class Dic extends Activity implements OnClickListener, TextWatcher
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
        ((EditText)searchBar).addTextChangedListener(this);

        //searchBar.setFocusable(true);

        String key = getIntent().getStringExtra("search.key");
        getIntent().putExtra("search.key","");
        if(key!=null && key.equals("")==false)
            ((EditText)searchBar).setText(key);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
    public void afterTextChanged(Editable s) {
        String key = s.toString();
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
    }

    public void onClick (View v) {
        final int entryId = v.getId();
        TextView txt = (TextView)v;
        Intent i = new Intent(this, Entry.class);
        i.putExtra("entry.id", entryId);
        startActivity(i);
    }
    
    private String formatEntry(String text) {
        return
            text.replaceAll("`([^`]*)`","<b>$1</b>")
            .replaceAll("\\{([^}]*)\\}","<small><i><b><font color=\"red\">$1</font></b></i></small>")
            .replaceAll("\\[([^\\]]*)\\]","<small><u><b><font color=\"blue\">$1</font></b></u></small>");
    }
}
