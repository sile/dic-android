package net.reduls.diclookup;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public final class KeyId {
    private final long[] nodes;
    private final Char charcode;

    public KeyId(String dictionaryDirectory) throws IOException {
        final DataInputStream in =
            new DataInputStream(new FileInputStream(dictionaryDirectory+"/surface-id.bin"));
        try {
            final int nodeCount = in.readInt();
            nodes = new long[nodeCount];
            for(int i=0; i < nodeCount; i++)
                nodes[i] = in.readLong();
        } finally {
            in.close();
        }

        charcode = new Char(dictionaryDirectory);
    }

    public void eachPredictive(String key, Dic.Callback fn) {
        int node = 0;
        int id = -1;

        for(int i=0;; i++) {
            if(i==key.length()) {
                traverseDescendant(node, id, fn);
                return;
            }
            
            final char arc = charcode.code(key.charAt(i));
            final int next = base(node)+arc;
            if(chck(next) != arc)
                return;
            node = next;
            id = nextId(id, node);
        }
    }

    private boolean traverseDescendant(int node, int id, Dic.Callback fn) {
        if(isTerminal(node))
            if(fn.call(id)==false)
                return false;

        for(Character arc : charcode.arcs()) {
            final int next = base(node)+arc;
            if(chck(next)==arc)
                if(traverseDescendant(next, nextId(id,next), fn)==false)
                    return false;
        }
        return true;
    }

    private char chck(int node) {
        return (char)((nodes[node]>>24) & 0xFFFF);
    }
    
    private int base(int node) {
        return (int)(nodes[node] & 0xFFFFFF);
    }

    private boolean isTerminal(int node) {
        return ((nodes[node]>>40) & 0x1) == 0x1;
    }

    private int siblingTotal(int node) {
        return (int)(nodes[node]>>41);
    }  

    private int nextId(int id, int node) {
        return id + siblingTotal(node) + (isTerminal(node) ? 1 : 0);
    }
}