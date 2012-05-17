
package hudson.plugins.rubyMetrics.routePerformance.model;

import org.apache.commons.beanutils.Converter;

public class MetricTarget {

    private Targets metric;
    private Integer healthy;
    private Integer unhealthy;

    public static final TargetConverter CONVERTER = new TargetConverter();

    public MetricTarget() { }

    /**
     * @param metric
     * @param healthy
     * @param unhealthy
     * @stapler-constructor
     */
    public MetricTarget(Targets metric, Integer healthy, Integer unhealthy) {
        this.metric = metric;
        this.healthy = healthy != null ? healthy : 80;
        this.unhealthy = unhealthy;
    }

    public Targets getMetric() {
        return metric;
    }

    public Integer getHealthy() {
        return healthy != null ? healthy : 0;
    }

    public Integer getUnhealthy() {
        return unhealthy != null ? unhealthy : 0;
    }

    public void setMetric(Targets metric) {
        this.metric = metric;
    }

    public void setHealthy(Integer healthy) {
        this.healthy = healthy;
    }

    public void setUnhealthy(Integer unhealthy) {
        this.unhealthy = unhealthy;
    }

    private static class TargetConverter implements Converter {
        public Object convert(Class type, Object value) {
            return Targets.resolve(value.toString());
        }
    }

}
