package undecv.flip_0002;

import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * Obj LRC.
 * @author undecV
 * @since 0.1
 */
public class LRC {
    final static public String[] metasName = {"id", "ar", "ti", "al", "by"};
    public String[] metas = new String[metasName.length];

    public MSms length = new MSms();
    public int offset = 0;
    public TreeMap<Integer, String> Lines = new TreeMap<>();

    public LRC(){}
    public LRC(TreeMap<Integer, String> Lines) {
        this.Lines = Lines;
    }

    public void setID(String id, String str) {
        for (int i = 0; i<metasName.length; i++)
            if (id.matches(metasName[i]) == true) metas[i] = str;
    }

    public String getID(String id) {
        for (int i = 0; i<metasName.length; i++)
            if (id.matches(metasName[i]) == true) return metas[i];
        return null;
    }

    public void Show(){
        for (int i = 0; i<metasName.length; i++)
            System.out.println(metasName[i] + ": " + metas[i]);
        System.out.println("offset" + ": " + offset);

        System.out.println("length" + ": " + length.str());
        for (Entry<Integer, String> entry : Lines.entrySet())
            System.out.println(String.format("%10d", entry.getKey()) + " -> " + entry.getValue());
        System.out.println();
    }

    public void Show_C(){
        Show_C(0);
    }

    public void Show_C(int QuanLine){
        for (int i = 0; i<metasName.length; i++)
            MainActivity.ConsMsg(metasName[i] + ": " + metas[i]);
        MainActivity.ConsMsg("offset" + ": " + offset);
        MainActivity.ConsMsg("length" + ": " + length.str());
        if (QuanLine == 0) QuanLine = Lines.size();
        int CountLines = 0;
        for (Entry<Integer, String> entry : Lines.entrySet()) {
            MainActivity.ConsMsg(String.format("%010d", entry.getKey()) + " -> " + entry.getValue());
            CountLines++;
            if (CountLines >= QuanLine) break;
        }
    }

    public String getLine(int millisecond, int offset){
        if (Lines.isEmpty()) return null;
        Vector<Integer> time = new Vector<>(Lines.keySet());
        int LinePtr = 0;
        for (; LinePtr < time.size(); LinePtr++){
            if (LinePtr + 1 >= time.size()) break;
            if ((millisecond > time.get(LinePtr)) && (millisecond < time.get(LinePtr + 1))) break;
        }
        if (LinePtr + offset < 0) return null;
        if (LinePtr + offset >= time.size()) return null;
        return Lines.get(time.get(LinePtr + offset));
    }
}