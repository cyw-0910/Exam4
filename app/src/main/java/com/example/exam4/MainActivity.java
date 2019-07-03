package com.example.exam4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button getdata;//获取数据
    private Button analysisdata;//分析数据
    private TextView json_view;//json视图
    private TextView data_view;//解析视图
    //设置全局变量
    String city;
    String ws;
    String wd;
    String temp;
    String time;
    String sd;
    StringBuilder response_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getdata = findViewById(R.id.get_json);//关联ID
        analysisdata = findViewById(R.id.analysis_json);//关联ID
        json_view = findViewById(R.id.text_json);//关联ID
        data_view = findViewById(R.id.text_data);//关联ID
        //使用接口方式
        getdata.setOnClickListener(this);
        analysisdata.setOnClickListener(this);
    }

    //按钮点击事件
    public void onClick(View v) {
        if (v.getId() == R.id.get_json) {
            //通过调用Response_Data传递原始数据并进行解析
            //Response_Data(response_data.toString());
            sendRequestWithHttpURLConnection();
        }
        if (v.getId() == R.id.analysis_json) {
            //通过调用Response_Json传递解析数据
            Response_Json(response_data);

        }
    }

    //发送网络请求
    private void sendRequestWithHttpURLConnection() {
        //开启一个新线程获取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL("http://www.weather.com.cn/data/sk/101300901.html");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    response_data = response;
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Response_Data(response.toString());//调用后，该方法用于显示原始数据
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    //数据传递屏显示
    private void Response_Data(final String response) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //设置显示
                json_view.setText(response);
            }
        });
    }
    //解析数据并显示
    private void Response_Json(StringBuilder response) {
        try {
            //JSON数据
            JSONObject object = new JSONObject(String.valueOf(response));
            JSONObject jsonObject = object.getJSONObject("weatherinfo");
            city = jsonObject.getString("city");//通过name字段获取其所包含的字符串
            temp = jsonObject.getString("temp");
            ws = jsonObject.getString("WS");
            wd = jsonObject.getString("WD");
            time = jsonObject.getString("time");
            sd = jsonObject.getString("SD");
            //Log.d(">>>", city);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //显示解析以后的json数据
        //开启新线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                data_view.setText("\n当前城市：" + city +
                        "\n更新时间：" + time +
                        "\n实时温度：" + temp +
                        "\n相对湿度：" + sd +
                        "\n风向风力：" + wd + ws);
            }
        });
    }

    //专门用于显示原始数据,没有调用
    private void showParseJson() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //设置清空
                json_view.setText("");
                //设置显示
                json_view.setText(response_data);
            }
        });
    }

    //显示解析以后的json数据，没有调用
    private void showResponse() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                data_view.setText("\n当前城市：" + city +
                        "\n更新时间：" + time +
                        "\n实时温度：" + temp +
                        "\n相对湿度：" + sd +
                        "\n风向风力：" + wd + ws);
            }
        });
    }
}
