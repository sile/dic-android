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
import android.text.SpannableString;
import java.util.List;

public class Dic extends Activity implements OnClickListener, TextWatcher
{
    private static final int SEARCH_RESULT_LIMIT=16;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View searchBar = findViewById(R.id.search_bar);
        ((EditText)searchBar).addTextChangedListener(this);

        String key = getIntent().getStringExtra("search.key");
        getIntent().putExtra("search.key","");
        if(key!=null && key.equals("")==false)
            ((EditText)searchBar).setText(key);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    public void afterTextChanged(Editable s) {
        String key = s.toString();
        LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        resultArea.removeAllViews();
        
        if(key.length() > 0) { 
            for(net.reduls.diclookup.Dic.Entry e : ((DicLookup)getApplication()).getDic().lookup(key, SEARCH_RESULT_LIMIT)) {
                TextView txt = new TextView(this);
                SpannableString spannable = DataFormatter.format("<"+e.title+">\n"+e.summary);
                txt.setText(spannable, TextView.BufferType.SPANNABLE);
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
}
