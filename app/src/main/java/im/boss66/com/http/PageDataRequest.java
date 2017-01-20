package im.boss66.com.http;
import com.alibaba.fastjson.TypeReference;

/**
 * Summary: 带分页参数的上传数据对象
 */
public abstract class PageDataRequest<T> extends BaseDataRequest<T> {

    protected PageDataRequest(String tag, Object... params) {
        super(tag, params);
    }

    protected abstract TypeReference getSubPojoType();
}
