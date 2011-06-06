package net.reduls.dic;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.content.Intent;
import java.util.List;
import java.util.ArrayList;
import android.graphics.Typeface;
import android.text.style.StyleSpan;

public class DataFormatter {
    private static class StyleWithRegion {
        public final CharacterStyle style;
        public final int start;
        public final int end;
        public StyleWithRegion(CharacterStyle style, int start, int end) {
            this.style = style;
            this.start = start;
            this.end = end;
        }
    }
    public static SpannableString format(String text) {
        StringBuilder sb = new StringBuilder();
        List<StyleWithRegion> spans = new ArrayList<StyleWithRegion>();

        format_impl(text, sb, spans);
        
        SpannableString spannable = new SpannableString(sb.toString());
        for(StyleWithRegion s : spans) 
            spannable.setSpan(s.style, s.start, s.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    private static void format_impl(String text, StringBuilder sb, List<StyleWithRegion> styles) {
        for(int i = 0; i < text.length(); i++) {
            switch(text.charAt(i)) {
            case '`':
                i = format1(text, i+1, sb, styles);
                break;
            default:
                sb.append(text.charAt(i));
            }
        }
    }

    private static int format1(String text, int start, StringBuilder sb, List<StyleWithRegion> styles) {
        int end = text.indexOf('`', start);
        styles.add(new StyleWithRegion(new StyleSpan(Typeface.BOLD), sb.length(), sb.length()+end-start));
        sb.append(text.substring(start,end));
        return end+1;
    }

    public static void link (Activity a, String text, Spannable span) {
        int beg = nextNonDelimitorPosition(text, 0);
        int end = nextDelimitorPosition(text, beg);
        while(beg < text.length()) {
            span.setSpan(new WordLinkSpan(a, beg, end), beg, end, 
                         Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            beg = nextNonDelimitorPosition(text, end);
            end = nextDelimitorPosition(text, beg);
        }
    }

    private static int nextDelimitorPosition(String text, int start) {
        for(int i = start; i < text.length(); i++) {
            switch(text.charAt(i)) {
            case ' ': case ',': case ':': case ';': case '(': case ')': case '━':
            case '!': case '?': case '/':
            case '"': case '.': case 10: case 13: case 9:
                return i;
            }
        }
        return text.length();
    }

    private static int nextNonDelimitorPosition(String text, int start) {
        for(int i = start; i < text.length(); i++) {
            switch(text.charAt(i)) {
            case ' ': case ',': case ':': case ';': case '(': case ')': case '━':
            case '!': case '?': case '/':
            case '"': case '.': case 10: case 13: case 9:
                continue;
            default:
                return i;
            }
        }
        return text.length();        
    }

    private static class WordLinkSpan extends ClickableSpan {
        private final Activity activity;
        private final int start;
        private final int end;

        public WordLinkSpan(Activity activity, int start, int end) {
            super();
            this.activity = activity;
            this.start = start;
            this.end = end;
        }
        
        public void onClick(View v) {
            Intent i = new Intent(activity, Dic.class);
            i.putExtra("search.key", ((TextView)v).getText().toString().substring(start,end));
            activity.startActivity(i);            
        }
        
        //public void updateDrawState(TextPaint ds) {}
    }    
}