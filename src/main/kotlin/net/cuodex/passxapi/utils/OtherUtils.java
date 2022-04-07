package net.cuodex.passxapi.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OtherUtils {

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(new Date());
    }
}
