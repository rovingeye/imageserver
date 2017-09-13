package com.github.rovingeye.imageserver.rest;

import com.github.rovingeye.imageserver.service.ImageService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageControllerTest {

    private static final byte[] BYTES = new byte[42];
    private static final String IMAGE_NAME = "image.jpg";
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final boolean FORCE_RESIZE_FALSE = false;
    private static final boolean FORCE_RESIZE_TRUE = true;
    private ImageController imageController;
    private ImageService imageServiceMock;
    private HttpServletResponse httpServletResponseMock;

    @Before
    public void setUp() throws Exception {
        this.httpServletResponseMock = mock(HttpServletResponse.class);
        this.imageServiceMock = mock(ImageService.class);
        this.imageController = new ImageController(this.imageServiceMock);
    }

    @Test
    public void getResizedImage() throws Exception {
        when(this.imageServiceMock.getResizedImage(IMAGE_NAME, WIDTH, HEIGHT, FORCE_RESIZE_FALSE)).thenReturn(BYTES);

        final ResponseEntity<byte[]> result = this.imageController.getResizedImage(WIDTH, HEIGHT, IMAGE_NAME, this.httpServletResponseMock);

        assertThat(result.getBody(), is(BYTES));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        verify(this.imageServiceMock, times(1)).getResizedImage(IMAGE_NAME, WIDTH, HEIGHT, FORCE_RESIZE_FALSE);
        verify(this.httpServletResponseMock, times(1)).setContentType(MediaType.IMAGE_JPEG_VALUE);
    }

    @Test
    public void getForcedResizedImage() throws Exception {
        when(this.imageServiceMock.getResizedImage(IMAGE_NAME, WIDTH, HEIGHT, FORCE_RESIZE_TRUE)).thenReturn(BYTES);

        final ResponseEntity<byte[]> result = this.imageController.getForcedResizedImage(WIDTH, HEIGHT, IMAGE_NAME, this.httpServletResponseMock);

        assertThat(result.getBody(), is(BYTES));
        assertThat(result.getStatusCode(), is(HttpStatus.OK));

        verify(this.imageServiceMock, times(1)).getResizedImage(IMAGE_NAME, WIDTH, HEIGHT, FORCE_RESIZE_TRUE);
        verify(this.httpServletResponseMock, times(1)).setContentType(MediaType.IMAGE_JPEG_VALUE);
    }

}