package com.doctl.patientcare.main.utility;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 1/1/2015.
 */
public class HttpFileUpload {
    URL connectURL;
    FileInputStream fileInputStream = null;
    private Context c;
    private String fileName;

    public HttpFileUpload(Context c, String urlString){
        try{
            this.c = c;
            connectURL = new URL(urlString);
        }catch(Exception ex){
            Log.e("HttpFileUpload", "URL Malformatted");
        }
    }

    public String Send_Now(String fileName, FileInputStream fStream){
        this.fileName = fileName;
        fileInputStream = fStream;
        return Sending();
    }

    String Sending(){
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        String Tag="fSnd";
        String ServerAccessToken = Utils.getAuthTokenFromSharedPreference(c);
        try {
            HttpURLConnection conn = (HttpURLConnection)connectURL.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            conn.setRequestProperty("Authorization", "Token " + ServerAccessToken);

            DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"file\";filename=\"" + fileName +"\"" + lineEnd);
            dos.writeBytes(lineEnd);

            int bytesAvailable = fileInputStream.available();
            int maxBufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[ ] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0,bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // close streams
            fileInputStream.close();
            dos.flush();

            InputStream is = conn.getInputStream();

            // retrieve the response from server
            int ch;
            StringBuffer b =new StringBuffer();
            while( ( ch = is.read() ) != -1 ){ b.append( (char)ch ); }
            String s = b.toString();
            Log.i("Response",s);
            JSONTokener tokener = new JSONTokener(s);
            JSONObject jsonResponse = new JSONObject(tokener);
            if(jsonResponse.has("profilePicUrl")) {
                return jsonResponse.getString("profilePicUrl");
            }
            dos.close();
        } catch (MalformedURLException ex) {
            Log.e(Tag, "URL error: " + ex.getMessage(), ex);
        } catch (IOException ioe) {
            Log.e(Tag, "IO error: " + ioe.getMessage(), ioe);
        } catch (JSONException je) {
            Log.e(Tag, "IO error: " + je.getMessage(), je);
        }

        return "";
    }
}
