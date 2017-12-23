package undecv.flip_0002;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * FLIP is a lyric parser, for straitened LRC file, as shown in the following:
 * <p><ul>
 * <li>[ar:Lyrics artist]
 * <li>[al:Album where the song is from]
 * <li>[ti:Lyrics (song) title]
 * <li>[au:Creator of the Song text]
 * <li>[length:How long the song is]
 * <li>[by:Creator of the LRC file]
 * <li>[offset: 0]
 * <li>
 * <li>[00:12.00]Line 1 lyrics
 * <li>[00:17.20]Line 2 lyrics
 * <li>[00:21.10]Line 3 lyrics
 * </ul><p>
 *
 * @author undecV
 *
 * @see LRC
 * @see MSms
 */
public class flip {
    /** Regex of:[mm:ss.ms], lyric time tag. */
    final private static String TimeTagRegex = "[0-9]+:[0-6]?[0-9]\\.[0-9][0-9]";
    /** Regex of:[mm:ss] */
    final private static String TimeRegex = "[0-9]+:[0-6]?[0-9]";

    /**
     * Parse a string, format like:[mm:ss].
     * @since 0.1
     * @return <MSms> A time data.
     */
    public static MSms timeParser(String TimeStr){
        String[] StrParsed = TimeStr.split(":");
        return new MSms(Integer.parseInt(StrParsed[0]),
                Integer.parseInt(StrParsed[1]) );
    }

    /**
     * Parse a string, format like:[mm:ss].
     * @since 0.1
     * @return <MSms> A time data.
     * @throws Exception Format not right.
     */
    public static MSms timeParser_s(String TimeStr) throws Exception{
        if (TimeStr.matches(TimeRegex) == false) throw new Exception("Format not right.");
        return timeParser(TimeStr);
    }

    /**
     * Parse a string, format like:[mm:ss.ms].
     * @since 0.1
     * @return <MSms> A time data.
     */
    public static MSms timeTagParser(String TimeStr){
        String[] StrParsed = TimeStr.split(":|\\.");
        return new MSms(Integer.parseInt(StrParsed[0]),
                Integer.parseInt(StrParsed[1]),
                Integer.parseInt(StrParsed[2]) );
    }

    /**
     * Parse a string, format like:[mm:ss.ms]. With check the format.
     * @since 0.1
     * @return <MSms> A time data.
     */
    public static MSms timeTagParser_s(String TimeStr) throws Exception{
        if (TimeStr.matches(TimeTagRegex) == false) throw new Exception("Format not right.");
        return timeTagParser(TimeStr);
    }

    /**
     * Parse a line of LRC.
     * @since 0.1
     * @return <TreeMap> A treeMap for parsed result: keys are the time of line, values are the string of line.
     */
    public static TreeMap<Integer, String> lineStrParser(String lineStr) {

        ArrayList<String> timeTags = new ArrayList<>();

        while (lineStr.matches("(\\[" + TimeTagRegex + "\\])+.*") == true){
            String[] lineStrParsed = lineStr.split("\\[|\\]",3);
            timeTags.add(lineStrParsed[1]);
            lineStr = lineStrParsed[2];
        }

        TreeMap<Integer, String> lineMap = new TreeMap<>();
        for (String timeTag : timeTags){
            lineMap.put(timeTagParser(timeTag).TOms(), lineStr.trim());
        }
        return lineMap;
    }

    /**
     * Parse Tag of LRC.
     * @since 0.1
     * @return <String> Value of tag.
     */
    public static String infoTagParser(String id, String line){
        String[] lineParsed = line.split("\\[" + id + ":|\\]",3);
        return lineParsed[1].trim();
    }

    /**
     * Parse lines of LRC.
     * @since 0.1
     * @return <LRC> A LRC data.
     */
    public static LRC LinesParser(ArrayList<String> lines){
        LRC lrc = new LRC();
        for (String line : lines){
            if (line.matches("(\\[" + TimeTagRegex + "\\])+.*") == true) {
                lrc.Lines.putAll(lineStrParser(line));
            } else if (line.matches("(\\["+ "offset" +":.*\\])+.*") == true) {
                lrc.offset = Integer.valueOf(infoTagParser("offset", line));
            } else if (line.matches("(\\["+ "length" +":(\\s)*" + TimeRegex + "(\\s)*\\])+.*") == true) {
                lrc.length = timeParser(infoTagParser("length", line));
            } else if (line.matches("(\\[.+:.*\\])+.*") == true) {
                for (int i = 0; i<LRC.metasName.length; i++){
                    if (line.matches("(\\["+ LRC.metasName[i] +":.*\\])+.*") == true) {
                        lrc.metas[i] = infoTagParser(LRC.metasName[i], line);
                        break;
                    }
                }
            } else {}
        }
        return lrc;
    }

    /**
     * Parse file of LRC.
     * @since 0.1
     * @return <LRC> A LRC data.
     * @throws IOException File open fail.
     */
    public static LRC Parse(File file) throws IOException{
        try{
            return LinesParser(fileOperation.fileToLines(file));
        }catch(IOException e){throw e;}
    }
}

