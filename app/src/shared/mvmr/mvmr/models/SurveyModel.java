/**
 * Created by PeerlessGate on 5/14/2017.
 */
package mvmr.mvmr.models;

public class SurveyModel extends BaseModel{

    //public String UserName;
    public String Result;
    public String User;
    public String UserTimeStamp;

    public SurveyModel(String user, String utime)
    {
        super();
        User = user;
        UserTimeStamp = utime;
    }
}
