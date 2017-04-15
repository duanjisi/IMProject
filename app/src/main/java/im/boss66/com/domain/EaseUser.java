/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.boss66.com.domain;

import com.hyphenate.chat.EMContact;

public class EaseUser extends EMContact {

    /**
     * initial letter for nickname
     */
    protected String initialLetter;
    /**
     * avatar of the user
     */
    protected String avatar;
    protected String userid;
    protected String fid;

    protected String friend_id;

    protected boolean isAdded = false;
    protected boolean checked = false;


    protected String is_friends = "";
    protected String contactName = "";
    protected String request_status = "";

    protected String request_status_mess = "";
    protected String msgType;

    public EaseUser() {
    }

    public EaseUser(String username) {
        this.username = username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getInitialLetter() {
        if (initialLetter == null) {
//            EaseCommonUtils.setUserInitialLetter(this);
        }
        return initialLetter;
    }

    public void setInitialLetter(String initialLetter) {
        this.initialLetter = initialLetter;
    }

    public String getFriend_id() {
        return friend_id;
    }

    public void setFriend_id(String friend_id) {
        this.friend_id = friend_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
    }

    public String getIs_friends() {
        return is_friends;
    }

    public void setIs_friends(String is_friends) {
        this.is_friends = is_friends;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }

    public String getRequest_status_mess() {
        return request_status_mess;
    }

    public void setRequest_status_mess(String request_status_mess) {
        this.request_status_mess = request_status_mess;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    @Override
    public int hashCode() {
        return 17 * getUsername().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof EaseUser)) {
            return false;
        }
        return getUsername().equals(((EaseUser) o).getUsername());
    }

    @Override
    public String toString() {
        return nick == null ? username : nick;
    }
}
