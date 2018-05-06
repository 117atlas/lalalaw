package cm.g2i.lalalaworker.models;

import java.io.Serializable;

/**
 * Created by Sim'S on 06/07/2017.
 */

public class Worker implements Serializable {
    private int ID;
    private String name;
    private String nationality;
    private String photo;
    private String phoneNumber;
    private String quartier;
    private String ville;
    private String passwd;

    private double note;
    private int nbr_sollicitations;

    private String localPhoto;

    public String getlocalPhoto() {
        return localPhoto;
    }

    public void setLocalPhoto(String localPhoto) {
        this.localPhoto = localPhoto;
    }

    public Worker() {
    }

    public Worker(int ID, String name, String nationality, String photo, String phoneNumber, String quartier, String ville) {
        this.ID = ID;
        this.name = name;
        this.nationality = nationality;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.quartier = quartier;
        this.ville = ville;
    }

    public Worker(int ID, String name, String nationality, String photo, String phoneNumber, String quartier, String ville, String passwd) {
        this.ID = ID;
        this.name = name;
        this.nationality = nationality;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.quartier = quartier;
        this.ville = ville;
        this.passwd = passwd;
    }

    public Worker(int ID, String name, String nationality, String photo, String phoneNumber, String quartier, String ville, String passwd,
                  double note, int nbr_sollicitations) {
        this.ID = ID;
        this.name = name;
        this.nationality = nationality;
        this.photo = photo;
        this.phoneNumber = phoneNumber;
        this.quartier = quartier;
        this.ville = ville;
        this.passwd = passwd;

        this.note = note;
        this.nbr_sollicitations = nbr_sollicitations;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public int getNbr_sollicitations() {
        return nbr_sollicitations;
    }

    public void setNbr_sollicitations(int nbr_sollicitations) {
        this.nbr_sollicitations = nbr_sollicitations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public int getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "Worker{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", photo='" + photo + '\'' +
                ", localPhoto='" + localPhoto + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", quartier='" + quartier + '\'' +
                ", ville='" + ville + '\'' +
                ", passwd='" + passwd + '\'' +
                ", note=" + note +
                ", nbr_sollicitations=" + nbr_sollicitations +
                '}';
    }
}
