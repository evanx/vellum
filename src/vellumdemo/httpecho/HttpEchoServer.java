/*
 * Source https://github.com/evanx by @evanxsummers

       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements. See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership. The ASF licenses this file to
       you under the Apache License, Version 2.0 (the "License").
       You may not use this file except in compliance with the
       License. You may obtain a copy of the License at:

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.  
 */
package vellumdemo.httpecho;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Context;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import vellum.util.Bytes;

/**
 *
 * @author evan.summers
 */
public class HttpEchoServer {

    final static int httpPort = 9980;
    final static int httpsPort = 9981;
    
    Tomcat tomcat = new Tomcat();

    Connector httpsConnector = new Connector();
       
    public void start(String[] args) throws Exception {
        tomcat.setPort(httpPort);
        if (args.length == 0) {
            printUsage(System.out);
        } else if (args.length == 3) {
            initHttpsConnector(args[0], args[1], args[2]);
            Service service = tomcat.getService();
            service.addConnector(httpsConnector);
            Connector defaultConnector = tomcat.getConnector();
            defaultConnector.setRedirectPort(httpsPort);
        } else {
            System.err.println("args.length " + args.length);
            printUsage(System.err);
        }
        tomcat.setSilent(false);
        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());
        Tomcat.addServlet(ctx, "hello", helloServlet);
        Tomcat.addServlet(ctx, "info", infoServlet);
        Tomcat.addServlet(ctx, "echo", echoServlet);
        ctx.addServletMapping("/hello", "hello");
        ctx.addServletMapping("/info", "info");
        ctx.addServletMapping("/echo", "echo");
        ctx.addServletMapping("/*", "echo");
        tomcat.start();
        Thread.sleep(8000);
        tomcat.getServer().stop();
    }
    
    void printUsage(PrintStream stream) {
        stream.println("Usage: keystorePath password keyAlias");
    }
    
    void initHttpsConnector(String keystorePath, String password, String keyAlias) {
       httpsConnector.setPort(httpsPort);
       httpsConnector.setSecure(true);
       httpsConnector.setScheme("https");
       httpsConnector.setAttribute("keyAlias", keyAlias);
       httpsConnector.setAttribute("keystorePass", password);
       httpsConnector.setAttribute("keystoreFile", keystorePath);
       httpsConnector.setAttribute("clientAuth", true);
       httpsConnector.setAttribute("sslProtocol", "TLS");
       httpsConnector.setAttribute("SSLEnabled", true);
    }
    
    HttpServlet echoServlet = new HttpServlet() {

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            if (req.getContentLength() < Bytes.fromK(4)) {
                echoLines(req, resp);
            }  
        }
    };

    void echoBytes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Writer writer = resp.getWriter();
        if (req.getContentLength() < Bytes.fromK(4)) {
            while (true) {
                int c = req.getInputStream().read();
                if (c == -1) {
                    break;
                } else if (c < ' ') {
                }
                writer.write(c);
                System.out.print((char) c);
            }
        }
    }
    
    void echoLines(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
        Writer writer = resp.getWriter();
        PrintWriter printer = new PrintWriter(writer);
        while (true) {
            String string = reader.readLine();
            if (string == null) {
                break;
            }
            printer.println(string);
            System.out.println(string);
        }
    }
    

    HttpServlet infoServlet = new HttpServlet() {

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            Writer w = resp.getWriter();
            PrintWriter pw = new PrintWriter(w);
            pw.printf("encoding %s\n", req.getCharacterEncoding());
            pw.printf("contentLength %s\n", req.getContentLength());
            pw.printf("contentType %s\n", req.getContentType());
            pw.printf("method %s\n", req.getMethod());
            if (req.getContentLength() < Bytes.fromK(4)) {
                byte[] data = new byte[req.getContentLength()];
                req.getInputStream().read(data);
                pw.printf("data: %s\n", new String(data));
            }
            pw.printf("pathInfo %s\n", req.getPathInfo());
            pw.printf("parts.size %d\n", req.getParts().size());
            pw.printf("parameterMap.size %d\n", req.getParameterMap().size());
            pw.printf("queryString %s\n", req.getQueryString());
            pw.printf("requestURI %s\n", req.getRequestURI());
        }
    };
    
    HttpServlet helloServlet = new HttpServlet() {

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            Writer w = resp.getWriter();
            w.write("Hello, World!");
            w.flush();
        }
    };

    public static void main(String[] args) {
        try {
            HttpEchoServer server = new HttpEchoServer();
            server.start(args);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }

    }
}
