package me.piitex.renjava.api.saves;

import me.piitex.renjava.RenJava;
import me.piitex.renjava.addons.Addon;
import me.piitex.renjava.api.saves.data.Data;
import me.piitex.renjava.api.saves.data.PersistentData;
import me.piitex.renjava.api.saves.exceptions.SaveFileEncryptedState;
import me.piitex.renjava.api.saves.file.SaveFileState;
import me.piitex.renjava.api.saves.file.SectionKeyValue;
import me.piitex.renjava.loggers.RenLogger;
import me.piitex.renjava.tasks.Tasks;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class SaveManager {
    private final Save save;

    private SectionKeyValue sceneSection;

    public SaveManager(Save save) {
        this.save = save;
    }

    // Loads save file
    public void load(boolean process) {

        if (RenJava.CONFIGURATION.isEncryptSaves())
            save.decrypt(); // Decrypts the save so it can load

        try {
            save.canModify();
        } catch (SaveFileEncryptedState e) {
            RenLogger.LOGGER.error("Unable to load save file!", e);
            return;
        }

        RenLogger.LOGGER.info("Loading save slot-'{}'", save.getSlot());

        String fullData = null;
        try {
            fullData = new Scanner(save.getFile()).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            // Only error if they are trying to process the loading function.
            if (process) {
                RenLogger.LOGGER.error("Save file does not exist!", e);
                RenJava.writeStackTrace(e);
            }
        }

        if (fullData == null) {
            return;
        }

        // Gather save data
        String[] newLine = fullData.split("\n");
        for (String section : newLine) {
            if (section.startsWith("name: ")) {
                save.setName(section.split(":")[1].trim());
            }
            if (section.startsWith("updated: ")) {
                save.setUpdatedTime(Long.parseLong(section.split(":")[1].trim()));
            }
        }

        Collection<PersistentData> allData = RenJava.getInstance().getRegisteredData();
        for (Addon addon : RenJava.ADDONLOADER.getAddons()) {
            allData.addAll(addon.getRegisteredData());
        }

        for (PersistentData persistentData : allData) {
            String clazzName = persistentData.getClass().getName();
            SectionKeyValue rootSection = new SectionKeyValue(clazzName);
            SectionKeyValue mapSection = null;

            String[] classSplit = fullData.split(clazzName + ":");
            String fields = classSplit[1];
            String[] fieldSplit = fields.split("\n");
            for (String field : fieldSplit) {
                if (field.trim().isEmpty()) continue;
                String[] keyValueSplit = field.split(":");
                String key = keyValueSplit[0];
                if (keyValueSplit.length == 1) {
                    if (key.startsWith("    ") || key.startsWith("\t")) {
                        mapSection = new SectionKeyValue(key.trim());
                        rootSection.addSubSection(mapSection);
                        continue;
                    } else {
                        break;
                    }
                }
                String value = keyValueSplit[1];
                if ((key.startsWith("    ") || key.startsWith("\t")) && value.trim().isEmpty()) {
                    mapSection = new SectionKeyValue(key.trim());
                    rootSection.addSubSection(mapSection);
                } else if (value.trim().isEmpty()) {
                    break;
                } else if (key.startsWith("\t\t") || key.startsWith("        ")) {
                    if (mapSection != null) {
                        mapSection.addKeyValue(key.trim(), value.trim());
                    }
                } else if (key.startsWith("\t") || key.startsWith("    ")) {
                    if (value.contains(",") || value.contains("[")) {
                        if (value.contains("[")) {
                            value = value.replace("[", "").replace("]", "");
                        }
                        String[] arraySplit = value.trim().split(",");
                        rootSection.addArray(key.trim(), arraySplit);
                        continue;
                    }
                    rootSection.addKeyValue(key.trim(), value.trim());
                }
            }
            if (process) {
                processSection(persistentData, rootSection);
            } else {
                if (rootSection.getSection().contains("me.piitex.renjava.api.player.Player")) {
                    this.sceneSection = rootSection;
                }
            }
        }

        RenLogger.LOGGER.info("Loaded save '{}", save.getName());

        // Re-encrypt the save if necessary

        // Necessary to run on JavaFX to prevent rendering issues.
        if (RenJava.CONFIGURATION.isEncryptSaves())
            save.encrypt();

    }


    private void processSection(PersistentData persistentData, SectionKeyValue rootSection) {
        List<Field> fields = new ArrayList<>(List.of(persistentData.getClass().getDeclaredFields()));
        fields.addAll(List.of(persistentData.getClass().getFields()));
        for (Field field : fields) {
            if (field.isAnnotationPresent(Data.class)) {
                field.setAccessible(true);
                if (field.getGenericType().getTypeName().contains("Map<")) {
                    // This is a map load. Scan for subsections if the field name matches. Add all data
                    for (SectionKeyValue subSection : rootSection.getSubSections()) {
                        if (subSection.getSection().equalsIgnoreCase(field.getName())) {
                            // Convert Map<String, String> to whatever the map of the object is
                            // Set the current values to whatever the load file is
                            deconstructMap(subSection, persistentData, field);
                        }
                    }
                }

                String keyToSet = (String) rootSection.getKeyValueMap().keySet().stream().filter(key -> key.toString().equalsIgnoreCase(field.getName())).findAny().orElse(null);
                if (keyToSet != null) {
                    try {
                        // Casting string might not be a good idea.
                        String value = (String) rootSection.getKeyValueMap().get(keyToSet);
                        setField(field, persistentData, value.strip());
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        RenLogger.LOGGER.error("Could not set field for save file!", e);
                        RenJava.writeStackTrace(e);
                    }
                } else {
                    RenLogger.LOGGER.error("Could not set key '{}'", field.getName());
                }
            }
        }
    }

    private void deconstructMap(SectionKeyValue subSection, PersistentData data, Field field) {
        Map<Object, Object> objectMap;
        try {
            objectMap = (Map<Object, Object>) field.get(data);
        } catch (IllegalAccessException e) {
            objectMap = new HashMap<>();
        }

        if (objectMap == null) {
            objectMap = new HashMap<>();
        }


        // TODO: 3/3/2024 Convert generic map type to actual type using the Mapper class
        Map<Object, Object> finalObjectMap = objectMap;
        subSection.getKeyValueMap().forEach((key, value) -> {
            finalObjectMap.put(key, value);
        });
    }

    private void setField(Field field, PersistentData data, String string2) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        Type type = field.getGenericType();
        if (type.getTypeName().toLowerCase().contains("string")) {
            field.set(data, string2);
        } else if (type.getTypeName().toLowerCase().contains("int")) {
            field.set(data, Integer.parseInt(string2));
        } else if (type.getTypeName().toLowerCase().contains("boolean")) {
            field.set(data, Boolean.parseBoolean(string2));
        } else if (type.getTypeName().toLowerCase().contains("double")) {
            field.set(data, Double.parseDouble(string2));
        } else if (type.getTypeName().toLowerCase().contains("float")) {
            field.set(data, Float.parseFloat(string2));
        } else if (type.getTypeName().toLowerCase().contains("long")) {
            field.set(data, Long.parseLong(string2));
        } else if (type.getTypeName().toLowerCase().contains("short")) {
            field.set(data, Short.parseShort(string2));
        } else if (type.getTypeName().toLowerCase().contains("byte")) {
            field.set(data, Byte.parseByte(string2));
        }
    }

    public void update() {
        if (RenJava.CONFIGURATION.isEncryptSaves()) {
            // Assume file is encrypted
            save.decrypt();
        }

        try {
            save.canModify();
        } catch (SaveFileEncryptedState e) {
            RenLogger.LOGGER.error("Unable to update save file!", e);
            return;
        }

        // Update the save file without re-writing the data.
        // Only useful for applying name or creation time.
        String data = save.retrieveCurrentData();
        String otherName = data.split("name: ")[1].split("\n")[0];
        data = data.replace("name: " + otherName, "name: " + save.getName());
        FileWriter fileWriter = null;
        if (!save.exists()) {
            try {
                save.getFile().createNewFile();
            } catch (IOException e) {
                RenLogger.LOGGER.error("Could not create save file: {}", e.getMessage());
                return;
            }
        }
        try {
            fileWriter = new FileWriter(save.getFile(), false);
            fileWriter.write(data);
        } catch (IOException e) {
            RenLogger.LOGGER.error("Failed to write to save file!", e);
            RenJava.writeStackTrace(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    RenLogger.LOGGER.error("Failed to write to close stream!", e);
                }
            }
        }
    }

    // Writes save file
    public void write() {
        // Set to decrypted state when writing. The new values will be unencrypted.
        save.setFileState(SaveFileState.DECRYPTED);

        try {
            save.canModify();
        } catch (SaveFileEncryptedState e) {
            RenLogger.LOGGER.error("Unable to write save file!", e);
            return;
        }

        save.setUpdatedTime(System.currentTimeMillis());
        // First get all PersistentData to write.
        // me.piitex.MyDataClass:
        //    field1: data1
        //    map:
        //        key1: value1
        //        key2: value2

        StringBuilder appendString = new StringBuilder();
        appendString.append("# Please do not modify the save file. Doing so, may cause unexpected errors and crashes.").append("\n");
        appendString.append("name: ").append(save.getName()).append("\n"); // Appends slot name
        appendString.append("updated: ").append(save.getUpdatedTime()).append("\n");

        Collection<PersistentData> allData = RenJava.getInstance().getRegisteredData();
        for (Addon addon : RenJava.ADDONLOADER.getAddons()) {
            allData.addAll(addon.getRegisteredData());
        }

        for (PersistentData data : allData) {
            SectionKeyValue rootSection = new SectionKeyValue(data.getClass().getName());
            Class<?> claz = data.getClass();
            if (appendString.toString().contains(claz.getName())) {
                continue;
            }
            List<Field> fields = new ArrayList<>(List.of(claz.getDeclaredFields()));
            fields.addAll(List.of(claz.getFields()));
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Data.class)) {
                    SectionKeyValue mapSection = handleMap(data, field);
                    if (mapSection != null) {
                        rootSection.addSubSection(mapSection);

                        continue;
                    }
                    try {
                        Object object = field.get(data);
                        if (object == null) {
                            RenLogger.LOGGER.warn("Value for '" + field.getName() + "' is null. Will not process data.");
                            continue;
                        }
                        rootSection.addKeyValue(field.getName(), object.toString());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (IllegalArgumentException ignored) {
                        rootSection.addKeyValue(field.getName(), "null");
                    }
                }
            }
            appendString.append(rootSection);
        }

        FileWriter fileWriter = null;
        if (!save.getFile().exists()) {
            try {
                save.getFile().createNewFile();
            } catch (IOException e) {
                RenLogger.LOGGER.error("Could not create save file: {}", e.getMessage());
                return;
            }
        }
        try {
            fileWriter = new FileWriter(save.getFile());
            fileWriter.write(appendString.toString());
        } catch (IOException e) {
            RenLogger.LOGGER.error("Failed to write to save file!", e);
            RenJava.writeStackTrace(e);
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    if (RenJava.CONFIGURATION.isEncryptSaves())
                        save.encrypt();
                } catch (IOException e) {
                    RenLogger.LOGGER.error("Failed to write to close stream!", e);
                }
            }
        }
    }

    private SectionKeyValue handleMap(PersistentData data, Field field) {
        if (field.getGenericType().getTypeName().contains("Map<")) {
            try {
                SectionKeyValue sectionKeyValue = new SectionKeyValue(field.getName());
                reconstructMap(sectionKeyValue, data, field);
                return sectionKeyValue;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private void reconstructMap(SectionKeyValue sectionKeyValue, PersistentData data, Field field) throws IllegalAccessException {
        Map<Object, Object> map = (Map<Object, Object>) field.get(data);
        map.entrySet().forEach(objectObjectEntry -> {
            sectionKeyValue.addKeyValue(objectObjectEntry.getKey().toString(), objectObjectEntry.getValue().toString());
        });
    }

    public SectionKeyValue getSceneSection() {
        return sceneSection;
    }
}
