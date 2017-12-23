package undecv.flip_0002;

/**
 * Store time as minute, second and millisecond. Like 'mm:ss.ms'.
 * @author undecV
 * @since 0.1
 */
public class MSms {

    private int min, sec, ms;

    public void Set (int minute, int second){
        min = minute;
        sec = second;
        ms = 0;
    }
    public void Set (int minute, int second, int millisecond){
        Set (minute, second);
        ms = millisecond;
    }

    public MSms (){
        Set (0, 0, 0);
    }
    public MSms (int minute, int second, int millisecond){
        Set (minute, second, millisecond);
    }
    public MSms (int minute, int second){
        Set (minute, second);
    }


    public static String str (int minute, int second, int millisecond){
        return (String.format("%02d:%02d.%02d",minute, second, millisecond));
        // min:sec:ms
    }
    public String str (){
        return str(min, sec, ms);
        // min:sec:ms
    }

    public static int TOms (int minute, int second, int millisecond){
        return (minute*60*1000 + second*1000 + millisecond*10);
    }
    public int TOms(){
        return TOms(min, sec, ms);
    }

    public static double TOs(int minute, int second, int millisecond){
        return (minute*60 + second + millisecond/100.0);
    }
    public double TOs(){
        return TOs(min, sec, ms);
    }
}