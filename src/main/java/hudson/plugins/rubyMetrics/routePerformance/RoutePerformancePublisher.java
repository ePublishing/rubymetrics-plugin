package hudson.plugins.rubyMetrics.routePerformance;

import hudson.Extension;
import hudson.Launcher;
import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.rubyMetrics.AbstractRubyMetricsPublisher;
import hudson.plugins.rubyMetrics.routePerformance.model.MetricTarget;
import hudson.plugins.rubyMetrics.routePerformance.model.RoutePerformanceResult;
import hudson.plugins.rubyMetrics.routePerformance.model.Targets;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * RoutePerformance {@link Publisher}
 *
 * @author David Calavera
 *
 */
@SuppressWarnings({"unchecked", "serial"})
public class RoutePerformancePublisher extends AbstractRubyMetricsPublisher {

    private static final String DEFAULT_ROUTES_PERFORMANCE_LOG_FILE = "log/routes_integration_spec.log";

    protected String routesPerformanceLogFile;
    private List<MetricTarget> targets;

    @DataBoundConstructor
    public RoutePerformancePublisher(String routesPerformanceLogFile) {
        this.routesPerformanceLogFile = routesPerformanceLogFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Publishing routes performance report...");

        FilePath fp = build.getWorkspace().child(routesPerformanceLogFile != null ? routesPerformanceLogFile : DEFAULT_ROUTES_PERFORMANCE_LOG_FILE);
        if (!fp.exists()) {
            listener.getLogger().println("Skipping route performance report because log file missing: " + fp);
            return true;
        }

        RoutePerformanceParser parser = new RoutePerformanceParser();
        RoutePerformanceResult results = parser.parse(fp.read());

        RoutePerformanceBuildAction action = new RoutePerformanceBuildAction(build, results, targets);
        build.getActions().add(action);

        if (failMetrics(results, listener)) {
            build.setResult(Result.UNSTABLE);
            return false;
        }

        return true;
    }

    private boolean failMetrics(RoutePerformanceResult results, BuildListener listener) {
        for (MetricTarget target : targets) {
            int val = results.getMetricDuration(target.getMetric());
            if (target.getUnhealthy() != null && val >= target.getUnhealthy()) {
                listener.getLogger().println("Route performance enforcement failed for the following metrics:");
                listener.getLogger().println("    " + target.getMetric().getName());
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new RoutePerformanceProjectAction(project);
    }

    public String getRoutesPerformanceLogFile() {
        return routesPerformanceLogFile;
    }

    public List<MetricTarget> getTargets() {
        return targets;
    }

    public void setTargets(List<MetricTarget> targets) {
        this.targets = targets;
    }

    /**
     * Descriptor should be singleton.
     */
    @Extension(ordinal=8)
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private final List<MetricTarget> targets;

        protected DescriptorImpl() {
            super(RoutePerformancePublisher.class);
            targets = new ArrayList<MetricTarget>(){{
                add(new MetricTarget(Targets.MEAN_RESPONSE_TIME, 500, 1000));
                add(new MetricTarget(Targets.MAX_RESPONSE_TIME, 1000, 2000));
            }};
        }

        public String getToolShortName() {
            return "routePerformance";
        }

        @Override
        public String getDisplayName() {
            return "Publish RoutePerformance report";
        }

        public List<MetricTarget> getTargets(RoutePerformancePublisher instance) {
            return instance != null && instance.getTargets() != null ? instance.getTargets() : getDefaultTargets();
        }

        private List<MetricTarget> getDefaultTargets() {
            return targets;
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public RoutePerformancePublisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            RoutePerformancePublisher instance = req.bindParameters(RoutePerformancePublisher.class, "routePerformance.");

            ConvertUtils.register(MetricTarget.CONVERTER, Targets.class);
            List<MetricTarget> targets = req.bindParametersToList(MetricTarget.class, "routePerformance.target.");
            instance.setTargets(targets);
            return instance;
        }

    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    private static class RoutePerformanceFilenameFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return name.equalsIgnoreCase("index.html");
        }
    }

}
