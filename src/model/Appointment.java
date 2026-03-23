package model;

public class Appointment {
    private String name, phone, date, time, doctor;
    private int queueNumber;

    public Appointment(String name, String phone, String date, String time, String doctor, int queueNumber) {
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.queueNumber = queueNumber;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getDoctor() { return doctor; }
    public int getQueueNumber() { return queueNumber; }
}