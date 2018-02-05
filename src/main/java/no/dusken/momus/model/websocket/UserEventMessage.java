package no.dusken.momus.model.websocket;

public class UserEventMessage {
    private UserAction userAction;
    private Long userid;

    public UserAction getUserAction() {
        return userAction;
    }

    public void setUserAction(UserAction userAction) {
        this.userAction = userAction;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
}
