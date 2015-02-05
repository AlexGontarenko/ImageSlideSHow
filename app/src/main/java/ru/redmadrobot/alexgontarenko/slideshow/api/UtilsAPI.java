package ru.redmadrobot.alexgontarenko.slideshow.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import ru.redmadrobot.alexgontarenko.slideshow.parcers.BasePanoramioResponse;
import ru.redmadrobot.alexgontarenko.slideshow.parcers.PanoramioParcer;

public class UtilsAPI {

    private final static String URL_FORMAT = "http://www.panoramio.com/map/get_panoramas.php?set=public&from=0&to=%1$d&minx=%2$f&miny=%3$f&maxx=%4$f&maxy=%5$f&size=medium&mapfilter=true";

    private static String createURL(int count, double lat, double lon, double epsM) {
        final double epsLat = calculateLatitudeEPS(epsM), epsLon = calculateLongitudeEPS(lat, epsM);
        return String.format(Locale.US, URL_FORMAT, count, lon - epsLon, lat - epsLat, lon + epsLon, lat + epsLat);
    }

    public static BasePanoramioResponse getPlaceImg(Context context, int count, double lat, double lon, double eps) throws APIWebEsception {
        try {
            final PanoramioParcer parser = new PanoramioParcer();

            HttpURLConnection connection = null;
            try {
                connection = getApiConnection(context, count, lat, lon, eps);
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    if (parser != null) {
                        try {
                            parser.readInputStream(connection.getInputStream());
                        } finally {
                            if (connection.getInputStream() != null)
                                connection.getInputStream().close();
                        }
                    } else {
                        BufferedReader in = null;
                        final InputStream is = connection.getInputStream();
                        try {
                            in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null)
                                Log.d("PANORAMIO_RESPONSE", inputLine);
                        } finally {
                            if (in != null)
                                in.close();

                            if (is != null)
                                is.close();
                        }
                    }
                } else {
                    throw new APIWebEsception(APIWebEsception.NOT_200);
                }

            } catch (APIWebEsception apiWebEsception) {
                apiWebEsception.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

                throw new APIWebEsception(APIWebEsception.BAD_CONNECTION);
            }
            return parser.getObject();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }


    private static HttpURLConnection getApiConnection(Context context, int count, double lat, double lon, double epsM)
            throws IOException, APIWebEsception {
        if (isNetworkAvailable(context)) {
            final int timeOut = 15000;

            final String url = createURL(count, lat, lon, epsM);

            final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setRequestProperty("content-type", "application/json; charset=utf-8");

            connection.setConnectTimeout(timeOut);
            connection.setReadTimeout(timeOut * 2);
            connection.setUseCaches(false);
            connection.setDoInput(true);

            return connection;
        } else {
            throw new APIWebEsception(APIWebEsception.NO_INTERNET);
        }
    }

    public static boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static double calculateLatitudeEPS(double epsM) {
        return (360 * epsM / (2 * Math.PI * 6400000.0));
    }

    private static double calculateLongitudeEPS(double lat, double epsM) {
        return (360 * epsM / (Math.cos(lat*Math.PI/180) * 40000000.0));
    }
}
