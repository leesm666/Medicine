package kr.co.company.medicine;

public class Pill {
    private String pcode;
    private String pname;
    private String pcompany;

    public String getPcode(){
        return  pcode;
    }
    public String getPname(){
        return  pname;
    }
    public String getPcompany(){
        return  pcompany;
    }
    public void setPcode(String pcode){
        this.pcode = pcode;
    }
    public void setPname(String pname){
        this.pname = pname;
    }
    public void setPcompany(String pcompany){
        this.pcompany = pcompany;
    }

}
