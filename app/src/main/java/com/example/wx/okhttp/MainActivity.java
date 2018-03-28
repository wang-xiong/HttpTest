package com.example.wx.okhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }





    private void setRequestWithHttpURLConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://www.baidu.com");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    /*connection.setRequestMethod("POST");
                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes("username=admin&password=123456");*/
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();
                    //对获取到的数据流进行读取
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //返回结果
            }
        });
    }

    private void setRequestWithOkHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client= new OkHttpClient();
                    /*RequestBody requestBody = new FormBody.Builder()
                            .add("username", "admin")
                            .add("password", "123456")
                            .build();*/
                    Request request = new Request.Builder()
                            .url("http:/www.baidu.com")
                            //.post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().toString();
                    parseXMLWithPull(responseData);
                    showResponse(responseData);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    //网络传输最常用的两种方式xml和Gson

    /*解析xml数据如下
    * <apps>
    *     <app>
    *         <id>1</id>
    *         <name>zhangsan</name>
    *         <version>1.0</version>
    *     </app>
    *      <app>
    *         <id>1</id>
    *         <name>zhangsan</name>
    *         <version>1.0</version>
    *     </app>
    *      <app>
    *         <id>1</id>
    *         <name>zhangsan</name>
    *         <version>1.0</version>
    *     </app>
    * </apps>
    * */
    //pull解析方式
    private void parseXMLWithPull(String xmlData) throws IOException {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String id = "";
            String name = "";
            String version = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    //开始解析某个节点
                    case  XmlPullParser.START_TAG: {
                        if ("id".equals(nodeName)) {
                            id = xmlPullParser.nextText();
                        } else  if ("name".equals(nodeName)) {
                            name = xmlPullParser.nextText();
                        } else if ("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
                    // 完成解析某个节点
                    case XmlPullParser.END_TAG:
                        if("app".equals(nodeName)) {
                            Log.d("wx", "id is " + id + "name" + name + "version" +version);
                        }
                        break;
                    default:
                        break;
                }
                eventType= xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    //sax解析
    private void parseXMLWithSAX(String response) {

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            XMLReader xmlReader  = factory.newSAXParser().getXMLReader();
            SAXHandler handler = new SAXHandler();
            xmlReader.setContentHandler(handler);
            xmlReader.parse(new InputSource(new StringReader(response)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //Json数据解析
    //[{"id":"5", "name":"zhangsan", "version":"1"},{....},{...}]
    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String id =jsonObject.getString("id");
                String name = jsonObject.getString("name");
                String version = jsonObject.getString("version");
                Log.d("wx", "id is " + id + "name" + name + "version" + version);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pasrseJSONWithGson(String jsonData) {
        Gson gson = new Gson();
        List<App> appList = gson.fromJson(jsonData, new TypeToken<List<App>>(){}.getType());
        for(App app : appList) {
            Log.d("wx", "id is " + app.getId() + "name" + app.getName() + "version" + app.getVersion());
        }
    }

    private void test() {
        HttpUtil.sendOkHttpRequest("", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
            }
        });
    }
}
