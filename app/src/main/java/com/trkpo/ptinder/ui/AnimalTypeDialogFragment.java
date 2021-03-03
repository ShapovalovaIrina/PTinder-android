package com.trkpo.ptinder.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.HTTP.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import static com.trkpo.ptinder.config.Constants.PETS_PATH;

public class AnimalTypeDialogFragment {

    public static void showOpenDialog(final Context context, final ArrayAdapter adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.alert_layout, null);
        builder.setView(view);
        final TextView typeName = view.findViewById(R.id.add_new_type);

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!Connection.hasConnection(context)) {
                    Toast.makeText(context, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                    return;
                }
                adapter.clear();
                JSONObject requestObject = new JSONObject();
                try {
                    requestObject.put("type", "" + typeName.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final String requestBody = requestObject.toString();
                Log.i("REGISTRATION", "Going to register new animal type with request: " + requestBody);

                if (context != null) {
                    RequestQueue queue = Volley.newRequestQueue(context);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, PETS_PATH + "/types", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("VOLLEY", response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", error.toString());
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            try {
                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                            } catch (UnsupportedEncodingException uee) {
                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                return null;
                            }
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String responseString = "";
                            if (response != null) {
                                responseString = String.valueOf(response.statusCode);
                            }
                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };

                    StringRequest stringGetRequest = new StringRequest(Request.Method.GET, PETS_PATH + "/types",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        adapter.addAll(getTypesFromJSON(response));
                                    } catch (JSONException e) {
                                        Log.e("VOLLEY", "Making get request (load pets): json error - " + e.toString());
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("VOLLEY", "Making get request (load pets): request error - " + error.toString());
                        }
                    });

                    queue.add(stringRequest);
                    queue.add(stringGetRequest);
                }
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static Collection<String> getTypesFromJSON(String response) throws JSONException {
        Collection<String> types = new ArrayList<>();
        JSONArray jArray = new JSONArray(response);
        for (int i = 0; i < jArray.length(); i++) {
            JSONObject jsonObject = jArray.getJSONObject(i);
            String name = jsonObject.getString("type");
            types.add(name);
        }
        Log.i("TEST_INFO", "Got types in func " + types);

        return types;
    }
}
