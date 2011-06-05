package net.reduls.diclookup;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public final class EntryRetriever {
    private final int[] id2offsets;
    private final int[] offsets;
    private final String dataPath;

    public EntryRetriever(String dictionaryDirectory) throws IOException {
        {
            DataInputStream in = 
                new DataInputStream(new FileInputStream(dictionaryDirectory+"/id2offsets.bin"));
            try {
                final int size = in.available()/4;
                id2offsets = new int[size];
                for(int i=0; i < size; i++) 
                    id2offsets[i] = in.readInt();
            } finally {
                in.close();
            }
        }
        {
            DataInputStream in = 
                new DataInputStream(new FileInputStream(dictionaryDirectory+"/offsets.bin"));
            try {
                final int size = in.available()/4;
                offsets = new int[size];
                for(int i=0; i < size; i++) 
                    offsets[i] = in.readInt();
            } finally {
                in.close();
            }
        }
        dataPath = dictionaryDirectory+"/dic.dat";
    }

    public List<Dic.Entry> getEntryList(int keyId) throws IOException {
        final RandomAccessFile in = new RandomAccessFile(dataPath,"r");
        try {
            final int entryBeg = id2offsets[keyId];
            final int entryEnd = id2offsets[keyId+1];
            final List<Dic.Entry> entrys = new ArrayList<Dic.Entry>();    
            
            for(int e = entryBeg; e < entryEnd; e++) {
                in.seek(offsets[e]);
                entrys.add(new Dic.Entry(e, in));
            }

            return entrys;
        } finally {
            in.close();
        }
    }

    public Dic.Entry getEntry(int entryId) throws IOException {
        final RandomAccessFile in = new RandomAccessFile(dataPath,"r");
        try {
            in.seek(offsets[entryId]);
            return new Dic.Entry(entryId, in);
        } finally {
            in.close();
        }        
    }
}

