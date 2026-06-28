package Model;

public class Achievement {
    private String aid;
    private String name;
    private String description;
    private boolean unlocked;

    public Achievement(String aid, String name, String description) {
        this.aid = aid;
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }
}
