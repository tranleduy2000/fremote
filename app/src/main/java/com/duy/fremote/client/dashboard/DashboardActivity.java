package com.duy.fremote.client.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.duy.fremote.LoginActivity;
import com.duy.fremote.R;
import com.duy.fremote.server.ServerActivity;
import com.duy.fremote.server.services.FRemoteService;
import com.duy.fremote.views.CustomViewPager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseUser mFirebaseUser;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private CustomViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        setTitle(R.string.title_activity_dashboard);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        initNavigationView();

        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(new DashboardPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());

        initBottomNavigationView();

        findViewById(R.id.btn_voice_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 27-Aug-18
            }
        });
    }

    private void initBottomNavigationView() {
        final BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_show_scenes:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.action_show_devices:
                        mViewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.action_show_scenes);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.action_show_devices);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        TextView txtName = headerView.findViewById(R.id.txt_name);
        String displayName = mFirebaseUser.getDisplayName();
        txtName.setText(displayName);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    private void initView() {
        mViewPager = findViewById(R.id.view_pager);
        DashboardPagerAdapter adapter = new DashboardPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sign_out:
                signOut();
                return true;
            case R.id.action_switch_server_mode:
                startActivity(new Intent(this, ServerActivity.class));
                finish();
                break;
        }
        return false;
    }

    private void signOut() {
        Intent intent = new Intent(this, FRemoteService.class);
        stopService(intent);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        GoogleSignInOptions gos = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gos);
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                overridePendingTransition(0, 0);
                startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                finish();
            }
        });

    }
}
