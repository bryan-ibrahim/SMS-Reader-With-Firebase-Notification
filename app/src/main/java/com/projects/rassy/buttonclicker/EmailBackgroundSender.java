package com.projects.rassy.buttonclicker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EmailBackgroundSender extends AppCompatActivity {

    EditText et_to, et_message;
    Button btn_send;
    Context c;
    String GMail = "deurola@gmail.com"; //replace with you GMail
    String GMailPass = "XizzleGameR"; // replace with you GMail Password

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // for hiding title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        c = this;
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String str_to = et_to.getText().toString();
                String str_message = et_message.getText().toString();
//                String str_subject = et_subject.getText().toString();

                // Check if there are empty fields
                if (!str_to.equals("") &&
                        !str_message.equals("")){


                    //Check if 'To:' field is a valid email
                    if (isValidEmail(str_to)){
                        et_to.setError(null);


                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(c, "Sending... Please wait", Toast.LENGTH_LONG).show();
                            }
                        });
                        sendEmail(str_to, "Test", str_message);
                    }else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                et_to.setError("Not a valid email");
                            }
                        });
                    }


                }else{
                    Toast.makeText(c, "There are empty fields.", Toast.LENGTH_LONG).show();
                }

            }


        });


    }

    private void sendEmail(final String to, final String subject, final String message) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(GMail,
                            GMailPass);
                    sender.sendMail(subject,
                            message,
                            GMail,
                            to);
                    Log.w("sendEmail","Email successfully sent!");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, "Email successfully sent!", Toast.LENGTH_LONG).show();
                            et_to.setText("");
                            et_message.setText("");
//                            et_subject.setText("");
                        }
                    });

                } catch (final Exception e) {
                    Log.e("sendEmail", e.getMessage(), e);

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(c, "Email not sent. \n\n Error: " + e.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    });


                }
            }

        }).start();
    }


    // Check if parameter 'emailAddress' is a valid email
    public final static boolean isValidEmail(CharSequence emailAddress) {
        if (emailAddress == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
        }
    }
}
