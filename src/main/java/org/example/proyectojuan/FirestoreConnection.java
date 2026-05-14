package org.example.proyectojuan;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.IOException;
import java.io.InputStream;

public class FirestoreConnection {
    private static Firestore db;
    private static volatile FirestoreConnection INSTANCE;

    private FirestoreConnection() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream in = getClass().getResourceAsStream("/serviceAccountKey.json");

                if (in == null) {
                    in = Thread.currentThread().getContextClassLoader().getResourceAsStream("serviceAccountKey.json");
                }

                if (in == null) {
                    throw new IOException("❌ ERROR: No se encontró serviceAccountKey.json en src/main/resources");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(in))
                        .build();

                FirebaseApp.initializeApp(options);
                System.out.println("🔥 Firebase inicializado correctamente.");
            }
            db = FirestoreClient.getFirestore();
        } catch (IOException e) {
            System.err.println("❌ Error al iniciar Firebase: " + e.getMessage());
        }
    }

    public static FirestoreConnection getInstance() {
        if (INSTANCE == null) {
            synchronized (FirestoreConnection.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FirestoreConnection();
                }
            }
        }
        return INSTANCE;
    }

    public Firestore db() { return db; }

    /**
     * Registra la actividad real en la colección 'consultoria'
     * @param log Objeto Auditoria con la acción, usuario y timestamp reales.
     */
    public void registrarActividad(Auditoria log) {
        try {
            // Usamos la instancia de la base de datos
            Firestore database = FirestoreConnection.getInstance().db();

            // Enviamos el objeto 'log' directamente.
            // Firebase lo convertirá automáticamente usando los getters de la clase Auditoria.
            database.collection("consultoria").add(log).get();

            System.out.println("✅ Actividad registrada en el historial: " + log.getAccion());
        } catch (Exception e) {
            System.err.println("❌ ERROR AL REGISTRAR ACTIVIDAD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}