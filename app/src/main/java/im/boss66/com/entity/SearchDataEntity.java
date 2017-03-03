package im.boss66.com.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GMARUnity on 2017/3/3.
 */
public class SearchDataEntity {
    public SearchUser user;
    public SearchStore store;
    public SearchCofriend cofriend;

    public class SearchUser {
        public List<ContactEntity> getList() {
            return list;
        }

        public void setList(List<ContactEntity> list) {
            this.list = list;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        private List<ContactEntity> list = new ArrayList<>();
        private String more;
    }

    public class SearchStore {
        public List<EmoStore> getList() {
            return list;
        }

        public void setList(List<EmoStore> list) {
            this.list = list;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        private List<EmoStore> list = new ArrayList<>();
        private String more;
        private String moe;
    }

    public class SearchCofriend {
        public List<SearchCofriendEntity> getList() {
            return list;
        }

        public void setList(List<SearchCofriendEntity> list) {
            this.list = list;
        }

        public String getMore() {
            return more;
        }

        public void setMore(String more) {
            this.more = more;
        }

        private List<SearchCofriendEntity> list = new ArrayList<>();
        private String more;
    }

}
