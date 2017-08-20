package arduino.bogdan.ro.aplicatiearduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    ImageButton btnLeft;
    ImageButton btnRight;
    ImageButton btnUp;
    ImageButton btnDown;

    boolean upPressed = false;
    boolean downPressed = false;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> devices;

    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnLeft = (ImageButton) findViewById(R.id.leftBtn);
        btnRight = (ImageButton) findViewById(R.id.rightBtn);
        btnUp = (ImageButton) findViewById(R.id.upBtn);
        btnDown = (ImageButton) findViewById(R.id.downBtn);

        btnLeft.setOnTouchListener(this);
        btnRight.setOnTouchListener(this);
        btnUp.setOnTouchListener(this);
        btnDown.setOnTouchListener(this);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "No bluetooth detected :(", Toast.LENGTH_SHORT).show();
        } else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }

            getPairedDevices();
        }

    }

    private void getPairedDevices() {
        devices = bluetoothAdapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bDev :
                    devices) {
                if (bDev.getName().equals("HC-06")) {

                    Toast.makeText(this, "found!", Toast.LENGTH_SHORT).show();
                    ParcelUuid[] uuids = bDev.getUuids();

                    try {
                        BluetoothSocket socket = bDev.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        outputStream = socket.getOutputStream();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "A crapat ceva :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_CANCELED) {
//            Toast.makeText(this, "Please enable Bluetooth!", Toast.LENGTH_LONG).show();
//            finish();
//        }
//    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Please enable Bluetooth!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendCommandToArduino(String command){
        try {
            outputStream.write(command.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (v.getId()) {

            case R.id.upBtn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Snackbar.make(v, "pressed up!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    sendCommandToArduino("f");

                    upPressed = true;
                    btnUp.setImageDrawable(getResources().getDrawable(R.drawable
                            .up_arrow_clicked));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Snackbar.make(v, "released up!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    sendCommandToArduino("g");

                    upPressed = false;
                    btnUp.setImageDrawable(getResources().getDrawable(R.drawable
                            .up_arrow_not_clicked));
                }
                break;

            case R.id.downBtn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Snackbar.make(v, "pressed down!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    sendCommandToArduino("b");

                    downPressed = true;
                    btnDown.setImageDrawable(getResources().getDrawable(R.drawable
                            .down_arrow_clicked));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Snackbar.make(v, "released down!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    sendCommandToArduino("g");

                    downPressed = false;
                    btnDown.setImageDrawable(getResources().getDrawable(R.drawable
                            .down_arrow_not_clicked));
                }
                break;

            case R.id.leftBtn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (upPressed) {
                        Snackbar.make(v, "pressed up_left!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("l");
                    }

                    if (downPressed) {
                        Snackbar.make(v, "pressed down_left!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("i");
                    }

                    btnLeft.setImageDrawable(getResources().getDrawable(R.drawable.left_arrow_clicked));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (upPressed) {
                        Snackbar.make(v, "released up_left!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("f");
                    }

                    if (downPressed) {
                        Snackbar.make(v, "released down_left!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("b");
                    }

                    btnLeft.setImageDrawable(getResources().getDrawable(R.drawable.left_arrow_not_clicked));
                }
                break;

            case R.id.rightBtn:
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (upPressed) {
                        Snackbar.make(v, "pressed up_right!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("r");
                    }

                    if (downPressed) {
                        Snackbar.make(v, "pressed down_right!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("y");
                    }

                    btnRight.setImageDrawable(getResources().getDrawable(R.drawable
                            .right_arrow_clicked));
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (upPressed) {
                        Snackbar.make(v, "released up_right!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("f");
                    }
                    if (downPressed) {
                        Snackbar.make(v, "released down_right!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                        sendCommandToArduino("b");
                    }
                    btnRight.setImageDrawable(getResources().getDrawable(R.drawable
                            .right_arrow_not_clicked));
                }
                break;
        }
        return true;
    }
}
