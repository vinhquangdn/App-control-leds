package luandn.dt.ck;

public class RoomData {
    private int pic;
    private String name;
    private String IP;

    public RoomData(int pic, String name, String IP) {
        this.pic = pic;
        this.name = name;
        this.IP = IP;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }
}
