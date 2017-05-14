/**
 * Created by PeerlessGate on 5/14/2017.
 */
package mvmr.mvmr.models;

public class UserModel {

    //public String UserName;
    public String Platform;
    public boolean HasSocialMedia;

    public UserModel(String pl)
    {
        //this.UserName = us;
        this.Platform = pl;
        this.HasSocialMedia = false;
    }
}
