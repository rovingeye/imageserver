package com.github.rovingeye.imageserver.rest;

import com.github.rovingeye.imageserver.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(final ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/image/{width}/{height}/{imageName:.+}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getResizedImage(@PathVariable("width") final int width,
                                                  @PathVariable("height") final int height,
                                                  @PathVariable("imageName") final String imageName,
                                                  final HttpServletResponse response) {
        final byte[] image = this.imageService.getResizedImage(imageName, width, height, false);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        return ResponseEntity.ok(image);
    }

    @GetMapping("/image/forced/{width}/{height}/{imageName:.+}")
    public ResponseEntity<byte[]> getForcedResizedImage(@PathVariable("width") final int width,
                                                        @PathVariable("height") final int height,
                                                        @PathVariable("imageName") final String imageName,
                                                        final HttpServletResponse response) {
        final byte[] image = this.imageService.getResizedImage(imageName, width, height, true);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        return ResponseEntity.ok(image);
    }
}