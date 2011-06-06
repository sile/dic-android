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
        SpannableString spannable = DataFormatter.format(e.data);
        DataFormatter.link(this, spannable.toString(), spannable);
        entryView.setText(spannable, TextView.BufferType.SPANNABLE);
        entryView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}