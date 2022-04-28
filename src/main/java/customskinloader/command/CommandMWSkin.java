// Decompiled by ImCzf233, L wanghang

package customskinloader.command;

import net.minecraft.command.CommandException;
import java.io.IOException;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.command.ICommandSender;
import java.io.File;
import net.minecraft.command.CommandBase;

public class CommandMWSkin extends CommandBase
{
    private static final File MWSKIN_FILE;
    
    public String getCommandName() {
        return "mwskin";
    }
    
    public String getCommandUsage(final ICommandSender sender) {
        return "mwskin";
    }
    
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
    
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (CommandMWSkin.MWSKIN_FILE.exists()) {
            CommandMWSkin.MWSKIN_FILE.delete();
            sender.addChatMessage((IChatComponent)new ChatComponentText("Disabled! Please Restart Client!"));
            return;
        }
        try {
            CommandMWSkin.MWSKIN_FILE.createNewFile();
            sender.addChatMessage((IChatComponent)new ChatComponentText("Enabled! Please Restart Client!"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    static {
        MWSKIN_FILE = new File("./mwskin");
    }
}
