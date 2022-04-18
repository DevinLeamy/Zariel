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
                new Pair(Config.BEDROCK_LEVEL, BlockType.BEDROCK),
                new Pair(Config.WATER_LEVEL, BlockType.WATER),
                new Pair(Config.SAND_LEVEL, BlockType.SAND),
                new Pair(Config.GRASS_LEVEL, BlockType.GRASS),
                new Pair(Config.STONE_LEVEL, BlockType.STONE),
                new Pair(Config.SNOW_LEVEL, BlockType.SNOW),
        };
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
