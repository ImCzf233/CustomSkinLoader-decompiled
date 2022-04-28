// Decompiled by ImCzf233, L wanghang

package customskinloader.fake.itf;

import net.minecraft.client.Minecraft;

public interface IFakeMinecraft
{
    default void execute(final Runnable runnable) {
        ((Minecraft)this).addScheduledTask(runnable);
    }
}
