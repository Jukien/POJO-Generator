package fr.jukien.intellij.plugins;

import com.intellij.database.model.DasColumn;
import com.intellij.database.psi.DbTable;
import com.intellij.database.util.DasUtil;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.twelvemonkeys.util.LinkedSet;
import fr.jukien.intellij.plugins.ui.POJOGeneratorSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created on 19/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class EntityPOJO extends AnAction {
    private POJOGeneratorSettings settings;
    private static final String POSTGRES = "PostgreSQL";
    private static final Map<String, Map<String, String>> map = new HashMap<>();

    static {
        map.put(POSTGRES, new HashMap<>());
        map.get(POSTGRES).put("bigint", "Long");
        map.get(POSTGRES).put("boolean", "Boolean");
        map.get(POSTGRES).put("varchar", "String");
        map.get(POSTGRES).put("date", "java.sql.Date");
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        this.settings = ServiceManager.getService(project, POJOGeneratorSettings.class);

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                continue;
            }

            TableInfo tableInfo = new TableInfo((DbTable) psiElement);

            final DataContext dataContext = anActionEvent.getDataContext();
            assert project != null;
            final PsiFile currentFile = DataKeys.PSI_FILE.getData(dataContext);
            VirtualFile chooseFile = project.getBaseDir();
            if (currentFile != null) {
                chooseFile = currentFile.getVirtualFile();
            }
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            chooseFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (chooseFile == null) {
                return;
            }

            Set<Field> fields = getFields((DbTable) psiElement);

            StringBuilder javaTextFile = new StringBuilder();
            //javaTextFile.append("package com.sample;").append("\n");
            javaTextFile.append("\n");
            javaTextFile.append("import javax.persistence.*;").append("\n");

            javaTextFile.append("\n");
            javaTextFile.append("@Entity").append("\n");
            if (this.settings.getCapitalize()) {
                javaTextFile.append("@Table(name=\"").append(tableInfo.getTableName().toUpperCase()).append("\")").append("\n");
            } else {
                javaTextFile.append("@Table(name=\"").append(tableInfo.getTableName()).append("\")").append("\n");
            }
            javaTextFile.append("public class ").append(javaName(tableInfo.getTableName(), true)).append(" {").append("\n");

            for (Field field : fields) {
                if (field.getPrimary()) {
                    javaTextFile.append("    @Id").append("\n");
                }
                if (this.settings.getCapitalize()) {
                    javaTextFile.append("    @Column(name=\"").append(field.getName().toUpperCase()).append("\")").append("\n");
                } else {
                    javaTextFile.append("    @Column(name=\"").append(field.getName()).append("\")").append("\n");
                }
                javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                javaTextFile.append("\n");
            }

            javaTextFile.append("\n");

            for (Field field : fields) {
                javaTextFile.append("    public ").append(field.getJavaType()).append(" get").append(javaName(field.getName(), true)).append("() {").append("\n");
                javaTextFile.append("        return this.").append(javaName(field.getName(), false)).append(";").append("\n");
                javaTextFile.append("    }").append("\n");

                javaTextFile.append("\n");

                javaTextFile.append("    public void set").append(javaName(field.getName(), true)).append("(").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(") {").append("\n");
                javaTextFile.append("        this.").append(javaName(field.getName(), false)).append(" = ").append(javaName(field.getName(), false)).append(";").append("\n");
                javaTextFile.append("    }").append("\n");
            }

            javaTextFile.append("}").append("\n");


            PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(javaName(tableInfo.getTableName(), true) + ".java", JavaClassFileType.INSTANCE, javaTextFile);
            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(chooseFile);

            Runnable r = () -> psiDirectory.add(file);

            WriteCommandAction.runWriteCommandAction(project, r);
        }
    }

    public LinkedSet<Field> getFields(DbTable dbTable) {
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

    public String javaName(String str, Boolean capitalizeFirstLetter) {
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
