package net.minestom.generators.loot_tables;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import net.minestom.datagen.JsonOutputter;
import net.minestom.generators.common.DataGeneratorCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class GameplayLootTableGenerator extends DataGeneratorCommon<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameplayLootTableGenerator.class);

    @Override
    public void generateNames() {
        // Not required for block loot tables
    }

    @Override
    public JsonObject generate() {
        File lootTablesFolder = new File(dataFolder, "loot_tables");
        File gameplayTables = new File(lootTablesFolder, "gameplay");
        File[] listedFiles = gameplayTables.listFiles();
        if (listedFiles != null) {
            List<File> children = new ArrayList<>(Arrays.asList(listedFiles));
            JsonObject gameplayLootTables = new JsonObject();
            for (int i = 0; i < children.size(); i++) {
                File file = children.get(i);
                // Add subdirectories files to the for-loop.
                if (file.isDirectory()) {
                    File[] subChildren = file.listFiles();
                    if (subChildren != null) {
                        children.addAll(Arrays.asList(subChildren));
                    }
                    continue;
                }
                JsonObject gameplayLootTable;
                try {
                    gameplayLootTable = JsonOutputter.GSON.fromJson(new JsonReader(new FileReader(file)), JsonObject.class);
                } catch (FileNotFoundException e) {
                    LOGGER.error("Failed to read gameplay loot table located at '" + file + "'.", e);
                    continue;
                }
                String fileName = file.getAbsolutePath().substring(gameplayTables.getAbsolutePath().length() + 1);
                // Make sure we use the correct slashes.
                fileName = fileName.replace("\\", "/");
                // Remove .json by removing last 5 chars of the name.
                String tableName = fileName.substring(0, fileName.length() - 5);
                gameplayLootTables.add("minecraft:" + tableName, gameplayLootTable);
            }
            return gameplayLootTables;
        } else {
            LOGGER.error("Failed to find gameplay loot tables in data folder.");
            return new JsonObject();
        }
    }
}
