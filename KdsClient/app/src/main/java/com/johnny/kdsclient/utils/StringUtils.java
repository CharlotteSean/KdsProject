package com.johnny.kdsclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.johnny.kdsclient.bean.ContentParsedBean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目名称：KdsClient
 * 类描述：
 * 创建人：孟忠明
 * 创建时间：2016/10/11
 */
public class StringUtils {

    private StringUtils() {
    }

    public static SpannableString getItemContent(Context context, TextView tv, String content) {
        String regex = "(\\{emoji)(.*?)(\\})";

        SpannableString spannableString = new SpannableString(content);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(spannableString);

        if (matcher.find()) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            matcher.reset();
        }

        while (matcher.find()) {
            String emojiStr = matcher.group(2);
            int start = matcher.start(1);
            if (emojiStr != null) {
                int imgRes = EmotionUtils.getImgByName(Integer.parseInt(emojiStr));
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);

                if (bitmap != null) {
                    int size = (int) tv.getTextSize();
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);

                    ImageSpan imageSpan = new ImageSpan(context, bitmap);
                    spannableString.setSpan(imageSpan, start, start + emojiStr.length() + 7,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }


        }


        return spannableString;
    }

    /**
     * 解析HTML内容
     */
    public static ContentParsedBean parseReplyContent(String originContent) {

        String refrence = null;
        String content = null;
        List<String> refrenceImgs = new ArrayList<String>();
        List<String> contentImgs = new ArrayList<String>();

        // 替换\n,\n会干扰正则匹配
        originContent = originContent.replace("\n", "<br/>");

        // 抓出引用内容，和正文内容
        Pattern refrencePat = Pattern.compile(
                "(<table border=\"0\" width=\"100%\" cellspacing=\"1\" cellpadding=\"5\"><tr>" +
                        "<td bgcolor=\"#FFFEF2\">)(.*)(</td></tr></table>)(.*)");
        Matcher m = refrencePat.matcher(originContent);
        if (m.find()) {
            refrence = m.group(2);
            content = m.group(4);
        } else {
            content = originContent;
        }

        /**
         * 替换表情
         */
        Pattern pattern = Pattern.compile("(<img)(.*?)(.gif\" />)");
        String iconStr;
        String replacement;
        //替换引用部分
        if (refrence != null) {
            m = pattern.matcher(refrence);
            while (m.find()) {
                iconStr = m.group(2);
                iconStr = iconStr.substring(iconStr.length() - 2);
                int icon = -1;
                try{
                    icon = Integer.parseInt(iconStr);
                }catch (Exception e){
                    iconStr = iconStr.substring(iconStr.length() - 1);
                    icon = Integer.parseInt(iconStr);
                }
                replacement = "{emoji" + icon + "}";
                refrence = m.replaceFirst(replacement);
                m = pattern.matcher(refrence);
            }
        }

        m = pattern.matcher(content);
        while (m.find()) {
            iconStr = m.group(2);
            iconStr = iconStr.substring(iconStr.length() - 2);
            int icon = -1;
            try{
                icon = Integer.parseInt(iconStr);
            }catch (Exception e){
                iconStr = iconStr.substring(iconStr.length() - 1);
                icon = Integer.parseInt(iconStr);
            }
            replacement = "{emoji" + icon + "}";
            content = m.replaceFirst(replacement);
            m = pattern.matcher(content);
        }

        /**
         * 解析图片
         */
        Pattern imgPattern = Pattern.compile("(<a href=)(.*?)(<img src=\")(.*?)(\" alt.*?</a>)");
        //解析引用部分
        if (refrence != null) {
            m = imgPattern.matcher(refrence);
            while (m.find()) {
                String picUrl = m.group(4);
                refrenceImgs.add(picUrl);
                refrence = m.replaceFirst("");
                m = imgPattern.matcher(refrence);
            }
        }
        m = imgPattern.matcher(content);
        while (m.find()) {
            String picUrl = m.group(4);
            contentImgs.add(picUrl);
            content = m.replaceFirst("");
            m = imgPattern.matcher(content);
        }

        //处理掉多余的HTML标签
        if (refrence != null) {
            refrence = getClearStr(refrence);
        }
        content = getClearStr(content);

        ContentParsedBean contentParsedBean = new ContentParsedBean();
        contentParsedBean.setRefrence(refrence);
        contentParsedBean.setContent(content);
        contentParsedBean.setRefrenceImgs(refrenceImgs);
        contentParsedBean.setContentImgs(contentImgs);
        return contentParsedBean;
    }

    private static String getClearStr(String str){
        str = str.replaceAll("-==.*?==-", "");
        str = str.replaceAll("(<a.*?>)|(</a>)|(<b>)|(</b>)|(<center>)|(</center>)|(<strong>)|(</strong>)", "");
        str = str.replaceAll("(<em>)|(</em>)|(<FONT.*?>)|(</FONT>)|&.*?;", "");
        str = str.replaceAll("(<p.*?>)|(<br>)|(</span>)|(<span.*?>)", "");
        str = str.replaceAll("(<br\\s?/>)|(</p>)", "\n");
        str = str.replaceAll("\\n{2,}", "\n");
        String[] strParts = str.split("\n");
        StringBuffer sb = new StringBuffer();
        boolean hasContent = false;
        for (String parts : strParts){
            if(parts.trim().equals("")){
                continue;
            }
            hasContent = true;
            sb.append(parts);
            sb.append("\n");
        }
        str = sb.toString();
        if(hasContent){
            str = str.substring(0,str.length()-1);
        }
        return str;
    }

}
