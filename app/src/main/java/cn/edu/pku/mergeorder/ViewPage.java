package cn.edu.pku.mergeorder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by kingvern on 17/12/27.
 */

public class ViewPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
