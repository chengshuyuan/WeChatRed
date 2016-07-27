# 1 AccessibilityService的介绍
许多Android使用者因为各种情况导致他们要以不同的方式与手机交互。这包括了在有些用户在视力上、身体上、年龄上的问题导致他们不能看清完整的屏幕或者使用触屏。也包括了无法很好接收到语音信息和提示的听力能力比较弱的用户。

Android提供了Accessibility功能和服务来帮助这些用户更简单的操作折欸，包括文字转语音(这个不支持中文)，触觉反馈、手势操作、轨迹球和手柄操作。

。Accesssibility叫做Android中的钩子。在Windows中，钩子的含义就是能够监听到一切你想监听的内容，而Android中的AccessibilityService也可以监听到我们需要的某些功能。AccessibilityService是一个辅助类，可以监听我们手机的焦点、窗口变化、按钮点击等。实现它的服务需要在手机设置里->辅助功能在这里边找到自己实现的辅助类，然后打开它就可以进行我们一些列的监听了。

辅助服务的声明周期只能被系统管理，启动或者停止这个服务必须由明确的用户通过启用或停用设备的设定

# 2 AccessibilityService的用法和理解
AccessiblityService的功能其实就可以概括为两句话：
- 寻找我们想要的View节点
- 模拟点击，实现特定的功能

我们知道Android的View体系是一个树形结构，每一个View就是一个结点，所以我们可以找到指定的结点。那么我们该如何查找我们想要的结点呢？这里首先看一下辅助功能AccessibilityService的用法。

### 1 自定义一个继承AccessibilityService的Service
我们继承实现了一个Service，当然service要在AndroidManifest.xml中声明：

> 需要注意的地方

> 1 这个服务需要注明一个权限
```
android:permission="android.permission.BIND_ACCESSIBILITY_SERVICE" 
```

当然还有一个meta-data声明，这个声明是对AccessibilityService的一些配置，我们看一下这个配置文件的内容。
```
<?xml version="1.0" encoding="utf-8"?>  
<accessibility-service xmlns:android="http://schemas.android.com/apk/res/android"  
    android:accessibilityEventTypes="typeNotificationStateChanged|typeWindowStateChanged"  
    android:accessibilityFeedbackType="feedbackGeneric"  
    android:accessibilityFlags="flagDefault"  
    android:canRetrieveWindowContent="true"  
    android:description="@string/desc"  
    android:notificationTimeout="100"  
    android:packageNames="com.tencent.mm" />  
```

我们以此介绍一下这些常用的配置

