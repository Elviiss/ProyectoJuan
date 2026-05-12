package org.example.proyectojuan;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

public class FirestoreConnection {
    static Firestore db;
    static HashMap<String, Object> dataList;
    private static volatile FirestoreConnection INSTANCE;

    private FirestoreConnection() throws IOException {
        if(FirebaseApp.getApps().isEmpty()){
            try (FileInputStream in = new FileInputStream("proyecto-juan-8d7c7-firebase-adminsdk-fbsvc-94e91865f8.json")){
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();
                FirebaseApp.initializeApp(options);
            }
        }
        db = FirestoreClient.getFirestore();
        dataList = new java.util.HashMap<String, Object>();
    }

    public static FirestoreConnection getInstance() {
        if (INSTANCE == null){
            synchronized (FirestoreConnection.class){
                if(INSTANCE == null) {
                    try { INSTANCE = new FirestoreConnection(); }
                    catch (IOException e) { throw new RuntimeException(e);}
                }
            }
        }
        return INSTANCE;
    }

    public Firestore db() { return db; }

    public HashMap<String, Object> getDataList() { return dataList; }


    public void registrarActividad(Auditoria a) throws java.util.concurrent.ExecutionException, InterruptedException {
        db.collection("consultoria").document()
                .set(new java.util.HashMap<String, Object>() {{
                    put("accion", a.getAccion());
                    put("usuario", a.getUsuario());
                    put("fecha", com.google.cloud.Timestamp.now().toString());
                }}).get();

        var query = db.collection("consultoria").get().get();
        dataList.clear();
        query.forEach(d -> dataList.put(d.getId(), d.getData()));
    }
}

