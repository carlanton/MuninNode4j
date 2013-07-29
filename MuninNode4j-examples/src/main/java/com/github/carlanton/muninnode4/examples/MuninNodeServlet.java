package com.github.carlanton.muninnode4.examples;

import com.github.carlanton.muninnode4j.MuninNode4j;
import com.github.carlanton.muninnode4j.MuninPlugin;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class MuninNodeServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(MuninNodeServlet.class);
    private MuninNode4j muninNode4j;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String pkg = config.getInitParameter("package");
        String listen = config.getInitParameter("listen");

        muninNode4j = new MuninNode4j(pkg, listen);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/plain");
        String plugin = req.getParameter("plugin");
        String action = req.getParameter("action");

        if (action == null || plugin == null)
            return;

        MuninPlugin muninPlugin = muninNode4j.getPlugin(plugin);

        if (muninPlugin == null)
            return;

        PrintWriter out = resp.getWriter();

        if ("config".equals(action)) {
            out.print(muninPlugin.getConfig());
        } else if ("fetch".equals("action")) {
            out.print(muninPlugin.execute());
        }

        out.close();
    }
}
