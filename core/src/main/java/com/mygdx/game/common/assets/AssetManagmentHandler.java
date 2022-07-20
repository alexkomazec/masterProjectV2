package com.mygdx.game.common.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Logger;

public class AssetManagmentHandler implements AssetErrorListener {

    /*Class Members*/
    protected static final Logger logger = new Logger(AssetManagmentHandler.class.getSimpleName(), Logger.INFO);
    private final InternalFileHandleResolver internalFileHandleREsolver;
    private AssetManager assetManager;

    /*Class Methods*/
    public AssetManagmentHandler()
    {
        this.assetManager = new AssetManager();
        this.assetManager.setErrorListener(this);
        internalFileHandleREsolver = new InternalFileHandleResolver();
    }

    public void setAssetManager(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    public AssetManager getAssetManager()
    {
        return this.assetManager;
    }

    @SafeVarargs
    public final void loadResources(AssetDescriptor... assetDescriptors)
    {

        /*Load all assets for received assetDescriptors*/
        for(AssetDescriptor assetDescriptor : assetDescriptors){
            loadResource(assetDescriptor);
        }

        /*Be sure that all assets have been loaded until this line*/
        this.assetManager.finishLoading();
    }

    public synchronized <T>void loadResource(AssetDescriptor assetDescriptor)
    {
        if(internalFileHandleREsolver.resolve(assetDescriptor.fileName).exists() )
        {
            this.assetManager.load(assetDescriptor);
            logger.debug("Loaded asset without AssetLoaderParameters" + assetDescriptor.fileName);
        }
        else
        {
            logger.error("This asset has not been found: " + assetDescriptor.fileName);
        }
    }

    public synchronized <T>void loadResource (String fileName, Class<T> type, AssetLoaderParameters<T> parameter)
    {
        if(internalFileHandleREsolver.resolve(fileName).exists())
        {
            this.assetManager.load(fileName, type, parameter);
            logger.debug("Loaded asset with AssetLoaderParameters" + fileName);
        }
        else
        {
            logger.error("This asset has not been found: " + fileName);
        }

        /*Be sure that all assets have been loaded until this line*/
        this.assetManager.finishLoading();
    }

    public <T> void unloadResources(AssetDescriptor<T>... assetDescriptors)
    {

        /*Unload all resources*/
        for (AssetDescriptor<T> assetDescriptor : assetDescriptors)
        {

            if (this.assetManager.isLoaded(assetDescriptor.fileName))
            {
                this.assetManager.unload(assetDescriptor.fileName);
                logger.debug("This resourse has been unloaded " + assetDescriptor.fileName);
            }
            else
            {
                logger.debug("This resourse has not been loaded " + assetDescriptor.fileName);
            }
        }
    }

    public <T> T getResources(AssetDescriptor ... assetDescriptors)
    {

        for(AssetDescriptor assetDescriptor : assetDescriptors)
        {

            /* Check if the asset is loaded */
            if(this.assetManager.isLoaded(assetDescriptor.fileName))
            {
                return this.assetManager.get(assetDescriptor.fileName);
            }
            else
            {
                logger.debug("This asset has not been gotten: " + assetDescriptor.fileName);
            }
        }
        return null;
    }

    public float getProgress()
    {
        return this.assetManager.getProgress();
    }

    public boolean updateAssetLoading()
    {
        return this.assetManager.update();
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        logger.error("Could not load asset" + asset.fileName, (Exception)throwable);
    }
}
