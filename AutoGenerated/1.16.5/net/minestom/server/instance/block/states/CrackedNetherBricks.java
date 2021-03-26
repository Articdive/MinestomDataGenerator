package net.minestom.server.instance.block.states;

import java.lang.Deprecated;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockState;

@Deprecated(
    since = "forever",
    forRemoval = false
)
public final class CrackedNetherBricks {
  public static final BlockState CRACKED_NETHER_BRICKS_0 = new BlockState((short) 17110, Block.CRACKED_NETHER_BRICKS);

  public static void initStates() {
    Block.CRACKED_NETHER_BRICKS.addBlockState(CRACKED_NETHER_BRICKS_0);
  }
}