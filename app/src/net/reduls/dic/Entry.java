package net.reduls.dic;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Entry extends Activity {
    public net.reduls.diclookup.Dic dic;
    
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry);

        dic = ((DicLookup)this.getApplication()).getDic();
        net.reduls.diclookup.Dic.Entry e = dic.getEntryFromId(getIntent().getIntExtra("entry.id",0));

        TextView entryView = (TextView)findViewById(R.id.entry_body);
        entryView.setText(e.data);

        // TODO: タイトル変更
    }
}