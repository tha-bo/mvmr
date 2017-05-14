/**
 * Created by PeerlessGate on 5/14/2017.
 */
package mvmr.mvmr.models;

public class UsageModel {
    public String Lit;
    public String Unlit;
    public String User;

    public UsageModel(String user, String lit, String unlit)
    {
        Lit = lit;
        Unlit = unlit;
        User = user;
    }
}
