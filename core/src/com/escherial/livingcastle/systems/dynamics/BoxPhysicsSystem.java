package com.escherial.livingcastle.systems.dynamics;

import com.artemis.Aspect;
import com.artemis.BaseEntitySystem;
import com.artemis.ComponentMapper;
import com.artemis.utils.IntBag;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.structure.Constants;
import com.escherial.livingcastle.structure.Level;

/**
 * Created by Faisal on 10/21/2016.
 */
public class BoxPhysicsSystem extends BaseEntitySystem {
    // world-stepping dynamics
    protected static final float TIME_STEP = 1 / 60f;
    protected static final int VELOCITY_ITERATIONS = 6;
    protected static final int POSITION_ITERATIONS = 2;
    private final Level level;
    protected float delta_accumulator = 0f;

    // for adding moving entities
    ComponentMapper<Physical> mPhysical;
    ComponentMapper<Position> mPosition;

    protected Box2DDebugRenderer debugRenderer;
    protected World physWorld;
    protected final OrthographicCamera camera;

    public BoxPhysicsSystem(Level level, OrthographicCamera camera) {
        super(Aspect.all(Physical.class, Position.class));
        this.camera = camera;
        this.level = level;

        debugRenderer = new Box2DDebugRenderer();
        physWorld = new World(new Vector2(0f, -20f), true);

        // adds the collision layer of the level as static collidable geometry
        addStaticTiles(level);
        // adds in the collision_geom layer consisting of collidable polygons
        addStaticGeometry(level);
    }

    @Override
    protected void initialize() {
        physWorld.setContactListener(new FootContactListener(mPhysical));
    }

    // ===========================================================================
    // === game entity -> box2d entity mapping maintenance
    // ===========================================================================

    @Override
    protected void inserted(int entityId) {
        Physical physical = mPhysical.get(entityId);
        Position p = mPosition.get(entityId);

        // First we create a body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(p.pos);

        // Create our body in the world using our body definition
        Body body = physWorld.createBody(bodyDef);
        body.setFixedRotation(true); // don't ever rotate entities

        // apply initial velocity
        body.setLinearVelocity(physical.vel);

        // Create a circle shape and set its radius
        PolygonShape bbox = new PolygonShape();
        bbox.setAsBox(p.width/2f, p.height/2f);

        // Create a fixture definition to apply our shape to
        // FIXME: these values should ideally come from Physical
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.groupIndex = (short) -physical.collideCategory.ordinal();
        fixtureDef.shape = bbox;
        fixtureDef.density = 5f;
        fixtureDef.friction = 1.0f; // full floor friction
        fixtureDef.restitution = 0.2f; // don't bounce at all

        // Create our fixture and attach it to the body
        Fixture fixture = body.createFixture(fixtureDef);

        // Remember to dispose of any shapes after you're done with them!
        // BodyDef and FixtureDef don't need disposing, but shapes do.
        bbox.dispose();

        if (physical.tracksGround) {
            // we also need a foot sensor
            PolygonShape feet = new PolygonShape();
            feet.setAsBox(p.width/2.1f, p.height/8f, new Vector2(0, -p.height/2f), 0);
            FixtureDef foot_fixture_def = new FixtureDef();
            foot_fixture_def.shape = feet;
            foot_fixture_def.isSensor = true;
            Fixture foot_fixture = body.createFixture(foot_fixture_def);
            foot_fixture.setUserData(entityId);

            feet.dispose();
        }

        // assign the entity a physics representation that we can look up later
        // we should probably associate this with the entity at its creation (not here) and filter for it in this system
        physical.body = body;

        if (physical.isBullet) {
            body.setGravityScale(0);
            body.setBullet(true);
        }
    }

    @Override
    protected void removed(int entityId) {
        // i suppose we should destroy the body?
        Physical physical = mPhysical.get(entityId);
        physWorld.destroyBody(physical.body);
    }

    // ===========================================================================
    // === core system processing
    // ===========================================================================

    @Override
    protected void processSystem() {
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();

        // run the initial process that applies inputs as forces
        for (int i = 0, s = actives.size(); s > i; i++) {
            processInputs(ids[i]);
        }

        // do all the physics
        doPhysicsStep(Gdx.graphics.getDeltaTime());

        // reconcile the engine's position and our game's position
        for (int i = 0, s = actives.size(); s > i; i++) {
            processResults(ids[i]);

            // check if it's a bullet and if it's outside of the level
            if (mPhysical.has(ids[i]) && mPosition.has(ids[i])) {
                Physical phys = mPhysical.get(ids[i]);

                // skip things that aren't bullets
                if (!phys.isBullet)
                    continue;

                Position p = mPosition.get(ids[i]);

                if (p.pos.len2() > level.mapWidth * level.mapPixelWidth) {
                    world.delete(ids[i]);
                }
            }
        }
    }

