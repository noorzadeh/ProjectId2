public class User {
    public long chatid;
    public int status;
    public int errortimes;
    public float height;
    public float weight;
    public User(long ci){
        chatid = ci;
        errortimes=0;
        status =0;
    }
    public void update (int s){
        status =s;
    }
}
