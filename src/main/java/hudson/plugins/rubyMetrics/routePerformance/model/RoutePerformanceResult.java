
package hudson.plugins.rubyMetrics.routePerformance.model;

import hudson.model.ModelObject;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jvnet.localizer.Localizable;

public class RoutePerformanceResult implements ModelObject, Serializable  {

    private static final long serialVersionUID = -2471901428847143039L;

    private static final Comparator<RoutePerformanceUrlResult> DURATION_COMPARATOR = new Comparator<RoutePerformanceUrlResult>() {
        public int compare(RoutePerformanceUrlResult r1, RoutePerformanceUrlResult r2) {
            return r2.getDuration() - r1.getDuration();
        }
    };

    private String name;
    private Date date_time;
    private int total_duration;
    private int avg_duration;
    private int route_count;
    private int pass_count;
    private int fail_count;
    private double coverage_percent;
    private int total_body_size;
    private List<RoutePerformanceUrlResult> results;

     public RoutePerformanceResult(Map data) throws NoSuchFieldException, IllegalAccessException {
        for (Map.Entry e : (Set<Map.Entry>)data.entrySet()) {
            if (!(e.getValue() instanceof List)) {
                String field = e.getKey().toString().replaceAll("[^a-z_]", "");
                Object val = e.getValue();
                if (field.endsWith("_duration") && val instanceof Double) {
                    val = (int)Math.round(((Double)val) * 1000.0);
                }
                getClass().getDeclaredField(field).set(this, val);
            }
        }
        List<String> cols = (List<String>)data.get(":column_names");
        this.results = new LinkedList();
        for (List res : (List<List>)data.get(":results")) {
            this.results.add(new RoutePerformanceUrlResult(cols, res));
        }
        Collections.sort(this.results, DURATION_COMPARATOR);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getName() {
        return name;
    }

    public Date getDateTime() {
        return date_time;
    }

    public int getTotalDuration() {
        return total_duration;
    }

    public int getAvgDuration() {
        return avg_duration;
    }

    public int getMaxDuration() {
        int max = Integer.MIN_VALUE;
        for (RoutePerformanceUrlResult res : results) {
            max = Math.max(max, res.getDuration());
        }
        return max;
    }

    public int getRouteCount() {
        return route_count;
    }

    public int getPassCount() {
        return pass_count;
    }

    public int getFailCount() {
        return fail_count;
    }

    public double getCoveragePercent() {
        return coverage_percent;
    }

    public int getTotalBodySize() {
        return total_body_size;
    }

    public List<RoutePerformanceUrlResult> getResults() {
        return results;
    }

    public int getMetricDuration(Targets metric) {
        return metric.equals(Targets.MEAN_RESPONSE_TIME)? getAvgDuration() : getMaxDuration();
    }

    public String getDisplayName() {
        return "Route Performance report (" + name + ")";
    }

}
