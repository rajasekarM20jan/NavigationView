package com.example.navigationview;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ConstraintLayout drawer;
    private ImageView profilePicture;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        drawer = findViewById(R.id.drawer);
        profilePicture = findViewById(R.id.imageView);
        navigationView=findViewById(R.id.menuNavigation);


        // Set up ImageView click listener to open the navigation drawer
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        /*// Programmatically add menu items and handle clicks
        Menu navMenu = navigationView.getMenu();
        navMenu.clear(); // Clear existing menu items

        // Add menu items programmatically
        navMenu.add(Menu.NONE, R.id.about, Menu.NONE, "About");
        navMenu.add(Menu.NONE, R.id.terms, Menu.NONE, "Terms And Conditions");

        // Add a submenu item
        Menu subMenu=navMenu.addSubMenu("YOUR ACTIVITY");
        subMenu.add(Menu.NONE, R.id.profile, Menu.NONE, "Profile");
        subMenu.add(Menu.NONE, R.id.logout, Menu.NONE, "Logout");*/



        navigationView.bringToFront();
        // Set up ActionBarDrawerToggle for the navigation drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String postUrl = "https://ifvr-api-management-dev.azure-api.net/api/v1/IFVR/MobileApp/SSLPinningTesting";
                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                JSONObject Details = new JSONObject();
                String insertString = Details.toString();
                RequestBody body = RequestBody.create(JSON, insertString);
                Request request = new Request.Builder()
                        .url(postUrl)
                        .post(body)
                        .build();


                OkHttpClient client = generateSecureOkHttpClient(MainActivity.this);
                Response staticResponse = null;
                try {
                    staticResponse = client.newCall(request).execute();
                    String staticRes = staticResponse.body().string();

                    System.out.println("response: " + staticRes);


                    if (staticResponse.code() == 200) {

                        final JSONObject staticJsonObj = new JSONObject(staticRes);
                        System.out.println(staticJsonObj);

                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();


    }


    public static OkHttpClient generateSecureOkHttpClient(Context context) {
        try {
            // Create a simple builder for our http client, this is only for example purposes
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.readTimeout(180, TimeUnit.SECONDS);
            httpClientBuilder.connectTimeout(180, TimeUnit.SECONDS);

            // Here you may wanna add some headers or custom setting for your builder

            // Get the file of our certificate
            InputStream caFileInputStream = context.getResources().openRawResource(R.raw.starhealth);

            // We're going to put our certificates in a Keystore
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(caFileInputStream, "WeLove9SWA".toCharArray());

            // Create a KeyManagerFactory with our specific algorithm our our public keys
            // Most of the cases is gonna be "X509"
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
            keyManagerFactory.init(keyStore, "WeLove9SWA".toCharArray());

            // Create a SSL context with the key managers of the KeyManagerFactory
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());

           /* TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);*/
            //Finally set the sslSocketFactory to our builder and build it
            return httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory()).build();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            finishAffinity();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId= item.getItemId();
        if(itemId==R.id.about){
            Toast.makeText(this, "About is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(itemId==R.id.terms){
            Toast.makeText(this, "Terms and Conditions is clicked", Toast.LENGTH_SHORT).show();
        }
        else if(itemId==R.id.version){
            Toast.makeText(this, "Version is clicked", Toast.LENGTH_SHORT).show();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}