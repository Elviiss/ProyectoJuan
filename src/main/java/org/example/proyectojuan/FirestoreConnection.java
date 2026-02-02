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
            try (FileInputStream in = new FileInputStream("proyecto-juan-8d7c7-firebase-adminsdk-fbsvc-dfd7bc9ec9.json")){
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

    //todo Con este método agregas auditorías, cambiá el objeto Album por auditorias
//    public void addAlbum (Album a) throws ExecutionException, InterruptedException {
//        db.collection("albums").document(a.getNombre())
//                .set(new java.util.HashMap<String, String>() { {
//                    put("Nombre: ", a.getNombre());
//                    put("Artista: ", a.getArtista());
//                }}).get();
//
//        var query = db.collection("albums").get().get();
//        query.forEach(d ->dataList.put(d.getId(),d.getData()));
//    }

    //todo Ejemplo para leer auditorias
//    public void readAlbums() throws ExecutionException, InterruptedException {
//        var query = db.collection("albums").get().get();
//        query.forEach(r -> System.out.println(r.getId()+", "+r.getData()));
//    }
}

