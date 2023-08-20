package kr.co.company.medicine;


public class ListItem {
    int mediId;
    String mediList;
    String mediName;
    String startDate;
    String endDate;
    int timesPerDay;
    int mon, tue, wed, thu, fri, sat, sun;
    String oneTime, twoTime, threeTime;

    public ListItem() {
    }

    public ListItem(String mediList, String mediName, String startDate, String endDate, int timesPerDay) {
        this.mediList = mediList;
        this.mediName = mediName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.timesPerDay = timesPerDay;
    }

    public int getMediId() {
        return mediId;
    }

    public void setMediId(int mediId) {
        this.mediId = mediId;
    }

    public String getMediList() {
        return mediList;
    }

    public void setMediList(String mediList) {
        this.mediList = mediList;
    }

    public String getMediName() {
        return mediName;
    }

    public void setMediName(String mediName) {
        this.mediName = mediName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = timesPerDay;
    }

    public int getMon() {
        return mon;
    }

    public void setMon(int mon) {
        this.mon = mon;
    }

    public int getTue() {
        return tue;
    }

    public void setTue(int tue) {
        this.tue = tue;
    }

    public int getWed() {
        return wed;
    }

    public void setWed(int wed) {
        this.wed = wed;
    }

    public int getThu() {
        return thu;
    }

    public void setThu(int thu) {
        this.thu = thu;
    }

    public int getFri() {
        return fri;
    }

    public void setFri(int fri) {
        this.fri = fri;
    }

    public int getSat() {
        return sat;
    }

    public void setSat(int sat) {
        this.sat = sat;
    }

    public int getSun() {
        return sun;
    }

    public void setSun(int sun) {
        this.sun = sun;
    }

    public String getOneTime() {
        return oneTime;
    }

    public void setOneTime(String oneTime) {
        this.oneTime = oneTime;
    }

    public String getTwoTime() {
        return twoTime;
    }

    public void setTwoTime(String twoTime) {
        this.twoTime = twoTime;
    }

    public String getThreeTime() {
        return threeTime;
    }

    public void setThreeTime(String threeTime) {
        this.threeTime = threeTime;
    }

}
