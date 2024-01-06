/*
 *  Created by Azamov X ㋡ on 1/6/24, 5:03 PM
 *  Copyright (c) 2024 . All rights reserved.
 *  Last modified 1/6/24, 5:03 PM
 *
 *
 */

package com.azamovhudstc.graphqlanilist.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class GlideThumbnailTransformation extends BitmapTransformation {

    private static final int MAX_LINES = 7;
    private static final int MAX_COLUMNS = 7;
    private static final int THUMBNAILS_EACH = 5000; // milliseconds

    private int x;
    private int y;

    public GlideThumbnailTransformation(long position) {
        int square = (int) position / THUMBNAILS_EACH;
        y = square / MAX_LINES;
        x = square % MAX_COLUMNS;
    }

    private int getX() {
        return x;
    }

    private int getY() {
        return y;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform,
                               int outWidth, int outHeight) {
        int width = toTransform.getWidth() / MAX_COLUMNS;
        int height = toTransform.getHeight() / MAX_LINES;
        return Bitmap.createBitmap(toTransform, x * width, y * height, width, height);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        byte[] data = ByteBuffer.allocate(8).putInt(x).putInt(y).array();
        messageDigest.update(data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GlideThumbnailTransformation that = (GlideThumbnailTransformation) o;

        if (x != that.x) return false;
        return y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}