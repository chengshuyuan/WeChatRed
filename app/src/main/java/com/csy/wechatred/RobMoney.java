package com.csy.wechatred;

/**
 * Created by csy on 2016/7/9.
 */

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.PendingIntent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class RobMoney extends AccessibilityService {

    private AccessibilityNodeInfo mAccessibilityNodeInfo = null;


    @Override public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo previousAccessibilityNodeInfo = null;
        mAccessibilityNodeInfo = event.getSource();

        if (mAccessibilityNodeInfo == null) {
            return;
        }

        //通知栏事件
        if(event.getEventType() ==  AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            List<CharSequence> texts = event.getText();
            if (!texts.isEmpty()) {
                for (CharSequence text : texts) {
                    String content = text.toString();
                    Log.e("abcerk", "通知栏内容：" + content);
                    if (content.contains("微信红包")) {
                        //模拟打开通知栏消息
                        if (event.getParcelableData() != null &&
                                event.getParcelableData() instanceof Notification) {
                            Notification notification = (Notification) event.getParcelableData();
                            PendingIntent pendingIntent = notification.contentIntent;
                            try {
                                pendingIntent.send();
                            } catch (PendingIntent.CanceledException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            List<AccessibilityNodeInfo> accessibilityNodeInfos = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("微信红包");
            if (accessibilityNodeInfos != null && accessibilityNodeInfos.size() > 0) {
                AccessibilityNodeInfo info = accessibilityNodeInfos.get(accessibilityNodeInfos.size() - 1);
                if (previousAccessibilityNodeInfo == null) {
                    previousAccessibilityNodeInfo = info;
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                else if (previousAccessibilityNodeInfo != null && previousAccessibilityNodeInfo != info) {
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                else {
                    return;
                }

            }

        }

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            List<AccessibilityNodeInfo> infos = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("拆红包");
            if (infos != null && infos.size() > 0) {
                AccessibilityNodeInfo accessibilityNodeInfo = infos.get(infos.size() - 1);
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }

            List<AccessibilityNodeInfo> infoKais = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("发了一个红包");
            if (infoKais != null && infoKais.size() > 0) {
                AccessibilityNodeInfo accessibilityNodeInfo = infoKais.get(infoKais.size() - 1);
                int size = accessibilityNodeInfo.getParent().getChildCount();
                Log.d("sunxu_log", "size-->" + size);
                //for (AccessibilityNodeInfo info : infoKais) {
                //    info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                //}
                for (int i = 0; i < accessibilityNodeInfo.getParent().getChildCount(); i++) {
                    accessibilityNodeInfo.getParent().getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }

//            List<AccessibilityNodeInfo> infoDetails = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("红包详情");
//            if (infoDetails != null && infoDetails.size() > 0) {
//                AccessibilityNodeInfo accessibilityNodeInfo = infoDetails.get(infoDetails.size() - 1);
//                accessibilityNodeInfo.getParent().getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                return;
//            }

            List<AccessibilityNodeInfo> infoSlows = mAccessibilityNodeInfo.findAccessibilityNodeInfosByText("手慢了");
            if (infoSlows != null && infoSlows.size() > 0) {
                AccessibilityNodeInfo accessibilityNodeInfo = infoSlows.get(infoSlows.size() - 1);
                accessibilityNodeInfo.getParent().getChild(3).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }


        }
    }

    @Override public void onInterrupt() {

    }


}
