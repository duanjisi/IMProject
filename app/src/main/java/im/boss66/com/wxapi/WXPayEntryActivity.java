package im.boss66.com.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import im.boss66.com.R;
import im.boss66.com.activity.base.BaseActivity;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {
    private static final String TAG = WXPayEntryActivity.class.getSimpleName();
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.weixin_app_id2));
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.app_tip);
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
            Log.i("info", "=============errCode:" + resp.errCode);
            if (resp.errCode == 0) {
                showToast("支付成功!", true);
//                requesResult();
            } else {
                showToast("支付失败!", true);
                finish();
            }
        }
    }
//    private void requesResult() {
//        showLoadingDialog();
//        String tradeNo = App.getInstance().getTrade_no();
//        if (tradeNo != null && !tradeNo.equals("")) {
//            ChargeStatusRequest request = new ChargeStatusRequest(TAG, tradeNo);
//            request.send(new BaseDataRequest.RequestCallback() {
//                @Override
//                public void onSuccess(Object pojo) {
//                    cancelLoadingDialog();
//                    Session.getInstance().refreshCashesPager();
//                    showToast("支付成功!", true);
//                    finish();
//                }
//
//                @Override
//                public void onFailure(String msg) {
//                    cancelLoadingDialog();
//                    showToast("支付失败!", true);
//                }
//            });
//        }
//    }
}