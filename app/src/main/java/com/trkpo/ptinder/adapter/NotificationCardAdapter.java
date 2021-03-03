package com.trkpo.ptinder.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Notification;
import com.trkpo.ptinder.HTTP.Connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.trkpo.ptinder.config.Constants.NOTIFICATIONS_PATH;
import static com.trkpo.ptinder.config.Constants.CONTACT_PATH;

public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.ViewHolder> {
    private List<Notification> notifications = new ArrayList<>();

    class ViewHolder extends RecyclerView.ViewHolder {
        private Notification notificationInfo;
        private TextView title;
        private TextView text;
        private Button denyBtn;
        private Button acceptBtn;
        private LinearLayout btnsLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.notification_title);
            this.text = itemView.findViewById(R.id.notification_text);
            this.denyBtn = itemView.findViewById(R.id.deny_btn);
            this.acceptBtn = itemView.findViewById(R.id.accept_btn);
            this.btnsLayout = itemView.findViewById(R.id.btns_layout);
            this.denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // ToDo: deny contact info request
                }
            });
            this.acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notificationInfo.getTitle().equals("CONTACT_INFO_REQUEST")) {
                        responseUserContacts(v);
                    } else {
                        markAsRead(v);
                    }
                    deleteNotificationById(notificationInfo.getId());
                }
            });
        }

        public void bind(Notification notificationInfo) {
            this.notificationInfo = notificationInfo;
            if (notificationInfo.isRead()) {
                this.btnsLayout.setVisibility(View.INVISIBLE);
            }
            switch (notificationInfo.getTitle()) {
                case "CONTACT_INFO_REQUEST":
                    this.title.setText("Новый запрос доступа к контактной информации");
                    this.denyBtn.setVisibility(View.VISIBLE);
                    this.acceptBtn.setText("Принять");
                    break;
                case "CONTACT_INFO_ANSWER":
                    this.title.setText("Ответ на Ваш запрос доступа к контактной информации");
                    break;
                case "NEW_PET":
                    this.title.setText("Новые питомцы у пользователей, на которых вы подписаны");
                    break;
                case "EDIT_PET":
                    this.title.setText("Изменения в анкетах питомцев пользователей, на которых вы подписаны");
                    break;
                case "EDIT_FAVOURITE":
                    this.title.setText("Изменения в анкетах Ваших избранных питомцев");
            }
            this.text.setText(notificationInfo.getText());
        }

        private void markAsRead(View view) {
            if (!Connection.hasConnection(view.getContext())) {
                Toast.makeText(view.getContext(), "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = NOTIFICATIONS_PATH + "/" + notificationInfo.getId();

            StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("VOLLEY", "Success response (read notification). " +
                                    "Notification id: " + notificationInfo.getId());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("VOLLEY", "Not Success response (read notification) " + error.toString());
                }
            });
            queue.add(jsonObjectRequest);
            btnsLayout.setVisibility(View.INVISIBLE);
        }

        private void responseUserContacts(View view) {
            if (!Connection.hasConnection(view.getContext())) {
                Toast.makeText(view.getContext(), "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
                return;
            }
            RequestQueue queue = Volley.newRequestQueue(view.getContext());
            String url = CONTACT_PATH + "/response/" +
                    notificationInfo.getAddresseeGoogleId() + "/" +
                    notificationInfo.getAddresseeFromGoogleId() + "/" +
                    notificationInfo.getId();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("VOLLEY", "Success response (response user info) from " +
                                    notificationInfo.getAddresseeGoogleId() + " to " + notificationInfo.getAddresseeFromGoogleId());
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", "Not Success response (response user info): " + error.toString());
                }
            });
            queue.add(stringRequest);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void setItems(Collection<Notification> notifications) {
        Collections.sort((List<Notification>) notifications, new Comparator<Notification>() {
            @Override
            public int compare(Notification o1, Notification o2) {
                return Boolean.compare(o1.isRead(), o2.isRead());
            }
        });
        this.notifications.clear();
        this.notifications.addAll(notifications);
        notifyDataSetChanged();
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void clearItems() {
        notifications.clear();
        notifyDataSetChanged();
    }

    public void deleteNotificationById(String notificationId) {
        int position = -1;
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getId().equals(notificationId)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            notifications.remove(position);
            notifyDataSetChanged();
        }
    }
}
