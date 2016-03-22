package in.olivo.patientcare.main.utility;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/1/2015.
 */
public class HttpFileUpload {
    private static final String TAG = HttpFileUpload.class.getSimpleName();
    URL connectURL;
    InputStream fileInputStream = null;
    List<NameValuePair> nameValuePairs;
    private Context c;
    private String fileName;

    public HttpFileUpload(Context c, String urlString) {
        try {
            this.c = c;
            connectURL = new URL(urlString);
        } catch (Exception ex) {
            Logger.e("HttpFileUpload", "URL Malformatted");
        }
    }

    public JSONObject Send_Now(String fileName, InputStream fStream) {
        this.fileName = fileName;
        fileInputStream = fStream;
        this.nameValuePairs = new ArrayList<>();
        return Sending();
    }

    public JSONObject Send_Now(String fileName, InputStream fStream, List<NameValuePair> nameValuePairs) {
        this.fileName = fileName;
        this.nameValuePairs = nameValuePairs;
        fileInputStream = fStream;
        return Sending();
    }

    JSONObject Sending() {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag = "fSnd";
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);
        try {
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("Authorization", "Token " + ServerAccessToken);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);

            for (NameValuePair nvp : nameValuePairs) {
                nvp.getName();
                dos.writeBytes("Content-Disposition: form-data; name=\"" + nvp.getName() + "\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(nvp.getValue());
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + lineEnd);
            }

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            int uploadedData = 0;
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                uploadedData += bytesRead;
                Logger.v(TAG, String.format("uploaded bytes = %d and remaining bytes = %d", uploadedData, bytesAvailable));
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;
            StringBuffer b = new StringBuffer();
            while ((ch = is.read()) != -1) {
                b.append((char) ch);
            }
            String s = b.toString();
            Logger.i("Response", s);
            JSONTokener tokener = new JSONTokener(s);
            JSONObject jsonResponse = new JSONObject(tokener);
            dos.close();
            return jsonResponse;
            /*System.out.println(jsonResponse.toString());
            if(jsonResponse.has("profilePicUrl")) {
                return jsonResponse.getString("profilePicUrl");
            }else if(jsonResponse.has("fileURL")){
                return jsonResponse.getString("fileURL");
            }*/

        } catch (MalformedURLException ex) {
            Logger.e(Tag, "URL error: " + ex.getMessage());
        } catch (IOException | JSONException e) {
            Logger.e(Tag, "IO error: " + e.getMessage());
        }

        return null;
    }
}
