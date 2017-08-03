package mvmr.mvmr;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by PeerlessGate on 8/3/2017.
 */

public class FirebaseRepository {
    private static FirebaseDatabase mDatabase;

    public static FirebaseDatabase getDatabaseInstance() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(true);
        }
        return mDatabase;
    }

}
