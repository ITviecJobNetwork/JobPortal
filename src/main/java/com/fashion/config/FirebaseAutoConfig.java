package com.fashion.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class FirebaseAutoConfig implements AutoConfig {

    private Properties properties;
    private Map<String, Object> beanContainer;

    @Override
    public void init(Map<String, Object> beanContainer, Properties properties) {
        this.beanContainer = beanContainer;
        this.properties = properties;
        try {
            this.createFirebaseAppBean();
            FirebaseApp firebaseApp = (FirebaseApp) this.beanContainer.get(FirebaseApp.class.getName());
            this.createBucketBean(firebaseApp);
        }catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void createFirebaseAppBean() throws IOException {
        String firebaseConfigLocation = properties.getProperty("firebase.config.location");
        InputStream inputStream = FirebaseAutoConfig.class.getResourceAsStream("/" + firebaseConfigLocation);
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(inputStream))
                .build();
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
        beanContainer.put(FirebaseApp.class.getName(), firebaseApp);
    }

    private void createBucketBean(FirebaseApp app) {
        Bucket bucket = StorageClient
                .getInstance(app)
                .bucket(this.properties.getProperty("firebase.bucket"));
        this.beanContainer.put(Bucket.class.getName(), bucket);
    }
}