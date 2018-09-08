package men.brakh.chat;

public class User {
    private String name;
    private UsersTypes userType;



    public User() {

    }

    public UsersTypes getUserType() {
        return userType;
    }

    public void setUserType(UsersTypes userType) {
        this.userType = userType;
    }

    public User(String name) {
        this.name = name;
        this.userType = UsersTypes.NONE;
    }


    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public Boolean equal(User user) {
        return (this.getName().equals(user.getName())) && (this.userType == user.userType);
    }
}
