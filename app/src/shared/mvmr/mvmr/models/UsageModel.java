/**
 * Created by PeerlessGate on 5/14/2017.
 */
package mvmr.mvmr.models;

import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class UsageModel extends BaseModel {
    public TimeStamp Lit;
    public TimeStamp Unlit;
    public String User;

    public UsageModel(String user, String lit, String unlit)
    {
        super();
        Lit = null;
        Unlit = null;
        User = user;

        if(lit != null)
        {
            this.Lit = new TimeStamp(lit);
        }

        if(unlit != null)
        {
            this.Unlit = new TimeStamp(unlit);
        }
    }
}
