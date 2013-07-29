package com.github.carlanton.muninnode4j;

import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MuninNode4j {
    private static final Logger log = Logger.getLogger(MuninNode4j.class);
    private Map<String, MuninPlugin> plugins;

    public MuninNode4j(String pkg) {
        plugins = findMuninPlugins(pkg);
    }

    public MuninNode4j(String pkg, String listen) {
        this(pkg);

        if (listen == null)
            return;

        String[] fields = listen.split(":");

        InetAddress inetAddress = null;
        int port;

        try {
            if (!"*".equals(fields[0]))
                inetAddress = InetAddress.getByName(fields[0]);

            port = Integer.parseInt(fields[1]);
        } catch (Throwable t) {
            log.error("Failed parsing listen: " + t.getMessage());
            return;
        }

        log.info("Spawning MuninNode4j server on " +
                (inetAddress == null ? "*" : inetAddress.toString()) +
                ":" + port);

        ConnectionHandler.methods = plugins;

        new Thread(new MuninNodeServer(inetAddress, port), "MuninNode4j").start();
    }

    private static Map<String, MuninPlugin> findMuninPlugins(String pkg) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setUrls(ClasspathHelper.forPackage(pkg));
        configurationBuilder.setScanners(new MethodAnnotationsScanner());
        Reflections reflections = new Reflections(configurationBuilder);

        Map<String, MuninPlugin> plugins = new HashMap<String, MuninPlugin>();

        for (Method method : reflections.getMethodsAnnotatedWith(MuninMonitor.class)) {
            MuninMonitor annotation = method.getAnnotation(MuninMonitor.class);

            String title = annotation.title().length() > 0 ? annotation.title() : method.getName();

            if (plugins.containsKey(title)) {
                log.warn("Graph with title '" + title + "' already exists! Ignoring method '" +
                        method.getName() + "'");
            } else if (!Modifier.isStatic(method.getModifiers())) {
                log.warn("Method must be static! Ignoring method '" + method.getName() + "'");
            } else {
                plugins.put(title, new MuninPlugin(method, annotation));
            }
        }

        return Collections.unmodifiableMap(plugins);
    }

    private static class MuninNodeServer implements Runnable {
        private final InetAddress inetAddress;
        private final int port;

        private MuninNodeServer(InetAddress inetAddress, int port) {
            this.inetAddress = inetAddress;
            this.port = port;
        }

        @Override
        public void run() {
            try {
                ServerSocket serverSocket;

                if (inetAddress != null)
                    serverSocket = new ServerSocket(port, 10, inetAddress);
                else
                    serverSocket = new ServerSocket(port, 10);

                while (!Thread.currentThread().isInterrupted()) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ConnectionHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public MuninPlugin getPlugin(String plugin) {
        return plugins.get(plugin);
    }

    public Map<String, MuninPlugin> getPluginMap() {
        return plugins;
    }

    public Collection<MuninPlugin> getAllPlugins() {
        return plugins.values();
    }
}
