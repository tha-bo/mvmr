package reporting.mvmr.models;

import com.google.firebase.database.ServerValue;

/**
 * Created by PeerlessGate on 5/23/2017.
 */

public class BaseModel {
    public Object CreatedAt;

    public BaseModel(){
        this.CreatedAt = ServerValue.TIMESTAMP;
    }
}
