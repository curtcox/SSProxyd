import java.net.*;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;

class SSProxyd implements Runnable {

    URL    url;
    int    port;
    Thread thread;

    static public void main(String args[]) throws Exception {
        if (args.length == 2) {
            go(args);
            return;
        }
        usage();
    }

    static void usage() {
        log("usage: URL port");
        log("       URL  -- to proxy to");
        log("       port -- to listen on");
    }

    static void go(String args[]) throws Exception {
        new SSProxyd(new URL(args[0]), Integer.parseInt(args[1])).start();
    }

    SSProxyd(URL url, int port) {
        this.url  = url;
        this.port = port;
        thread = new Thread(this,"server socket");
    }

    void start() {
        thread.start();
    }

    public void run() {
        try{
           log("starting SSProxyd on port number " + port);
           serve(new ServerSocket(port));
        } catch (IOException e) {
           log("Unable to start SSproxyd on port number " + port);
        }
    }

    void serve(ServerSocket ss) throws IOException {
       while ( true ) {
            Socket server = ss.accept();
            log("Accepted client");
            try {
                Socket client = openClient();
                log("There is a server on port " + url.getPort() + " of " + url.getHost());
                CopyThread.of(server.getInputStream(), client.getOutputStream());
                CopyThread.of(client.getInputStream(), server.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
       }
    }

    Socket openClient() throws IOException {
        if (url.getProtocol().endsWith("s")) {
            return openSecureSocket();
        }
        return openSocket();
    }

    Socket openSocket() throws IOException {
        return new Socket(url.getHost(), url.getPort());
    }

    Socket openSecureSocket() throws IOException {
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        String  host = url.getHost();
        int hostPort = url.getPort();
        hostPort = (hostPort<0) ? 443 : hostPort;
    	SSLSocket socket = (SSLSocket)factory.createSocket(host, hostPort);
    	socket.startHandshake();
        return socket;
    }

    static void log(Object o) {
        System.out.println(o.toString());
    }
}
