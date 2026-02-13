package pio.daw;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library implements Controlable {
    private Map<String,User> users;

    /**
     * Read the library register file (.txt) and create a library object
     * with the current status of the users.
     * @param path Library registry file path.
     * @return Library object.
     */
    public static Library fromFile(Path path){
        Library library = new Library();
        try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(path)){
            lines.forEach(line -> {
                if (line == null) return;
                String l = line.trim();
                if (l.isEmpty()) return;
                String[] parts = l.split(";", 2);
                if (parts.length < 2) return;
                String id = parts[0].trim();
                String ev = parts[1].trim();
                EventType e = null;
                if (ev.equalsIgnoreCase("ENTRADA")) e = EventType.ENTRY;
                else if (ev.equalsIgnoreCase("SALIDA")) e = EventType.EXIT;
                if (e != null) {
                    library.registerChange(id, e);
                }
            });
        } catch (Exception ex){
            System.err.println("Error reading file: " + ex.getMessage());
            System.exit(1);
        }
        return library;
    }

    private Library(){
        this.users = new HashMap<>();
    }

    @Override
    public void registerChange(String id, EventType e){
        if (id == null || id.isEmpty() || e == null) return;
        User u = this.users.get(id);
        if (u == null){
            u = new User(id);
            this.users.put(id, u);
        }
        u.registerNewEvent(e);
    }

    @Override
    public List<User> getCurrentInside(){
        List<User> res = new ArrayList<>();
        for (User u : this.users.values()){
            if (u.isInside()) res.add(u);
        }
        Collections.sort(res, (a,b) -> a.getId().compareTo(b.getId()));
        return res;
    }

    @Override
    public List<User> getMaxEntryUsers(){
        List<User> res = new ArrayList<>();
        int max = 0;
        for (User u : this.users.values()){
            int e = u.getNEntries();
            if (e > max){
                max = e;
            }
        }
        if (max == 0) return res;
        for (User u : this.users.values()){
            if (u.getNEntries() == max) res.add(u);
        }
        Collections.sort(res, (a,b) -> a.getId().compareTo(b.getId()));
        return res;
    }

    @Override
    public List<User> getUserList(){
        List<User> res = new ArrayList<>();
        for (User u : this.users.values()){
            if (u.getNEntries() > 0) res.add(u);
        }
        Collections.sort(res, (a,b) -> a.getId().compareTo(b.getId()));
        return res;
    }

    @Override
    public void printResume(){
        StringBuilder sb = new StringBuilder();
        sb.append("Usuarios actualmente dentro de la biblioteca:\n");
        for (User u : getCurrentInside()){
            sb.append(u.getId()).append("\n");
        }

        sb.append("\n");
        sb.append("Número de entradas por usuario:\n");
        for (User u : getUserList()){
            sb.append(u.getId()).append(" -> ").append(u.getNEntries()).append("\n");
        }

        sb.append("\n");
        sb.append("Usuario(s) con más entradas:\n");
        for (User u : getMaxEntryUsers()){
            sb.append(u.getId()).append("\n");
        }

        System.out.print(sb.toString().trim());
    }
}
