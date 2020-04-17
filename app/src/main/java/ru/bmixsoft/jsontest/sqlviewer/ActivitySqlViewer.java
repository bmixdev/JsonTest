package ru.bmixsoft.jsontest.sqlviewer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ru.bmixsoft.jsontest.R;

public class ActivitySqlViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_container, new
                    FragmentSqlViewer(), FragmentSqlViewer.class.getSimpleName()).commit();
        }

    }
}
