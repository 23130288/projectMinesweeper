package Model;

public class User {
    public String uid;

    public String name;
    public String email;
    public String password;
    public int avatarId;
    public String avatar;
    public User() { }

    public User(String uid, String name, String email, String password, int avatarId, String avatar) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarId = avatarId;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}