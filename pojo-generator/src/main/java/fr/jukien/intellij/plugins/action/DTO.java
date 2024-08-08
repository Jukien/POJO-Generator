package fr.jukien.intellij.plugins.action;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import fr.jukien.intellij.plugins.ui.JPAMappingSettings;
import fr.jukien.intellij.plugins.ui.POJOGeneratorSettings;
import fr.jukien.intellij.plugins.util.Field;
import fr.jukien.intellij.plugins.util.TableInfo;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;

import static fr.jukien.intellij.plugins.util.Util.*;

/**
 * Created on 25/04/2019
 *
 * @author JDI
 * @version 2.6.0
 * @since 1.0.0
 */
public class DTO extends AnAction {
    private String actionText = "";

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }

        final POJOGeneratorSettings pojoGeneratorSettings = project.getService(POJOGeneratorSettings.class);
        final JPAMappingSettings jpaMappingSettings = project.getService(JPAMappingSettings.class);

        PsiElement[] psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            return;
        }

        if (null != project.getBasePath()) {
            Path projectPath = Paths.get(project.getBasePath());
            VirtualFile chooseFile = null;
            try {
                chooseFile = VfsUtil.findFileByURL(projectPath.toUri().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            if (null != pojoGeneratorSettings.getDtoFolderPath()) {
                try {
                    chooseFile = VfsUtil.findFileByURL(Paths.get(pojoGeneratorSettings.getDtoFolderPath()).toUri().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (null == lastChoosedFile) {
                return;
            } else {
                pojoGeneratorSettings.setDtoFolderPath(lastChoosedFile.getPath());
            }

            for (PsiElement psiElement : psiElements) {
                if (!(psiElement instanceof DbTable)) {
                    continue;
                }

                TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                LinkedHashSet<Field> fields = getFields((DbTable) psiElement, jpaMappingSettings);
                String className = String.format("%s%s%s", pojoGeneratorSettings.getPrefixDto(), javaName(tableInfo.getTableName(), true), pojoGeneratorSettings.getSuffixDto());

                StringBuilder javaTextFile = new StringBuilder();
                //javaTextFile.append("\n");
                String header = pojoGeneratorSettings.getHeaderDTO().replace("${CLASS_NAME}", className);

                javaTextFile.append(header).append("\n");

//                javaTextFile.append("\n");
//                javaTextFile.append("public class ").append(className).append(" {").append("\n");

                for (Field field : fields) {
                    javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                }

                javaTextFile.append("\n");
                addGetterSetter(fields, javaTextFile);
                javaTextFile.append("}").append("\n");

                String fileName = String.format("%s%s", className, ".java");
                createFile(project, javaTextFile, fileName, pojoGeneratorSettings);
            }
        }
    }

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        if (actionText.isEmpty()) {
            actionText = anActionEvent.getPresentation().getText();
        }

        checkActionVisibility(anActionEvent, actionText);
        super.update(anActionEvent);
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
