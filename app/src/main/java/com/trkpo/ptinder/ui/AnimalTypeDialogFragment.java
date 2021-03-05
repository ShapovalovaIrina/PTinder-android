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

import com.trkpo.ptinder.HTTP.Connection;
import com.trkpo.ptinder.HTTP.GetRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

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
                saveType(context, adapter, (String) typeName.getText());
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

    public static void saveType(final Context context, final ArrayAdapter adapter, String newType, String ... optUrl) {
        boolean connectionPermission = optUrl.length != 2 || Boolean.parseBoolean(optUrl[1]);
        if (!Connection.hasConnection(context) | !connectionPermission) {
            Toast.makeText(context, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl.length == 0 ? PETS_PATH + "/types" : optUrl[0];
        adapter.clear();
        View view = LayoutInflater.from(context).inflate(R.layout.alert_layout, null);

        JSONObject requestObject = new JSONObject();
        try {
            requestObject.put("type", newType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = requestObject.toString();
        Log.i("REGISTRATION", "Going to register new animal type with request: " + requestBody);

        if (context != null) {
            try {
                String response = new PostRequest().execute(new PostRequestParams(url, requestBody)).get();
                if (!response.equals("")) {
                    Log.i("VOLLEY", response);
                }
            } catch (ExecutionException | InterruptedException error) {
                Log.e("VOLLEY", "Making post request (save type): request error - " + error.toString());
                Toast.makeText(context, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }

            try {
                String response = new GetRequest().execute(url).get();
                adapter.addAll(getTypesFromJSON(response));
            } catch (ExecutionException | InterruptedException | JSONException error) {
                Log.e("VOLLEY", "Making get request (load type): request error - " + error.toString());
                Toast.makeText(context, "Request error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Collection<String> getTypesFromJSON(String response) throws JSONException {
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
