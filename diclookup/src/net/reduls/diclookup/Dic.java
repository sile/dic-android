package net.reduls.diclookup;

import java.util.List;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.io.IOException;

public final class Dic {
    public interface Callback {
        public boolean call(int id);
    }

    public static final class Entry {
        private final static String END_OF_ENTRY = "-=+=-=+=-=+=-=+=-=+=-";
        public final String title;
        public final String summary;
        public final String data;

        public Entry(RandomAccessFile entry) throws IOException {
            title = readLine(entry);
            summary = readLine(entry);
            
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=readLine(entry)).equals(END_OF_ENTRY)==false) {
                sb.append(line);
            }
            data = sb.toString();
        }
        
        private String readLine(RandomAccessFile entry) throws IOException {
            final long beg = entry.getFilePointer();
            while(entry.read()!='\n');
            final long end = entry.getFilePointer();
            final byte[] bytes = new byte[(int)(end-beg-1)];
            entry.seek(beg);
            entry.readFully(bytes);
            entry.read();
            return new String(bytes, "UTF-8");
        }
    }

    private final KeyId keyid;
    private final EntryRetriever retriever;
    public Dic(String dictionaryDirectory) throws IOException {
        keyid = new KeyId(dictionaryDirectory);
        retriever = new EntryRetriever(dictionaryDirectory);
    }
    
    private /*static*/ final class Cl implements Callback {
        public boolean call(int id) {
            System.out.println("# "+id);
            try {
                for(Dic.Entry e : retriever.getEntryList(id)) {
                    System.out.println(": "+e.title);
                }
            } catch (IOException e) {
                System.out.println("! "+e.getMessage());
            }
            return true;
        }
    }

    public List lookup(String key, int limit) {
        List<Entry> result = new ArrayList<Entry>();
        keyid.eachPredictive(key, new Cl());
        return result;
    }

    public static void main(String[] args) throws IOException {
        new Dic(args[0]).lookup(args[1], 10);
    }
}

