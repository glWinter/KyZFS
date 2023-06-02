package com.zto56.kyzfs

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.huantansheng.easyphotos.engine.ImageEngine

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 15:02
 */
object ImageLoadEngine :ImageEngine{

    override fun loadPhoto(context: Context, uri: Uri, imageView: ImageView) {
        Glide.with(context).load(uri).transition(DrawableTransitionOptions.withCrossFade())
            .into(imageView)
    }

    override fun loadGifAsBitmap(context: Context, gifUri: Uri, imageView: ImageView){
        Glide.with(context).asBitmap().load(gifUri).into(imageView)
    }

    override fun loadGif(context: Context, gifUri: Uri, imageView: ImageView) {
        Glide.with(context).asGif().load(gifUri)
            .transition(DrawableTransitionOptions.withCrossFade()).into(imageView)
    }

    override fun getCacheBitmap(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {
        return Glide.with(context).asBitmap().load(uri).submit(width, height).get()
    }

}