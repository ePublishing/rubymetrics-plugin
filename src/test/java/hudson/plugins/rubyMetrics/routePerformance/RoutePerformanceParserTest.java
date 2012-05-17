
package hudson.plugins.rubyMetrics.routePerformance;

import java.io.InputStream;
import java.util.*;

import junit.framework.TestCase;

import hudson.plugins.rubyMetrics.routePerformance.model.RoutePerformanceResult;
import hudson.plugins.rubyMetrics.routePerformance.model.RoutePerformanceUrlResult;

public class RoutePerformanceParserTest extends TestCase {

    public void testParser() throws Exception {
        InputStream log = getClass().getResourceAsStream("routes_integration_spec.log");
        RoutePerformanceResult res = new RoutePerformanceParser().parse(log);
        assertEquals("'GET' routes integration test'", res.getName());
        assertEquals("Thu Feb 17 15:32:46 EST 2011", res.getDateTime().toString());
        assertEquals(7548, res.getTotalDuration());
        assertEquals(80, res.getAvgDuration());
        assertEquals(290, res.getMaxDuration());
        assertEquals(3, res.getPassCount());
        assertEquals(1, res.getFailCount());
        assertEquals(93.2, res.getCoveragePercent());
        assertEquals(923230, res.getTotalBodySize());
        assertEquals(4, res.getResults().size());

        RoutePerformanceUrlResult first = (RoutePerformanceUrlResult)res.getResults().get(0);
        assertEquals("get /articles/:id.html: 200", first.getGloss());
        assertEquals(290, first.getDuration());
        assertEquals(200, first.getResponseCode());
        assertEquals(":get", first.getVerb());
        assertEquals("/articles/:id", first.getUrl());
        assertEquals(":html", first.getFormat());
        assertEquals("articles", first.getController());
        assertEquals("show", first.getAction());
        assertEquals(true, first.getSuccess());
        assertEquals("text/html", first.getContentType());
        assertEquals(14272, first.getBodySize());
    }

}
