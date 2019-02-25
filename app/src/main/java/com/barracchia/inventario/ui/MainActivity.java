package com.barracchia.inventario.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.barracchia.inventario.R;
import com.barracchia.inventario.model.ItemInventario;
import com.barracchia.inventario.utils.KeyboardUtil;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int REQUEST_SCAN = 8;

    private List<ItemInventario> myList = new ArrayList<>();

    private TextView txtCode;
    private TextView txtGroup;
    private TextView txtBrand;
    private TextView txtDescription;
    private TextView txtRemark;

    private EditText edtCode;

    private Button btnAdd;
    private Button btnClear;
    private Button btnClearList;

    private ListView lswList;

    private ItemAdapter myItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtCode = findViewById(R.id.txtCode);
        txtGroup = findViewById(R.id.txtGroup);
        txtBrand = findViewById(R.id.txtBrand);
        txtDescription = findViewById(R.id.txtDescription);
        txtRemark = findViewById(R.id.txtRemark);

        edtCode = findViewById(R.id.edtCode);
        edtCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Procesar
                    showItem(v.getText().toString(), false);

                    // Ocultar teclado virtual
                    KeyboardUtil.hideSoftKeyboard(MainActivity.this);

                    procesado = true;
                }
                return procesado;
            }
        });

        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearItem();
            }
        });

        btnClearList = findViewById(R.id.btnClearList);
        btnClearList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myList.clear();
                myItemAdapter.updateItems(myList);
            }
        });

        lswList = findViewById(R.id.lvwList);
        lswList.setClickable(true);
        lswList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ItemInventario item = (ItemInventario) lswList.getItemAtPosition(position);

                String text = item.getCode() + " - " + item.getDescription() + " || " + item.getRemark();
                Snackbar.make(findViewById(android.R.id.content), text,
                        Snackbar.LENGTH_LONG)
                        .show();
            }
        });
        lswList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                myList.remove(position);
                myItemAdapter.updateItems(myList);
                return true;
            }
        });
        myItemAdapter = new ItemAdapter(this);
        lswList.setAdapter(myItemAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareList();
            }
        });

        // Check if the READ_EXTERNAL_STORAGE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE permission has not been granted.
            requestReadExternalStoragePermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_inventario) {
            showInventario();
            return true;
        }
        if (id == R.id.action_scan) {
            showCamera();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInventario() {
        // Check if the READ_EXTERNAL_STORAGE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE permission has not been granted.
            requestReadExternalStoragePermission();
        } else {
            Intent intent = new Intent(this, InventarioActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_SCAN) {
            if (data.hasExtra("returnCode")) {
                Bundle bundle = data.getExtras();
                if (bundle != null)
                    showItem(bundle.getString("returnCode"), true);
            }
        }
    }

    /**
     * Called when the 'show camera' button is clicked.
     */
    public void showCamera() {
        Log.i(TAG, "Show camera button pressed. Checking permission.");
        // Check if the Camera permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Camera permission has not been granted.
            requestCameraPermission();
        } else {
            // Camera permissions is already available, show the camera preview.
            Log.i(TAG,
                    "CAMERA permission has already been granted. Displaying camera preview.");
            Intent intent = new Intent(this, ScanActivity.class);
            startActivityForResult(intent, REQUEST_SCAN);
        }
    }

    /**
     * Requests the Camera permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestCameraPermission() {
        Log.i(TAG, "CAMERA permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying camera permission rationale to provide additional context.");
            Snackbar.make(findViewById(android.R.id.content), R.string.permission_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA);
                        }
                    })
                    .show();
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA);
        }
    }

    /**
     * Requests the READ_EXTERNAL_STORAGE permission.
     * If the permission has been denied previously, a SnackBar will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestReadExternalStoragePermission() {
        Log.i(TAG, "READ_EXTERNAL_STORAGE permission has NOT been granted. Requesting permission.");

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            Log.i(TAG,
                    "Displaying storage permission rationale to provide additional context.");
            Snackbar.make(findViewById(android.R.id.content), R.string.permission_read_external_storage,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.label_ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {

            // Storage permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void showItem(String codigo, boolean fromScan) {
        ItemInventario item = ItemInventario.getByCodigo(codigo);
        if (item != null) {
            txtCode.setText(item.getCode());
            txtGroup.setText(item.getGroup());
            txtBrand.setText(item.getBrand());
            txtDescription.setText(item.getDescription());
            txtRemark.setText(item.getRemark());

            edtCode.setText(item.getCode());
            edtCode.setVisibility(View.INVISIBLE);

        } else {
            txtCode.setText("");
            txtGroup.setText("");
            txtBrand.setText("");
            txtDescription.setText("");
            txtRemark.setText("");

            edtCode.setText(codigo);
            if (fromScan)
                edtCode.setVisibility(View.VISIBLE);
        }
    }

    private void clearItem() {
        txtCode.setText("");
        txtGroup.setText("");
        txtBrand.setText("");
        txtDescription.setText("");
        txtRemark.setText("");

        edtCode.setText("");
        edtCode.setVisibility(View.VISIBLE);
    }

    private void addItem() {
        // Check if the READ_EXTERNAL_STORAGE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_EXTERNAL_STORAGE permission has not been granted.
            requestReadExternalStoragePermission();
        } else {
            if (!TextUtils.isEmpty(edtCode.getText())) {
                ItemInventario item = ItemInventario.getByCodigo(String.valueOf(edtCode.getText()));
                if (item != null) {
                    // Check if duplicated item
                    boolean duplicated = false;
                    for (ItemInventario itemCheck : myList) {
                        if (item.equals(itemCheck)) {
                            duplicated = true;
                            break;
                        }
                    }

                    if (!duplicated) {
                        myList.add(item);
                        myItemAdapter.updateItems(myList);
                    } else {
                        Toast.makeText(this, R.string.code_duplicated, Toast.LENGTH_SHORT).show();
                    }
                    clearItem();
                } else {
                    Toast.makeText(this, R.string.code_not_ok, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void shareList() {
        if (!myList.isEmpty()) {
            StringBuilder bld = new StringBuilder();
            for (ItemInventario item : myList) {
                bld.append(item.getCode());
                bld.append(System.getProperty("line.separator"));
            }
            String shareInfo = bld.toString();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareInfo);
            startActivity(shareIntent);
        }

    }
}
