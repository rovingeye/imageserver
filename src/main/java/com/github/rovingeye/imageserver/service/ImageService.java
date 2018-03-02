/*
 * Copyright 2017 Mark Hendriks <markhendriks85@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.rovingeye.imageserver.service;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

@Slf4j
@Service
public class ImageService {

    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private final String directory;
    private final String defaultImage;

    @Autowired
    public ImageService(@Value("${imageserver.directory}") final String directory, @Value("${imageserver.default}") final String defaultImage) {
        this.directory = directory;
        this.defaultImage = defaultImage;
    }

    @Cacheable("images")
    public byte[] getResizedImage(final String imageName, final int width, final int height, final boolean forceResize) {
        log.info("Actual method call!");
        final File imageFile = getImageFromDisk(imageName);
        final BufferedImage resizedImage;


        resizedImage = getResizedImage(imageFile, width, height, forceResize);

        if (isNotNull(resizedImage)) {
            return convertBufferedImageToByteArray(resizedImage);
        }
        return EMPTY_BYTE_ARRAY;
    }

    private BufferedImage getResizedImage(final File imageFile, final int width, final int height, final boolean forceResize) {
        return forceResize ? forcedResizeImage(imageFile, width, height) : resizeImage(imageFile, width, height);
    }

    private BufferedImage resizeImage(final File imageFile, final int width, final int height) {
        try {
            return Thumbnails.of(imageFile).size(width, height).asBufferedImage();
        } catch (final IOException e) {
            log.warn("Something went wrong while resizing the image.", e);
            return null;
        }
    }

    private BufferedImage forcedResizeImage(final File imageFile, final int width, final int height) {
        try {
            return Thumbnails.of(imageFile).forceSize(width, height).asBufferedImage();
        } catch (final IOException e) {
            log.warn("Something went wrong while resizing the image.", e);
            return null;
        }
    }

    private byte[] convertBufferedImageToByteArray(final BufferedImage resizedImage) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "jpg", out);
            out.flush();
        } catch (final IOException e) {
            log.error("Could not convert image to a byte array.", e);
            return EMPTY_BYTE_ARRAY;
        }
        return out.toByteArray();
    }

    private boolean isNotNull(final Object resizedImage) {
        return !isNull(resizedImage);
    }

    private File getImageFromDisk(final String imageName) {
        return Files.exists(Paths.get(this.directory, imageName)) ? Paths.get(this.directory, imageName).toFile() : getDefaultImageFile();
    }

    private File getDefaultImageFile() {
        return Paths.get(this.directory, this.defaultImage).toFile();
    }
}