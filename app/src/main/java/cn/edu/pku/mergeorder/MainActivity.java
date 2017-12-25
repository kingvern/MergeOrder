package cn.edu.pku.mergeorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.pku.mergeorder.TimeChange.secondTurnMinute;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final int UPDATE_ORDER_LIST = 1;

    private TextView title_name;
    private TextView register_signin;
    private TextView add_order;

    public static String username = "";
    public static String phoneNo = "";
    static ListView mlist;
    static SimpleAdapter simplead;
    static List<Map<String, String>> listems;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
//            switch (msg.what) {
//                case UPDATE_ORDER_LIST:
                    updateOrderList();//更新列表
//                default:
//                    break;
            }
//        }

};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_order = (TextView) findViewById(R.id.add_order);
        register_signin = (TextView) findViewById(R.id.register_signin);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        phoneNo = sharedPreferences.getString("phoneNo","null");
        Log.d("phoneNo",phoneNo);
        if(phoneNo.equals("null")){
            Intent i = new Intent(this, RigisterAndSignin.class);
            startActivity(i);
            finish();
        }


        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {//判断网络状态
            Log.d("order", "网络OK");
            Toast.makeText(MainActivity.this,"网络OK!", Toast.LENGTH_LONG).show();
            //开始获取表格数据
            queryOrderlist();

        }else
        {
            Log.d("order", "网络挂了");
            Toast.makeText(MainActivity.this,"网络挂了!", Toast.LENGTH_LONG).show();
        }

        mlist = (ListView) findViewById(R.id.order_list);
        listems = new ArrayList<Map<String, String>>();
        for(int i = 0; i<3; i++){
            Map<String, String> listem = new HashMap<String, String>();
            listem.put("orderNo","NA");
            listem.put("start","NA");
            listem.put("end","NA");
            listem.put("memberNo","已拼"+"NA"+"人");
            listem.put("endtime","剩余"+"NA");
            listems.add(listem);
        }
        simplead = new SimpleAdapter(this, listems,
                R.layout.item, new String[] { "start", "end", "memberNo", "endtime"},
                new int[] {R.id.order_start,R.id.order_end,R.id.order_memberNo,R.id.order_lefttime});
        mlist.setAdapter(simplead);
        mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Map<String, String> map = listems.get(pos);
                Intent i = new Intent(MainActivity.this,OrderActivity.class);
                i.putExtra("start",map.get("start"));
                i.putExtra("end",map.get("end"));
                i.putExtra("memberNo",map.get("memberNo"));
                i.putExtra("endTime",map.get("endTime"));
                i.putExtra("id","guest");
                Log.d("start", map.get("start"));
                startActivity(i);
//                setResult(RESULT_OK,i);
                finish();
            }
        });
    }
    // http://47.95.255.230/orderlist.php
    private void queryOrderlist()  {
        final String address = "http://47.95.255.230/checklist.php";
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);

                        HttpURLConnection con = null;
                        try {
                            URL url = new URL(address);
                            con = (HttpURLConnection) url.openConnection();
                            con.setRequestMethod("GET");
                            con.setConnectTimeout(8000);
                            con.setReadTimeout(8000);
                            InputStream in = con.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String str;
                            while ((str = reader.readLine()) != null) {
                                response.append(str);
                            }
                            String responseStr = response.toString();
                            Log.d("orderlistjson", responseStr);

                            listems.clear();

                            try {
                                long time=System.currentTimeMillis();
                                JSONArray jsonArray = new JSONArray(responseStr);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = (JSONObject) jsonArray.opt(i);
                                    Map<String, String> listem = new HashMap<String, String>();
                                    listem.put("start", jsonObject.getString("start"));
                                    listem.put("end", jsonObject.getString("end"));
                                    listem.put("memberNo", "已拼"+jsonObject.getString("memberNo")+"人");
//                                    int lefttimeInt = (int)(Long.valueOf(jsonObject.getString("endtime"))/1000 - time/1000);
//                                    String lefttime = secondTurnMinute(lefttimeInt);
                                    listem.put("endTime",jsonObject.getString("endTime"));

                                    listem.put("endtime", "剩余"+secondTurnMinute((int)((Long.valueOf(jsonObject.getString("endTime"))-time)/1000)));
//                                    Log.d("endtime", jsonObject.getString("endtime"));


                                    listems.add(listem);

                                }
//                                updateOrderList();
                                Log.d("gengxinbiaole", "shuaxinle");


                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            if (responseStr != null) {
//                                Message msg = new Message();
//                                msg.what = UPDATE_ORDER_LIST;
//                                mHandler.sendMessage(msg);
                                mHandler.sendMessage(mHandler.obtainMessage());

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (con != null) {
                                con.disconnect();
                            }
                        }
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    public void updateOrderList(){//更新数据，过程中会反映到UI

        simplead.notifyDataSetChanged();
//        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.register_signin){
            Intent i = new Intent(this, RigisterAndSignin.class);
             startActivity(i);
             finish();
        }

        if (view.getId() == R.id.add_order){
            Intent i = new Intent(this, AddOrder.class);
            startActivity(i);
            finish();


        }

    }
}
