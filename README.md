# LibraryPlus
## 借阅查询
用户可以查询借阅的图书
## 书刊查询
用户可以搜索图书馆中的书籍并查看详情信息
## 数据收藏
收藏自己喜欢的数据便于日后借阅
## 美文阅读
系统每天会随机推荐20篇优美文章
## 小图机器人
接入Tuling机器人api，封装了聊天，新闻，美食，菜谱，笑话等功能

# 问题解决

## genymotion模拟器中文输入法

> 直接搜索x86架构的输入法安装

## 爬虫-从网上爬取小说

1. 分析网页格式
2. 获取网页
3. 获取所需内容
4. 保存内容

## 数据库乱码

现象

- 浏览器不乱码
- 控制台一部分乱码
- 数据库不乱码

解决

```
catalog.setId(result.getLong(1));
// 用utf-8转为iso8859-1
catalog.setTitle(new String(result.getString(2).getBytes("utf-8"), "iso8859-1"));
catalog.setAuthor(new String(result.getString(3).getBytes("utf-8"), "iso8859-1"));
catalog.setTime(new String(result.getString(4).getBytes("utf-8"), "iso8859-1"));
catalog.setTag(new String(result.getString(5).getBytes("utf-8"), "iso8859-1"));
// 这个不用转码就能正常显示
catalog.setSummary(result.getString(6));
```

同在一张数据库表，字符编码有两种？但是在数据中中显示却正常。

## mysql连接维持时间

### Connection单例的错误

```
public static Connection getConnection() {
	if (conn == null) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("找不到驱动程序类 ，加载驱动失败！");
			e.printStackTrace();
		}

		// 连接MySql数据库，用户名和密码都是root
		String url = "jdbc:mysql://***.**.***.**:3306/library?useUnicode=true&characterEncoding=UTF-8";
		String username = "root";
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException se) {
			System.out.println("数据库连接失败！");
			se.printStackTrace();
		}
	}
	return conn;
}
```

> 连接使用单例模式会发现一段时间后不能请求到数据

### mysql连接时长

MySQL如果长连接而长期没有对数据库进行任何操作，在timeout值后，mysql server就会关闭此链接，程序执行查询的时候就会得到一个类似于“MYSQL server has gone away”这样的错误。

在MYSQL的默认设置中，如果一个数据库连接超过8小时没有使用(闲置8小时)，服务器将断开这条连接，后续在该连接上进行的查询操作都将失败。

> 上面的单例Connetcion对象虽然存在但是连接已不可用

## jdbc批量导入到mysql的效率

要求：导入用户数据27万条，书籍信息27万条，评分记录110万条

### 方法一

简单的statement或preparedStatement单条导入

> 效率：没敢试

###   方法二

使用批量处理方法

```
 conn.setAutoCommit(false);

// sql语句形式
String sql = "INSERT adlogs(ip,website,yyyymmdd,hour,object_id) VALUES(?,?,?,?,?)";
PreparedStatement prest = conn.prepareStatement(sql);
for(int x = 0; x < size; x++){
 prest.setString(1, "192.168.1.1");
 prest.setString(2, "localhost");
 prest.setString(3, "20081009");
 prest.setInt(4, 8);
 prest.setString(5, "11111111");
 prest.addBatch();
}
prest.executeBatch();
conn.commit();
conn.close();
```

> 这种方法也是很慢，特别慢

> 需要注意的是sql语句的形式 "INSERT adlogs(ip,website,yyyymmdd,hour,object_id) VALUES(?,?,?,?,?)"

### 方法三

```

// sql语句形式
String prefix = "INSERT INTO rating(userid, isbn, rating) VALUES ";

while ((line = bufferedReader.readLine()) != null) {


  ...
  ...
  ...


    stringBuffer.append("(" + userid + ", '" + isbn + "', " + rating + "),");

    i++;

    if (i % 10000 == 0) {
        String sql = prefix + stringBuffer.substring(0, stringBuffer.length() - 1);
        preparedStatement.addBatch(sql);
        preparedStatement.executeBatch();
        connection.commit();
        stringBuffer = new StringBuffer();
    }
}
```

> 快！！

#### 总结

方法二采用的是 insert into tb (...) values (...); insert into tb (...) values (...);的方式

方法三采用的是 insert into tb (...) values(...),(...)...;的方式

速度差别很大

## 隐藏软键盘

- EditText调用代码隐藏软键盘

```
InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
```

- SearchView失去焦点隐藏软键盘

```
mSearchView.clearFocus();
```

#### 软键盘弹出时页面向上推移

在menifest文件中的activity中添加
```
<activity
    android:name=".MainActivity"
    android:windowSoftInputMode="adjustResize" />
```

> **注意：** 这种情况下activity不能设置为全屏， 1、不能再代码中设置， 2、不能使用全屏主题。否则失效，还是会推移页面。


## 添加ripple效果

### 普通控件添加ripple效果

1. 定义ripple文件

```
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/menuRippleColor">
    <!-- 未点击时的状态 -->
    <item android:drawable="@color/menuBackgroundColor" />
</ripple>
```

2. 添加到控件android:background="@drawable/btn_ripple"

```
<Button
android:id="@+id/robot_send_button"
android:layout_width="70dp"
android:layout_height="match_parent"
android:background="@drawable/btn_ripple"
android:textColor="@android:color/white" />
```


### ListView添加ripple效果

1. 定义ripple文件

