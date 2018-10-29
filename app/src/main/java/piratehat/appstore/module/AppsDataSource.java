package piratehat.appstore.module;

import android.util.Log;

import com.shizhefei.mvc.IAsyncDataSource;
import com.shizhefei.mvc.IDataSource;
import com.shizhefei.mvc.RequestHandle;
import com.shizhefei.mvc.ResponseSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import piratehat.appstore.Bean.AppBean;
import piratehat.appstore.config.Constant;
import piratehat.appstore.config.Url;
import piratehat.appstore.contract.IAppDataSource;
import piratehat.appstore.dto.AppsDataDto;
import piratehat.appstore.utils.GsonUtil;
import piratehat.appstore.utils.OkHttpResultCallback;
import piratehat.appstore.utils.OkHttpUtil;

/**
 *
 * Created by PirateHat on 2018/10/29.
 */

public class AppsDataSource implements IAsyncDataSource<List<AppBean>> {
     private int mPageContext ;
     private boolean mHasMore;


    private static final String TAG = "AppsDataSource";
    @Override
    public RequestHandle refresh(ResponseSender<List<AppBean>> sender) {
        mPageContext = 20;
        return loadApps(sender,mPageContext);
    }

    @Override
    public RequestHandle loadMore(ResponseSender<List<AppBean>> sender) {
        mPageContext +=20;
        return loadApps(sender,mPageContext);
    }

    @Override
    public boolean hasMore() {
        return mHasMore;
    }

    private RequestHandle loadApps(final ResponseSender<List<AppBean>> sender, int pageContext) {
        Map<String, String> map = new HashMap<>();
        map.put(Constant.USER_AGENT, Constant.USER_AGENT_VALUE);
        OkHttpUtil.getInstance().getAsync(Url.LOAD_MORE+pageContext, new OkHttpResultCallback() {
            @Override
            public void onError(Call call, Exception e) {
                sender.sendError(e);
            }

            @Override
            public void onResponse(String msg) {
                ArrayList<AppBean> beans = (ArrayList<AppBean>) GsonUtil.gsonToBean(msg, AppsDataDto.class).transform();
                mHasMore = beans.size() != 0;

                sender.sendData(beans);
            }
        },map);
        return new OkHttpRequestHandle();
    }

    private class OkHttpRequestHandle implements RequestHandle{


        OkHttpRequestHandle() {
            super();

        }

        @Override
        public void cancle() {
            OkHttpUtil.getInstance().cancelAllRequest();
        }

        @Override
        public boolean isRunning() {
            return false;
        }
    }
}
