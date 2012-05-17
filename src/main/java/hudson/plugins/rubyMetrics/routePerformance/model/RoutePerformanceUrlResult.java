
package hudson.plugins.rubyMetrics.routePerformance.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jvnet.localizer.Localizable;

public class RoutePerformanceUrlResult implements Serializable  {

    private static final long serialVersionUID = -1585891402648243908L;

    private String gloss;
    private int duration;
    private int response_code;
    private String verb;
    private String url;
    private String format;
    private String controller;
    private String action;
    private boolean success;
    private String content_type;
    private int body_size;

    public RoutePerformanceUrlResult(List<String> columns, List data) throws NoSuchFieldException, IllegalAccessException {
        int idx = 0;
        for (String col : columns) {
            String field = col.replaceAll("[^a-z_]", "");
            Object val = data.get(idx);
            if (field.equals("duration") && val instanceof Double) {
                val = (int)Math.round(((Double)val) * 1000.0);
            }
            getClass().getDeclaredField(field).set(this, val);
            idx++;
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getGloss() {
        return gloss;
    }

    public int getDuration() {
        return duration;
    }

    public int getResponseCode() {
        return response_code;
    }

    public String getVerb() {
        return verb;
    }

    public String getUrl() {
        return url;
    }

    public String getFormat() {
        return format;
    }

    public String getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getContentType() {
        return content_type;
    }

    public int getBodySize() {
        return body_size;
    }

}
