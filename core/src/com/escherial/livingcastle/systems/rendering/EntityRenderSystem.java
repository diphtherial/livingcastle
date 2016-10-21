package com.escherial.livingcastle.systems.rendering;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.components.Sprited;

public class EntityRenderSystem extends IteratingSystem {
    ComponentMapper<Sprited> mSprited;
    ComponentMapper<Position> mPosition;
    SpriteBatch batch;
    OrthographicCamera camera;

    public EntityRenderSystem(SpriteBatch batch, OrthographicCamera camera) {
        super(Aspect.all(Sprited.class, Position.class));
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    protected void process(int e) {
        Sprited re = mSprited.get(e);
        Position p = mPosition.get(e);
        batch.draw(re.img, p.pos.x, p.pos.y);
    }
}
