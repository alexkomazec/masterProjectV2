package com.mygdx.game.common.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Logger;
import com.mygdx.game.entitycomponentsystem.system.DataReceivingSystem;

public class AssetManagmentHandler {

    /*Class Members*/
    protected static final Logger logger = new Logger(AssetManagmentHandler.class.getSimpleName(), Logger.INFO);
    private InternalFileHandleResolver filePathResolver =  new InternalFileHandleResolver();
    private AssetManager assetManager;

    /*Class Methods*/
    public AssetManagmentHandler()
    {
        assetManager = new AssetManager();
        filePathResolver = new InternalFileHandleResolver();
    }

    public void setAssetManager(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    public AssetManager getAssetManager()
    {
        return assetManager;
    }

    @SafeVarargs
    public final void loadResource(AssetDescriptor... assetDescriptors)
    {

        /*Load all assets for received assetDescriptors*/
        for(AssetDescriptor assetDescriptor : assetDescriptors){

            if( filePathResolver.resolve(assetDescriptor.fileName).exists() )
            {
                assetManager.load(assetDescriptor);
                logger.debug("Loaded" + assetDescriptor.fileName);
            }
            else
            {
                logger.error("This asset has not been found: " + assetDescriptor.fileName);
            }
        }

        /*Be sure that all assets have been loaded until this line*/
        assetManager.finishLoading();
    }

    public <T> void unloadResources(AssetDescriptor<T>... assetDescriptors)
    {

        /*Unload all resources*/
        for (AssetDescriptor<T> assetDescriptor : assetDescriptors)
        {

            if (assetManager.isLoaded(assetDescriptor.fileName))
            {
                assetManager.unload(assetDescriptor.fileName);
                logger.debug("This resourse has been unloaded " + assetDescriptor.fileName);
            }
            else
            {
                logger.debug("This resourse has not been loaded " + assetDescriptor.fileName);
            }
        }
    }

    public <T> T getResource(AssetDescriptor ... assetDescriptors)
    {

        for(AssetDescriptor assetDescriptor : assetDescriptors)
        {

            /*Check if the assed is loaded*/
            if(assetManager.isLoaded(assetDescriptor.fileName))
            {
                return assetManager.get(assetDescriptor.fileName);
            }
            else
            {
                logger.debug("This asset has not been loaded: " + assetDescriptor.fileName);
            }
        }
        return null;
    }

    public float getProgress()
    {
        return assetManager.getProgress();
    }

    public boolean updateAssetLoading()
    {
        return assetManager.update();
    }
}
