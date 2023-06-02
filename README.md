# KyZfs

[![](https://jitpack.io/v/glWinter/KyZfs.svg)](https://jitpack.io/#glWinter/KyZfs)

### 引入kyzfs

gradle7.0以下
在项目的build.gradle中加入以下代码
```
allprojects{
  repositories {
     maven { url 'https://jitpack.io' }
  }   
}
```

gradle7.0以上
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
         maven { url 'https://jitpack.io' }
    }
}
```

在模块的build.gradle中加入以下代码
```
implementation 'com.github.glWinter:KyZfs:last-version'
```

### 使用kyzfs
#### kotlin使用
```
KyZFS.build {
   setAuthorization("bearer ***")
   setRefreshToken("abcdefg")
   setChooseVideo(false)
   setContext(this)
}.start(object : KyZFSCb{
     override fun success(list: List<JSONObject>) {
       //list 可以直接传给RN
     }
})
```

#### java使用
```
KyZFS zfs = new KyZFS.Builder()
  .setAuthorization("bearer ***")
  .setRefreshToken("abcdefg")
  .setChooseVideo(false)
  .setContext(this)
  .build();
zfs.start(list -> {
  //list 可以直接传给RN
});
```

### 参数介绍

| 参数名称     | 含义 |
| ----------- | ----------- |
| activity      | 当前上下文(需继承FragmentActivity)       |
| maxSize   | 选择文件的最大大小 KB      |
| selectedSize      | 已选择文件的大小 KB       |
| type   | 功能选择 0相机 1相册 2文件        |
| chooseVideo      | 相册是否显示视频       |
| authorization   | authorization       |
| refreshToken      | refreshToken       |
| url   | 获取uploadToken的url        |
| waterMaker      | 水印内容       |
| isShowWaterMaker   | 是否添加水印        |
| isPrivate   | zfs文件是否是私有模式        |
