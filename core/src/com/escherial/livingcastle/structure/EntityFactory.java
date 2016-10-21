package com.escherial.livingcastle.structure;

import com.artemis.World;
import com.escherial.livingcastle.components.Physical;
import com.escherial.livingcastle.components.PlayerControlled;
import com.escherial.livingcastle.components.Position;
import com.escherial.livingcastle.components.Sprited;

/**
 * Created by Faisal on 10/14/2016.
 */
public class EntityFactory {
    public static int createPlayer(World world, float x, float y) {
        int player_entity = world.create();

        world.edit(player_entity)
                .add(new Sprited("sprites/bluetank-still.png"))
                .add(new Position(x, y))
                .add(new Physical())
                .add(new PlayerControlled());

        return player_entity;
    }
}
