package www.practice.com.searchcafe;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private boolean isValid(String text, TextInputLayout til) {
        if ("".equals(text)) {
            til.setError("This field must be non-empty string");
            return false;
        } else {
            til.setError(null);
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViewById(R.id.button_cafe_search).setOnClickListener(l -> {
            EditText si = findViewById(R.id.edit_text_si);
            EditText gu = findViewById(R.id.edit_text_gu);
            EditText dong = findViewById(R.id.edit_text_dong);
            TextInputLayout til_si = findViewById(R.id.text_input_layout_si);
            TextInputLayout til_gu = findViewById(R.id.text_input_layout_gu);
            TextInputLayout til_dong = findViewById(R.id.text_input_layout_dong);
            if (isValid(si.getText().toString(), til_si) && isValid(gu.getText().toString(), til_gu)
                    && isValid(dong.getText().toString(), til_dong)) {
                Intent intent = MapsActivity.newIntent(this, new CharSequence[]{
                        si.getText().toString(),
                        gu.getText().toString(),
                        dong.getText().toString()
                });
                startActivity(intent);
                finish();
            }
        });
    }
}
