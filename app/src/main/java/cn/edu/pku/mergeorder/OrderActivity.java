package cn.edu.pku.mergeorder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static cn.edu.pku.mergeorder.TimeChange.minuteTurnSecond;
import static cn.edu.pku.mergeorder.TimeChange.secondTurnMinute;

/**
 * Created by kingvern on 17/12/19.
 */

public class OrderActivity extends Activity {

    TextView start,end,memberNo,memberNow,submit,lefttime,back,done;
    long lefttimeInt = 0;
    String endTime = "";
    int guestInt=0;

    int retCode;

    String s;
    OkHttpClient client = new OkHttpClient.Builder().build();

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };

    private void refreshUI() {
        long time=System.currentTimeMillis();
        lefttime.setText(secondTurnMinute((int)((Long.valueOf(endTime)-time)/1000)));

//        memberNow.setText("已拼人数："+i.getStringExtra("memberNow"));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        start = (TextView) findViewById(R.id.start);

        end = (TextView) findViewById(R.id.end);
        memberNo = (TextView) findViewById(R.id.memberNo);
        memberNow = (TextView) findViewById(R.id.memberNow);
        submit = (TextView) findViewById(R.id.submit);
        lefttime = (TextView) findViewById(R.id.lefttime);
        back = (TextView) findViewById(R.id.back);
        done = (TextView) findViewById(R.id.done);

        Intent i = this.getIntent();
        start.setText("起点："+i.getStringExtra("start"));
//        Log.d("start", i.getStringExtra("start"));
        end.setText("终点："+i.getStringExtra("end"));
        memberNo.setText("人数上限："+i.getStringExtra("memberNo"));
        memberNow.setText("已拼人数："+i.getStringExtra("memberNow"));

        endTime = i.getStringExtra("endTime");
        if(i.getStringExtra("id").equals("guest")) {//客人来了
            done.setVisibility(View.GONE);
            guestInt = 0;

            submit.setText("加入");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(guestInt == 0) {
//                        submit("http://47.95.255.230/join.php?endTime="+endTime);
                        submit("http://47.95.255.230/join.php");
                        submit.setText("退出");
                        guestInt = 1;

                    }else {
//                        submit("http://47.95.255.230/quit.php?endTime="+endTime);
                        submit.setText("加入");
                        submit("http://47.95.255.230/join.php");
                        guestInt = 0;
                    }


                }
            });
        }else {//主人做客
            done.setVisibility(View.VISIBLE);

            submit.setText("取消");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
//                    submit("http://47.95.255.230/join.php");
                    Intent i = new Intent(OrderActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();

                }
            });

        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                submit("http://47.95.255.230/done.php?endTime="+endTime);
                submit("http://47.95.255.230/join.php");
                Intent i = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        new Thread(mRunnable).start();




    }
    private Runnable mRunnable = new Runnable() {
         public void run() {
          while(true) {
               try {
               Thread.sleep(1000);
                mHandler.sendMessage(mHandler.obtainMessage());
                } catch (InterruptedException e) {
                e.printStackTrace();
                }
                }
          }
     };


    private void submit(String s) {
        //初始化okhttp客户端
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("endTime", endTime)
                .build();
        //开始请求，填入url，和表单
        Request request = new Request.Builder()
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
                            OrderActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(OrderActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(OrderActivity.this,"成功!",Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(OrderActivity.this,"错误!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

//-----------------------------------------------------------------------------------------------------------------------------------------

    private void cancel() {
        String s = "http://47.95.255.230/cancel.php";

        //初始化okhttp客户端
//        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("endTime", endTime)
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
                            OrderActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(OrderActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(OrderActivity.this,"取消成功!",Toast.LENGTH_SHORT).show();
                            //跳转
                            Intent i = new Intent(OrderActivity.this,MainActivity.class);
                            startActivity(i);
//                setResult(RESULT_OK,i);
                            finish();

                        } else {
                            Toast.makeText(OrderActivity.this,"取消失败!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void join() {
        String s = "http://47.95.255.230/join.php";

        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("endTime", endTime)
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
                            OrderActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(OrderActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(OrderActivity.this,"加入成功!",Toast.LENGTH_SHORT).show();
                            //跳转
                            Intent i = new Intent(OrderActivity.this,MainActivity.class);
                            startActivity(i);
//                setResult(RESULT_OK,i);
                            finish();

                        } else {
                            Toast.makeText(OrderActivity.this,"加入失败!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void quit() {
        String s = "http://47.95.255.230/quit.php";

        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("endTime", endTime)
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
                            OrderActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(OrderActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(OrderActivity.this,"褪裙成功!",Toast.LENGTH_SHORT).show();
                            //跳转
                            Intent i = new Intent(OrderActivity.this,MainActivity.class);
                            startActivity(i);
//                setResult(RESULT_OK,i);
                            finish();

                        } else {
                            Toast.makeText(OrderActivity.this,"褪裙失败!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void done() {
        String s = "http://47.95.255.230/done.php";

        //初始化okhttp客户端
        OkHttpClient client = new OkHttpClient.Builder().build();
        //创建post表单，获取username和password（没有做非空判断）
        RequestBody post = new FormBody.Builder()
                .add("endTime", endTime)
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
                            OrderActivity.this.s = response.body().string();

                            //解析出后端返回的数据来
                            JSONObject jsonObject = new JSONObject(String.valueOf(OrderActivity.this.s));
                            retCode = jsonObject.getInt("success");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //客户端自己判断是否成功。
                        if (retCode == 1) {
                            Toast.makeText(OrderActivity.this,"成交了!",Toast.LENGTH_SHORT).show();
                            //跳转
                            Intent i = new Intent(OrderActivity.this,MainActivity.class);
                            startActivity(i);
//                setResult(RESULT_OK,i);
                            finish();

                        } else {
                            Toast.makeText(OrderActivity.this,"未能成交!同志仍需努力",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


}
