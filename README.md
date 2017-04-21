# ZRefresh
### 仿百度外卖的下拉刷新实现
------
    实现 Android 中ListView, RecycleView, ScrollView 实现百度外卖下拉刷新方式， 支持下拉各种平滑滑动 加载回弹。
    
简单修改刷新头部图片，即可成为自己的刷新框架。

![gif](https://github.com/zetaoWu/ZRefresh/blob/master/imgs/1.gif)

<br>
<br>
### 基本的目录结构：

![menu](https://github.com/zetaoWu/ZRefresh/blob/master/imgs/menu.png)


<br>
<br>

### 基本思路：
    以[RefListhview](https://github.com/zetaoWu/ZRefresh/blob/master/app/src/main/java/com/nick/wzt/likerefresh/list/RefListhview.java)为例,[RefRecycleView](https://github.com/zetaoWu/ZRefresh/blob/master/app/src/main/java/com/nick/wzt/likerefresh/recycle/RefRecycleView.java)，[RefScrollView](https://github.com/zetaoWu/ZRefresh/blob/master/app/src/main/java/com/nick/wzt/likerefresh/scroll/RefScrollView.java)基本差不多。代码均有注释。

  步骤：<br>  
  1. 基本通过分别`继承ListView，RecycleView，ScrollView`，加载头部视图，通过paddingTop(0,-headHeight,0,0)将头部试图隐藏; <br>
  2. 基本的下拉刷新框架(下拉状态，回放状态，刷新状态等.具体移步RefListhview#onTouchEvent())；RefListhview#ondispatchTouchEvent实现刷新中禁止滑动；<br>
  3. anim中动画实现(背景的移动，轮子、太阳旋转等) `线性插值器需要在代码中使用才能生效`，通过值动画去控制ListView的平滑滑动；<br>
 4. Activity实现onRefreshListener，实现onRefresh处理`请求`逻辑。<br>  

<br>
<br>

## 注意点：特殊说明的在各Activity均有备注。


<br>
<br>

### 备注：
  1.anim中动画参考
 ```
     <!--android:fromXDelta="0%p" 从百分之多少的地方开始移动
    android:fromDegrees 起始的角度度数
    android:toDegrees 结束的角度度数，负数表示逆时针，正数表示顺时针。如10圈则比android:fromDegrees大3600即可
    android:pivotX 旋转中心的X坐标
    android:pivotY 旋转中心的Y坐标
    浮点数或是百分比。浮点数表示相对于Object的左边缘，如5; 百分比表示相对于Object的左边缘，如5%; 另一种百分比表示相对于父容器的左边缘，如5%p; 一般设置为50%表示在Object中心
    android:duration 表示从android:fromDegrees转动到android:toDegrees所花费的时间，单位为毫秒。可以用来计算速度。
    android:interpolator表示变化率，但不是运行速度。一个插补属性，可以将动画效果设置为加速，减速，反复，反弹等。默认为开始和结束慢中间快
    android:startOffset 在调用start函数之后等待开始运行的时间，单位为毫秒，若为10，表示10ms后开始运行
    android:repeatCount 重复的次数，默认为0，必须是int，可以为-1表示不停止
    android:repeatMode 重复的模式，默认为restart，即重头开始重新运行，可以为reverse即从结束开始向前重新运行。在android:repeatCount大于0或为infinite时生效
    android:detachWallpaper 表示是否在壁纸上运行
    android:zAdjustment 表示被animated的内容在运行时在z轴上的位置，默认为normal。normal保持内容当前的z轴顺序   top运行时在最顶层显示  bottom运行时在最底层显示
    -->
 ```












