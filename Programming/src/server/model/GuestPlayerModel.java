package server.model;

import server.entity.database.T3DB;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GuestPlayerModel {
    private GuestPlayerModel() {

    }

    private static class GuestPlayerModelSingleton
    {
        private static final GuestPlayerModel INSTANCE = new GuestPlayerModel();
    }

    public static GuestPlayerModel getGuestPlayerModelInstance() {
        return GuestPlayerModel.GuestPlayerModelSingleton.INSTANCE;
    }

    public void removeGuestPlayerFromDB(String username) throws SQLException {
        // remove record in table GuestPlayer (case: user is anon
        PreparedStatement stm = T3DB.getConnection().prepareStatement(
                "delete from GuestPlayer where displayname=?");
        stm.setString(1, username);
        stm.executeUpdate();
    }
}
