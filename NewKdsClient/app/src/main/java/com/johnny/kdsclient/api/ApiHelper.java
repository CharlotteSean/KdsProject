package com.johnny.kdsclient.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.johnny.kdsclient.bean.LoginResponse;
import com.johnny.kdsclient.bean.ReplyListResponse;
import com.johnny.kdsclient.bean.SendTopicRequest;
import com.johnny.kdsclient.bean.TopicListResponse;
import com.johnny.kdsclient.bean.TopicListTypeEnum;
import com.johnny.kdsclient.bean.UserDetailResponse;
import com.johnny.kdsclient.bean.UserInfo;
import com.johnny.kdsclient.bean.UserTopicResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static com.johnny.kdsclient.bean.TopicListTypeEnum.Daliy;

/**
 * 项目名称：KdsClient
 * 类描述：API操作类
 * 创建人：孟忠明
 * 创建时间：2016/10/8
 */
public class ApiHelper {

    private static final String TAG = ApiHelper.class.getSimpleName();

    private static ApiHelper apiHelper;

    private RequestQueue queue;

    private Gson gson;

    private ApiHelper() {
    }

    public static ApiHelper getInstance() {
        if (apiHelper == null) {
            apiHelper = new ApiHelper();
        }
        return apiHelper;
    }

    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
        gson = new Gson();
    }

    public RequestQueue getHttpQueue() {
        return queue;
    }

    /**
     * 获取帖子列表
     *
     * @param page     给定页码
     * @param listener
     */
    public void getTopicList(TopicListTypeEnum type, int page, final SimpleResponseListener<TopicListResponse> listener) {
        String url = "";
        switch (type) {
            case Normal:
                url = ApiConstant.GET_TOPIC_LIST_NORMAL + "?page=" + page + "&topicId=" + 15;
                break;
            case Daliy:
                url = ApiConstant.GET_TOPIC_LIST_DAILY + "?page=" + page + "&topicId=" + 15;
                break;
            case Week:
                url = ApiConstant.GET_TOPIC_LIST_WEEK + "?page=" + page + "&topicId=" + 15;
                break;
            case Month:
                url = ApiConstant.GET_TOPIC_LIST_MONTH + "?page=" + page + "&topicId=" + 15;
                break;
        }
        Request request = new SimpleRequest(url, new SimpleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    TopicListResponse topicListResponse = gson.fromJson(response, TopicListResponse.class);
                    listener.onResponse(topicListResponse);
                } catch (Exception e) {
                    VolleyError volleyError = new VolleyError();
                    volleyError.setStackTrace(e.getStackTrace());
                    listener.onErrorResponse(volleyError);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        getHttpQueue().add(request);
    }

    /**
     * 获取帖子回复列表
     *
     * @param bbsId
     * @param page
     * @param isOnlyLandlord
     * @param listener
     */
    public void getReplyList(String bbsId, int page, int isOnlyLandlord,
                             final SimpleResponseListener<ReplyListResponse> listener) {
        String url = ApiConstant.GET_REPLY_LIST + "?bbsId=" + bbsId + "&page=" + page + "&topicId="
                + 15 + "&ol=" + isOnlyLandlord;
        Request request = new SimpleRequest(url, new SimpleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String data = jsonObject.getString("data");
                    ReplyListResponse replyListResponse = gson.fromJson(data, ReplyListResponse.class);
                    listener.onResponse(replyListResponse);
                } catch (Exception e) {
                    VolleyError volleyError = new VolleyError();
                    volleyError.setStackTrace(e.getStackTrace());
                    listener.onErrorResponse(volleyError);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        getHttpQueue().add(request);
    }

    /**
     * 获取指定用户的帖子列表
     *
     * @param userId
     * @param page
     * @param listener
     */
    public void getUserTopicList(String userId, int page, final SimpleResponseListener listener) {
        String url = ApiConstant.GET_USER_TOPIC_LIST + "?userId=" + userId + "&page=" + page;
        Request request = new SimpleRequest(url, new SimpleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    UserTopicResponse userTopics = gson.fromJson(response, UserTopicResponse.class);
                    listener.onResponse(userTopics);
                } catch (Exception e) {
                    VolleyError volleyError = new VolleyError();
                    volleyError.setStackTrace(e.getStackTrace());
                    listener.onErrorResponse(volleyError);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        getHttpQueue().add(request);
    }

    /**
     * 用户登录
     *
     * @param userName
     * @param password
     * @param listener
     */
    public void login(String userName, String password, final SimpleResponseListener<LoginResponse> listener) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appver", "3.1.1");
        paramMap.put("system", "Android");
        paramMap.put("userName", userName);
        paramMap.put("passWord", password);
        Request request = new SimpleRequest(ApiConstant.LOGIN, paramMap, new SimpleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
                    listener.onResponse(loginResponse);
                } catch (Exception e) {
                    VolleyError volleyError = new VolleyError();
                    volleyError.setStackTrace(e.getStackTrace());
                    listener.onErrorResponse(volleyError);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        getHttpQueue().add(request);
    }

    /**
     * 发送帖子
     *
     * @param sendTopicRequest
     * @param listener
     */
    public void sendTopic(SendTopicRequest sendTopicRequest, final SimpleResponseListener<String> listener) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("htmlcode", sendTopicRequest.getHtmlcode());
        paramMap.put("description", "");
        paramMap.put("title", sendTopicRequest.getTitle());
        paramMap.put("areaId", sendTopicRequest.getAreaId());
        paramMap.put("sortId", sendTopicRequest.getSortId());
        paramMap.put("topicId", sendTopicRequest.getTopicId());
        paramMap.put("postUserId", sendTopicRequest.getPostUserId());
        Request request = new SimpleRequest(ApiConstant.SEND_TOPIC, paramMap, new SimpleResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    listener.onResponse(response);
                } catch (Exception e) {
                    VolleyError volleyError = new VolleyError();
                    volleyError.setStackTrace(e.getStackTrace());
                    listener.onErrorResponse(volleyError);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
        getHttpQueue().add(request);
    }

    public void uploadPicture(String userId, File photoFile, final SimpleResponseListener<String> listener) {
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appver", "3.1.1");
        paramMap.put("system", "Android");
        paramMap.put("topicId", "15");
        paramMap.put("isTopic", "1");
        paramMap.put("postUserId", userId);
        Request request = new MultipartRequest(ApiConstant.UPLOAD_PICTURE, "attachments", photoFile, paramMap
                , new SimpleResponseListener<String>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String msg = jsonObject.getString("msg");
                    if (msg.equals("success")) {
                        jsonObject = jsonObject.getJSONObject("uploadRes");
                        String htmlCodeBlock = jsonObject.getString("HTMLCodeBlock");
                        listener.onResponse(htmlCodeBlock);
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listener.onResponse("");
            }
        });
        getHttpQueue().add(request);
    }


}
