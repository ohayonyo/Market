package OutResources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestSender {

    public String send(String path, String method, String params, String msg) throws IOException {
        byte[] bParams = params.getBytes();
        byte[] bMsg = msg.getBytes();
        int len = bParams.length + bMsg.length;
        URL url = new URL(path);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setFixedLengthStreamingMode(len);
        http.setRequestMethod(method);
        http.setDoOutput(true);
        http.connect();
        OutputStream outputStream = http.getOutputStream();
        outputStream.write(bParams);
        outputStream.write(bMsg);
        String output = "";

            http.setConnectTimeout(10000);
            http.setReadTimeout(10000);

            InputStream inputStream = http.getInputStream();
            int next = 0;
            do {
                next = inputStream.read();
                if (next != -1) {
                    output += (char) next;
                }
            } while (next != -1);

            if (output.equals("unexpected-output")) throw new IOException();
        return output;
    }

}
