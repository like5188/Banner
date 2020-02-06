#### 最新版本

模块|Banner
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/Banner.svg)](https://jitpack.io/#like5188/Banner)

## 功能介绍
1、封装了banner功能。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188:Banner:版本号'
    }
```

2、使用
```java
    ①在布局文件中添加：com.like.banner.BannerViewPager。
        <com.like.banner.BannerViewPager
            android:id="@+id/vp"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginLeft="20dp"
            android:layout_marginRight="20dp"
            android:clipChildren="false"
            app:auto_start="false"
            app:cycle_interval="3000"
            app:height_width_ratio="0.4" />

    ②在代码中设置：
        binding.vp.setScrollSpeed()
        binding.vp.adapter = MyBannerPagerAdapter(context, item.bannerList)
        binding.vp.pageMargin = DimensionUtils.dp2px(context, 10f)
        binding.vp.setPageTransformer(true, CascadingPageTransformer())

        val indicator: ImageIndicator = createBannerIndicator(item.bannerList.size, binding.indicatorContainer)
        indicator.init(6f)
        binding.vp.setBannerIndicator(indicator)

        binding.vp.play()
```