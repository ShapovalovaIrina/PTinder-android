package com.trkpo.ptinder.adapter;

import android.content.Context;
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
import com.trkpo.ptinder.HTTP.DeleteRequest;
import com.trkpo.ptinder.HTTP.PostRequest;
import com.trkpo.ptinder.HTTP.PostRequestParams;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.pojo.Notification;
import com.trkpo.ptinder.HTTP.Connection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.trkpo.ptinder.config.Constants.FAVOURITE_PATH;
import static com.trkpo.ptinder.config.Constants.NOTIFICATIONS_PATH;
import static com.trkpo.ptinder.config.Constants.CONTACT_PATH;

public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.ViewHolder> {
    private List<Notification> notifications = new ArrayList<>();

    /* Variable for testing*/
    private String optUrl;
    private boolean connectionPermission;

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
                        responseUserContacts(v.getContext(), notificationInfo);
                    } else {
                        markAsRead(v.getContext(), notificationInfo);
                        btnsLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        public void bind(Notification notificationInfo) {
            this.notificationInfo = notificationInfo;
            if (notificationInfo.isRead()) {
                this.btnsLayout.setVisibility(View.INVISIBLE);
            }
            this.text.setText(formTitle(notificationInfo));
            if (notificationInfo.getTitle().equals("CONTACT_INFO_REQUEST")) {
                this.denyBtn.setVisibility(View.VISIBLE);
                this.acceptBtn.setText("Принять");
            }
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

    public void markAsRead(Context context, Notification notificationInfo) {
        if (!Connection.hasConnection(context) || !connectionPermission) {
            Toast.makeText(context, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }

        String url = optUrl == null ? NOTIFICATIONS_PATH + "/" + notificationInfo.getId() : optUrl;
        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new PostRequest().execute(new PostRequestParams(url, "")).get();
            Log.d("VOLLEY", "Success response (read notification). " +
                    "Notification id: " + notificationInfo.getId());
            deleteNotificationById(notificationInfo.getId());
        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.d("VOLLEY", "Not Success response (read notification) " + error.toString());
        }
    }

    public void responseUserContacts(Context context, Notification notificationInfo) {
        if (!Connection.hasConnection(context) || !connectionPermission) {
            Toast.makeText(context, "Отсутствует подключение к интернету. Невозможно обновить страницу.", Toast.LENGTH_LONG).show();
            return;
        }
        String url = CONTACT_PATH + "/response/" +
                notificationInfo.getAddresseeGoogleId() + "/" +
                notificationInfo.getAddresseeFromGoogleId() + "/" +
                notificationInfo.getId();
        url = optUrl == null ? url : optUrl;

        if (optUrl != null) resetOptUrlAndConnectionPermission();
        try {
            String response = new PostRequest().execute(new PostRequestParams(url, "")).get();
            Log.d("VOLLEY", "Success response (response user info) from " +
                    notificationInfo.getAddresseeGoogleId() + " to " + notificationInfo.getAddresseeFromGoogleId());
            deleteNotificationById(notificationInfo.getId());
        } catch (ExecutionException | InterruptedException | IllegalStateException error) {
            Log.e("VOLLEY", "Not Success response (response user info): " + error.toString());
        }
    }

    public String formTitle(Notification notificationInfo) {
        switch (notificationInfo.getTitle()) {
            case "CONTACT_INFO_REQUEST":
                return "Новый запрос доступа к контактной информации";
            case "CONTACT_INFO_ANSWER":
                return "Ответ на Ваш запрос доступа к контактной информации";
            case "NEW_PET":
                return "Новые питомцы у пользователей, на которых вы подписаны";
            case "EDIT_PET":
                return "Изменения в анкетах питомцев пользователей, на которых вы подписаны";
            case "EDIT_FAVOURITE":
                return "Изменения в анкетах Ваших избранных питомцев";
        }
        return notificationInfo.getTitle();
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

    public void setOptUrlAndConnectionPermission(String optUrl, boolean connectionPermission) {
        this.optUrl = optUrl;
        this.connectionPermission = connectionPermission;
    }

    public void resetOptUrlAndConnectionPermission() {
        optUrl = null;
        connectionPermission = true;
    }
}
