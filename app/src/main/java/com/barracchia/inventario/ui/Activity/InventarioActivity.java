package com.barracchia.inventario.ui.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.barracchia.inventario.R;
import com.barracchia.inventario.model.ItemInventario;
import com.barracchia.inventario.ui.Adapter.ItemAdapter;
import com.barracchia.inventario.utils.KeyboardUtil;

public class InventarioActivity extends AppCompatActivity {

    private static final String TAG = InventarioActivity.class.getSimpleName();

    private String lastFilter  = "";

    private EditText edtFind;
    private Handler handlerRefresh = new Handler();
    private Runnable runnableRefresh;

    private ListView lswList;

    private ItemAdapter myItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        edtFind = findViewById(R.id.edtFind);
        edtFind.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean procesado = false;

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Filter
                    Filter();

                    // Ocultar teclado virtual
                    KeyboardUtil.hideSoftKeyboard(InventarioActivity.this);

                    procesado = true;
                }
                return procesado;
            }
        });

        lswList = findViewById(R.id.lvwList);
        ViewGroup headerView = (ViewGroup) getLayoutInflater().inflate(R.layout.listview_inventario_header, lswList, false);
        lswList.addHeaderView(headerView);
        lswList.setClickable(true);
        lswList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ItemInventario item = (ItemInventario) lswList.getItemAtPosition(position);

                if (item != null) {
                    String text = item.getCode() + " - " + item.getDescription() + " || " + item.getRemark();
                    Snackbar.make(findViewById(android.R.id.content), text,
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
        myItemAdapter = new ItemAdapter(this);
        myItemAdapter.setResource(R.layout.listview_inventario_item);
        lswList.setAdapter(myItemAdapter);
        myItemAdapter.updateItems(ItemInventario.getInventario());

        handlerRefresh = new Handler();
        runnableRefresh = new Runnable() {
            public void run() {
                // Filter
                Filter();

                handlerRefresh.postDelayed(this, 1000);
            }
        };
        handlerRefresh.postDelayed(runnableRefresh, 1000);
    }

    private void Filter() {
        // Filter
        String newFilter = edtFind.getText().toString();
        // Only when change detected
        if (!lastFilter.equals(newFilter)) {
            myItemAdapter.updateItems(ItemInventario.getInventario(newFilter));
            lastFilter = newFilter;
        }
    }

}
