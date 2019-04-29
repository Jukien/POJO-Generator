package fr.jukien.intellij.plugins.util;

import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.codeStyle.NameUtil;
import com.twelvemonkeys.util.LinkedSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 25/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class Util {
    private static final String POSTGRES = "PostgreSQL";
    private static final Map<String, Map<String, String>> map = new HashMap<>();

    static {
        map.put(POSTGRES, new HashMap<>());
        map.get(POSTGRES).put("bigint", "Long");
        map.get(POSTGRES).put("integer", "Long");
        map.get(POSTGRES).put("boolean", "Boolean");
        map.get(POSTGRES).put("varchar", "String");
        map.get(POSTGRES).put("date", "java.sql.Date");
    }

    public static LinkedSet<Field> getFields(DbTable dbTable) {
        LinkedSet<Field> fields = new LinkedSet<>();
        for (DasColumn column : DasUtil.getColumns(dbTable)) {
            Field field = new Field();
            field.setName(column.getName());
            field.setSQLType(column.getDataType());
            field.setJavaType(map.get(dbTable.getDataSource().getDatabaseVersion().name).get(column.getDataType().typeName));
            field.setPrimary(DasUtil.isPrimary(column));
            fields.add(field);
        }
        return fields;
    }

    public static String javaName(String str, Boolean capitalizeFirstLetter) {
        String[] strings = NameUtil.splitNameIntoWords(str);
        StringBuilder name = new StringBuilder();

        for (int i = 0; strings.length > i; i++) {
            if (i == 0) {
                if (capitalizeFirstLetter) {
                    name.append(convertToTitleCaseIteratingChars(strings[i]));
                } else {
                    name.append(strings[i].toLowerCase());
                }
            } else {
                name.append(convertToTitleCaseIteratingChars(strings[i]));
            }
        }
        return name.toString();
    }

    public static String convertToTitleCaseIteratingChars(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder converted = new StringBuilder();

        boolean convertNext = true;
        for (char ch : text.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            converted.append(ch);
        }

        return converted.toString();
    }
}
