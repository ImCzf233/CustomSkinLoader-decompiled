package customskinloader.fake.texture;

public interface FakeImage
{
    FakeImage createImage(final int p0, final int p1);
    
    int getWidth();
    
    int getHeight();
    
    int getRGBA(final int p0, final int p1);
    
    void setRGBA(final int p0, final int p1, final int p2);
    
    void copyImageData(final FakeImage p0);
    
    void fillArea(final int p0, final int p1, final int p2, final int p3);
    
    void copyArea(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final boolean p6, final boolean p7);
    
    void close();
}