```
 android:accessibilityEventTypes="typeNotificationStateChanged|typeWindowStateChanged"  
```
指定时间的类型，即辅助服务响应的事件。这些事件是在AccessibilityEvent类中定义的。我们看一下这个累的源码
```JAVA
@Deprecated
public static final int MAX_TEXT_LENGTH = 500;

/**
 * Represents the event of clicking on a {@link android.view.View} like
 * {@link android.widget.Button}, {@link android.widget.CompoundButton}, etc.
 */
public static final int TYPE_VIEW_CLICKED = 0x00000001;

/**
 * Represents the event of long clicking on a {@link android.view.View} like
 * {@link android.widget.Button}, {@link android.widget.CompoundButton}, etc.
 */
public static final int TYPE_VIEW_LONG_CLICKED = 0x00000002;

/**
 * Represents the event of selecting an item usually in the context of an
 * {@link android.widget.AdapterView}.
 */
public static final int TYPE_VIEW_SELECTED = 0x00000004;

/**
 * Represents the event of setting input focus of a {@link android.view.View}.
 */
public static final int TYPE_VIEW_FOCUSED = 0x00000008;

/**
 * Represents the event of changing the text of an {@link android.widget.EditText}.
 */
public static final int TYPE_VIEW_TEXT_CHANGED = 0x00000010;

/**
 * Represents the event of opening a {@link android.widget.PopupWindow},
 * {@link android.view.Menu}, {@link android.app.Dialog}, etc.
 */
public static final int TYPE_WINDOW_STATE_CHANGED = 0x00000020;

/**
 * Represents the event showing a {@link android.app.Notification}.
 */
public static final int TYPE_NOTIFICATION_STATE_CHANGED = 0x00000040;

/**
 * Represents the event of a hover enter over a {@link android.view.View}.
 */
public static final int TYPE_VIEW_HOVER_ENTER = 0x00000080;

/**
 * Represents the event of a hover exit over a {@link android.view.View}.
 */
public static final int TYPE_VIEW_HOVER_EXIT = 0x00000100;

/**
 * Represents the event of starting a touch exploration gesture.
 */
public static final int TYPE_TOUCH_EXPLORATION_GESTURE_START = 0x00000200;

/**
 * Represents the event of ending a touch exploration gesture.
 */
public static final int TYPE_TOUCH_EXPLORATION_GESTURE_END = 0x00000400;

/**
 * Represents the event of changing the content of a window and more
 * specifically the sub-tree rooted at the event's source.
 */
public static final int TYPE_WINDOW_CONTENT_CHANGED = 0x00000800;

/**
 * Represents the event of scrolling a view.
 */
public static final int TYPE_VIEW_SCROLLED = 0x00001000;

/**
 * Represents the event of changing the selection in an {@link android.widget.EditText}.
 */
public static final int TYPE_VIEW_TEXT_SELECTION_CHANGED = 0x00002000;

/**
 * Represents the event of an application making an announcement.
 */
public static final int TYPE_ANNOUNCEMENT = 0x00004000;

/**
 * Represents the event of gaining accessibility focus.
 */
public static final int TYPE_VIEW_ACCESSIBILITY_FOCUSED = 0x00008000;

/**
 * Represents the event of clearing accessibility focus.
 */
public static final int TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED = 0x00010000;

/**
 * Represents the event of traversing the text of a view at a given movement granularity.
 */
public static final int TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY = 0x00020000;

/**
 * Represents the event of beginning gesture detection.
 */
public static final int TYPE_GESTURE_DETECTION_START = 0x00040000;

/**
 * Represents the event of ending gesture detection.
 */
public static final int TYPE_GESTURE_DETECTION_END = 0x00080000;

/**
 * Represents the event of the user starting to touch the screen.
 */
public static final int TYPE_TOUCH_INTERACTION_START = 0x00100000;

/**
 * Represents the event of the user ending to touch the screen.
 */
public static final int TYPE_TOUCH_INTERACTION_END = 0x00200000;

/**
 * Change type for {@link #TYPE_WINDOW_CONTENT_CHANGED} event:
 * The type of change is not defined.
 */
public static final int CONTENT_CHANGE_TYPE_UNDEFINED = 0x00000000;

/**
 * Change type for {@link #TYPE_WINDOW_CONTENT_CHANGED} event:
 * A node in the subtree rooted at the source node was added or removed.
 */
public static final int CONTENT_CHANGE_TYPE_SUBTREE = 0x00000001;

/**
 * Change type for {@link #TYPE_WINDOW_CONTENT_CHANGED} event:
 * The node's text changed.
 */
public static final int CONTENT_CHANGE_TYPE_TEXT = 0x00000002;

/**
 * Change type for {@link #TYPE_WINDOW_CONTENT_CHANGED} event:
 * The node's content description changed.
 */
public static final int CONTENT_CHANGE_TYPE_CONTENT_DESCRIPTION = 0x00000004;
```
比如在刚在的配置中，我们选择响应通知栏事件和窗口状态改变事件。如果要响应所有的类型，可以这样设置
```JAVA
android:accessibilityEventTypes="typeAllMask"
```
```
android:accessibilityFeedbackType="feedbackSpoken"
```
设置回馈给用户的方式，有语音播出和振动。可以配置一些TTS引擎，让它实现发音。
```
android:notificationTimeout="100"
```
响应时间的设置就不用多说了
```
android:packageNames="com.example.android.apis"
```

可以指定响应某个应用的事件，这里因为要响应所有应用的事件，所以不填，默认就是响应所有应用的事件。比如我们写一个微信抢红包的辅助程序，就可以在这里填写微信的包名，便可以监听微信产生的事件了。
#### 2 在onAccessibilityEvent方法中监听指定的事件
比如我们要监听通知栏消息的事件
```
    @Override  
    public void onAccessibilityEvent(AccessibilityEvent event) {  
        int eventType = event.getEventType();  
        switch (eventType) {  
        case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:  
            //.......  
        }  
}  
```
#### 3 查找我们想要处理的结点View
系统提供了两哥方法来让我们查找想要的结点
- 通过结点View的Text内容来查找
```
findAccessibilityNodeInfosByText("查找的内容")
```
返回的结果是List<AccessibilityNodeInfo>，我们需要for循环来处理
- 通过结点View在布局中的id名称来查找
```
findAccessibilityNodeInfosByViewId("@id/xxx");
```

我们可以DDMS工具里的Dump View Hierarchy For UI Automator 来分析一个UI的结构

虽然返回值也是 List<AccessibilityNodeInfo>， 但其实结果只有一个
### 4 模拟点击事件
我们找到我们想要的View结点之后，调用方法模拟点击：
```
performAction(AccessibilityNodeInfo.ACTION_CLICK)
```
调用这个方法即可，当然这里的参数就是事件的名称和操作， 这里是模拟点击事件，我们当然可以去模拟滚动事件、长按事件等。

我们看一下AccessibilityNodeInfo的介绍， 官方介绍是以这样
>This class represents a node of the window content as well as actions that can be requested from its source.

这个类封装了一些窗口的结点和这个结点的动作。


# 3 使用AccessibilityService类来实现抢红包
首先，先回想一下微信红包的领取流程。
- 1.通知栏出现微信红包的消息
- 2.点击通知栏，进入领取红包界面
- 3.点击领取红包，领取成功后，进入拆红包界面
- 4.拆红包，显示获得金额
- 5.等待下一个红包
# 4 AccessiblityService在应用中使用
其实看一下手机360手机助手就可以看到我们能用AccService实现哪些功能
- 1 静默安装
如果有了辅助功能，我们就可以很容易实现静默安装，如爱奇艺的静默安装。
- 2 关闭后台进程
- 3 后台卸载
- 4 Android自动化测试
