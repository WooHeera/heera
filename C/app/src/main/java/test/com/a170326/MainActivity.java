package test.com.a170326;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    private Context mContect = null;
    private boolean m_bTrackingMode = true;

    private TMapGpsManager tmapgps = null;
    private TMapData Tmapdata = new TMapData();
    private TMapView tmapview = null;
    private static String mApiKey = "759b5f01-999a-3cb1-a9ed-f05e2f121476";
    private static int mMarkerID;

    private Button search;
    private Button route;
    private TMapPoint start_point = null;
    private TMapPoint dest_point = null;
    private EditText input;
    private Double search_lat = null;
    private Double search_lon = null;
    private Double start_lat = null;
    private Double start_lon = null;

    private ArrayList<TMapPoint> m_tmapPoint = new ArrayList<TMapPoint>();
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public String received_user_name[] = new String[100];
    public String received_user_email[] = new String[100];

    /***
     * HTTP CLIENT
     ***/
    public String URI_RECEIVE_USER_ID = "https://apis.skplanetx.com/tmap/routes?callback=&version=1&format=json&appKey="+mApiKey;
    public static HttpClient httpclient;
    HttpPost httppost;
    private JSONArray countriesArray;
    private JSONArray countries;

    /***
     * HTTP CLIENT
     ***/


    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        start();

        mContect = this;

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);
        linearLayout.addView(tmapview);
        tmapview.setSKPMapApiKey(mApiKey);

        tmapview.setCompassMode(true);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);
        tmapgps = new TMapGpsManager(MainActivity.this);
        tmapgps.setMinTime(1000);
        tmapgps.setMinDistance(5);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);    //연결된 인터넷으로 위치 파악
        //tmapgps.setProvider(tmapgps.GPS_PROVIDER);     //GPS로 위치 파악
        tmapgps.OpenGps();
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                Toast.makeText(MainActivity.this, "클릭", Toast.LENGTH_SHORT).show();
            }
        });

        input = (EditText) findViewById(R.id.search_con);
        search = (Button) findViewById(R.id.search_button);
        route = (Button) findViewById(R.id.route);

        Log.i("hhr",String.valueOf(tmapview.getLatitude()));

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputtext = input.getText().toString();
                Log.i("ee", "ee");
                start_point = tmapgps.getLocation();

                Tmapdata.findAllPOI(inputtext, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        /*for (int i = 0; i< poiItem.size(); i++){
                            TMapPOIItem item = poiItem.get(i);

                            Log.d("주소로 찾기","POI Name: "+item.getPOIName().toString()+", "+
                                    "Address: "+item.getPOIAddress().replace("null","")+", "+ "Point: "
                                    + item.getPOIPoint().toString());


                        }*/

                        TMapPOIItem item = poiItem.get(0);

                        Log.d("주소로 찾기", "POI Name: " + item.getPOIName().toString() + ", " +
                                "Address: " + item.getPOIAddress().replace("null", "") + ", " + "Point: "
                                + item.getPOIPoint().toString());

                        search_lat = item.getPOIPoint().getLatitude();
                        search_lon = item.getPOIPoint().getLongitude();
                        Log.i(Double.toString(search_lat), Double.toString(search_lon));

                        tmapview.setTrackingMode(false);
                        tmapview.setCenterPoint(search_lon,search_lat);
                        Log.i("hr",String.valueOf(tmapview.getLatitude()));
                        tmapview.refreshMap();
                        Log.i("hhrr",String.valueOf(tmapview.getLatitude()));


                    }
                });
            }
        });

        route.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dest_point = new TMapPoint(search_lat,search_lon);


                Log.i("dd"+start_point.toString(),dest_point.toString());
                start_lat = start_point.getLatitude();
                start_lon = start_point.getLongitude();
                Log.i("hh: "+ Double.toString(start_lon), Double.toString(start_lat));
                Tmapdata.findPathData(start_point, dest_point, new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine tMapPolyLine) {
                        tmapview.addTMapPath(tMapPolyLine);
                        tmapview.setTrackingMode(true);
                    }
                });
                Send_Login_Info(Double.toString(start_lon),Double.toString(start_lat),Double.toString(search_lon),Double.toString(search_lat),"WGS84GEO","WGS84GEO");
            }
        });


    }

    public void start() {
        httpclient = new DefaultHttpClient();
        /***  time out  ***/
        httpclient.getParams().setParameter("http.protocol.expect-continue", false);
        httpclient.getParams().setParameter("http.connection.timeout", 10000);
        httpclient.getParams().setParameter("http.socket.timeout", 10000);
        Log.i("psj", "heera : 00002");


    }

    public void Send_Login_Info(String _start_x, String _start_y, String _end_x, String _end_y, String _req_coordtype, String _res_coordtype) {
        Log.i("psj", "heera : 00001");
        RequestRoad requestlogin = new RequestRoad();
        requestlogin.execute(URI_RECEIVE_USER_ID, _start_x, _start_y, _end_x, _end_y, _req_coordtype, _res_coordtype);

    }

    public class RequestRoad extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... params) {
            String uri = params[0];
            String start_x = params[1];
            String start_y = params[2];
            String end_x = params[3];
            String end_y = params[4];
            String req_coordtype = params[5];
            String res_coordtype = params[6];
            String result = "";
            String pro = "";
            String proo = "";
            String temp = "";
            double coorx;

            /*** Add data to send ***/
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("startX", start_x));
            nameValuePairs.add(new BasicNameValuePair("startY", start_y));
            nameValuePairs.add(new BasicNameValuePair("endX", end_x));
            nameValuePairs.add(new BasicNameValuePair("endY", end_y));
            nameValuePairs.add(new BasicNameValuePair("reqCoordType", req_coordtype));
            nameValuePairs.add(new BasicNameValuePair("resCoordType", res_coordtype));
            StringBuilder builder = new StringBuilder();

            /*** Send post message ***/
            httppost = new HttpPost(uri);
            try {
                UrlEncodedFormEntity urlendoeformentity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
                httppost.setEntity(urlendoeformentity);
                HttpResponse response = httpclient.execute(httppost);
                StatusLine statusLine = response.getStatusLine();

                Log.i("ljw",String.valueOf(statusLine.getStatusCode()));
                if (statusLine.getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));

                    String line;
                    int j = 0;

                    while ((line = reader.readLine()) != null) {
                        Log.i("psj", "server result " + line);
                        if (j > 0) {
                            temp = temp + "\n";
                        }
                        temp = temp + line;
                        j++;
                    }
                    Log.i("psj", "server result " + temp);
                    builder.append(temp);

                }


            } catch (Exception e) {
                Log.i("psj", "Exception try1:" + e.getStackTrace());
                e.printStackTrace();
            }

             /* -- Treat JSON data to string array  --*/
            try {
                JSONObject root = new JSONObject(builder.toString());
                countriesArray = root.getJSONArray("features");
                //countries = root.getJSONArray("coordinates");// 자료 갯수

                       /* -- No data --*/

                if (countriesArray.length() < 1) {
                    return "FALSE";
                }



                  /* -- Save data --*/
                for (int i = 0; i < countriesArray.length(); i++) {

                    JSONObject JObject = countriesArray.getJSONObject(i);

                    result = JObject.getString("geometry");
                    pro = JObject.getJSONObject("geometry").getString("type");
                    proo = JObject.getJSONObject("geometry").getJSONArray("coordinates").getString(0);


                    //proo = subJObject.optString("type");
                    //coorx = ;



                    //received_user_name[i] = JObject.getString("description");
                    //received_user_email[i] = JObject.getString("description");
                    Log.i("whrcp",result);
                    Log.i("whrcp2",pro);
                    Log.i("whrcp3",proo);


                    /*rsiv_message[i] = JObject.getString("message");
                    rsiv_msg_id[i] = Integer.parseInt(JObject.getString("msg_id"));
                    rsiv_like_count[i] = Integer.parseInt(JObject.getString("like_count"));
                    rsiv_like_flag[i] = Boolean.valueOf(JObject.getString("like_flag")).booleanValue();
                    rsiv_profile_pic[i]    = JObject.getString("profile_pic");
                    rsiv_uid[i]         = Integer.parseInt(JObject.getString("uid"));
                    rsiv_pic[i]         = JObject.getString("pic");
                    rsiv_comment_count[i] = Integer.parseInt(JObject.getString("comment_count"));*/

                    //messageindex++;
                    //m_arr.add(new Item(rsiv_username[i],rsiv_message[i],rsiv_msg_id[i],rsiv_like_count[i],rsiv_like_flag[i],rsiv_profile_pic[i],rsiv_uid[i],rsiv_pic[i],rsiv_comment_count[i]));
                }




            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //return "true";
            if(result.equals("1"))
            {
                return "TRUE";
            }
            else{
                return "FALSE";
            }

        }

        protected void onPostExecute(String result) {
            Log.i("psj", "heera : login 00001 ttt"+result);

        }
    }

}


