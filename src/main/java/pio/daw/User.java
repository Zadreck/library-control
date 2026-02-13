package pio.daw;

public class User implements Localizable {
    private String id;
    private EventType lasEvent = null;
    private Boolean inside = false;
    private int entries = 0;

    public User(String id){
        this.id = id;
    }

    public String getId(){
        return this.id;
    }

    public Boolean isInside(){
        return this.inside;
    }

    public int getEntries(){
        return this.entries;
    }

    /**
     * Process an event for this user. Follows rules:
     * - ENTRY when already inside -> ignored
     * - ENTRY when outside -> becomes inside and entries++
     * - EXIT when outside -> ignored
     * - EXIT when inside -> becomes outside
     */
    public void registerNewEvent(EventType e){
        if (e == null) return;
        if (e == EventType.ENTRY){
            if (!this.inside){
                this.inside = true;
                this.lasEvent = e;
                this.entries++;
            }
        } else if (e == EventType.EXIT){
            if (this.inside){
                this.inside = false;
                this.lasEvent = e;
            }
        }
    }

    public int getNEntries() {
        return this.getEntries();
    }
}
