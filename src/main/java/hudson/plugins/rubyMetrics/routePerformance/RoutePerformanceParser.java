package hudson.plugins.rubyMetrics.routePerformance;

import hudson.plugins.rubyMetrics.routePerformance.model.RoutePerformanceResult;
import hudson.util.IOException2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class RoutePerformanceParser {

    private static final String REPORT_CLASS_VALUE = "report";

    public RoutePerformanceResult parse(File file) throws IOException {
        return parse(new FileInputStream(file));
    }

    public RoutePerformanceResult parse(InputStream input) throws IOException {
        try {
            Yaml yaml = new Yaml(new ResultConstructor());
            Object obj = yaml.load(input);
            if (obj instanceof Map) {
              obj = new RoutePerformanceResult((Map)obj);
            }
            return (RoutePerformanceResult)obj;
        } catch (Exception e) {
            throw new IOException2("cannot parse routePerformance report file", e);
        }
    }

    private static class ResultConstructor extends org.yaml.snakeyaml.constructor.Constructor {

        public ResultConstructor() {
            yamlConstructors.put(
                new Tag("!ruby/object:Mime::Type"),
                new org.yaml.snakeyaml.constructor.Constructor.ConstructMapping() {
                    public Object construct(Node node) {
                        return null;
                    }
                }
            );
            yamlConstructors.put(
                new Tag("!omap"),
                new org.yaml.snakeyaml.constructor.Constructor.ConstructMapping() {
                    public Object construct(Node node) {
                        if ("!omap".equals(node.getTag().getValue()) && node instanceof SequenceNode) {
                            try {
                                MappingNode omap = mappingNodeFromOmap((SequenceNode)node);
                                return new RoutePerformanceResult(constructMapping(omap));
                            } catch (Throwable t) {
                                throw new YAMLException("Failure mapping results: " + t);
                            }
                        } else {
                            return super.construct(node);
                        }
                    }
                }
            );
        }

        private static MappingNode mappingNodeFromOmap(SequenceNode node) {
            List<NodeTuple> tuples = new LinkedList();
            for (Node child : node.getValue()) {
                for (NodeTuple tuple : ((MappingNode)child).getValue()) {
                    tuples.add(tuple);
                }
            }
            return new MappingNode(node.getTag(), tuples, node.getFlowStyle());
        }

    }

}
