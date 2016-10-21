package com.escherial.livingcastle.systems.rendering;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.escherial.livingcastle.structure.Level;

public class FGLayerRenderSystem extends LayerRenderSystem {
    public FGLayerRenderSystem(SpriteBatch batch, OrthographicCamera camera, Level curLevel, int[] layers) {
        super(batch, camera, curLevel, layers);
    }
}
