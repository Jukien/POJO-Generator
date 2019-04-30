package fr.jukien.intellij.plugins.action;

import com.intellij.database.psi.DbTable;
import com.intellij.ide.highlighter.JavaClassFileType;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.command.WriteCommandAction;
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
import fr.jukien.intellij.plugins.util.Field;
import fr.jukien.intellij.plugins.util.TableInfo;

import java.util.Set;

import static fr.jukien.intellij.plugins.util.Util.getFields;
import static fr.jukien.intellij.plugins.util.Util.javaName;

/**
 * Created on 25/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class DTO extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();

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
            javaTextFile.append("\n");
            javaTextFile.append("import javax.persistence.*;").append("\n");

            javaTextFile.append("\n");
            javaTextFile.append("public class ").append(javaName(tableInfo.getTableName(), true)).append("DTO {").append("\n");

            for (Field field : fields) {
                javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
            }

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


            PsiFile file = PsiFileFactory.getInstance(project).createFileFromText(javaName(tableInfo.getTableName(), true) + "DTO.java", JavaClassFileType.INSTANCE, javaTextFile);
            PsiDirectory psiDirectory = PsiDirectoryFactory.getInstance(project).createDirectory(chooseFile);

            Runnable r = () -> psiDirectory.add(file);

            WriteCommandAction.runWriteCommandAction(project, r);
        }
    }
}
