package me.activated.core.punishments.utilities;

import lombok.Getter;
import me.activated.core.plugin.AquaCore;
import me.activated.core.utilities.chat.Color;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class PunishmentsConfigFile extends YamlConfiguration {

	@Getter private File file;
	private final JavaPlugin plugin;
	private final String name;

	public PunishmentsConfigFile(JavaPlugin plugin, String name) {
		this.plugin = plugin;
		this.name = name;

		this.load();
	}

	public void load() {
		File directory = new File(plugin.getDataFolder(), "punishments");
		if (!directory.exists()) {
			directory.mkdir();
		}
		this.file = new File(directory , name);

		InputStream inputStream = null;
		OutputStream outputStream = null;

		if(!this.file.exists()) {
			try {
				inputStream = plugin.getResource(name);

				outputStream = new FileOutputStream(this.file);

				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		try {
			this.load(this.file);
		} catch(IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			this.save(this.file);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getInt(String path) {
		return super.getInt(path, 0);
	}

	@Override
	public double getDouble(String path) {
		return super.getDouble(path, 0.0);
	}

	@Override
	public boolean getBoolean(String path) {
		return super.getBoolean(path, false);
	}

    public String getString(String path, boolean ignored) {
		if (path.equalsIgnoreCase("SERVER-NAME")) {
			return AquaCore.INSTANCE.getEssentialsManagement().getServerName();
		}
        return super.getString(path, null);
    }

	@Override
	public String getString(String path) {
		if (path.equalsIgnoreCase("SERVER-NAME")) {
			return AquaCore.INSTANCE.getEssentialsManagement().getServerName();
		}
		return Color.translate(super.getString(path, "&bString at path &7'&3" + path + "&7' &bnot found."));
	}

	@Override
	public List<String> getStringList(String path) {
		return super.getStringList(path).stream().map(Color::translate).collect(Collectors.toList());
	}
}