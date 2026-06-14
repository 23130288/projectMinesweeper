package Model;

public class User {
    public String name;
    public String email;
    public String password;
    public int avatarId;
    public User() { }
    public User(String name, String email,String password, int avatarId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatarId = avatarId;
    }
}

