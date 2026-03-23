package model;

import java.util.*;
import java.io.*;

public class AppointmentManager {

    private ArrayList<Appointment> list = new ArrayList<>();
    private String masterPhone = "911";

    public int getNextQueue(String date, String doctor) {
        int max = 0;
        for (Appointment a : list) {
            if (a.getDate().equals(date) && a.getDoctor().equals(doctor)) {
                if (a.getQueueNumber() > max) {
                    max = a.getQueueNumber();
                }
            }
        }
        return max + 1;
    }

    public boolean isDuplicatePerson(String name, String phone) {
        for (Appointment a : list) {
            if (a.getName().equalsIgnoreCase(name) &&
                    a.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public boolean add(Appointment a) {
        for (Appointment x : list) {
            if (x.getDate().equals(a.getDate()) &&
                    x.getTime().equals(a.getTime()) &&
                    x.getDoctor().equals(a.getDoctor())) {
                return false;
            }
        }
        list.add(a);
        return true;
    }

    public boolean remove(int index, String phone) {
        if (phone == null) return false;
        if (index < 0 || index >= list.size()) return false;

        Appointment a = list.get(index);

        if (phone.equals(a.getPhone()) || phone.equals(masterPhone)) {
            list.remove(index);
            return true;
        }
        return false;
    }

    public ArrayList<Appointment> search(String keyword) {
        ArrayList<Appointment> result = new ArrayList<>();
        for (Appointment a : list) {
            if (a.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                    a.getPhone().contains(keyword) ||
                    a.getDoctor().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(a);
            }
        }
        return result;
    }

    public List<String> getAvailableTimes(String date, String doctor) {
        String[] all = {"09:00", "10:00", "11:00", "13:00", "14:00", "15:00"};
        List<String> free = new ArrayList<>();

        for (String t : all) {
            boolean used = false;

            for (Appointment a : list) {
                if (a.getDate().equals(date) &&
                        a.getDoctor().equals(doctor) &&
                        a.getTime().equals(t)) {
                    used = true;
                    break;
                }
            }

            if (!used) free.add(t);
        }
        return free;
    }

    public ArrayList<Appointment> getAll() { return list; }

    public void saveToFile() {
        try {
            PrintWriter pw = new PrintWriter("data.txt");
            for (Appointment a : list) {
                pw.println(a.getName() + "," + a.getPhone() + "," + a.getDate() + "," +
                        a.getTime() + "," + a.getDoctor() + "," + a.getQueueNumber());
            }
            pw.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile() {
        try {
            File f = new File("data.txt");
            if (!f.exists()) return;

            Scanner sc = new Scanner(f);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                if(line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                list.add(new Appointment(d[0], d[1], d[2], d[3], d[4], Integer.parseInt(d[5])));
            }
            sc.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean update(int index, Appointment newA, String phone) {
        if (phone == null) return false;
        if (index < 0 || index >= list.size()) return false;

        Appointment old = list.get(index);

        if (!(phone.equals(old.getPhone()) || phone.equals(masterPhone))) {
            return false;
        }

        for (int i = 0; i < list.size(); i++) {
            if (i == index) continue;

            Appointment a = list.get(i);

            if (a.getDate().equals(newA.getDate()) &&
                    a.getTime().equals(newA.getTime()) &&
                    a.getDoctor().equals(newA.getDoctor())) {
                return false;
            }
        }

        list.set(index, newA);
        return true;
    }
}