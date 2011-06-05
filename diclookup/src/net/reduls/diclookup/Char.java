package net.reduls.diclookup;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList; 

public final class Char {
    private final char[] charCode;
    private final List<Character> arcs;
    
    public Char(String dictionaryDirectory) throws IOException {
        final DataInputStream in =
            new DataInputStream(new FileInputStream(dictionaryDirectory+"/code-map.bin"));
        try {
            final int codeLimit = in.readInt();
            charCode = new char[codeLimit];
            
            arcs = new ArrayList<Character>();

            for(int i=0; i < codeLimit; i++) {
                charCode[i] = in.readChar();
                if(charCode[i] != 0)
                    arcs.add(charCode[i]);
            }
        } finally  {
            in.close();
        }
    }

    public char code(char ch) {
        return charCode[Character.toLowerCase(ch)];
    }

    public List<Character> arcs() {
        return arcs;
    }
}
