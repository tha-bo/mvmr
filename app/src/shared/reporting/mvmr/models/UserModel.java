/**
 * Created by PeerlessGate on 5/14/2017.
 */
package reporting.mvmr.models;

public class UserModel extends BaseModel {

    //public String UserName;
    public String Manufacterer;
    public String Model;
    public String Version;
    public String SocialMedia;

    public UserModel(String man,String model,String ros)
    {
        super();
        this.Manufacterer = man;
        this.Model = model;
        this.Version = ros;
        this.SocialMedia = "";
    }
}