```
<?xml version="1.0" encoding="utf-8"?>
<ripple xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="@color/menuRippleColor">

    <!-- 未点击时的状态 -->
    <item
        android:id="@android:id/mask"
        android:drawable="@color/menuBackgroundColor" />
</ripple>
```

> **注意:** 必须为item添加id，id必须为 @android:id/mask ，否则点击后item中的内容会消失

2. 定义ListItem Selector文件

```
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@android:color/white" />
</selector>
```
> 在selector中只需要指定默认颜色，如果添加了pressed=“true”时的item，会造成点击时有两种颜色出现

3. 把上面定义的listitem selector添加到listitem布局中

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/search_book_list_item_normal_sel"
    android:orientation="vertical"
    android:paddingBottom="10dp"
    android:paddingTop="10dp">

    ...
    ...

</LinearLayout>
```

4. 设置ListView

```
<android.support.v7.widget.ListViewCompat
    android:id="@+id/search_book_list"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:divider="#eee"
    android:dividerHeight="7dp"

    // 1
    android:drawSelectorOnTop="true"
    // 2
    android:listSelector="@drawable/search_book_list_selector_ripple" />
```

> **注意：** 添加drawSelectorOnTop和listSelector，缺一不可


## Dialog

### 自定义Dialog

- 继承Dialog (目前好像是推荐使用DialogFragment)
- 重写onCreate方法

### Dialog中的方法

- show(); // 先执行show方法再执行onCreate方法
- dismiss();
- cancel();
> dismiss()和cancel()的区别：cancel()内部调用了dismiss()，使用dismiss可以回调setOnCancelListener中的方法。

```
@Override
public void cancel() {
    if (!mCanceled && mCancelMessage != null) {
        mCanceled = true;
        // Obtain a new message so this dialog can be re-used
        Message.obtain(mCancelMessage).sendToTarget();
    }
    dismiss();
}
```

### Dialog中的事件

- onBackPressed();

> 当一个dialog显示的时候会注册onBackPressed()事件，当dismiss()方法调用时会取消监听


## 动画Circular Reveal （圆形揭示）

> android5提供的新动画，在较低的版本调用会报错.

### 为普通view添加动画

```
mAnimator = ViewAnimationUtils.createCircularReveal(
    view,
    (int) mPoint.x,
    (int) mPoint.y,
    0,
    mRect.height());
mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
mAnimator.setDuration(500);
mAnimator.start();
```

```
public static Animator createCircularReveal(View view,
        int centerX,  int centerY, float startRadius, float endRadius)

view 操作的视图
centerX 动画开始的中心点X
centerY 动画开始的中心点Y
startRadius 动画开始半径
startRadius 动画结束半径
```

### Dialog使用动画

#### 显示动画

在onCreate中添加

```
// 设置动画
view.post(new Runnable() {
    @Override
    public void run() {
        mDialogView = view;
        // 设置动画
        mAnimator = ViewAnimationUtils.createCircularReveal(view, (int) mPoint.x, (int) mPoint.y, 0, mRect.height());
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(500);
        mAnimator.start();
    }
});
```

#### 隐藏动画

覆盖Dialog的onBackPressed方法

```
@Override
public void onBackPressed() {
    final Dialog dialog = this;

    if (this.isShowing()) {
        mAnimator = ViewAnimationUtils.createCircularReveal(mDialogView, (int) mPoint.x, (int) mPoint.y, mRect.height(), 0);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(400);
        mAnimator.start();

        // 添加动画监听器
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 注意this指针的指向
                dialog.dismiss();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
```

> **注意：** 必须等动画执行完再调用dismiss()方法，否则view被销毁，报错。


## 双击退出

```
long time0 = 0;
...
...
...
long time = System.currentTimeMillis();
if (time - time0 > 2000) {
    ToastUtil.showMsg(this, "再按一次退出程序");
    time0 = time;
} else {
    finish();
}
```

## EditText和自定义软键盘Enter键图标

EditTextLine的singleLine属性被废弃，推荐使用maxLines

**注意：** 单独使用maxLines无效，需要加上inputType属性

### 自定义enter图标

```
<EditText
    android:id="@+id/text"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:inputType="text"
    android:maxLines="1"
    android:imeOptions="actionSearch" />
```

#### imeOptions

- actionUnspecified：默认值，根据设备的情况而定
- actionGo：go
- actionSearch：search图标
- actionSend：显示send
- actionNext：显示next
- actionDone：显示done
- ...

**注意：** 只是用imeOptions属性无效，需要singleLine配合

上面说到singleLine废弃，**但是，** 只使用maxLinex配合任然无效，还需要加上inputType

## FloatingActionButton（悬浮按钮）设置出错

##### 想要更改fab的背景颜色

**错误方法**

```
// 报错
app:backgroundTint="@android:color/white"
```
或

```
// 无效
android:background="@android:color/white"
```

**两者缺一不可**

```
app:backgroundTint="@android:color/white"
android:background="@android:color/white"
```

##### 修改fab ripple颜色

```
app:rippleColor="@color/colorPrimary"
```

## View.setVisible(View.GONE)或View.setVisible(View.INVISIBLE)失效

##### 可能是view上正在执行动画

调用

```
view.clearAnimation();
```
后再调用setVisible

## 网址

[豆瓣图书api](https://developers.douban.com/wiki/?title=book_v2)

[图灵机器人api](http://www.tuling123.com/help/h_cent_webapi.jhtml?nav=doc)

[慕课网-Android智能机器人“小慕”的实现](http://www.imooc.com/learn/217)