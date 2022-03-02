package com.moers.ssl;

import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Establish a SSL connection to a host and port, writes a byte and
 * prints the response. 
 * 
 * Based on the famous SSLPoke from Atlassian, but support http proxy and 
 * updated to Java 11.
 *
 * See http://confluence.atlassian.com/display/JIRA/Connecting+to+SSL+services
 * 
 * Run {@code java -Dhttps.proxyHost=proxy-out -Dhttps.proxyPort=13128 SSLPoke.java google.com 443}
 */
public class SSLPokeProxy {

    public static void main(String[] args) {

        System.out.println(SSLPokeProxy.class.getName());

        if (args.length != 2) {
            System.out.println("Usage: " + SSLPoke.class.getName() + " <host> <port>");
            System.exit(1);
        }

        String tunnelHost = System.getProperty("https.proxyHost", null);
        Integer tunnelPort = Integer.getInteger("https.proxyPort", 0);
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try (SSLSocket sslSocket = connectTlsSocket(
                sslSocketFactory,
                tunnelHost,
                tunnelPort,
                args[0],
                Integer.parseInt(args[1]))) {
            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            sslSocket.setSSLParameters(sslParams);
            InputStream in = sslSocket.getInputStream();
            OutputStream out = sslSocket.getOutputStream();
            out.write(1); // Write a test byte to get a reaction :)
            while (in.available() > 0) {
                System.out.print(in.read());
            }
            System.out.println("Successfully connected");
        } catch (Exception exception) {
            exception.printStackTrace();
            System.exit(1);
        }
    }

    private static SSLSocket connectTlsSocket(SSLSocketFactory sslSocketFactory, String tunnelHost, int tunnelPort,
            String host, int port)
            throws IOException {
        if (tunnelHost != null || tunnelPort != 0) {
            System.out.println(String.format("Using proxy settings: %s:%d", tunnelHost, tunnelPort));
            Socket tunnel = new Socket(tunnelHost, tunnelPort);
            connectTunnel(tunnel, host, port);
            return (SSLSocket) sslSocketFactory.createSocket(tunnel, host, port, true);
        }

        return (SSLSocket) sslSocketFactory.createSocket(host, port);
    }

    private static void connectTunnel(Socket proxySocket, String proxyHost, int proxyPort)
            throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(proxySocket.getOutputStream(), "ASCII7"));
        bw.write("CONNECT " + proxyHost + ":" + proxyPort + " HTTP/1.1\n"
                + "Proxy-Connection: Keep-Alive"
                + "User-Agent: " + SSLPoke.class.getName()
                + "\r\n\r\n");
        bw.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(proxySocket.getInputStream(), "ASCII7"));

        
        String statusLine = br.readLine();


        if (!statusLine.startsWith("HTTP/1.1 200")) {
            throw new IOException("Unable to tunnel through "
                    + proxySocket.getInetAddress().getHostAddress() + ":" + proxySocket.getPort()
                    + ".  Proxy returns \"" + statusLine + "\"");
        }
    }
}
