package com.github.carlanton.muninnode4j;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Map;

class ConnectionHandler implements Runnable {
    private static final Logger log = Logger.getLogger(ConnectionHandler.class);
    private static final String nodeName = "MuninNode4j";

    public static Map<String, MuninPlugin> methods;

    private final Socket clientSocket;

    public ConnectionHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (IOException e) {
            log.error("Got exception ", e);
        }
    }

    private void handleClient() throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));

        // Based on https://github.com/samuel/python-munin/blob/master/bin/munin-node.py

        String line;

        while ((line = in.readLine()) != null) {
            line = line.trim();

            String[] cmd = line.split(" ", 2);

            if (cmd[0].equals("list")) {

                StringBuilder sb = new StringBuilder();
                for (String title : methods.keySet())
                    sb.append(title).append(" "); // TODO: OK?

                out.write(sb.toString());
                out.write("\n");

            } else if (cmd[0].equals("nodes")) {

                out.write("nodes\n" + nodeName + "\n.\n");

            } else if (cmd[0].equals("version")) {

                out.write("com.github.carlanton.muninnode4j.MuninNode4j 1.0\n");

            } else if (cmd[0].equals("config") || cmd[0].equals("fetch")) {

                if (cmd.length <= 1 || !methods.containsKey(cmd[1])) {
                    out.write("# Unknown service");
                } else if (cmd[0].equals("config")) {
                    out.write(methods.get(cmd[1]).getConfig());
                } else {
                    out.write(methods.get(cmd[1]).execute());
                }

                out.write(".\n");
            } else if (cmd[0].equals("quit")) {
                break;
            } else {
                out.write("# Unknown command. Try list, nodes, config, fetch, version or quit\n");
            }

            out.flush();
        }

        in.close();
        out.close();
        clientSocket.close();
    }
}
