package cn.edu.pku.mergeorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by kingvern on 17/12/27.
 */

public class ViewPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);

        setContentView(R.layout.view_page);


        new Thread(mRunnable).start();
    }

    private Runnable mRunnable = new Runnable() {
        public void run() {

            try {
                Thread.sleep(2000);

                Intent i = new Intent(ViewPage.this, MainActivity.class);
                startActivity(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };
}
