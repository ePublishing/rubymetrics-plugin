
package hudson.plugins.rubyMetrics.routePerformance.model;

public enum Targets {

    MEAN_RESPONSE_TIME,
    MAX_RESPONSE_TIME;

    public String getName() {
        String name = this.toString().toLowerCase().replaceAll("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static Targets resolve(String name) {
        return Targets.valueOf(name.toUpperCase().replaceAll(" ", "_"));
    }

}
