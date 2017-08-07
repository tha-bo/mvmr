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
    public boolean InformSchool;
    public boolean HasImage;

    public ReportModel(String user, String date, String description, String platformOccurred, String isVictim, boolean inform)
    {
        super();
        Date = date;
        Description = description;
        PlatformOccurred = platformOccurred;
        IsVictim = isVictim;
        User = user;
        InformSchool = inform;
        HasImage = false;
    }
}
