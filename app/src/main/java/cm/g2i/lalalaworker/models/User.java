package cm.g2i.lalalaworker.models;

import java.io.Serializable;

/**
 * Created by Sim'S on 21/07/2017.
 */
public class User implements Serializable{
    private int ID;
    private String name;
    private String passwd;

    public User() {
    }

    public User(int ID, String name, String passwd) {
        this.ID = ID;
        this.name = name;
        this.passwd = passwd;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
