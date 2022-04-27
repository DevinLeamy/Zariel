package engine.main;

public class TerrainGenerator {
    private class Pair {
        public int first;
        public BlockType second;
        public Pair(int first, BlockType second) {
            this.first = first;
            this.second = second;
        }
    }

    private Pair[] levels;
    public TerrainGenerator() {
        levels = new Pair[] {
                new Pair(0, BlockType.BEDROCK),
                new Pair(7, BlockType.STONE),
                new Pair(13, BlockType.GRASS),
                new Pair(40, BlockType.SNOW),
//                new Pair(engine.config.Config.BEDROCK_LEVEL, engine.main.BlockType.STONE),
//                new Pair(engine.config.Config.WATER_LEVEL, engine.main.BlockType.SNOW),
//                new Pair(engine.config.Config.SAND_LEVEL, engine.main.BlockType.SNOW),
//                new Pair(engine.config.Config.GRASS_LEVEL, engine.main.BlockType.GRASS),
//                new Pair(engine.config.Config.STONE_LEVEL, engine.main.BlockType.STONE),
//                new Pair(engine.config.Config.SNOW_LEVEL, engine.main.BlockType.SNOW),
        };
        for (Pair pair : levels) {
//            pair.first *= 2;
        }
    }

    public Block getBlock(int height) {
        if (height < levels[0].first) {
            return new Block();
        }

        for (int i = 0; i < levels.length - 1; ++i) {
            if (height >= levels[i].first && height < levels[i + 1].first) {
                return new Block(true, levels[i].second);
            }
        }

        return new Block(true, BlockType.SNOW);
    }

}
