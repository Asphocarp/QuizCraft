package app.jyu;

import app.jyu.ModConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static app.jyu.QuizCraft.MOD_ID;

public class ModConfig implements ModMenuApi, ConfigScreenFactory<Screen> {
    public static final File CFG_FILE = new File(
            FabricLoader.getInstance().getConfigDir().toFile(),
            MOD_ID + ".properties");
    // The num of pings save for each player (work only in client side)
    // TODO: only to avoid memory leak, maybe do not allow config for this
    public static final int DEFAULT_pingNumEach = 100; 
    public static int pingNumEach = DEFAULT_pingNumEach;
    // Whether you can ping on the fluid
    public static final boolean DEFAULT_includeFluids = false;
    public static boolean includeFluids = DEFAULT_includeFluids;
    // Resize the icon hud
    public static final float DEFAULT_iconSize = 1f;
    public static float iconSize = DEFAULT_iconSize;
    // Info shown when looking at the ping point, format: 0xAARRGGBB
    public static final int DEFAULT_infoColor = 0xFFEB9D39;
    public static int infoColor = DEFAULT_infoColor;
    // 0 means never, only work at your client side
    public static final long DEFAULT_secondsToVanish = 0;
    public static long secondsToVanish = DEFAULT_secondsToVanish;
    // Color of the highlight block, format: 0xAARRGGBB
    public static final int DEFAULT_highlightColor = 0xFFEB9D39;
    public static int highlightColor = DEFAULT_highlightColor;
    // The index of the ping sound
    public static final byte DEFAULT_soundIndex = 0;
    public static byte soundIndex = DEFAULT_soundIndex;

    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return this;
    }

    @Override
    public Screen create(Screen screen) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(screen)
                .setTitle(Text.translatable("title." + MOD_ID + ".config"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config." + MOD_ID + ".general"));

        // Ping num each
        general.addEntry(entryBuilder.startIntField(Text.translatable("config." + MOD_ID + ".pingNumEach"), pingNumEach)
                .setDefaultValue(DEFAULT_pingNumEach)
                .setTooltip(Text.translatable("config." + MOD_ID + ".pingNumEach.description"))
                .setSaveConsumer(ModConfig::setPingNumEach)
                .build());

        // Toggle include fluids
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config." + MOD_ID + ".includeFluids"), includeFluids)
                .setDefaultValue(DEFAULT_includeFluids)
                .setTooltip(Text.translatable("config." + MOD_ID + ".includeFluids.description"))
                .setSaveConsumer(ModConfig::setIncludeFluids)
                .build());

        // icon size
        general.addEntry(entryBuilder.startFloatField(Text.translatable("config." + MOD_ID + ".iconSize"), iconSize)
                .setDefaultValue(DEFAULT_iconSize)
                .setTooltip(Text.translatable("config." + MOD_ID + ".iconSize.description"))
                .setSaveConsumer(ModConfig::setIconSize)
                .build());

        // info color
        general.addEntry(entryBuilder.startAlphaColorField(Text.translatable("config." + MOD_ID + ".infoColor"), infoColor)
                .setDefaultValue(DEFAULT_infoColor)
                .setTooltip(Text.translatable("config." + MOD_ID + ".infoColor.description"))
                .setSaveConsumer(ModConfig::setInfoColor)
                .build());

        // seconds to vanish
        general.addEntry(entryBuilder.startLongField(Text.translatable("config." + MOD_ID + ".secondsToVanish"), secondsToVanish)
                .setDefaultValue(DEFAULT_secondsToVanish)
                .setTooltip(Text.translatable("config." + MOD_ID + ".secondsToVanish.description"))
                .setSaveConsumer(ModConfig::setSecondsToVanish)
                .build());

        // highlight color
        general.addEntry(entryBuilder.startAlphaColorField(Text.translatable("config." + MOD_ID + ".highlightColor"), highlightColor)
                .setDefaultValue(DEFAULT_highlightColor)
                .setTooltip(Text.translatable("config." + MOD_ID + ".highlightColor.description"))
                .setSaveConsumer(ModConfig::setHighlightColor)
                .build());

        // Sound index
        general.addEntry(entryBuilder.startIntField(Text.translatable("config." + MOD_ID + ".soundIndex"), soundIndex)
                .setDefaultValue(DEFAULT_soundIndex)
                .setTooltip(Text.translatable("config." + MOD_ID + ".soundIndex.description"))
                .setSaveConsumer(ModConfig::setSoundIndex)
                .build());

        return builder.setSavingRunnable(() -> {
            saveConfig(CFG_FILE);
            loadConfig(CFG_FILE);
        }).build();
    }

    public static void setSoundIndex(int input) {
        if (input < 0 || input > QuizCraft.soundEventsForPing.size()) {
            input = 0;
        }
        ModConfig.soundIndex = (byte) input;
    }

    public static void setIncludeFluids(boolean includeFluids) {
        ModConfig.includeFluids = includeFluids;
    }

    public static void setIconSize(float iconSize) {
        ModConfig.iconSize = iconSize;
    }

    public static void setPingNumEach(int pingNumEach) {
        ModConfig.pingNumEach = pingNumEach;
    }

    public static void setSecondsToVanish(long secondsToVanish) {
        ModConfig.secondsToVanish = secondsToVanish;
    }

    public static void setInfoColor(int infoColor) {
        ModConfig.infoColor = infoColor;
    }

    public static void setHighlightColor(int highlightColor) {
        ModConfig.highlightColor = highlightColor;
    }

    public static void loadConfig(File file) {
        try {
            Properties cfg = new Properties();
            if (!file.exists()) {
                saveConfig(file);
            }
            cfg.load(new FileInputStream(file));

            pingNumEach = Integer.parseInt(cfg.getProperty("pingNumEach", String.valueOf(DEFAULT_pingNumEach)));
            includeFluids = Boolean.parseBoolean(cfg.getProperty("includeFluids", String.valueOf(DEFAULT_includeFluids)));
            iconSize = Float.parseFloat(cfg.getProperty("iconSize", String.valueOf(DEFAULT_iconSize)));
            // TODO: (not important) better format for colors in config file
            infoColor = Integer.parseInt(cfg.getProperty("infoColor", String.valueOf(DEFAULT_infoColor)));
            secondsToVanish = Long.parseLong(cfg.getProperty("secondsToVanish", String.valueOf(DEFAULT_secondsToVanish)));
            highlightColor = Integer.parseInt(cfg.getProperty("highlightColor", String.valueOf(DEFAULT_highlightColor)));
            soundIndex = (byte) Integer.parseInt(cfg.getProperty("soundIndex", String.valueOf(DEFAULT_soundIndex)));

            // Re-save so that new properties will appear in old config files
            saveConfig(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(("pingNumEach=" + pingNumEach + "\n").getBytes());
            fos.write(("includeFluids=" + includeFluids + "\n").getBytes());
            fos.write(("iconSize=" + iconSize + "\n").getBytes());
            fos.write(("infoColor=" + infoColor + "\n").getBytes());
            fos.write(("secondsToVanish=" + secondsToVanish + "\n").getBytes());
            fos.write(("highlightColor=" + highlightColor + "\n").getBytes());
            fos.write(("soundIndex=" + soundIndex + "\n").getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