    protected void processInputs(int entityId) {
        // for each entity, apply forces, set positions from ground truth, etc.
        Physical physical = mPhysical.get(entityId);
        Position p = mPosition.get(entityId);
        Body myBody = physical.body;

        // apply forces from Physical to this body
        myBody.applyForceToCenter(physical.force, true);
        physical.force.set(0, 0); // and then clear out the force
    }

    private void doPhysicsStep(float deltaTime) {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        delta_accumulator += frameTime;
        while (delta_accumulator >= TIME_STEP) {
            physWorld.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            delta_accumulator -= TIME_STEP;
        }
    }

    protected void processResults(int entityId) {
        Position p = mPosition.get(entityId);
        Physical physical = mPhysical.get(entityId);
        Body myBody = physical.body;

        // set the position according to the body's pos
        p.pos.set(myBody.getPosition());

        // if we're out of view of the level, teleport us up right past view of the level and to the center
        if (p.pos.y < -Constants.VIEWPORT_HEIGHT/2) {
            p.pos.x = level.mapWidth/2f;
            p.pos.y = level.mapHeight + Constants.VIEWPORT_HEIGHT*2f;
            // and sync up the body and the camera so it doesn't have to make a super jump
            myBody.setTransform(p.pos.x, p.pos.y, 0);
            camera.position.set(p.pos.x, p.pos.y, 0);
        }
    }

    public void debugRender() {
        debugRenderer.render(physWorld, camera.combined);
    }

    // ===========================================================================
    // === level geometry creation stuff below
    // ===========================================================================

    public void addStaticTiles(Level level) {
        // get the collision layer
        TiledMapTileLayer collider = (TiledMapTileLayer) level.getMap().getLayers().get("collider");

        // we can't create geometry if there isn't a collision layer
        if (collider == null)
            return;

        // we use this rect as a scratchpad
        Rectangle span = new Rectangle();
        boolean in_span = false;

        // scan the tiles in horizontal stripes, creating rectangles as we go
        // if we encounter a tile and it's null, we create a new rect
        // if we encounter a tile and it's not null, we extend the current rect
        // if we encounter a non-tile and it's not null, we push the current rect and null it out
        // if we finish a span and it's not null, we push the current rect and null it out
        for (int row = 0; row < collider.getHeight(); row++) {
            for (int col = 0; col < collider.getWidth(); col++) {
                if (collider.getCell(col, row) != null) {
                    // we encountered a filled tile
                    if (in_span) {
                        // we have an existing span, so extend it by this tile
                        span.width += Level.TILEW;
                    }
                    else {
                        // there is no existing span, so create one and add this tile
                        span.set(col*Level.TILEW, row*Level.TILEW, Level.TILEW, Level.TILEW);
                        in_span = true;
                    }
                }
                else if (in_span) {
                    // we encountered a gap between spans
                    // push the current span as a solid object, then exit the span
                    commitTileSpan(span);
                    in_span = false; // marks us as 'not in span'
                }
            }

            if (in_span) {
                // we ended the row on a span, so push it as solid geometry
                commitTileSpan(span);
                in_span = false; // marks us as 'not in span'
            }

            // FIXME: eventually handle cells that aren't rectangular
            // this may mean generating one-off geometries for spans of cells that are the same shape, but so be it
        }
    }

    private void commitTileSpan(Rectangle span) {
        // create a body definition that specifies the position (shape comes later), then create a body from that
        BodyDef bodydef = new BodyDef();
        // the box's center is the center of our rect, not its corner
        bodydef.position.set(span.x + span.width/2, span.y + span.height/2);
        Body body = physWorld.createBody(bodydef);

        // create a box that represents this span's shape and attach it to our body
        PolygonShape box = new PolygonShape();
        box.setAsBox(span.width/2f, span.height/2f);
        body.createFixture(box, 0f);
        box.dispose();
    }

    private void addStaticGeometry(Level level) {
        // get the collision layer
        MapLayer geom_collider = level.getMap().getLayers().get("collider_geom");

        if (geom_collider == null)
            return;

        for (MapObject mo : geom_collider.getObjects()) {
            if (mo instanceof PolygonMapObject) {
                PolygonMapObject pmo = (PolygonMapObject)mo;

                // create a body definition that specifies the position (shape comes later), then create a body from that
                BodyDef bodydef = new BodyDef();
                // trust whatever tiled is telling us, i guess?
                bodydef.position.set(
                        pmo.getPolygon().getX() * Constants.TILE_PIXEL_MAPPING,
                        pmo.getPolygon().getY()* Constants.TILE_PIXEL_MAPPING
                );
                Body body = physWorld.createBody(bodydef);

                // apparently all the vertices need to be scaled down
                float[] tiled_vertices = pmo.getPolygon().getVertices();
                float[] vertices = new float[tiled_vertices.length];

                for (int i = 0; i < tiled_vertices.length; i++) {
                    vertices[i] = tiled_vertices[i] * Constants.TILE_PIXEL_MAPPING;
                }

                // create a box that represents this span's shape and attach it to our body
                PolygonShape poly = new PolygonShape();
                poly.set(vertices);
                body.createFixture(poly, 0f);
                poly.dispose();
            }
        }
    }
}
