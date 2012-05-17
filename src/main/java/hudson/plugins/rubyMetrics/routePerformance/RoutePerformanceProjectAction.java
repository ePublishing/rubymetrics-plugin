package hudson.plugins.rubyMetrics.routePerformance;

import hudson.model.AbstractProject;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsProjectAction;

public class RoutePerformanceProjectAction<RoutePerformanceBuildAction> extends AbstractRubyMetricsProjectAction {

    public RoutePerformanceProjectAction(AbstractProject<?, ?> project) {
        super(project);
    }

    public String getDisplayName() {
        return "Route performance report";
    }

    public String getUrlName() {
        return "routePerformance";
    }

    @Override
    protected Class getBuildActionClass() {
        return hudson.plugins.rubyMetrics.routePerformance.RoutePerformanceBuildAction.class;
    }

}
