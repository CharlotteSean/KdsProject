package com.johnny.kdsclient.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.johnny.kdsclient.BaseActivity;
import com.johnny.kdsclient.R;
import com.johnny.kdsclient.adapter.UserTopicRecycleAdapter;
import com.johnny.kdsclient.api.ApiHelper;
import com.johnny.kdsclient.api.SimpleResponseListener;
import com.johnny.kdsclient.bean.Reply;
import com.johnny.kdsclient.bean.Topic;
import com.johnny.kdsclient.bean.UserDetailResponse;
import com.johnny.kdsclient.bean.UserInfo;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 项目名称：KdsClient
 * 类描述：
 * 创建人：孟忠明
 * 创建时间：2016/10/27
 */
public class UserDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String EXTRA_REPLY = "reply";
    public static final String NAME_IMG_AVATAR = "imgAvatar";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.img_avatar)
    CircleImageView imgAvatar;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_signature)
    TextView tvSignature;
    @BindView(R.id.tv_register_time)
    TextView tvRegisterTime;
    @BindView((R.id.tv_score))
    TextView tvScore;
    @BindView(R.id.progress_wheel)
    ProgressWheel progressWheel;
    @BindView(R.id.id_swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.id_recyclerview)
    RecyclerView recyclerView;

    private UserTopicRecycleAdapter topicAdapter;
    private Reply reply;
    private int loadedPage;
    private int lastVisibleItem;
    private boolean isBottom;

    @Override
    protected int layout() {
        return R.layout.activity_user;
    }

    @Override
    protected void initDate() {
        ViewCompat.setTransitionName(imgAvatar, NAME_IMG_AVATAR);

        Intent intent = getIntent();
        reply = (Reply) intent.getSerializableExtra(EXTRA_REPLY);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadDate();
            }
        });
    }

    @Override
    protected void initView() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.finishAfterTransition(UserDetailActivity.this);
            }
        });
        tvUsername.setText(reply.getUserName());
        Glide.with(this).load(reply.getUserAvatar()).into(imgAvatar);

        topicAdapter = new UserTopicRecycleAdapter(this);
        final RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(topicAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == topicAdapter.getItemCount()&&!isBottom) {
                    loadedPage += 1;
                    loadDate();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
        });
    }

    @Override
    public void onRefresh() {
        loadDate();
    }

    private void loadDate() {
        ApiHelper.getInstance().getUserDetail(reply.getUserLink(),
                new SimpleResponseListener<UserDetailResponse>() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressWheel.stopSpinning();
                    }

                    @Override
                    public void onResponse(UserDetailResponse response) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressWheel.stopSpinning();

                        UserInfo userInfo = response.getUserInfo();
                        tvUsername.setText(userInfo.getUserName());
                        tvRegisterTime.setText(userInfo.getRegister());
                        tvScore.setText("HP:" + userInfo.getHp() + " PP:" + userInfo.getPp());

                        List<Topic> topicList = response.getTopicList();
                        if (topicList == null || topicList.size() == 0) {
                            isBottom = true;
                        }
                        if (loadedPage > 1) {
                            topicAdapter.getDatas().addAll(topicList);
                        } else {
                            topicAdapter.setDatas(topicList);
                        }
                        topicAdapter.notifyDataSetChanged();

//                        if (topicAdapter.getDatas().size() >= topic.getReplyNum() || isBottom) {
//                            topicAdapter.setFooterViewType(true);
//                            isBottom = true;
//                        } else {
//                            topicAdapter.setFooterViewType(false);
//                        }

                    }
                });
    }
}
