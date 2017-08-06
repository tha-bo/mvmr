package mvmr.mvmr.models;

/**
 * Created by PeerlessGate on 8/6/2017.
 */

public class UsageItem extends BaseModel {
    public String Lit;
    public long LitStamp;
    public String UnLit;
    public long UnLitStamp;

    public UsageItem(String lit, long litStamp, String unlit, long unlitStamp)
    {
        super();
        Lit = lit;
        UnLit = unlit;
        LitStamp = litStamp;
        UnLitStamp = unlitStamp;
    }
}
