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
                sb.append(line+"\n");
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
    
    private final class EntryCollector implements Callback {
        private final List<Entry> entrys;
        private final int limit;
        
        public EntryCollector(List<Entry> entrys, int limit) {
            this.entrys = entrys;
            this.limit = limit;
        }

        public boolean call(int id) {
            try {
                for(Dic.Entry e : retriever.getEntryList(id)) {
                    entrys.add(e);
                    if(entrys.size() >= limit)
                        return false;
                }
            } catch (IOException e) {
                System.err.println("ERROR: "+e.getMessage());
                return false;
            }
            return true;
        }
    }

    public List<Entry> lookup(String key, int limit) {
        List<Entry> result = new ArrayList<Entry>();
        keyid.eachPredictive(key, new EntryCollector(result, limit));
        return result;
    }
}

