package net.cuodex.passxapi.utils;

import java.util.HashMap;
import java.util.Map;

public class TimeoutUtils {

    // Path | [IP Address, Time]
    private static final Map<String, Map<String, Long>> timeouts = new HashMap<>();

    public static void sendRequest(String ip, String path) {

        if (!timeouts.containsKey(path)) {
            Map<String, Long> inner = new HashMap<>();
            timeouts.put(path, inner);
        }

        Map<String, Long> pathRequests = timeouts.get(path);
        pathRequests.put(ip, System.currentTimeMillis());

        timeouts.put(path, pathRequests);
    }

    public static long getTimeout(String ip, String path) {
        if (!timeouts.containsKey(path))
            return 0;

        Map<String, Long> pathRequests = timeouts.get(path);
        if (!pathRequests.containsKey(ip))
            return 0;

        long lastTime = pathRequests.get(ip);
        long currentTime = System.currentTimeMillis();
        long delay = Variables.ENDPOINT_REQUEST_DELAY;

        return (currentTime - lastTime) < delay ? currentTime - lastTime : 0;

    }

}
