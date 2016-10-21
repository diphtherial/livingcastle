package com.escherial.livingcastle.systems.dynamics;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.structure.Level;

import java.util.ArrayList;

import static com.escherial.livingcastle.structure.Level.TILEW;

/**
 * Created by Faisal on 10/20/2016.
 */
public abstract class BasePhysicsSystem extends IteratingSystem {
    protected final ShapeRenderer shapeRenderer;
    protected final BitmapFont font;
    protected final SpriteBatch textBatch;
    protected final OrthographicCamera camera;
    ComponentMapper<Physical> mPhysical;
    ComponentMapper<Position> mPos;
    protected final Pool<Rectangle> rectanglePool = Pools.get(Rectangle.class);
    protected final ArrayList<BoundingBox> debugRects = new ArrayList<ArcadePhysicsSystem.BoundingBox>();
    protected final Level level;

    public BasePhysicsSystem(Aspect.Builder aspect, Level level, OrthographicCamera camera) {
        super(aspect);

        this.level = level;
        this.camera = camera;
        this.shapeRenderer = new ShapeRenderer();
        this.textBatch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.getData().setScale(0.5f);
    }

    // tile collision checking
    protected void checkTileMask(Physical physical, Position p) {
        // check if our bbox is colliding with a collision block
        // we're making the tile coordinates based on the center, not the corner, of our little dude
        int px = (int) ((p.pos.x + TILEW/2.0f) / TILEW);
        int py = (int) ((p.pos.y + TILEW/2.0f) / TILEW);

        Rectangle playerRect = rectanglePool.obtain();
        playerRect.set(p.pos.x, p.pos.y, TILEW, TILEW);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                // check in this mask around the player
                if (level.collidable(px + i, py + j)) {
                    // create a throwaway Rectangle for this
                    Rectangle rect = rectanglePool.obtain();
                    rect.set((px + i) * TILEW, (py + j) * TILEW, TILEW, TILEW);

                    // default color for checked but non-colliding tiles
                    Color c = Color.WHITE;

                    if (rect.overlaps(playerRect)) {
                        c = Color.FIREBRICK; // color it as we're colliding with it

                        // allows derived classes to decide how they want to deal with collision restitution
                        // (by default we just attempt to adjust positions until they don't overlap)
                        collideWithTile(physical, p, playerRect, rect, i, j);
                    }

                    // and make it renderable in our debug view (which will be cleared after this frame)
                    debugRects.add(new BoundingBox(rect, c, Integer.toString(i) + ", " + j));
                }
            }
        }

        debugRects.add(new BoundingBox(playerRect, Color.BLUE, physical.isOnGround?"g":"f"));
    }

    /**
     * Invoked when the player is found to be overlapping with an adjacent tile in its 3x3 tile mask.
     *
     * Should be overridden to provide collision restitution (i.e. make the entities no longer collide)
     * @param physical the physics aspects of the entity
     * @param p the position of the entity
     * @param playerRect the bounding rectangle of the entity (in world coords)
     * @param other the bounding rectangle of the tile (in world coords)
     * @param i the horizontal position of the tile in the mask (from -1 to 1)
     * @param j the vertical position of the tile in the mask (from -1 to 1)
     */
    protected void collideWithTile(Physical physical, Position p, Rectangle playerRect, Rectangle other, int i, int j) {
        // don't resolve collisions at all
    }

    //
    // DEBUG RENDERING STUFF BELOW
    //

    public void debugRender() {
        // step 1. draw all the rects
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // draw all the rectangles we were colliding with earlier, then clear them
        for (ArcadePhysicsSystem.BoundingBox rc : debugRects) {
            shapeRenderer.setColor(rc.c);
            // draw this rectangle on the screen
            shapeRenderer.rect(rc.r.x, rc.r.y, rc.r.width, rc.r.height);
        }

        shapeRenderer.end();

        // step 2. draw the associated debug text on the rects
        textBatch.setProjectionMatrix(camera.combined);
        textBatch.begin();
        for (ArcadePhysicsSystem.BoundingBox rc : debugRects) {
            if (rc.text != null) {
                font.draw(textBatch, rc.text, rc.r.x + 1, rc.r.y + 8);
            }
        }
        textBatch.end();

        debugRects.clear();
    }

    // for debug rendering
    protected class BoundingBox {
        public Rectangle r;
        public Color c;
        public String text;

        public BoundingBox(Rectangle r, Color c, String text) {
            this.r = r;
            this.c = c;
            this.text = text;
        }
    }
}
