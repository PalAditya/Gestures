package com.app.lenovo.gestures;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.PixelFormat;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
public class MainActivity extends AppCompatActivity implements OnGesturePerformedListener {
    GestureLibrary library;
    int flag=0,a=0;
    private static final String T = "Torch";
    private static final String H = "Hotspot";
    private static final String B = "Bluetooth";
    private static final String F = "Facebook";
    private static final String G="Google";
    private static final String S="Stopwatch";
    private static final String V="Voice";
    public static final String L="Love";
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        //Dummy method to Satisfy JVM
    }
    public static Camera cam = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);*/
        setContentView(R.layout.activity_main);
        library = GestureLibraries.fromRawResource(this, R.raw.gestures);
        if(!library.load()) {
            //Toast.makeText(getApplicationContext(),"Library Didn't Load",Toast.LENGTH_SHORT).show();
            //finish();
        }
        GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        gestureView.addOnGesturePerformedListener(this);
        //GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        gestureView.addOnGesturePerformedListener(new GestureOverlayView.OnGesturePerformedListener() {
            @Override
            public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
                ArrayList<Prediction> predictions = library.recognize(gesture);

                // Get highest-ranked prediction
                if (predictions.size() > 0) {
                    Prediction prediction = predictions.get(0);

                    // Ignore weak predictions

                    if (prediction.score > 2.0) {
                        if(prediction.name.equals(T))
                        {
                            setImage(1);
                            //Toast.makeText(getApplicationContext(),"Torch",Toast.LENGTH_SHORT).show();
                            if(flag==0) {
                                flashLightOn(null);
                                flag = 1;
                            }
                            else {
                                flashLightOff(null);
                                flag = 0;
                            }
                        }
                        else if(prediction.name.equals(H))
                        {
                            setImage(2);
                            //Toast.makeText(getApplicationContext(),"Hotspot",Toast.LENGTH_SHORT).show();
                            ApManager.isApOn(MainActivity.this); // check Ap state :boolean
                            ApManager.configApState(MainActivity.this); // change Ap state :boolean

                        }
                        else if(prediction.name.equals(S))
                        {
                            setImage(3);
                            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            if (mBluetoothAdapter.isEnabled()) {
                                mBluetoothAdapter.disable();
                            }
                            else
                                mBluetoothAdapter.enable();
                            //Toast.makeText(getApplicationContext(),"Yes",Toast.LENGTH_SHORT).show();
                        }
                        else if(prediction.name.equals(G))
                        {
                            Context ctx=MainActivity.this; // or you can replace **'this'** with your **ActivityName.this**
                            try {
                                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.android.chrome");
                                ctx.startActivity(i);
                            } catch (Exception e) {
                               // Toast.makeText(getApplicationContext(),"Name not Found",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(prediction.name.equals(F))
                        {
                            Context ctx=MainActivity.this; // or you can replace **'this'** with your **ActivityName.this**
                            try {
                                Intent i = ctx.getPackageManager().getLaunchIntentForPackage("com.facebook.lite");
                                ctx.startActivity(i);
                            } catch (Exception e) {
                                //Toast.makeText(getApplicationContext(),"Name not Found",Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(prediction.name.equals(V))
                        {
                            Intent recordIntent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                            startActivity(recordIntent);
                            //Toast.makeText(getApplicationContext(),"Voice",Toast.LENGTH_SHORT).show();
                        }
                        else if(prediction.name.equals(L))
                        {
                            try
                            {
                                setImage(4);
                                //CustomGifView gif=(CustomGifView)findViewById(R.id.gifview);
                                //Toast.makeText(getApplicationContext(),"Love",Toast.LENGTH_SHORT).show();
                            }catch(Exception e)
                            {
                                //Toast.makeText(getApplicationContext(),"Love_E",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

        });
    }
    public void flashLightOn(View view) {

        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam = Camera.open();
                Parameters p = cam.getParameters();
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
                cam.setParameters(p);
                cam.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void flashLightOff(View view) {
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                cam.stopPreview();
                cam.release();
                cam = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }
    void setImage(int setFlag)
    {
        try {
            ImageView img=(ImageView)findViewById(R.id.images);
            if(setFlag==1)
                img.setBackgroundResource(R.drawable.light);
            else if(setFlag==2)
                img.setBackgroundResource(R.drawable.hot);
            else if(setFlag==3)
                img.setBackgroundResource(R.drawable.bt);
            else
            {
                int arr[]={0,1,2,3,4,5,6,7,8,9,10};
                int rand=arr[a++%11];
                if(rand==0)
                    img.setBackgroundResource(R.drawable.dad);
                else if(rand==1)
                    img.setBackgroundResource(R.drawable.quotes1);
                else if(rand==6)
                {
                    Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }
                else if(rand==3)
                    img.setBackgroundResource(R.drawable.quotes2);
                else if(rand==4)
                    img.setBackgroundResource(R.drawable.images);
                else if(rand==5)
                    img.setBackgroundResource(R.drawable.quotes3);
                else if(rand==2)
                    img.setBackgroundResource(R.drawable.quotes4);
                else if(rand==7)
                    img.setBackgroundResource(R.drawable.quotes5);
                else if(rand==8)
                    img.setBackgroundResource(R.drawable.quotes6);
                else if(rand==9)
                    img.setBackgroundResource(R.drawable.quotes7);
                else
                    img.setBackgroundResource(R.drawable.quotes8);
            }
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
