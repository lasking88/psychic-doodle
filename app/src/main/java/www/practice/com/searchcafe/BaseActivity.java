package www.practice.com.searchcafe;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {

    private ProgressDialog mProgressDialog;

    protected void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Searching...");
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}