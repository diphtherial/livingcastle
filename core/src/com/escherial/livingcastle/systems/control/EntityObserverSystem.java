package com.escherial.livingcastle.systems.control;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.escherial.livingcastle.components.PlayerControlled;
import com.escherial.livingcastle.components.Position;

/**
 * Created by Faisal on 10/16/2016.
 */
public class EntityObserverSystem extends IteratingSystem {
    private final OrthographicCamera camera;
    public ComponentMapper<PlayerControlled> mPlayerControlled;
    public ComponentMapper<Position> mPosition;
    Vector3 target = new Vector3();

    public EntityObserverSystem(OrthographicCamera camera) {
        super(Aspect.all(PlayerControlled.class, Position.class));
        this.camera = camera;
    }

    @Override
    protected void process(int entityId) {
        Position p = mPosition.get(entityId);
        target.set(p.pos.x, p.pos.y, 0f);
        camera.position.set(camera.position.lerp(target, 0.2f));
    }
}
