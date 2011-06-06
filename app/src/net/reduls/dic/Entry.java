package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.text.style.ClickableSpan;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.content.Intent;

public class Entry extends Activity {
    public net.reduls.diclookup.Dic dic;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);

        dic = ((DicLookup)this.getApplication()).getDic();
        net.reduls.diclookup.Dic.Entry e = dic.getEntryFromId(getIntent().getIntExtra("entry.id",0));

        TextView entryView = (TextView)findViewById(R.id.entry_body);

        setTitle(e.title);

        //
        Spannable spannable = new SpannableString(e.data);
        spannable.setSpan(new CS(this,0,10), 0, 10, 
                          Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        entryView.setText(spannable, TextView.BufferType.SPANNABLE);
        entryView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private class CS extends ClickableSpan {
        private Activity ac;
        private int start,end;
        public CS(Activity ac, int s, int e) {
            super();
            this.ac = ac;
            start = s;
            end = e;
        }
        
        public void onClick(View v) {
            Intent i = new Intent(ac, Dic.class);
            i.putExtra("search.key", ((TextView)v).getText().toString().substring(start,end));
            startActivity(i);            
        }
        
        //public void updateDrawState(TextPaint ds) {}
    }
}