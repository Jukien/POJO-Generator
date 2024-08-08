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
 * Created on 19/04/2019
 *
 * @author JDI
 * @version 2.6.0
 * @since 1.0.0
 */
public class Entity extends AnAction {
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
            if (null != pojoGeneratorSettings.getEntityFolderPath()) {
                try {
                    chooseFile = VfsUtil.findFileByURL(Paths.get(pojoGeneratorSettings.getEntityFolderPath()).toUri().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            lastChoosedFile = FileChooser.chooseFile(descriptor, project, chooseFile);
            if (null == lastChoosedFile) {
                return;
            } else {
                pojoGeneratorSettings.setEntityFolderPath(lastChoosedFile.getPath());
            }

            for (PsiElement psiElement : psiElements) {
                if (!(psiElement instanceof DbTable)) {
                    continue;
                }

                TableInfo tableInfo = new TableInfo((DbTable) psiElement);
                LinkedHashSet<Field> fields = getFields((DbTable) psiElement, jpaMappingSettings);

                String header;
                if (pojoGeneratorSettings.getGenerateCompositePrimaryKey() && pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithIdClassAnnotation()) {
                    header = pojoGeneratorSettings.getHeaderEntityIdClass();
                } else {
                    header = pojoGeneratorSettings.getHeaderEntity();
                }

                String classNameComposite = null;
                if (isCompositePrimaryKeyAvailable(pojoGeneratorSettings, tableInfo)) {
                    classNameComposite = String.format("%s%s%s", pojoGeneratorSettings.getPrefixCompositePrimaryKey(), javaName(tableInfo.getTableName(), true), pojoGeneratorSettings.getSuffixCompositePrimaryKey());

                    header = header.replace("${ID_CLASS_NAME}", classNameComposite);

                    StringBuilder javaTextFile = new StringBuilder();
                    javaTextFile.append("\n");

                    /*if (pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithEmbeddedIdAnnotation()) {
                        javaTextFile.append("import javax.persistence.*;").append("\n");
                    }*/
                    javaTextFile.append("import java.io.Serializable;").append("\n");

                    javaTextFile.append("\n");
                    if (pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithEmbeddedIdAnnotation()) {
                        javaTextFile.append("@Embeddable").append("\n");
                    }
                    javaTextFile.append("public class ").append(classNameComposite).append(" implements Serializable {").append("\n");

                    LinkedHashSet<Field> primaryFields = new LinkedHashSet<>();
                    int index = 0;
                    for (Field field : fields) {
                        if (field.getPrimary()) {
                            primaryFields.add(field);

                            if (pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithEmbeddedIdAnnotation()) {
                                if (pojoGeneratorSettings.getCapitalize()) {
                                    if (pojoGeneratorSettings.getWithRelationshipAnnotations() && field.getForeignKey()) {
                                        javaTextFile.append("    @ManyToOne").append("\n");
                                        javaTextFile.append("    @JoinColumn(name = \"").append(field.getName().toUpperCase()).append("\"");
                                    } else {
                                        javaTextFile.append("    @Column(name = \"").append(field.getName().toUpperCase()).append("\"");
                                        addColumnAnnotationAttributes(javaTextFile, field);
                                    }
                                } else {
                                    if (pojoGeneratorSettings.getWithRelationshipAnnotations() && field.getForeignKey()) {
                                        javaTextFile.append("    @ManyToOne").append("\n");
                                        javaTextFile.append("    @JoinColumn(name = \"").append(field.getName()).append("\"");
                                    } else {
                                        javaTextFile.append("    @Column(name = \"").append(field.getName()).append("\"");
                                        addColumnAnnotationAttributes(javaTextFile, field);
                                    }
                                }
                                javaTextFile.append(")").append("\n");
                            }
                            javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                            if (pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithEmbeddedIdAnnotation() && index < tableInfo.getPrimaryKeys().size() - 1) {
                                javaTextFile.append("\n");
                            }
                            index++;
                        }
                    }

                    javaTextFile.append("\n");
                    addConstructor(classNameComposite, new LinkedHashSet<>(), javaTextFile);
                    javaTextFile.append("\n");
                    addConstructor(classNameComposite, primaryFields, javaTextFile);
                    javaTextFile.append("\n");
                    addGetterSetter(primaryFields, javaTextFile);
                    javaTextFile.append("}").append("\n");

                    String fileName = String.format("%s%s", classNameComposite, ".java");
                    createFile(project, javaTextFile, fileName, pojoGeneratorSettings);
                }

                String className = String.format("%s%s%s", pojoGeneratorSettings.getPrefixEntity(), javaName(tableInfo.getTableName(), true), pojoGeneratorSettings.getSuffixEntity());

                StringBuilder javaTextFile = new StringBuilder();
//                javaTextFile.append("\n");
                if (pojoGeneratorSettings.getCapitalize()) {
                    header = header.replace("${TABLE_NAME}", tableInfo.getTableName().toUpperCase());
                } else {
                    header = header.replace("${TABLE_NAME}", tableInfo.getTableName());
                }
                header = header.replace("${SCHEMA_NAME}", tableInfo.getSchemaName());
                header = header.replace("${CLASS_NAME}", className);

                javaTextFile.append(header).append("\n");

//                javaTextFile.append("\n");
//                javaTextFile.append("@Entity").append("\n");
                /*if (isCompositePrimaryKeyAvailable(pojoGeneratorSettings, tableInfo) && pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithIdClassAnnotation()) {
                    javaTextFile.append("@IdClass(").append(classNameComposite).append(".class)").append("\n");
                }*/
                if (isCompositePrimaryKeyAvailable(pojoGeneratorSettings, tableInfo) && pojoGeneratorSettings.getGenerateCompositePrimaryKeyWithEmbeddedIdAnnotation()) {
                    LinkedHashSet<Field> fieldsWithoutPrimary = new LinkedHashSet<>();

                    Field field = new Field();
                    field.setEmbeddedId(true);
                    field.setJavaType(classNameComposite);
                    field.setName(classNameComposite);
                    fieldsWithoutPrimary.add(field);

                    for (Field field1 : fields) {
                        if (!field1.getPrimary()) {
                            fieldsWithoutPrimary.add(field1);
                        }
                    }
                    fields.clear();
                    fields.addAll(fieldsWithoutPrimary);
                }
                /*if (pojoGeneratorSettings.getCapitalize()) {
                    javaTextFile.append("@Table(name = \"").append(tableInfo.getTableName().toUpperCase());
                } else {
                    javaTextFile.append("@Table(name = \"").append(tableInfo.getTableName());
                }*/
                /*if (pojoGeneratorSettings.getWithSchemaAttribute()) {
                    javaTextFile.append("\", schema = \"").append(tableInfo.getSchemaName());
                }*/
//                javaTextFile.append("\")").append("\n");
//                javaTextFile.append("public class ").append(className).append(" {").append("\n");

                for (Field field : fields) {
                    if (field.getEmbeddedId()) {
                        javaTextFile.append("    @EmbeddedId").append("\n");
                    } else {
                        if (field.getPrimary()) {
                            javaTextFile.append("    @Id").append("\n");
                        }
                        if (pojoGeneratorSettings.getAutoGenerated() && field.getAutoGenerated()) {
                            javaTextFile.append("    @GeneratedValue(strategy = GenerationType.IDENTITY)").append("\n");
                        }
                        if (pojoGeneratorSettings.getCapitalize()) {
                            if (pojoGeneratorSettings.getWithRelationshipAnnotations() && field.getForeignKey()) {
                                javaTextFile.append("    @ManyToOne").append("\n");
                                javaTextFile.append("    @JoinColumn(name = \"").append(field.getName().toUpperCase()).append("\"");
                            } else {
                                javaTextFile.append("    @Column(name = \"").append(field.getName().toUpperCase()).append("\"");
                                addColumnAnnotationAttributes(javaTextFile, field);
                            }
                        } else {
                            if (pojoGeneratorSettings.getWithRelationshipAnnotations() && field.getForeignKey()) {
                                javaTextFile.append("    @ManyToOne").append("\n");
                                javaTextFile.append("    @JoinColumn(name = \"").append(field.getName()).append("\"");
                            } else {
                                javaTextFile.append("    @Column(name = \"").append(field.getName()).append("\"");
                                addColumnAnnotationAttributes(javaTextFile, field);
                            }
                        }
                        javaTextFile.append(")").append("\n");
                    }
                    javaTextFile.append("    private ").append(field.getJavaType()).append(" ").append(javaName(field.getName(), false)).append(";").append("\n");
                    javaTextFile.append("\n");
                }

                addGetterSetter(fields, javaTextFile);
                javaTextFile.append("}").append("\n");

                String fileName = String.format("%s%s", className, ".java");
                createFile(project, javaTextFile, fileName, pojoGeneratorSettings);
            }
        }
    }

    private void addColumnAnnotationAttributes(StringBuilder javaTextFile, Field field) {
        if (null != field.getColumnDefinition()) {
            javaTextFile.append(", columnDefinition = \"");
            javaTextFile.append(field.getColumnDefinition());
            javaTextFile.append("\"");
        }
        if (null != field.getLength()) {
            javaTextFile.append(", length = ");
            javaTextFile.append(field.getLength());
        }
    }

    private boolean isCompositePrimaryKeyAvailable(POJOGeneratorSettings pojoGeneratorSettings, TableInfo tableInfo) {
        return pojoGeneratorSettings.getGenerateCompositePrimaryKey() && tableInfo.getPrimaryKeys().size() > 1;
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
