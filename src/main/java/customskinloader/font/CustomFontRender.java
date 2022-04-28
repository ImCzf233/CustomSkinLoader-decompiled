package customskinloader.font;

import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class CustomFontRender extends FontRenderer {
    private static final char[] randoms = new char[]{'\u4e00', '\u4e8c', '\u4e09', '\u56db', '\u4e94', '\u516d', '\u4e03', '\u516b', '\u4e5d', '\u5341', '\u554a', '\u5427', '\u624d', '\u7684', '\u997f', '\u98de', '\u4e2a', '\u597d', '\u5c31', '\u770b', '\u4e86', '\u5417', '\u4f60', '\u54e6', '\u5e73', '\u53bb', '\u4eba', '\u662f', '\u4ed6', '\u6211', '\u60f3', '\u4e00', '\u5728', '\u54c7', '\u560e', '\u54c8', '\u4e2a', '\u563b', '\u770b', '\u5566', '\u6728', '\u5e93', '\u7684', '\u5f97', '\u5bb6', '\u62a4', '\u5f20', '\u5efa', '\u4fe9', '\u9524', '\u7cca', '\u8865', '\u52aa', '\u5c2c', '\u5f00'};
    private static final ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
    private int[] colorCode = new int[32];
    private int[] charWidth = new int[256];
    protected float posX;
    protected float posY;
    private boolean unicodeFlag = false;
    private boolean bidiFlag;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean italicStyle;
    private boolean underlineStyle;
    private boolean strikethroughStyle;
    private String text = new String(new char[]{'\u00c0', '\u00c1', '\u00c2', '\u00c8', '\u00ca', '\u00cb', '\u00cd', '\u00d3', '\u00d4', '\u00d5', '\u00da', '\u00df', '\u00e3', '\u00f5', '\u011f', '\u0130', '\u0131', '\u0152', '\u0153', '\u015e', '\u015f', '\u0174', '\u0175', '\u017e', '\u0207', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', '\u0000', ' ', '!', '\"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.', '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<', '=', '>', '?', '@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '[', '\\', ']', '^', '_', '`', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '{', '|', '}', '~', '\u0000', '\u00c7', '\u00fc', '\u00e9', '\u00e2', '\u00e4', '\u00e0', '\u00e5', '\u00e7', '\u00ea', '\u00eb', '\u00e8', '\u00ef', '\u00ee', '\u00ec', '\u00c4', '\u00c5', '\u00c9', '\u00e6', '\u00c6', '\u00f4', '\u00f6', '\u00f2', '\u00fb', '\u00f9', '\u00ff', '\u00d6', '\u00dc', '\u00f8', '\u00a3', '\u00d8', '\u00d7', '\u0192', '\u00e1', '\u00ed', '\u00f3', '\u00fa', '\u00f1', '\u00d1', '\u00aa', '\u00ba', '\u00bf', '\u00ae', '\u00ac', '\u00bd', '\u00bc', '\u00a1', '\u00ab', '\u00bb', '\u2591', '\u2592', '\u2593', '\u2502', '\u2524', '\u2561', '\u2562', '\u2556', '\u2555', '\u2563', '\u2551', '\u2557', '\u255d', '\u255c', '\u255b', '\u2510', '\u2514', '\u2534', '\u252c', '\u251c', '\u2500', '\u253c', '\u255e', '\u255f', '\u255a', '\u2554', '\u2569', '\u2566', '\u2560', '\u2550', '\u256c', '\u2567', '\u2568', '\u2564', '\u2565', '\u2559', '\u2558', '\u2552', '\u2553', '\u256b', '\u256a', '\u2518', '\u250c', '\u2588', '\u2584', '\u258c', '\u2590', '\u2580', '\u03b1', '\u03b2', '\u0393', '\u03c0', '\u03a3', '\u03c3', '\u03bc', '\u03c4', '\u03a6', '\u0398', '\u03a9', '\u03b4', '\u221e', '\u2205', '\u2208', '\u2229', '\u2261', '\u00b1', '\u2265', '\u2264', '\u2320', '\u2321', '\u00f7', '\u2248', '\u00b0', '\u2219', '\u00b7', '\u221a', '\u207f', '\u00b2', '\u25a0', '\u0000'});
    
    public CustomFontRender(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
        super(gameSettingsIn, location, textureManagerIn, unicode);
        if(gameSettingsIn.language != null) {
            this.setUnicodeFlag(Minecraft.getMinecraft().isUnicode());
            this.setBidiFlag(Minecraft.getMinecraft().getLanguageManager().isCurrentLanguageBidirectional());
        }
        
        for(int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;
            if(i == 6) {
                k += 85;
            }
            
            if(gameSettingsIn.anaglyph) {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }
            
            if(i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }
            
            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
        
        this.readGlyphSizes();
    }
    
    public void onResourceManagerReload(IResourceManager p_onResourceManagerReload_1_) {
        this.readFontTexture();
        this.readGlyphSizes();
    }
    
    private void readFontTexture() {
        BufferedImage bufferedimage;
        try {
            bufferedimage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
        } catch (IOException var17) {
            throw new RuntimeException(var17);
        }
        
        int i = bufferedimage.getWidth();
        int j = bufferedimage.getHeight();
        int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        int k = j / 16;
        int l = i / 16;
        byte i1 = 1;
        float f = 8.0F / (float)l;
        
        for(int j1 = 0; j1 < 256; ++j1) {
            int k1 = j1 % 16;
            int l1 = j1 / 16;
            if(j1 == 32) {
                this.charWidth[j1] = 3 + i1;
            }
            
            int i2;
            for(i2 = l - 1; i2 >= 0; --i2) {
                int j2 = k1 * l + i2;
                boolean flag = true;
                
                for(int k2 = 0; k2 < k && flag; ++k2) {
                    int l2 = (l1 * l + k2) * i;
                    if((aint[j2 + l2] >> 24 & 255) != 0) {
                        flag = false;
                    }
                }
                
                if(!flag) {
                    break;
                }
            }
            
            ++i2;
            this.charWidth[j1] = (int)(0.5D + (double)((float)i2 * f)) + i1;
        }
        
    }
    
    private void readGlyphSizes() {
        InputStream inputstream = null;
        
        try {
            inputstream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
            inputstream.read(this.glyphWidth);
        } catch (IOException var6) {
            throw new RuntimeException(var6);
        } finally {
            IOUtils.closeQuietly(inputstream);
        }
        
    }
    
    private float func_181559_a(char p_181559_1_, boolean p_181559_2_) {
        if(p_181559_1_ == 32) {
            return 4.0F;
        } else {
            int i = this.text.indexOf(p_181559_1_);
            return i != -1 && !this.unicodeFlag?this.renderDefaultChar(i, p_181559_2_):this.renderUnicodeChar(p_181559_1_, p_181559_2_);
        }
    }
    
    protected float renderDefaultChar(int p_renderDefaultChar_1_, boolean p_renderDefaultChar_2_) {
        int i = p_renderDefaultChar_1_ % 16 * 8;
        int j = p_renderDefaultChar_1_ / 16 * 8;
        int k = p_renderDefaultChar_2_?1:0;
        this.bindTexture(this.locationFontTexture);
        int l = p_renderDefaultChar_1_ >= this.charWidth.length?6:this.charWidth[p_renderDefaultChar_1_];
        float f = (float)l - 0.01F;
        GL11.glBegin(5);
        GL11.glTexCoord2f((float)i / 128.0F, (float)j / 128.0F);
        GL11.glVertex3f(this.posX + (float)k, this.posY, 0.0F);
        GL11.glTexCoord2f((float)i / 128.0F, ((float)j + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX - (float)k, this.posY + 7.99F, 0.0F);
        GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, (float)j / 128.0F);
        GL11.glVertex3f(this.posX + f - 1.0F + (float)k, this.posY, 0.0F);
        GL11.glTexCoord2f(((float)i + f - 1.0F) / 128.0F, ((float)j + 7.99F) / 128.0F);
        GL11.glVertex3f(this.posX + f - 1.0F - (float)k, this.posY + 7.99F, 0.0F);
        GL11.glEnd();
        return (float)l;
    }
    
    private ResourceLocation getUnicodePageLocation(int p_getUnicodePageLocation_1_) {
        if(unicodePageLocations[p_getUnicodePageLocation_1_] == null) {
            unicodePageLocations[p_getUnicodePageLocation_1_] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[]{Integer.valueOf(p_getUnicodePageLocation_1_)}));
        }
        
        return unicodePageLocations[p_getUnicodePageLocation_1_];
    }
    
    private void loadGlyphTexture(int p_loadGlyphTexture_1_) {
        this.bindTexture(this.getUnicodePageLocation(p_loadGlyphTexture_1_));
    }
    
    protected float renderUnicodeChar(char p_renderUnicodeChar_1_, boolean p_renderUnicodeChar_2_) {
        if(this.glyphWidth[p_renderUnicodeChar_1_] == 0) {
            return 0.0F;
        } else {
            int i = p_renderUnicodeChar_1_ / 256;
            this.loadGlyphTexture(i);
            int j = this.glyphWidth[p_renderUnicodeChar_1_] >>> 4;
            int k = this.glyphWidth[p_renderUnicodeChar_1_] & 15;
            float f = (float)j;
            float f1 = (float)(k + 1);
            float f2 = (float)(p_renderUnicodeChar_1_ % 16 * 16) + f;
            float f3 = (float)((p_renderUnicodeChar_1_ & 255) / 16 * 16);
            float f4 = f1 - f - 0.02F;
            float f5 = p_renderUnicodeChar_2_?1.0F:0.0F;
            GL11.glBegin(5);
            GL11.glTexCoord2f(f2 / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f5, this.posY, 0.0F);
            GL11.glTexCoord2f(f2 / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX - f5, this.posY + 7.99F, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, f3 / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F + f5, this.posY, 0.0F);
            GL11.glTexCoord2f((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F);
            GL11.glVertex3f(this.posX + f4 / 2.0F - f5, this.posY + 7.99F, 0.0F);
            GL11.glEnd();
            return (f1 - f) / 2.0F + 1.0F;
        }
    }
    
    public int drawStringWithShadow(String p_drawStringWithShadow_1_, float p_drawStringWithShadow_2_, float p_drawStringWithShadow_3_, int p_drawStringWithShadow_4_) {
        return this.drawString(p_drawStringWithShadow_1_, p_drawStringWithShadow_2_, p_drawStringWithShadow_3_, p_drawStringWithShadow_4_, true);
    }
    
    public int drawString(String p_drawString_1_, int p_drawString_2_, int p_drawString_3_, int p_drawString_4_) {
        return this.drawString(p_drawString_1_, (float)p_drawString_2_, (float)p_drawString_3_, p_drawString_4_, false);
    }
    
    public int drawString(String p_drawString_1_, float p_drawString_2_, float p_drawString_3_, int p_drawString_4_, boolean p_drawString_5_) {
        this.unicodeFlag = false;
        this.enableAlpha();
        this.resetStyles();
        int i;
        if(p_drawString_5_) {
            i = this.renderString(p_drawString_1_, p_drawString_2_ + 1.0F, p_drawString_3_ + 1.0F, p_drawString_4_, true);
            i = Math.max(i, this.renderString(p_drawString_1_, p_drawString_2_, p_drawString_3_, p_drawString_4_, false));
        } else {
            i = this.renderString(p_drawString_1_, p_drawString_2_, p_drawString_3_, p_drawString_4_, false);
        }
        
        return i;
    }
    
    private String bidiReorder(String p_bidiReorder_1_) {
        try {
            Bidi var3 = new Bidi((new ArabicShaping(8)).shape(p_bidiReorder_1_), 127);
            var3.setReorderingMode(0);
            return var3.writeReordered(2);
        } catch (ArabicShapingException var31) {
            return p_bidiReorder_1_;
        }
    }
    
    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }
    
    private void renderStringAtPos(String p_renderStringAtPos_1_, boolean p_renderStringAtPos_2_) {
        for(int i = 0; i < p_renderStringAtPos_1_.length(); ++i) {
            char c0 = p_renderStringAtPos_1_.charAt(i);
            if(c0 == 167 && i + 7 < p_renderStringAtPos_1_.length() && p_renderStringAtPos_1_.charAt(i + 1) == 35) {
                int var13;
                try {
                    var13 = Integer.parseInt(p_renderStringAtPos_1_.substring(i + 2, i + 8), 16);
                } catch (Exception var12) {
                    var13 = -1;
                }
                
                if(var13 != -1) {
                    int var15 = -16777216 | var13;
                    int var16 = var15 >> 16 & 255;
                    int var17 = var15 >> 8 & 255;
                    int blue = var15 & 255;
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    this.textColor = var13;
                    this.setColor((float)var16 / 255.0F, (float)var17 / 255.0F, (float)blue / 255.0F, this.alpha);
                    i += 7;
                }
            } else {
                int i1;
                int j1;
                if(c0 == 167 && i + 1 < p_renderStringAtPos_1_.length()) {
                    i1 = "0123456789abcdefklmnor".indexOf(p_renderStringAtPos_1_.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if(i1 < 16) {
                        this.randomStyle = false;
                        this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        this.italicStyle = false;
                        if(i1 < 0 || i1 > 15) {
                            i1 = 15;
                        }
                        
                        if(p_renderStringAtPos_2_) {
                            i1 += 16;
                        }
                        
                        j1 = this.colorCode[i1];
                        this.textColor = j1;
                        this.setColor((float)(j1 >> 16) / 255.0F, (float)(j1 >> 8 & 255) / 255.0F, (float)(j1 & 255) / 255.0F, this.alpha);
                    } else if(i1 == 16) {
                        this.randomStyle = true;
                    } else if(i1 == 17) {
                        this.boldStyle = true;
                    } else if(i1 == 18) {
                        this.strikethroughStyle = true;
                    } else if(i1 == 19) {
                        this.underlineStyle = true;
                    } else if(i1 == 20) {
                        this.italicStyle = true;
                    } else {
                        this.randomStyle = false;
                        this.boldStyle = false;
                        this.strikethroughStyle = false;
                        this.underlineStyle = false;
                        this.italicStyle = false;
                        this.setColor(this.red, this.blue, this.green, this.alpha);
                    }
                    
                    ++i;
                } else {
                    i1 = this.text.indexOf(c0);
                    float offset = 0.0F;
                    if(this.randomStyle) {
                        char f1;
                        if(i1 != -1) {
                            j1 = this.getCharWidth(c0);
                            
                            do {
                                i1 = this.fontRandom.nextInt(this.text.length());
                                f1 = this.text.charAt(i1);
                            } while(j1 != this.getCharWidth(f1));
                            
                            c0 = f1;
                        } else {
                            f1 = c0;
                            c0 = randoms[this.fontRandom.nextInt(randoms.length)];
                            offset = (float)(this.getCharWidth(f1) - this.getCharWidth(c0));
                        }
                    }
                    
                    float var14 = i1 != -1 && !this.unicodeFlag?1.0F:0.5F;
                    boolean flag = (c0 == 0 || i1 == -1 || this.unicodeFlag) && p_renderStringAtPos_2_;
                    if(flag) {
                        this.posX -= var14;
                        this.posY -= var14;
                    }
                    
                    float f = this.func_181559_a(c0, this.italicStyle);
                    if(flag) {
                        this.posX += var14;
                        this.posY += var14;
                    }
                    
                    if(this.boldStyle) {
                        this.posX += var14;
                        if(flag) {
                            this.posX -= var14;
                            this.posY -= var14;
                        }
                        
                        this.func_181559_a(c0, this.italicStyle);
                        this.posX -= var14;
                        if(flag) {
                            this.posX += var14;
                            this.posY += var14;
                        }
                        
                        ++f;
                    }
                    
                    if(offset > 0.0F) {
                        this.posX += offset;
                    }
                    
                    this.doDraw(f);
                }
            }
        }
        
    }
    
    protected void doDraw(float p_doDraw_1_) {
        Tessellator tessellator1;
        WorldRenderer worldrenderer1;
        if(this.strikethroughStyle) {
            tessellator1 = Tessellator.getInstance();
            worldrenderer1 = tessellator1.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
            worldrenderer1.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
            worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)(this.FONT_HEIGHT / 2)), 0.0D).endVertex();
            worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
            worldrenderer1.pos((double)this.posX, (double)(this.posY + (float)(this.FONT_HEIGHT / 2) - 1.0F), 0.0D).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }
        
        if(this.underlineStyle) {
            tessellator1 = Tessellator.getInstance();
            worldrenderer1 = tessellator1.getWorldRenderer();
            GlStateManager.disableTexture2D();
            worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
            int l = this.underlineStyle?-1:0;
            worldrenderer1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
            worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)this.FONT_HEIGHT), 0.0D).endVertex();
            worldrenderer1.pos((double)(this.posX + p_doDraw_1_), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
            worldrenderer1.pos((double)(this.posX + (float)l), (double)(this.posY + (float)this.FONT_HEIGHT - 1.0F), 0.0D).endVertex();
            tessellator1.draw();
            GlStateManager.enableTexture2D();
        }
        
        this.posX += (float)((int)p_doDraw_1_);
    }
    
    private int renderStringAligned(String p_renderStringAligned_1_, int p_renderStringAligned_2_, int p_renderStringAligned_3_, int p_renderStringAligned_4_, int p_renderStringAligned_5_, boolean p_renderStringAligned_6_) {
        if(this.bidiFlag) {
            int i = this.getStringWidth(this.bidiReorder(p_renderStringAligned_1_));
            p_renderStringAligned_2_ = p_renderStringAligned_2_ + p_renderStringAligned_4_ - i;
        }
        
        return this.renderString(p_renderStringAligned_1_, (float)p_renderStringAligned_2_, (float)p_renderStringAligned_3_, p_renderStringAligned_5_, p_renderStringAligned_6_);
    }
    
    private int renderString(String p_renderString_1_, float p_renderString_2_, float p_renderString_3_, int p_renderString_4_, boolean p_renderString_5_) {
        if(p_renderString_1_ == null) {
            return 0;
        } else {
            if(this.bidiFlag) {
                p_renderString_1_ = this.bidiReorder(p_renderString_1_);
            }
            
            if((p_renderString_4_ & -67108864) == 0) {
                p_renderString_4_ |= -16777216;
            }
            
            if(p_renderString_5_) {
                p_renderString_4_ = (p_renderString_4_ & 16579836) >> 2 | p_renderString_4_ & -16777216;
            }
            
            this.red = (float)(p_renderString_4_ >> 16 & 255) / 255.0F;
            this.blue = (float)(p_renderString_4_ >> 8 & 255) / 255.0F;
            this.green = (float)(p_renderString_4_ & 255) / 255.0F;
            this.alpha = (float)(p_renderString_4_ >> 24 & 255) / 255.0F;
            this.setColor(this.red, this.blue, this.green, this.alpha);
            this.posX = p_renderString_2_;
            this.posY = p_renderString_3_;
            this.renderStringAtPos(p_renderString_1_, p_renderString_5_);
            return (int)this.posX;
        }
    }
    
    public int getStringWidth(String p_getStringWidth_1_) {
        if(p_getStringWidth_1_ == null) {
            return 0;
        } else {
            int i = 0;
            boolean flag = false;
            
            for(int j = 0; j < p_getStringWidth_1_.length(); ++j) {
                char c0 = p_getStringWidth_1_.charAt(j);
                int k = this.getCharWidth(c0);
                if(k < 0 && j < p_getStringWidth_1_.length() - 1) {
                    ++j;
                    c0 = p_getStringWidth_1_.charAt(j);
                    if(c0 == 35) {
                        j += 6;
                        continue;
                    }
                    
                    if(c0 != 108 && c0 != 76) {
                        if(c0 == 114 || c0 == 82) {
                            flag = false;
                        }
                    } else {
                        flag = true;
                    }
                    
                    k = 0;
                }
                
                i += k;
                if(flag && k > 0) {
                    ++i;
                }
            }
            
            return i;
        }
    }
    
    public int getCharWidth(char p_getCharWidth_1_) {
        if(p_getCharWidth_1_ == 167) {
            return -1;
        } else if(p_getCharWidth_1_ == 32) {
            return 4;
        } else {
            int i = this.text.indexOf(p_getCharWidth_1_);
            if(p_getCharWidth_1_ > 0 && i != -1 && !this.unicodeFlag) {
                return i >= this.charWidth.length?2:this.charWidth[i];
            } else if(this.glyphWidth[p_getCharWidth_1_] != 0) {
                int j = this.glyphWidth[p_getCharWidth_1_] >>> 4;
                int k = this.glyphWidth[p_getCharWidth_1_] & 15;
                ++k;
                return (k - j) / 2 + 1;
            } else {
                return 0;
            }
        }
    }
    
    public String trimStringToWidth(String p_trimStringToWidth_1_, int p_trimStringToWidth_2_) {
        return this.trimStringToWidth(p_trimStringToWidth_1_, p_trimStringToWidth_2_, false);
    }
    
    public String trimStringToWidth(String p_trimStringToWidth_1_, int p_trimStringToWidth_2_, boolean p_trimStringToWidth_3_) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = p_trimStringToWidth_3_?p_trimStringToWidth_1_.length() - 1:0;
        int k = p_trimStringToWidth_3_?-1:1;
        boolean flag = false;
        boolean flag1 = false;
        
        for(int l = j; l >= 0 && l < p_trimStringToWidth_1_.length() && i < p_trimStringToWidth_2_; l += k) {
            char c0 = p_trimStringToWidth_1_.charAt(l);
            int i1 = this.getCharWidth(c0);
            if(flag) {
                flag = false;
                if(c0 != 108 && c0 != 76) {
                    if(c0 == 114 || c0 == 82) {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if(i1 < 0) {
                flag = true;
            } else {
                i += i1;
                if(flag1) {
                    ++i;
                }
            }
            
            if(i > p_trimStringToWidth_2_) {
                break;
            }
            
            if(p_trimStringToWidth_3_) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }
        
        return stringbuilder.toString();
    }
    
    private String trimStringNewline(String p_trimStringNewline_1_) {
        while(p_trimStringNewline_1_ != null && p_trimStringNewline_1_.endsWith("\n")) {
            p_trimStringNewline_1_ = p_trimStringNewline_1_.substring(0, p_trimStringNewline_1_.length() - 1);
        }
        
        return p_trimStringNewline_1_;
    }
    
    public void drawSplitString(String p_drawSplitString_1_, int p_drawSplitString_2_, int p_drawSplitString_3_, int p_drawSplitString_4_, int p_drawSplitString_5_) {
        this.resetStyles();
        this.textColor = p_drawSplitString_5_;
        p_drawSplitString_1_ = this.trimStringNewline(p_drawSplitString_1_);
        this.renderSplitString(p_drawSplitString_1_, p_drawSplitString_2_, p_drawSplitString_3_, p_drawSplitString_4_, false);
    }
    
    private void renderSplitString(String p_renderSplitString_1_, int p_renderSplitString_2_, int p_renderSplitString_3_, int p_renderSplitString_4_, boolean p_renderSplitString_5_) {
        for(Iterator i$ = this.listFormattedStringToWidth(p_renderSplitString_1_, p_renderSplitString_4_).iterator(); i$.hasNext(); p_renderSplitString_3_ += this.FONT_HEIGHT) {
            String s = (String)i$.next();
            this.renderStringAligned(s, p_renderSplitString_2_, p_renderSplitString_3_, p_renderSplitString_4_, this.textColor, p_renderSplitString_5_);
        }
        
    }
    
    public int splitStringWidth(String p_splitStringWidth_1_, int p_splitStringWidth_2_) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(p_splitStringWidth_1_, p_splitStringWidth_2_).size();
    }
    
    public void setUnicodeFlag(boolean p_setUnicodeFlag_1_) {
        this.unicodeFlag = p_setUnicodeFlag_1_;
    }
    
    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }
    
    public void setBidiFlag(boolean p_setBidiFlag_1_) {
        this.bidiFlag = p_setBidiFlag_1_;
    }
    
    public List listFormattedStringToWidth(String p_listFormattedStringToWidth_1_, int p_listFormattedStringToWidth_2_) {
        return Arrays.asList(this.wrapFormattedStringToWidth(p_listFormattedStringToWidth_1_, p_listFormattedStringToWidth_2_).split("\n"));
    }
    
    String wrapFormattedStringToWidth(String p_wrapFormattedStringToWidth_1_, int p_wrapFormattedStringToWidth_2_) {
        int i = this.sizeStringToWidth(p_wrapFormattedStringToWidth_1_, p_wrapFormattedStringToWidth_2_);
        if(p_wrapFormattedStringToWidth_1_.length() <= i) {
            return p_wrapFormattedStringToWidth_1_;
        } else {
            String s = p_wrapFormattedStringToWidth_1_.substring(0, i);
            char c0 = p_wrapFormattedStringToWidth_1_.charAt(i);
            boolean flag = c0 == 32 || c0 == 10;
            String s1 = getFormatFromString(s) + p_wrapFormattedStringToWidth_1_.substring(i + (flag?1:0));
            return s + "\n" + this.wrapFormattedStringToWidth(s1, p_wrapFormattedStringToWidth_2_);
        }
    }
    
    private int sizeStringToWidth(String p_sizeStringToWidth_1_, int p_sizeStringToWidth_2_) {
        int i = p_sizeStringToWidth_1_.length();
        int j = 0;
        int k = 0;
        int l = -1;
        
        for(boolean flag = false; k < i; ++k) {
            char c0 = p_sizeStringToWidth_1_.charAt(k);
            switch(c0) {
                case '\n':
                    --k;
                    break;
                case ' ':
                    l = k;
                default:
                    j += this.getCharWidth(c0);
                    if(flag) {
                        ++j;
                    }
                    break;
                case '\u00a7':
                    if(k < i - 1) {
                        ++k;
                        char c1 = p_sizeStringToWidth_1_.charAt(k);
                        if(c1 != 108 && c1 != 76) {
                            if(c1 == 114 || c1 == 82 || isFormatColor(c1)) {
                                flag = false;
                            }
                        } else {
                            flag = true;
                        }
                    }
            }
            
            if(c0 == 10) {
                ++k;
                l = k;
                break;
            }
            
            if(j > p_sizeStringToWidth_2_) {
                break;
            }
        }
        
        return k != i && l != -1 && l < k?l:k;
    }
    
    private static boolean isFormatColor(char p_isFormatColor_0_) {
        return p_isFormatColor_0_ >= 48 && p_isFormatColor_0_ <= 57 || p_isFormatColor_0_ >= 97 && p_isFormatColor_0_ <= 102 || p_isFormatColor_0_ >= 65 && p_isFormatColor_0_ <= 70;
    }
    
    private static boolean isFormatSpecial(char p_isFormatSpecial_0_) {
        return p_isFormatSpecial_0_ >= 107 && p_isFormatSpecial_0_ <= 111 || p_isFormatSpecial_0_ >= 75 && p_isFormatSpecial_0_ <= 79 || p_isFormatSpecial_0_ == 114 || p_isFormatSpecial_0_ == 82;
    }
    
    public static String getFormatFromString(String p_getFormatFromString_0_) {
        String s = "";
        int i = -1;
        int j = p_getFormatFromString_0_.length();
        
        while((i = p_getFormatFromString_0_.indexOf(167, i + 1)) != -1) {
            if(i < j - 1) {
                char c0 = p_getFormatFromString_0_.charAt(i + 1);
                if(isFormatColor(c0)) {
                    s = String.valueOf('\u00a7') + c0;
                } else if(isFormatSpecial(c0)) {
                    s = s + '\u00a7' + c0;
                }
            }
        }
        
        return s;
    }
    
    public boolean getBidiFlag() {
        return this.bidiFlag;
    }
    
    protected void setColor(float p_setColor_1_, float p_setColor_2_, float p_setColor_3_, float p_setColor_4_) {
        GlStateManager.color(p_setColor_1_, p_setColor_2_, p_setColor_3_, p_setColor_4_);
    }
    
    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }
    
    protected InputStream getResourceInputStream(ResourceLocation p_getResourceInputStream_1_) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(p_getResourceInputStream_1_).getInputStream();
    }
    
    public int getColorCode(char p_getColorCode_1_) {
        return this.colorCode["0123456789abcdef".indexOf(p_getColorCode_1_)];
    }
}
