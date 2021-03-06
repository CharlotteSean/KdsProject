package com.johnny.kdsclient.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.johnny.kdsclient.BaseActivity;
import com.johnny.kdsclient.R;
import com.johnny.kdsclient.adapter.EmotionGvAdapter;
import com.johnny.kdsclient.adapter.EmotionPagerAdapter;
import com.johnny.kdsclient.adapter.WriteTopicGridImgsAdapter;
import com.johnny.kdsclient.utils.CommonUtils;
import com.johnny.kdsclient.utils.EmotionUtils;
import com.johnny.kdsclient.utils.ImageUtils;
import com.johnny.kdsclient.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 项目名称：KdsClient
 * 类描述：
 * 创建人：孟忠明
 * 创建时间：2016/10/31
 */
public class WriteTopicActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tv_menu_bg)
    TextView tvMenuBg;
    @BindView(R.id.cv_save)
    CardView cvSave;
    @BindView(R.id.cv_save_text)
    CardView cvSaveText;
    @BindView(R.id.cv_send)
    CardView cvSend;
    @BindView(R.id.cv_send_text)
    CardView cvSendText;
    @BindView(R.id.et_write_topic)
    EditText etWriteTopic;
    @BindView(R.id.gv_write_topic)
    GridView gvWriteTopicImgs;
    @BindView(R.id.iv_takephoto)
    ImageView ivTakePhoto;
    @BindView(R.id.iv_image)
    ImageView ivImage;
    @BindView(R.id.iv_image_library)
    ImageView ivImageLibrary;
    @BindView(R.id.iv_emoji)
    ImageView ivEmoji;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.ll_emotion_dashboard)
    LinearLayout llEmoji;
    @BindView(R.id.vp_emotion_dashboard)
    ViewPager vpEmoji;

    private WriteTopicGridImgsAdapter writeTopicGridImgsAdapter;
    private EmotionPagerAdapter emotionPagerGvAdapter;
    private ArrayList<Uri> imgUris = new ArrayList<Uri>();
    private boolean isMenuOpen;

    @Override
    protected int layout() {
        return R.layout.activity_write_topic;
    }

    @Override
    protected void initDate() {
        initEmotion();
    }

    @Override
    protected void initView() {
        setBackEnable(false);
        toolbar.setTitle("发帖");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        writeTopicGridImgsAdapter = new WriteTopicGridImgsAdapter(this, imgUris, gvWriteTopicImgs);
        gvWriteTopicImgs.setAdapter(writeTopicGridImgsAdapter);
        gvWriteTopicImgs.setOnItemClickListener(this);
    }

    @OnClick({R.id.iv_takephoto, R.id.iv_image, R.id.iv_image_library, R.id.iv_emoji, R.id.iv_voice,
            R.id.rb_emotion_dashboard_emoji, R.id.rb_emotion_dashboard_spacial, R.id.fab, R.id.cv_send,
            R.id.cv_send_text, R.id.cv_save, R.id.cv_save_text})
    protected void onAction(View view) {
        switch (view.getId()) {
            case R.id.iv_takephoto:
                ImageUtils.pickImageFromCamera(this);
                break;
            case R.id.iv_image:
                ImageUtils.pickImageFromAlbum(this);
                break;
            case R.id.iv_image_library:
                break;
            case R.id.iv_emoji:
                if (llEmoji.getVisibility() == View.VISIBLE) {
                    llEmoji.setVisibility(View.GONE);
                    ivEmoji.setImageResource(R.drawable.ic_tag_face_selector);
                    CommonUtils.openKeybord(etWriteTopic, this);
                } else {
                    llEmoji.setVisibility(View.VISIBLE);
                    ivEmoji.setImageResource(R.drawable.ic_keyboard_selector);
                    CommonUtils.closeKeybord(etWriteTopic, this);
                }
                break;
            case R.id.iv_voice:
                break;
            case R.id.rb_emotion_dashboard_emoji:
                initEmotion();
                break;
            case R.id.rb_emotion_dashboard_spacial:
                initSpecialEmotion();
                break;
            case R.id.fab:
                if (isMenuOpen) {
                    isMenuOpen = false;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", -135, 0);
                    AnticipateOvershootInterpolator interpolator = new AnticipateOvershootInterpolator();
                    animator.setInterpolator(interpolator);
                    animator.setDuration(300);
                    animator.start();
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0.7f, 0);
                    alphaAnimation.setDuration(300);
                    tvMenuBg.startAnimation(alphaAnimation);
                    tvMenuBg.setVisibility(View.GONE);

                    ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.2f, 1f, 0.2f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(100);
                    cvSave.startAnimation(scaleAnimation);
                    cvSave.setVisibility(View.GONE);
                    cvSaveText.startAnimation(scaleAnimation);
                    cvSaveText.setVisibility(View.GONE);
                    cvSend.setVisibility(View.GONE);
                    cvSend.startAnimation(scaleAnimation);
                    cvSendText.setVisibility(View.GONE);
                    cvSendText.startAnimation(scaleAnimation);
                } else {
                    isMenuOpen = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", 0, -135);
                    AnticipateOvershootInterpolator interpolator = new AnticipateOvershootInterpolator();
                    animator.setInterpolator(interpolator);
                    animator.setDuration(300);
                    animator.start();
                    AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0.7f);
                    alphaAnimation.setDuration(300);
                    alphaAnimation.setFillAfter(true);
                    tvMenuBg.setVisibility(View.VISIBLE);
                    tvMenuBg.startAnimation(alphaAnimation);


                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1f, 0.2f, 1f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setInterpolator(interpolator);
                    scaleAnimation.setDuration(300);
                    cvSave.setVisibility(View.VISIBLE);
                    cvSave.startAnimation(scaleAnimation);
                    cvSaveText.setVisibility(View.VISIBLE);
                    cvSaveText.startAnimation(scaleAnimation);
                    cvSend.setVisibility(View.VISIBLE);
                    cvSend.startAnimation(scaleAnimation);
                    cvSendText.setVisibility(View.VISIBLE);
                    cvSendText.startAnimation(scaleAnimation);
                }
                break;
            case R.id.cv_save:
            case R.id.cv_save_text:

                break;
            case R.id.cv_send:
            case R.id.cv_send_text:

                break;
        }
    }

    /**
     * 初始化表情面板内容
     */
    private void initEmotion() {
        int screenWidth = CommonUtils.getScreenWidthPixels(this);
        int spacing = CommonUtils.dp2Px(this, 16);

        int itemWidth = (screenWidth - spacing * 8) / 7;
        int gvHeight = itemWidth * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<Integer> emotionCodes = new ArrayList<Integer>();

        for (int emojiCode : EmotionUtils.emojiMap.keySet()) {
            if (emojiCode >= 56) {
                break;
            }
            emotionCodes.add(emojiCode);

            if (emotionCodes.size() == 21) {
                GridView gv = createEmotionGridView(emotionCodes, 7, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);

                emotionCodes = new ArrayList<Integer>();
            }
        }

        if (emotionCodes.size() > 0) {
            GridView gv = createEmotionGridView(emotionCodes, 7, screenWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vpEmoji.setLayoutParams(params);
        vpEmoji.setAdapter(emotionPagerGvAdapter);
    }

    /**
     * 创建显示表情的GridView
     */
    private GridView createEmotionGridView(List<Integer> emotionNames, int columnsNum, int gvWidth,
                                           int padding, int itemWidth, int gvHeight) {
        GridView gv = new GridView(this);
        gv.setBackgroundResource(R.color.bg_gray);
        gv.setSelector(R.color.transparent);
        gv.setNumColumns(columnsNum);
        gv.setPadding(padding, padding, padding, padding);
        gv.setHorizontalSpacing(padding);
        gv.setVerticalSpacing(padding);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gvWidth, gvHeight);
        gv.setLayoutParams(params);

        EmotionGvAdapter adapter = new EmotionGvAdapter(this, emotionNames, itemWidth);
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(this);

        return gv;
    }

    /**
     * 初始化表情面板内容
     */
    private void initSpecialEmotion() {
        int screenWidth = CommonUtils.getScreenWidthPixels(this);
        int spacing = CommonUtils.dp2Px(this, 4);

        int itemWidth = (screenWidth - spacing * 8) / 5;
        int itemHeight = (screenWidth - spacing * 8) / 7;
        int gvHeight = itemHeight * 3 + spacing * 4;

        List<GridView> gvs = new ArrayList<GridView>();
        List<Integer> emotionCodes = new ArrayList<Integer>();

        for (int emojiCode : EmotionUtils.emojiMap.keySet()) {
            if (emojiCode < 56) {
                continue;
            }
            emotionCodes.add(emojiCode);

            if (emotionCodes.size() == 10) {
                GridView gv = createEmotionGridView(emotionCodes, 5, screenWidth, spacing, itemWidth, gvHeight);
                gvs.add(gv);

                emotionCodes = new ArrayList<Integer>();
            }
        }

        if (emotionCodes.size() > 0) {
            GridView gv = createEmotionGridView(emotionCodes, 5, screenWidth, spacing, itemWidth, gvHeight);
            gvs.add(gv);
        }

        emotionPagerGvAdapter = new EmotionPagerAdapter(gvs);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth, gvHeight);
        vpEmoji.setLayoutParams(params);
        vpEmoji.setAdapter(emotionPagerGvAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemAdapter = parent.getAdapter();
        if (itemAdapter instanceof WriteTopicGridImgsAdapter) {
            WriteTopicGridImgsAdapter imgsAdapter = (WriteTopicGridImgsAdapter) itemAdapter;
            if (position == imgsAdapter.getCount() - 1) {
                ImageUtils.showImagePickDialog(this);
            }
        } else if (itemAdapter instanceof EmotionGvAdapter) {
            EmotionGvAdapter emotionAdapter = (EmotionGvAdapter) itemAdapter;
            int emotionCode = emotionAdapter.getItem(position);
            String emotionName = "{emoji" + emotionCode + "}";
            int curPosition = etWriteTopic.getSelectionStart();
            StringBuilder sb = new StringBuilder(etWriteTopic.getText().toString());
            sb.insert(curPosition, emotionName);
            SpannableString content = StringUtils.getItemContent(
                    this, etWriteTopic, sb.toString());
            etWriteTopic.setText(content);
            etWriteTopic.setSelection(curPosition + emotionName.length());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ImageUtils.REQUEST_CODE_FROM_ALBUM:
                if (resultCode == RESULT_CANCELED) {
                    return;
                }
                Uri imageUri = data.getData();

                imgUris.add(imageUri);
                updateImgs();
                break;
            case ImageUtils.REQUEST_CODE_FROM_CAMERA:
                if (resultCode == RESULT_CANCELED) {
                    ImageUtils.deleteImageUri(this, ImageUtils.imageUriFromCamera);
                } else {
                    Uri imageUriCamera = ImageUtils.imageUriFromCamera;

                    imgUris.add(imageUriCamera);
                    updateImgs();
                }
                break;

            default:
                break;
        }
    }

    private void updateImgs() {
        if (imgUris.size() > 0) {
            gvWriteTopicImgs.setVisibility(View.VISIBLE);
            writeTopicGridImgsAdapter.notifyDataSetChanged();
        } else {
            gvWriteTopicImgs.setVisibility(View.GONE);
        }
    }
}
