package luandn.dt.ck;

public class DeviceData {
    //tao cac bien, ham khoi tao cac phuong thuc get set cho bien
    private int pic;
    private String name;
    private String order;
    private int seekBarValue;
    private boolean switchChecked;

    public DeviceData(int pic, String name, String order, int seekBarValue, boolean switchChecked) {
        this.pic = pic;
        this.name = name;
        this.order = order;
        this.seekBarValue = seekBarValue;
        this.switchChecked = switchChecked;
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getSeekBarValue() {
        return seekBarValue;
    }

    public void setSeekBarValue(int seekBarValue) {
        this.seekBarValue = seekBarValue;
    }

    public boolean isSwitchChecked() {
        return switchChecked;
    }

    public void setSwitchChecked(boolean switchValue) {
        this.switchChecked = switchValue;
    }
}

