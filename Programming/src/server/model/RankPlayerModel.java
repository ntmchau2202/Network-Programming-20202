package server.model;

public class RankPlayerModel {
    private RankPlayerModel() {

    }

    private static class RankPlayerModelSingleton
    {
        private static final RankPlayerModel INSTANCE = new RankPlayerModel();
    }

    public static RankPlayerModel getRankPlayerModelInstance() {
        return RankPlayerModel.RankPlayerModelSingleton.INSTANCE;
    }
}
