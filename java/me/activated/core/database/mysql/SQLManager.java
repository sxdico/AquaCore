package me.activated.core.database.mysql;

import lombok.Getter;
import lombok.Setter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.Manager;
import me.activated.core.utilities.chat.Color;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.sql.*;
import java.util.*;

@Getter
@Setter
public class SQLManager extends Manager {
    private Connection connection;

    public SQLManager(AquaCore plugin) {
        super(plugin);
    }

    public void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" +
                            plugin.getDataBase().getString("MYSQL.DATABASE.HOST") + ":" +
                            plugin.getDataBase().getInt("MYSQL.DATABASE.PORT") + "/" +
                            plugin.getDataBase().getString("MYSQL.DATABASE.DATABASE")
                            + "?characterEncoding=latin1&useConfigs=maxPerformance",

                    plugin.getDataBase().getString("MYSQL.DATABASE.USER"),
                    plugin.getDataBase().getString("MYSQL.DATABASE.PASSWORD"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.getTables().forEach((table, st) -> {
            PreparedStatement statement = null;
            try {
                statement = this.connection.prepareStatement(st);
                statement.executeUpdate();
                statement.close();

                PreparedStatement preparedStatement = this.connection.prepareStatement("ALTER TABLE " + table + " ADD COLUMN IF NOT EXISTS(" +
                        "uuid VARCHAR(48) NOT NULL," +
                        "points INTEGER NOT NULL)"
                );

                preparedStatement.executeUpdate();
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                close(statement);
            }
        });

        if (this.connection != null) {
            Bukkit.getConsoleSender().sendMessage(Color.translate("&aMySQL has been connected successfully!"));
        }
    }

    private String createColumn(String table, String name, Serializable type, Object def, boolean last) {
        String key = "";

        if (type == Integer.class) {
            key = "INTEGER";
        } else if (type == String.class) {
            key = "TEXT";
        } else if (type == Float.class) {
            key = "FLOAT";
        } else if (type == Double.class) {
            key = "DOUBLE";
        } else if (type == Long.class) {
            key = "BIGINT";
        } else if (type == Boolean.class) {
            key = "BOOLEAN";
        }

        if (key.equalsIgnoreCase("")) return key;

        try {
            PreparedStatement statement = this.connection.prepareStatement("SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME =" + table + " AND COLUMN_NAME=" + name);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                if (def != null) {
                    return name + " " + key + " DEFAULT " + def + " NOT NULL" + (last ? ")" : ",");
                } else {
                    return name + " " + key + " NOT NULL" + (last ? ")" : ",");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public void close() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close(AutoCloseable... closeables) {
        Arrays.stream(closeables).filter(Objects::nonNull).forEach(closeable -> {
            try {
                closeable.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Map<String, String> getTables() {
        Map<String, String> toReturn = new HashMap<>();

        toReturn.put("docs", "CREATE TABLE IF NOT EXISTS docs("
                + "id SERIAL NOT NULL PRIMARY KEY)");

        return toReturn;
    }

    /*public boolean isCreated(String table, String where, String what) {
        try {
            PreparedStatement statement = plugin.getSQLManager().getConnection().prepareStatement("SELECT * FROM "
                    + table + " WHERE " + where + "=?");
            statement.setString(1, what);

            ResultSet result = statement.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }*/

    public Serializable getStatus(String table, String where, String what, Object value, Serializable type) {
        PreparedStatement statement = null;
        ResultSet result;

        try {
            statement = this.connection.prepareStatement("SELECT * FROM " + table + " WHERE " + where + "=?");
            statement.setString(1, what);
            result = statement.executeQuery();

            if (result.next()) {
                if (type == Integer.class) {
                    return result.getInt(value.toString());
                } else if (type == String.class) {
                    return result.getString(value.toString());
                } else if (type == Float.class) {
                    return result.getFloat(value.toString());
                } else if (type == Double.class) {
                    return result.getDouble(value.toString());
                } else if (type == Long.class) {
                    return result.getLong(value.toString());
                } else if (type == Boolean.class) {
                    return result.getBoolean(value.toString());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            close(statement);
        }

        if (type == Integer.class) {
            return -1;
        } else if (type == String.class) {
            return null;
        } else if (type == Float.class) {
            return -1F;
        } else if (type == Double.class) {
            return -1D;
        } else if (type == Long.class) {
            return -1L;
        } else if (type == Boolean.class) {
            return false;
        }

        return null;
    }
}
