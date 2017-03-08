package im.boss66.com.entity;


import java.util.List;

/**
 * Created by admin on 2017/3/1.
 */

public class JobEntity {


    /**
     * name : ucenter api
     * message : succeed
     * code : 1
     * status : 200
     * type : yii\Response
     * result : [{"id":1,"name":"互联网/电子商务"},{"id":2,"name":"计算机软件"},{"id":3,"name":"IT服务(系统/数据/维护)"},{"id":4,"name":"电子技术/半导体/集成电路"},{"id":5,"name":"计算机硬件"},{"id":6,"name":"通信/电信/网络设备"},{"id":7,"name":"通信/电信运营、增值服务"},{"id":8,"name":"网络游戏"},{"id":9,"name":"基金/证券/期货/投资"},{"id":10,"name":"保险"},{"id":11,"name":"银行"},{"id":12,"name":"信托/担保/拍卖/典当"},{"id":13,"name":"房地产/建筑/建材/工程"},{"id":14,"name":"家居/室内设计/装饰装潢"},{"id":15,"name":"物业管理/商业中心"},{"id":16,"name":"专业服务/咨询(财会/法律/人力资源等)"},{"id":17,"name":"广告/会展/公关"},{"id":18,"name":"中介服务"},{"id":19,"name":"检验/检测/认证"},{"id":20,"name":"外包服务"},{"id":21,"name":"快速消费品（食品/饮料/烟酒/日化）"},{"id":22,"name":"耐用消费品（服饰/纺织/皮革/家具/家电）"},{"id":23,"name":"贸易/进出口"},{"id":24,"name":"零售/批发"},{"id":25,"name":"租赁服务"},{"id":26,"name":"教育/培训/院校"},{"id":27,"name":"礼品/玩具/工艺美术/收藏品/奢侈品"},{"id":28,"name":"汽车/摩托车"},{"id":29,"name":"大型设备/机电设备/重工业"},{"id":30,"name":"加工制造（原料加工/模具）"},{"id":31,"name":"仪器仪表及工业自动化"},{"id":32,"name":"印刷/包装/造纸"},{"id":33,"name":"办公用品及设备"},{"id":34,"name":"医药/生物工程"},{"id":35,"name":"医疗设备/器械"},{"id":36,"name":"航空/航天研究与制造"},{"id":37,"name":"交通/运输"},{"id":38,"name":"物流/仓储"},{"id":39,"name":"医疗/护理/美容/保健/卫生服务"},{"id":40,"name":"酒店/餐饮"},{"id":41,"name":"旅游/度假"},{"id":42,"name":"媒体/出版/影视/文化传播"},{"id":43,"name":"娱乐/体育/休闲"},{"id":44,"name":"能源/矿产/采掘/冶炼"},{"id":45,"name":"石油/石化/化工"},{"id":46,"name":"电气/电力/水利"},{"id":47,"name":"环保"},{"id":48,"name":"政府/公共事业/非盈利机构"},{"id":49,"name":"学术/科研"},{"id":50,"name":"农/林/牧/渔"},{"id":51,"name":"跨领域经营"},{"id":52,"name":"其他"}]
     */

    private String name;
    private String message;
    private int code;
    private int status;
    private String type;
    private List<ResultBean> result;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean  {
        /**
         * id : 1
         * name : 互联网/电子商务
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Override
    public String toString() {
        return "JobEntity{" +
                "name='" + name + '\'' +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", status=" + status +
                ", type='" + type + '\'' +
                ", result=" + result +
                '}';
    }
}
