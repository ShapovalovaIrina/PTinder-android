package com.trkpo.ptinder.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.navigation.NavigationView;
import com.trkpo.ptinder.R;
import com.trkpo.ptinder.config.PhotoTask;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.trkpo.ptinder.config.Constants.USER_ICON_URL;

public class NavigationActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_user_profile, R.id.nav_search, R.id.nav_favourite, R.id.nav_feed)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (signInAccount != null) setUserInfo(navigationView.getHeaderView(0),signInAccount);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        final Activity activity = this;
        menu.findItem(R.id.notification).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                if (signInAccount != null) menuItemClick(activity, signInAccount);
                return false;
            }
        });
        return true;
    }

    public void menuItemClick(Activity activity, @NotNull GoogleSignInAccount signInAccount) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("googleId", signInAccount.getId());
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(R.id.nav_notifications, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void setUserInfo(View view, GoogleSignInAccount signInAccount) {
        if (signInAccount != null) {
            TextView username = view.findViewById(R.id.username_nav_header);
            CircleImageView user_icon = view.findViewById(R.id.user_icon_nav_header);
            if (signInAccount.getPhotoUrl() != null) {
                try {
                    USER_ICON_URL = signInAccount.getPhotoUrl().toString();
                    user_icon.setImageBitmap(new PhotoTask().execute(signInAccount.getPhotoUrl().toString()).get());
                } catch (ExecutionException | InterruptedException e) {
                    Log.e("BITMAP", "Got error during bitmap parsing" + e.toString());
                }
            }
            username.setText(signInAccount.getDisplayName());
        }
    }
}