package reporting.mvmr.models;

/**
 * Created by ThaboN on 04/03/2018.
 */

public class RatingsModel extends BaseModel{

    //public String UserName;
    public String Interface;
    public String Accessability;
    public String About;
    public String HowTo;
    public String Report;
    public String General;
    public String Ratings;
    public String User;
    public String Statistics;

    public RatingsModel()
    {
        super();
    }

    public RatingsModel(String user,String i, String acc, String ab, String h, String r,String g, String ra, String stats)
    {
        super();
        User = user;
        Interface = i;
        Accessability = acc;
        About = ab;
        HowTo = h;
        Report = r;
        General = g;
        Ratings = ra;
        Statistics = stats;
    }
}
