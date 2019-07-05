package fr.jukien.intellij.plugins.util;

import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.NameUtil;
import com.twelvemonkeys.util.LinkedSet;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 25/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class Util {
    private static final String MYSQL = "MySQL";
    private static final String ORACLE = "Oracle";
    private static final String POSTGRES = "PostgreSQL";
    private static final Map<String, Map<String, String>> map = new HashMap<>();

    static {
        map.put(MYSQL, new HashMap<>());
        map.get(MYSQL).put("bigint", "Long");
        map.get(MYSQL).put("bit", "Boolean");
        map.get(MYSQL).put("double", "Double");
        map.get(MYSQL).put("int", "Integer");
        map.get(MYSQL).put("json", "String");
        map.get(MYSQL).put("varchar", "String");

        map.put(ORACLE, new HashMap<>());
        map.get(ORACLE).put("DATE", "java.sql.Date");
        map.get(ORACLE).put("NUMBER", "Long");
        map.get(ORACLE).put("VARCHAR2", "String");

        map.put(POSTGRES, new HashMap<>());
        map.get(POSTGRES).put("bigint", "Long");
        map.get(POSTGRES).put("boolean", "Boolean");
        map.get(POSTGRES).put("date", "java.sql.Date");
        map.get(POSTGRES).put("integer", "Long");
        map.get(POSTGRES).put("varchar", "String");
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

    public static void addGetterSetter(Set<Field> fields, StringBuilder javaTextFile) {
        for (Field field : fields) {
            javaTextFile.append("\n");

            javaTextFile.append("    public ").append(field.getJavaType()).append(" get").append(javaName(field.getName(), true)).append("() {").append("\n");
            javaTextFile.append("        return this.").append(javaName(field.getName(), false)).append(";").append("\n");
            javaTextFile.append("    }").append("\n");

            javaTextFile.append("\n");

            javaTextFile.append("    public void set").append(javaName(field.getName(), true)).append("(").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(") {").append("\n");
            javaTextFile.append("        this.").append(javaName(field.getName(), false)).append(" = ").append(javaName(field.getName(), false)).append(";").append("\n");
            javaTextFile.append("    }").append("\n");
        }

        javaTextFile.append("}").append("\n");
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

    public static boolean isDatabaseSupported(PsiElement[] psiElements) {
        if (psiElements[0] instanceof DbTable) {
            return Util.getMap().containsKey(((DbTable) psiElements[0]).getDataSource().getDatabaseVersion().name);
        } else {
            return false;
        }
    }

    public static boolean checkActionVisibility(@NotNull AnActionEvent anActionEvent, String actionText) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return true;
        }

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return true;
        }

        if (isDatabaseSupported(psiElements)) {
            anActionEvent.getPresentation().setEnabled(true);
        } else {
            anActionEvent.getPresentation().setEnabled(false);
            anActionEvent.getPresentation().setText(String.format("%s : database not supported", actionText));
        }
        return false;
    }

    public static Map<String, Map<String, String>> getMap() {
        return map;
    }
}
