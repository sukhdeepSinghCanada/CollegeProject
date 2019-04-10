package com.example.simpleauthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText enterEmail;
    Button sendCodeButton;
    private ProgressDialog dialog;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgressDialog(MainActivity.this);
        getView();
        setListeners();
    }

    private void setListeners() {
        sendCodeButton.setOnClickListener(this);
    }

    private void getView() {
        enterEmail = findViewById(R.id.email);
        sendCodeButton = findViewById(R.id.sendButton);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                if (enterEmail.getText().toString().trim().length() == 0 || enterEmail.length() == 0) {
                    Toast.makeText(this, "Please provide a valid email id", Toast.LENGTH_SHORT).show();
                } else if (!isValid(enterEmail.getText().toString())) {
                    Toast.makeText(this, "Please provide a valid email id", Toast.LENGTH_SHORT).show();
                } else {
                    new MakeHttpCall("http://devs-services.000webhostapp.com/services/apis.php?sendTokenToEmail", this).execute();
                }
                break;
        }
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    EditText codeEdit;

    class MakeHttpCall extends AsyncTask<Void, Void, String> {

        String responseString = "";
        String STATUS = "";
        String url = "";

        WeakReference<MainActivity> mainActivity;

        private MakeHttpCall(String url, MainActivity activity) {
            this.url = url;
            mainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mainActivity.get());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            responseString = response;
                            try {
                                JSONObject jsonObject = new JSONObject(responseString);
                                if (jsonObject.getBoolean("status")) {
                                    final View verifyDialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pop_up, null);
                                    final android.app.AlertDialog verifyDialogLayout = new android.app.AlertDialog.Builder(MainActivity.this).create();
                                    verifyDialogLayout.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    verifyDialogLayout.setCancelable(false);
                                    verifyDialogLayout.setView(verifyDialog);
                                    Button verifyButton = verifyDialog.findViewById(R.id.verifyButton);
                                    codeEdit = verifyDialog.findViewById(R.id.codeEdit);
                                    verifyButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            verifyDialogLayout.cancel();
                                            new SubmitTheCode("http://devs-services.000webhostapp.com/services/apis.php?verifyUserToken", MainActivity.this).execute();
                                        }
                                    });
                                    verifyDialogLayout.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    responseString = error.getMessage();
                }

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", enterEmail.getText().toString());
                    return params;
                }
            };
            queue.add(stringRequest);
            return STATUS;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();

        }
    }


    class SubmitTheCode extends AsyncTask<Void, Void, String> {
        String responseString = "";
        String STATUS = "";
        String url = "";

        WeakReference<MainActivity> mainActivity;

        private SubmitTheCode(String url, MainActivity activity) {
            this.url = url;
            mainActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestQueue queue = Volley.newRequestQueue(mainActivity.get());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            responseString = response;
                            try {
                                JSONObject jsonObject = new JSONObject(responseString);

                                if (jsonObject.getBoolean("status")) {
                                    Toast.makeText(MainActivity.this, "Verification Successfull", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, ResultActivity.class).putExtra("email", enterEmail.getText().toString()));
                                }else{
                                    Toast.makeText(MainActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    responseString = error.getMessage();
                }

            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", enterEmail.getText().toString());
                    params.put("token", codeEdit.getText().toString());
                    return params;
                }
            };
            queue.add(stringRequest);
            return STATUS;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait...");
            dialog.show();

        }
    }
}
