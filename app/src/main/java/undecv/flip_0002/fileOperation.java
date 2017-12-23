package undecv.flip_0002;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Open a LRC file, store in a ArrayList<String>.
 * @author undecV
 * @since 0.1
 */
public class fileOperation {
    public static ArrayList<String> fileToLines(File file) throws IOException {
        File sdcard = Environment.getExternalStorageDirectory();
        MainActivity.ConsMsg(sdcard.getPath());
        ArrayList<String> lines = new ArrayList<>();
        try {
            FileReader fr = new FileReader(file.getPath());
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) lines.add(br.readLine());
            br.close(); fr.close();
        } catch (IOException e) { System.out.println("File open fail."); throw e;}
        return lines;
    }
}
