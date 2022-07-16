package com.like.banner.sample

class BannerDataSource {
    private var i = 0

    suspend fun load(): BannerInfo? {
        return getHeaders()
    }

    private fun getHeaders(): BannerInfo? {
        val bannerInfo = BannerInfo()
        when (i++) {
            0 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/c7a55d24-6fc1-4eb5-97c4-ee1ae694d175.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png"))
            }
            1 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall02.sogoucdn.com/image/2019/03/18/20190318094408_4590.png"))
            }
            2 -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2019/05/13/20190513191053_4977.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://mall03.sogoucdn.com/image/2018/12/21/20181221191646_4221.png"))
            }
            3 -> {
            }
            else -> {
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/c7a55d24-6fc1-4eb5-97c4-ee1ae694d175.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/90c6cc12-742e-4c9f-b318-b912f163b8d0.png"))
                bannerInfo.bannerList.add(BannerInfo.Banner("https://www.wanandroid.com/blogimgs/62c1bd68-b5f3-4a3c-a649-7ca8c7dfabe6.png"))
            }
        }
        return bannerInfo
    }
}