package hudson.plugins.rubyMetrics.routePerformance;

import hudson.model.AbstractBuild;
import hudson.model.HealthReport;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsBuildAction;
import hudson.plugins.rubyMetrics.routePerformance.model.MetricTarget;
import hudson.plugins.rubyMetrics.routePerformance.model.RoutePerformanceResult;
import hudson.plugins.rubyMetrics.routePerformance.model.Targets;
import hudson.util.ChartUtil;
import hudson.util.DataSetBuilder;
import hudson.util.ChartUtil.NumberOnlyBuildLabel;

import java.util.List;

import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;

public class RoutePerformanceBuildAction extends AbstractRubyMetricsBuildAction {

    private final RoutePerformanceResult results;
    private final List<MetricTarget> targets;

    public RoutePerformanceBuildAction(AbstractBuild<?, ?> owner, RoutePerformanceResult results, List<MetricTarget> targets) {
        super(owner);
        this.results = results;
        this.targets = targets;
    }

    public HealthReport getBuildHealth() {
        int minValue = 100;
        int metricValue = 0;
        String metricName = null;
        for (MetricTarget target : targets) {
            int score = results.getMetricDuration(target.getMetric());
            int value = calcRangeScore(target.getHealthy(), target.getUnhealthy(), score);
            if (value <= minValue) {
                minValue = value;
                metricValue = score;
                metricName = target.getMetric().getName();
            }
        }
        return metricName != null ? new HealthReport(minValue, metricName + " (" + metricValue + " ms)") : null;
    }

    public String getDisplayName() {
        return "Route Performance report";
    }

    public String getIconFileName() {
        return "graph.gif";
    }

    public String getUrlName() {
        return "routePerformance";
    }

    public RoutePerformanceResult getResults() {
        return results;
    }

    private int calcRangeScore(Integer min, Integer max, int value) {
        if (min == null || min < 0) min = 500;
        if (max == null) max = 1000;
        if (min > max) max = min + 1;
        if (value <= min) {
            return 100;
        } else if (value >= max) {
            return 0;
        } else {
            return 100 - (int)(100f * (value - min.floatValue()) / (max.floatValue() - min.floatValue()));
        }
    }

    @Override
    protected DataSetBuilder<String, NumberOnlyBuildLabel> getDataSetBuilder() {
        DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel> dsb = new DataSetBuilder<String, ChartUtil.NumberOnlyBuildLabel>();
        for (RoutePerformanceBuildAction a = this; a != null; a = a.getPreviousResult()) {
            ChartUtil.NumberOnlyBuildLabel label = new ChartUtil.NumberOnlyBuildLabel(a.owner);
            dsb.add(a.getResults().getMetricDuration(Targets.MEAN_RESPONSE_TIME), "mean response time", label);
            dsb.add(a.getResults().getMetricDuration(Targets.MAX_RESPONSE_TIME), "max response time", label);
        }
        return dsb;
    }

    @Override
    protected NumberAxis getRangeAxis(CategoryPlot plot) {
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperBound(results.getMetricDuration(Targets.MAX_RESPONSE_TIME) + 50);
        rangeAxis.setLowerBound(0);
        return rangeAxis;
    }

    @Override
    protected String getRangeAxisLabel() {
        return "ms";
    }

}
