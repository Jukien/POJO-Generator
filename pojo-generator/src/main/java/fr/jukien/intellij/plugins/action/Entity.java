package fr.jukien.intellij.plugins.action;

import com.intellij.database.psi.DbTable;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
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
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import fr.jukien.intellij.plugins.ui.POJOGeneratorSettings;
import fr.jukien.intellij.plugins.util.Field;
import fr.jukien.intellij.plugins.util.TableInfo;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static fr.jukien.intellij.plugins.util.Util.*;

/**
 * Created on 19/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class Entity extends AnAction {
    private POJOGeneratorSettings settings;
    private String actionText = StringUtils.EMPTY;

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        final Project project = anActionEvent.getProject();
        if (null == project) {
            return;
        }

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
            VirtualFile chooseFile = project.getBaseDir();
            FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
            if (null != lastChoosedFile) {
                chooseFile = lastChoosedFile;
            }
            lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (null == lastChoosedFile) {
                return;
            }

            Set<Field> fields = getFields((DbTable) psiElement);

            StringBuilder javaTextFile = new StringBuilder();
            javaTextFile.append("\n");
            javaTextFile.append("import javax.persistence.*;").append("\n");

            javaTextFile.append("\n");
            javaTextFile.append("@Entity").append("\n");
            if (this.settings.getCapitalize()) {
                javaTextFile.append("@Table(name = \"").append(tableInfo.getTableName().toUpperCase()).append("\")").append("\n");
            } else {
                javaTextFile.append("@Table(name = \"").append(tableInfo.getTableName()).append("\")").append("\n");
            }
            javaTextFile.append("public class ").append(javaName(tableInfo.getTableName(), true)).append(" {").append("\n");

            for (Field field : fields) {
                if (field.getPrimary()) {
                    javaTextFile.append("    @Id").append("\n");
                }
                if (this.settings.getCapitalize()) {
                    javaTextFile.append("    @Column(name = \"").append(field.getName().toUpperCase()).append("\")").append("\n");
                } else {
                    javaTextFile.append("    @Column(name = \"").append(field.getName()).append("\")").append("\n");
                }
                javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                javaTextFile.append("\n");
            }

            addGetterSetter(fields, javaTextFile);

            PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(javaName(tableInfo.getTableName(), true) + ".java", JavaClassFileType.INSTANCE, javaTextFile);
            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(lastChoosedFile);

            Runnable r = () -> psiDirectory.add(file);

            WriteCommandAction.runWriteCommandAction(project, r);
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
}
