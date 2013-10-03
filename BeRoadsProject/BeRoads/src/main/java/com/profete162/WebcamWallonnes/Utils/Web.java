package com.profete162.WebcamWallonnes.Utils;

import android.content.Context;
import android.content.pm.PackageManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Web {


    public static HttpResponse makeRequest(String path, String id, String lan, String area, String lat, String lng) throws Exception {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(path);

        //convert parameters into JSON object
        JSONObject holder = new JSONObject();
        holder.put("registration_id", id);
        holder.put("language", lan);
        holder.put("area", area);
        holder.put("lat", lat);
        holder.put("lng", lng);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(holder.toString());

        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        return (HttpResponse) httpclient.execute(httpost, responseHandler);
    }

    public static InputStream DownloadJsonFromUrlAndCacheToSd(String url,
                                                              String fileName, Context context) {
        // Log.i("", "*** DOWNLOAD HTTP");
        InputStream source = retrieveStream(url, context);

        if (fileName == null)
            return source;

        // Petite entourloupe pour éviter des soucis de InputSTream qui se
        // ferme
        // apres la premiere utilisation.
        Web test = new Web();
        CopyInputStream cis = test.new CopyInputStream(source);
        InputStream sourcetoReturn = cis.getCopy();
        InputStream sourceCopy = cis.getCopy();

        File dir = context.getDir("CACHE", Context.MODE_PRIVATE);
        dir.mkdirs();
        File file = new File(dir, fileName);

        // Write to SDCard
        try {
            FileOutputStream f = new FileOutputStream(file);
            byte[] buffer = new byte[32768];
            int read;
            try {
                while ((read = sourceCopy.read(buffer, 0, buffer.length)) > 0) {
                    f.write(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sourcetoReturn;
    }

    public static InputStream retrieveStream(String url, Context context) {

        DefaultHttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        // TODO: stocker la version pour ne pas faire un appel Ã  chaque fois.
        String myVersion = "0.0";
        PackageManager manager = context.getPackageManager();
        try {
            myVersion = (manager.getPackageInfo(context.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Log.i("","URL:"+url);

        request.setHeader("User-Agent", "Waza_Be: BeRoads " + myVersion
                + " for Android - " + System.getProperty("http.agent"));

        // Log.w("getClass().getSimpleName()", "URL TO CHECK " + url);

        try {
            HttpResponse response = client.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                // Log.w("getClass().getSimpleName()", "Error " + statusCode
                // + " for URL " + url);
                return null;
            }

            HttpEntity getResponseEntity = response.getEntity();
            return getResponseEntity.getContent();

        } catch (IOException e) {
            // Log.w("getClass().getSimpleName()", " Error for URL " + url, e);
        }

        return null;

    }

    public class CopyInputStream {
        private InputStream _is;
        private ByteArrayOutputStream _copy = new ByteArrayOutputStream();

        /**
         *
         */
        public CopyInputStream(InputStream is) {
            _is = is;
            try {
                copy();
            } catch (IOException ex) {
                // do nothing
            }
        }

        private int copy() throws IOException {
            int read = 0;
            int chunk;
            byte[] data = new byte[256];

            if (_is == null)
                return -1;

            while (-1 != (chunk = _is.read(data))) {
                read += data.length;
                _copy.write(data, 0, chunk);
            }

            return read;
        }

        public InputStream getCopy() {
            return new ByteArrayInputStream(_copy.toByteArray());
        }
    }

}
