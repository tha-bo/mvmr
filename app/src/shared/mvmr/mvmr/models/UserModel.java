/**
 * Created by PeerlessGate on 5/14/2017.
 */
package mvmr.mvmr.models;

public class UserModel {

    //public String UserName;
    public String Manufacterer;
    public String Model;
    public String Version;
    public String SocialMedia;

    public UserModel(String man,String model,String ros)
    {
        //this.UserName = us;
        this.Manufacterer = man;
        this.Model = model;
        this.Version = ros;
        this.SocialMedia = "";
    }
}
