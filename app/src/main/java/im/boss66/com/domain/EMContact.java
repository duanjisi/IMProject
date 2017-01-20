package im.boss66.com.domain;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Johnny on 2017/1/14.
 */
public class EMContact implements Parcelable {
    protected String username;
    protected String nick;

    protected EMContact() {
    }

    public EMContact(String var1) {
        this.username = var1;
    }

    public String getUsername() {
        return this.username;
    }

    public void setNick(String var1) {
        this.nick = var1;
    }

    public String getNick() {
        return this.nick == null ? this.getUsername() : this.nick;
    }

    public String toString() {
        return "<contact , username:" + this.username + ">";
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel var1, int var2) {
        var1.writeString(this.username);
    }

    private EMContact(Parcel var1) {
        this.username = var1.readString();
    }
}
