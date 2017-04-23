# 前言
想做的有很多，奈何能力实在有限，所以只能一步一步来，将自己做出来的尽量用简单易懂的语言描述出来，希望自己总结的对阅读这篇文章的同学有所帮助。

在上一篇文章中讲述了怎样在GLSurfaceView上预览Camera的视频数据，在本章中打算实现一个类似微信视频通话的效果，微信视频通话主要有大小两个视频数据渲染（自己的视频和对端的视频），手指点击小视频，可以切换大视频和小视频的位置，可以拖动小视频。

# 第一章 渲染多个视频流数据

第一次看到这个功能，大部分人的第一个解决方案，就是创建多个View，每个View渲染一条视频数据，这样是可行的，但是如果是多人视频呢？20个人就需要创建20个`GLSurfaceView`，这样显然是不可行的，所以最好的办法就是将所有的数据流都绘制在同一个`GLSurfaceView`上，这样只需要控制OpenGL来控制视频绘制的大小的位置就可以解决，这样很大程度上节省了内存，提升了效率。

首先看一下实际的效果图：

<center>
[这里写图片描述](http://img.blog.csdn.net/20170423120051566?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
<center>
[这里写图片描述](http://img.blog.csdn.net/20170423120103346?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>


可以看到小视频是叠加在大视频上面的，虽然看上去好像是两个view，但是其实所有的图像都是绘制在同一个GLSurfaceView上的，我们需要做的就是计算出每个小图缩放的比例，然后计算出每个小图摆放的位置，视频OpengGL的一些方法将视频渲染的位置绘制到相应的位置上。

首先，在函数`onDrawFrame`中绘制出所需要绘制的视频数据。
```java
 @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub
        LOG.logI("onDrawFrame...");
        // 设置白色为清屏
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        // 清除屏幕和深度缓存
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        // 更新纹理
        mSurface.updateTexImage();

        // mDirectDrawers中有两个对象，一个是绘制Camera传递过来的数据，一个是绘制由bitmap转换成的纹理
        for (int i = 0; i < mDirectDrawers.size(); i++) {
            DirectDrawer directDrawer = mDirectDrawers.get(i);
            if (i == 0) {
                directDrawer.resetMatrix();
            } else {
                directDrawer.calculateMatrix(mThumbnailRect, mScreenWidth, mScreenHeight);
            }
            directDrawer.draw();
        }
    }
```

从上面的代码可以看出，`directDrawer.draw()`调用了两次，也就是说OpenGL在这个GLSurfaceView上绘制了两次，但是如果不做处理的话，第二个视频渲染会覆盖第一个效果。这里我们需要对第二个视频流做一些处理：

* **缩小**：如下面的代码所示，我们将视频转换的矩阵存储在一个16位的数组中，即mMVP，我们需要在每次计算之前调用`setIdentityM()`，这行代码的意思是将数据初始化到开始的位置和大小，因为每次缩小都是相对于初始的状态，接下来我们计算x轴和y轴的缩小比例，这里我定义的是缩小1/4，然后调用scaleM就可以得到缩小后的比例，大概的过程如下图所示：
```java
		Matrix.setIdentityM(mMVP, 0);
        float scaleX = 1f / 4f;
        float scaleY = 1f / 4f;
        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);
```
<center>
[这里写图片描述](http://img.blog.csdn.net/20170423123032565?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>
可以从上图中看到，虚线为原来图像的大小，经过缩小后变为实线矩形的大小

* **移动**：小视频的初始化位置是左下方，所以需要将缩小后的视频移动到左下方，代码如下：
```java
float ratioX = (rectF.left - .5f * (1 - scaleX) * screenWidth) / rectF.width();
float ratioY = (rectF.top - .5f * (1 + scaleY) * screenHeight) / rectF.height();
Matrix.translateM(mMVP, 0, ratioX * 2, ratioY * 2, 0f);
```
大致的过程如下图：
<center>
[这里写图片描述](http://img.blog.csdn.net/20170423123055841?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvYTI5Njc3NzUxMw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
</center>

至此，在同一个GLSurfaceView上绘制两个视频数据，并且将第二个视频缩小和移动的过程就叙述完了，由于上面是将缩小和移动分开来讲，其实缩小和移动的代码是在一起的：
```java
 public void calculateMatrix(RectF rectF, float screenWidth, float screenHeight) {
        Matrix.setIdentityM(mMVP, 0);
        float scaleX = 1f / 4f;
        float scaleY = 1f / 4f;
        float ratioX = (rectF.left - .5f * (1 - scaleX) * screenWidth) / rectF.width();
        float ratioY = (rectF.top - .5f * (1 + scaleY) * screenHeight) / rectF.height();
        Matrix.scaleM(mMVP, 0, scaleX, scaleY, 0);
        Matrix.translateM(mMVP, 0, ratioX * 2, ratioY * 2, 0f);
    }
```

# 第二章 滑动视频
移动小视频还是比较简单的，上一章节已经叙述了根据小视频的位置(Rect)，来对小视频进行缩小和移动，所以我们只需要根据手机滑动来改变小视频的位置即可。下面给出移动视频的主要代码。
```java
 @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                if (mDownX > mThumbnailRect.left && mDownX < mThumbnailRect.right
                        && mDownY > mThumbnailRect.bottom && mDownY < mThumbnailRect.top) {
                    mTouchThumbnail = true;
                    mLastYLength = 0;
                    mLastXLength = 0;
                    return true;
                } else {
                    mTouchThumbnail = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if (mTouchThumbnail) {
                    float lengthX = Math.abs(mDownX - moveX);
                    float lengthY = Math.abs(mDownY - moveY);
                    float length = (float) Math.sqrt(Math.pow(lengthX, 2) + Math.pow(lengthY, 2));
                    if (length > mTouchSlop) {
                        moveView(mThumbnailRect, mDownY - moveY, moveX - mDownX);
                        isMoveThumbnail = true;
                    } else {
                        isMoveThumbnail = false;
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchThumbnail) {
                    mLastYLength = 0;
                    mLastXLength = 0;
                    //抬起手指时，如果不是移动小视频，那么就是点击小视频
                    if (!isMoveThumbnail) {
                        changeThumbnailPosition();
                    }
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
```
1. 判断手指按下的位置是否在小视频的区域中，如果在，则记录按下的X和Y的坐标值，然后将`mTouchThumbnail`置为`true`。
2. 如果手指移动的距离超过Android定义的最小移动距离，则开始改变小视频的位置，否则判断这次触摸事件为点击小视频。
3. 根据X轴移动的距离和Y轴移动的距离改变小视频的位置，然后在OpenGL绘制过程中移动小视频。


# 第三章 创建纹理
因为这里需要实现两个视频数据的渲染，由于现在只能获取摄像头的数据，另一个为了更加直观的显示出效果，这里用一个bitmap的纹理来代替，以后有了其他视频的数据，用相应的纹理代替即可。
```java
public static int loadTexture(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return 0;
        }

        int[] texture = new int[1];

        glGenTextures(1, texture, 0);

        if (texture[0] == 0) {
            return 0;
        }

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        // Configure min/mag filtering, i.e. what scaling method do we use if what we're rendering
        // is smaller or larger than the source image.
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        // Load the bitmap into the bound texture.
        texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        return texture[0];
    }
```
`glTexParameterf(int target, int pname, float param)`函数用来确定如何把图象从纹理图象空间映射到帧缓冲图象空间(如：映射时为了避免多边形上的图像失真，而重新构造纹理图像等)。

## target

 * 目标纹理(target)，必须为`GL_TEXTURE_1D`或`GL_TEXTURE_2D`或这是`GL_TEXTURE_3D`；

## pname

* 过滤器(pname)：`GL_TEXTURE_MAG_FILTER`(纹理放大时), `GL_TEXTURE_MIN_FILTER`(纹理缩小时)
* 环绕方向(pname):`GL_TEXTURE_WRAP_S`, `GL_TEXTURE_WRAP_T`, `GL_TEXTURE_WRAP_R`  分别为x，y，z方向。

## param

* pname为过滤器时的参数：`GL_NEARST`(最邻近的像素),`GL_LINEAR`(线性插值)
* pname为环绕方向时的参数：（以下，n为纹理方向上的纹理的长度）
	* `GL_REPEAT`：相当于忽略掉纹理坐标的整数部分。滤镜为线性时，处于[1/2n，1]与第一个纹理像素融合。处于[0,1/2n]与最后一个像素融合。
	* `GL_MIRRORED_REPEAT`：相当于将纹理坐标1.1变成0.9，达到镜像反射的效果。
	*   `GL_CLAMP`：截取纹理坐标到 [0,1] 。将导致纹理坐标处于[1-1/2n, 1]的像素，在纹理滤镜为线性滤镜时，与border融合，最终纹理坐标为1的像素，将为border和边界像素的中值。
	* `GL_CLAMP_TO_EDGE`：截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合。
	* `GL_CLAMP_TO_BORDER`：截取纹理坐标到[-1/2n,1+1/2n]。将导致纹理坐标处于[1-1/2n,1+1/2n]范围内的像素，在纹理滤镜为线性滤镜时，与border融合，最终纹理坐标为1+1/2n的像素将于border同色。



# 总结
这篇文章讲了实现类似微信视频聊天的三个功能的大体实现思路，和一些基本的知识点的介绍。下面我会给出源代码的下载地址，感兴趣的同学可以相互交流交流。



