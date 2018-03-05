/**
 * Created by PeerlessGate on 5/14/2017.
 */
package reporting.mvmr.models;

public class SurveyModel extends BaseModel{

    public String Result;
    public String User;
    public String UserTimeStamp;
    public String School;
    public String Grade;
    public String Race;
    public String Sex;
    public String Age;
    public String CandidateId;

    public SurveyModel(String user, String utime, String result)
    {
        super();
        User = user;
        UserTimeStamp = utime;
        Result = result;
    }
}
