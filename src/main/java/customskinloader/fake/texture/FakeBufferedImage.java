package customskinloader.fake.texture;

import java.awt.Color;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class FakeBufferedImage implements FakeImage
{
    private BufferedImage image;
    private Graphics graphics;
    
    public FakeBufferedImage(final int width, final int height) {
        this(new BufferedImage(width, height, 2));
    }
    
    public FakeBufferedImage(final BufferedImage image) {
        this.image = image;
        this.graphics = image.getGraphics();
    }
    
    public BufferedImage getImage() {
        this.graphics.dispose();
        return this.image;
    }
    
    @Override
    public FakeImage createImage(final int width, final int height) {
        return new FakeBufferedImage(width, height);
    }
    
    @Override
    public int getWidth() {
        return this.image.getWidth();
    }
    
    @Override
    public int getHeight() {
        return this.image.getHeight();
    }
    
    @Override
    public int getRGBA(final int x, final int y) {
        return this.image.getRGB(x, y);
    }
    
    @Override
    public void setRGBA(final int x, final int y, final int rgba) {
        this.image.setRGB(x, y, rgba);
    }
    
    @Override
    public void copyImageData(final FakeImage image) {
        if (!(image instanceof FakeBufferedImage)) {
            return;
        }
        final BufferedImage img = ((FakeBufferedImage)image).getImage();
        this.graphics.drawImage(img, 0, 0, null);
    }
    
    @Override
    public void fillArea(final int x0, final int y0, final int width, final int height) {
        this.graphics.setColor(new Color(0, 0, 0, 0));
        this.graphics.fillRect(x0, y0, width, height);
    }
    
    @Override
    public void copyArea(final int x0, final int y0, final int dx, final int dy, final int width, final int height, final boolean reversex, final boolean reversey) {
        final int x = x0 + width;
        final int x2 = x0 + dx;
        final int x3 = x2 + width;
        final int y = y0 + height;
        final int y2 = y0 + dy;
        final int y3 = y2 + height;
        this.graphics.drawImage(this.image, reversex ? x3 : x2, reversey ? y3 : y2, reversex ? x2 : x3, reversey ? y2 : y3, x0, y0, x, y, null);
    }
    
    @Override
    public void close() {
        this.graphics.dispose();
    }
}
