package cn.edu.pku.mergeorder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by kingvern on 17/12/19.
 */

public class RigisterAndSignin extends Activity {
    private EditText username, phoneNo;
    TextView signin;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rigister_signin);

        phoneNo = (EditText) findViewById(R.id.phoneNo);
        signin = (TextView) findViewById(R.id.signin);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String phoneNoStr = sharedPreferences.getString("phoneNo","null");
        if(!phoneNoStr.equals("null")){
            phoneNo.setText(phoneNoStr);
        }



        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String phoneNoStr2 = phoneNo.getText().toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phoneNo",phoneNoStr2);
                editor.commit();

                Intent i = new Intent(RigisterAndSignin.this,MainActivity.class);
                startActivity(i);

            }
        });
    }
}
