package Model;

import java.util.Date;

public class ServiceRegistration {

    private String idService;
    private String idUser;
    private SimpleDate date;
    private int purpose;

    public static final int SERVICE_CREATED = 0;
    public static final int SERVICE_UPDATED = 1;
    public static final int SERVICE_MODIFIED = 2;
    public static final int SERVICE_REMOVED = 3;
    public static final int OTHER = 4;

    public ServiceRegistration() {
        this.idService = "";
        this.idUser = "";
        this.date = new SimpleDate();
        this.purpose = -1;
    }

    public ServiceRegistration(String idService, String idUser, SimpleDate date, int purpose) {
        this.idService = idService;
        this.idUser = idUser;
        this.date = date;
        this.purpose = purpose;
    }

    public String getIdService() {
        return idService;
    }

    public void setIdService(String idService) {
        this.idService = idService;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public SimpleDate getDate() {
        return date;
    }

    public void setDate(SimpleDate date) {
        this.date = date;
    }

    public int getPurpose() {
        return purpose;
    }

    public void setPurpose(int purpose) {
        this.purpose = purpose;
    }
}
