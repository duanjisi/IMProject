package im.boss66.com.http;

import com.alibaba.fastjson.TypeReference;

/**
 * Summary: 带分页参数的上传数据对象
 */
public abstract class PageModel<T> extends BaseRequest<T> {

    protected PageModel(String tag, Object... params) {
        super(tag, params);
    }

    protected abstract TypeReference getSubPojoType();
}
