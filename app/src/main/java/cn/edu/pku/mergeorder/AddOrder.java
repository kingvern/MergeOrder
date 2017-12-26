package cn.edu.pku.mergeorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;
import static cn.edu.pku.mergeorder.MainActivity.username;
import static cn.edu.pku.mergeorder.TimeChange.minuteTurnSecond;

/**
 * Created by kingvern on 17/12/19.
 */

public class AddOrder extends Activity {
    private EditText start, end, memberNo, lefttime;
    private TextView submit, back;

    int retCode;

    String s;

    private String url = "http://47.95.255.230/add.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_order);

        start = (EditText) findViewById(R.id.start);
        end = (EditText) findViewById(R.id.end);
        memberNo = (EditText) findViewById(R.id.memberNo);
        lefttime = (EditText) findViewById(R.id.lefttime);
        submit = (TextView) findViewById(R.id.submit);
        back = (TextView) findViewById(R.id.back);



//
//
//
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork() // or .detectAll() for all detectable problems
//                .penaltyLog()
//                .build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                .detectLeakedSqlLiteObjects()
//                .detectLeakedClosableObjects()
//                .penaltyLog()
//                .penaltyDeath()
//                .build());
//


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add("http://47.95.255.230/add.php");
//                add();
            }
        });



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AddOrder.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });




    }

    private void add(String s) {

        String lefttimeStr = lefttime.getText().toString();

        long time = System.currentTimeMillis();

        final long endTimeLong = time + Long.valueOf(minuteTurnSecond(lefttimeStr)) * 1000;

        SharedPreferences sharedPrefeSrences = getSharedPreferences("config", MODE_PRIVATE);
        final String phoneNoStr = sharedPrefeSrences.getString("phoneNo", "null");

        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("start", start.getText().toString())
                .add("end", end.getText().toString())
                .add("memberNo", memberNo.getText().toString())
                .add("endTime", String.valueOf(endTimeLong))
                .add("master", phoneNoStr)
                .build();
        //开始请求，填入url，和表单
        final Request request = new Request.Builder()
                .url(s)
                .post(post)
                .build();

        //客户端回调
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //失败的情况（一般是网络链接问题，服务器错误等）
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                //UI线程运行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            //临时变量（这是okhttp的一个锅，一次请求的response.body().string()只能用一次，否则就会报错）
                            AddOrder.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(AddOrder.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(AddOrder.this,"成功!",Toast.LENGTH_SHORT).show();
                            //跳转
                            Intent i = new Intent(AddOrder.this,OrderActivity.class);
                            i.putExtra("start",start.getText().toString());
                            i.putExtra("end",end.getText().toString());
                            i.putExtra("memberNo",memberNo.getText().toString());
                            i.putExtra("endTime",String.valueOf(endTimeLong));
                            i.putExtra("id","master");
                            i.putExtra("master",phoneNoStr);
                            startActivity(i);
//                setResult(RESULT_OK,i);
                            finish();

                        } else {
                            Toast.makeText(AddOrder.this,"错误!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}
