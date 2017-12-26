package cn.edu.pku.mergeorder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TimeActivity extends AppCompatActivity {
    EditText mEditText;
    EditText mEditText2;
    Button mButton;
    Button mButton2;
    int retCode;
    String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_acitvity);

        mEditText = (EditText) findViewById(R.id.editText);
        mEditText2 = (EditText) findViewById(R.id.editText2);
        mButton = (Button) findViewById(R.id.button);
        mButton2 = (Button) findViewById(R.id.button2);


        //监听事件
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这是后端提供的登录接口
                start("http://47.95.255.230/add.php");
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这是后端提供的注册接口
                Intent intent = new Intent(Intent.ACTION_CALL);
                if (ActivityCompat.checkSelfPermission(TimeActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Uri data = Uri.parse("tel:17600200127");
                intent.setData(data);
                startActivity(intent);
            }
        });
    }
//
//    ("start",map.get("start"));
//                i.putExtra("end",map.get("end"));
//                i.putExtra("memberNo",map.get("memberNo"));
//                i.putExtra("endTime",map.get("endTime"));
//                i.putExtra("id","guest");

    private void start(String s) {
        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("start", mEditText.getText().toString())
                .add("end", mEditText2.getText().toString())
                .add("memberNo", mEditText.getText().toString())
                .add("endTime", mEditText2.getText().toString())
                .add("master", mEditText.getText().toString())
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
                            TimeActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(TimeActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(TimeActivity.this,"成功!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TimeActivity.this,"错误!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}

