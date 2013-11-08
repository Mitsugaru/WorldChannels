package com.mitsugaru.worldchannels.services;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.mitsugaru.worldchannels.WorldChannels;

/**
 * Represents a command.
 * 
 * @param <T>
 *           Game plugin.
 */
public interface ICommand {

   /**
    * Execution method for the command.
    * 
    * @param sender
    *           - Sender of the command.
    * @param command
    *           - Command used.
    * @param label
    *           - Label.
    * @param args
    *           - Command arguments.
    * @return True if valid command and executed. Else false.
    */
   boolean execute(final WorldChannels plugin, final CommandSender sender, final Command command, final String label, String[] args);

}
