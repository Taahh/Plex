package me.totalfreedom.plex.banning;

import com.google.common.collect.Lists;
import dev.morphia.query.Query;
import dev.morphia.query.experimental.filters.Filters;
import dev.morphia.query.experimental.updates.UpdateOperators;
import dev.morphia.query.internal.MorphiaCursor;
import me.totalfreedom.plex.Plex;
import me.totalfreedom.plex.storage.StorageType;
import me.totalfreedom.plex.util.PlexLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class BanManager
{
    private final String SELECT = "SELECT * FROM `bans` WHERE uuid=?";
    private final String INSERT = "INSERT INTO `bans` (`banID`, `uuid`, `banner`, `reason`, `enddate`, `active`) VALUES (?, ?, ?, ?, ?, ?);";

    public void executeBan(Ban ban)
    {
        if (Plex.get().getStorageType() == StorageType.MONGO)
        {
            Plex.get().getMongoConnection().getDatastore().save(ban);
        } else {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {

                PreparedStatement statement = con.prepareStatement(INSERT);
                statement.setString(1, ban.getId());
                statement.setString(2, ban.getUuid().toString());
                statement.setString(3, ban.getBanner() == null ? "" : ban.getBanner().toString());
                statement.setString(4, ban.getReason().isEmpty() ? "" : ban.getReason());
                statement.setLong(5, ban.getEndDate().toInstant().toEpochMilli());
                statement.setBoolean(6, ban.isActive());
                statement.execute();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public boolean isBanned(UUID uuid)
    {
        if (Plex.get().getStorageType() == StorageType.MONGO)
        {
            return Plex.get().getMongoConnection().getDatastore().find(Ban.class)
                    .filter(Filters.eq("uuid", uuid.toString())).filter(Filters.eq("active", true)).first() != null;
        } else {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement(SELECT);
                statement.setString(1, uuid.toString());
                ResultSet set = statement.executeQuery();
                PlexLog.log("-----------");
                PlexLog.log("Next: " + set.next());
                PlexLog.log("Active: " + set.getBoolean("active"));
                if (!set.next()) return false;
                while (set.next())
                {
                    if (set.getBoolean("active")) return true;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return false;
    }

    public void unban(UUID uuid)
    {
        if (Plex.get().getStorageType() == StorageType.MONGO)
        {
            Query<Ban> query = Plex.get().getMongoConnection().getDatastore().find(Ban.class).filter(Filters.eq("uuid", uuid.toString())).filter(Filters.eq("active", true));
            if (query.first() != null)
            {
                query.update(UpdateOperators.set("active", false)).execute();
            }
        } else {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement("UPDATE `bans` SET active=? WHERE uuid=?");
                statement.setBoolean(1, false);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public void unban(String id)
    {
        if (Plex.get().getStorageType() == StorageType.MONGO)
        {
            Query<Ban> query = Plex.get().getMongoConnection().getDatastore().find(Ban.class).filter(Filters.eq("_id", id)).filter(Filters.eq("active", true));
            if (query.first() != null)
            {
                query.update(UpdateOperators.set("active", false)).execute();
            }
        } else {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement("UPDATE `bans` SET active=? WHERE banID=?");
                statement.setBoolean(1, false);
                statement.setString(2, id);
                statement.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public List<Ban> getActiveBans()
    {
        List<Ban> bans = Lists.newArrayList();
        if (Plex.get().getStorageType() == StorageType.MONGO)
        {
            MorphiaCursor<Ban> cursor = Plex.get().getMongoConnection().getDatastore().find(Ban.class).filter(Filters.eq("active", true)).iterator();
            while (cursor.hasNext())
            {
                Ban ban = cursor.next();
                bans.add(ban);
            }
        } else {
            try (Connection con = Plex.get().getSqlConnection().getCon())
            {
                PreparedStatement statement = con.prepareStatement("SELECT * FROM `bans`");
                ResultSet set = statement.executeQuery();
                while (set.next())
                {
                    if (set.getBoolean("active"))
                    {
                        String id = set.getString("banID");
                        UUID uuid = UUID.fromString(set.getString("uuid"));
                        UUID banner = set.getString("banner").isEmpty() ? null : UUID.fromString(set.getString("banner"));
                        String reason = set.getString("reason");
                        Date endDate = set.getLong("enddate") != 0 ? new Date(set.getLong("enddate")) : null;
                        Ban ban = new Ban(id, uuid, banner, reason, endDate);
                        bans.add(ban);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return bans;
    }


}
