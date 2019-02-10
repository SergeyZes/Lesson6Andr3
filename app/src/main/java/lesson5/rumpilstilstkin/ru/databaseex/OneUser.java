package lesson5.rumpilstilstkin.ru.databaseex;

public class OneUser {
    private String userId;
    private String login;
    private String avatarUrl;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public OneUser(String userId, String login, String avatarUrl) {
        this.userId = userId;
        this.login = login;
        this.avatarUrl = avatarUrl;
    }
}
