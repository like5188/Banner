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
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2'
        implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2'
        implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-rc03'// Activity 或 Fragment 对协程的支持：lifecycleScope

        implementation 'com.github.like5188:Banner:版本号'
    }
```

2、使用
```java
    // ViewPager的使用：直接在布局文件中使用com.like.banner.BannerViewPager。adapter使用com.like.banner.BannerPagerAdapter
    val adapter: BannerPagerAdapter = MyBannerPagerAdapter(this, data)
    mBinding.vp.adapter = adapter

    // 指示器 com.like.banner.indicator.IBannerIndicator 的使用：
    val indicator: IBannerIndicator = StickyRoundRectIndicator(
        context,
        item.bannerList.size,
        binding.indicatorContainer,
        20f,
        10f,
        Color.GRAY,
        listOf(
            Color.parseColor("#ff4a42"),
            Color.parseColor("#fcde64"),
            Color.parseColor("#73e8f4")
        )
    )
    indicator.setIndicatorHeight(6f)
    indicator.setViewPager(binding.vp)// 调用 [setViewPager] 方法，和 [com.like.banner.BannerController] 设置同一个 [com.like.banner.BannerViewPager] 即可。

    // 使用 com.like.banner.BannerController 控制自动播放
    val bannerController = BannerController()
    bannerController.setViewPager(binding.vp)
        .setCycleInterval(3000L)
        .play()
```