package com.github.carlanton.muninnode4j;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;

public class MuninPlugin {
    private static Logger log = Logger.getLogger(MuninPlugin.class);

    private Method method;
    private MuninMonitor annotation;
    private String config;

    public MuninPlugin(Method method, MuninMonitor annotation) {
        this.method = method;
        this.annotation = annotation;
        this.config = parseConfig();
    }

    public String execute() {
        StringBuilder output = new StringBuilder();

        Number[] values;
        try {
            Object returnValue = method.invoke(null);

            if (returnValue instanceof Number[]) {
                values = (Number[]) returnValue;
            } else if (returnValue instanceof Number) {
                values = new Number[] {(Number) returnValue};
            } else {
                throw new Exception("Unimplemented return type: " + returnValue.getClass().getName());
            }
        } catch (Throwable t) {
            log.error("Failure while executing method '" + method.getName() + "': " + t.getMessage());
            return "";
        }

        for (int i = 0; i < annotation.labels().length; i++)
            output.append(annotation.labels()[i]).append(".value ").append(values[i]).append("\n");

        output.append("\n");

        return output.toString();
    }

    public String getConfig() {
        return config;
    }

    protected String parseConfig() {
        StringBuilder s = new StringBuilder();

        s.append("graph_title ");
        s.append(annotation.title().length() > 0 ? annotation.title() : method.getName());
        s.append("\n");

        if (annotation.args().length() > 0)
            s.append("graph_args ").append(annotation.args()).append("\n");

        if (annotation.category().length() > 0)
            s.append("graph_category ").append(annotation.category()).append("\n");

        if (annotation.info().length() > 0)
            s.append("graph_info ").append(annotation.info()).append("\n");

        if (annotation.order().length() > 0)
            s.append("graph_order ").append(annotation.order()).append("\n");

        if (annotation.scale().length() > 0)
            s.append("graph_scale ").append(annotation.scale()).append("\n");

        if (annotation.period().length() > 0)
            s.append("graph_period ").append(annotation.period()).append("\n");

        if (annotation.vlabel().length() > 0)
            s.append("graph_vlabel ").append(annotation.vlabel()).append("\n");

        for (String label : annotation.labels())
            s.append(label).append(".label ").append(label).append("\n");

        return s.toString();
    }
}
