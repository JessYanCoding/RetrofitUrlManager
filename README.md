# RetrofitUrlManager
[ ![Jcenter](https://img.shields.io/badge/Jcenter-v1.4.0-brightgreen.svg?style=flat-square) ](https://bintray.com/jessyancoding/maven/retrofit-url-manager/1.4.0/link)
[ ![Build Status](https://travis-ci.org/JessYanCoding/RetrofitUrlManager.svg?branch=master) ](https://travis-ci.org/JessYanCoding/RetrofitUrlManager)
[ ![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-RetrofitUrlManager-brightgreen.svg?style=flat-square) ](https://android-arsenal.com/details/1/6007)
[ ![API](https://img.shields.io/badge/API-9%2B-blue.svg?style=flat-square) ](https://developer.android.com/about/versions/android-2.3.html)
[ ![License](http://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square) ](http://www.apache.org/licenses/LICENSE-2.0)
[ ![Author](https://img.shields.io/badge/Author-JessYan-orange.svg?style=flat-square) ](https://www.jianshu.com/u/1d0c0bc634db)
[ ![QQ-Group](https://img.shields.io/badge/QQ%E7%BE%A4-455850365%20%7C%20301733278-orange.svg?style=flat-square) ](https://shang.qq.com/wpa/qunwpa?idkey=7e59e59145e6c7c68932ace10f52790636451f01d1ecadb6a652b1df234df753)

## Let Retrofit support multiple baseUrl and can be change the baseUrl at runtime.

[中文说明](README-zh.md)

## Overview
![overview](art/overview.gif)

## Notice
[**Framework analysis 1**](http://www.jianshu.com/p/2919bdb8d09a)

[**Framework analysis 2**](https://www.jianshu.com/p/35a8959c2f86)

[**More complete sample**](https://github.com/JessYanCoding/ArmsComponent)

## Download
``` gradle
 implementation 'me.jessyan:retrofit-url-manager:1.4.0'
```

## Usage
### Initialize
``` java
 // When building OkHttpClient, the OkHttpClient.Builder() is passed to the with() method to initialize the configuration
 OkHttpClient = RetrofitUrlManager.getInstance().with(new OkHttpClient.Builder())
                .build();
```

### Step 1
``` java
 public interface ApiService {
     @Headers({"Domain-Name: douban"}) // Add the Domain-Name header
     @GET("/v2/book/{id}")
     Observable<ResponseBody> getBook(@Path("id") int id);
}

```

### Step 2
``` java
 // You can change BaseUrl at any time while App is running (The interface that declared the Domain-Name header)
 RetrofitUrlManager.getInstance().putDomain("douban", "https://api.douban.com");
```

### If you want to change the global BaseUrl:
```java
 // BaseUrl configured in the Domain-Name header will override BaseUrl in the global setting
 RetrofitUrlManager.getInstance().setGlobalDomain("your BaseUrl");

```

## About Me
* **Email**: <jess.yan.effort@gmail.com>
* **Home**: <http://jessyan.me>
* **掘金**: <https://juejin.im/user/57a9dbd9165abd0061714613>
* **简书**: <https://www.jianshu.com/u/1d0c0bc634db>

## License
```
 Copyright 2017, jessyan

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
