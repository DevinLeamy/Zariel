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
                new Pair(13, BlockType.SNOW),
                new Pair(40, BlockType.SNOW),
//                new Pair(Config.BEDROCK_LEVEL, BlockType.STONE),
//                new Pair(Config.WATER_LEVEL, BlockType.SNOW),
//                new Pair(Config.SAND_LEVEL, BlockType.SNOW),
//                new Pair(Config.GRASS_LEVEL, BlockType.GRASS),
//                new Pair(Config.STONE_LEVEL, BlockType.STONE),
//                new Pair(Config.SNOW_LEVEL, BlockType.SNOW),
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
