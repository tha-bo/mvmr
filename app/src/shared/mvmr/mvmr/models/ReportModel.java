package mvmr.mvmr.models;

/**
 * Created by PeerlessGate on 5/24/2017.
 */

public class ReportModel extends BaseModel{

    //public String UserName;
    public String Date;
    public String Description;
    public String PlatformOccurred;
    public String IsVictim;
    public String User;

    public ReportModel(String user, String date, String description, String platformOccurred, String isVictim)
    {
        super();
        Date = date;
        Description = description;
        PlatformOccurred = platformOccurred;
        IsVictim = isVictim;
        User = user;
    }
}
