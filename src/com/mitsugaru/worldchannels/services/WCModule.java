package com.mitsugaru.worldchannels.services;

import com.mitsugaru.worldchannels.WorldChannels;

/**
 * Represents a module used for the jail plugin.
 */
public abstract class WCModule {

   /**
    * Plugin reference.
    */
   protected WorldChannels plugin;

   /**
    * Constructor.
    * 
    * @param plugin
    *           - Plugin hook.
    */
   public WCModule(WorldChannels plugin) {
      this.plugin = plugin;
   }

   /**
    * Called when the module has been registered to the API.
    */
   public abstract void starting();

   /**
    * Called when the module has been removed from the API.
    */
   public abstract void closing();

}
