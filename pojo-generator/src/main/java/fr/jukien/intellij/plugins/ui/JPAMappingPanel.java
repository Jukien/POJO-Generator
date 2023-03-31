package fr.jukien.intellij.plugins.ui;

import com.intellij.util.Function;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.IconTableCellRenderer;
import com.intellij.util.ui.table.TableModelEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import java.util.UUID;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.4.0
 * @since 2.0.0
 */
public class JPAMappingPanel {
    private JPanel panel;
    private JComponent jpaMappingTable;
    private TableModelEditor<ConfigurableJPAMapping> jpaMappingEditor;

    private static final ColumnInfo[] COLUMNS = {
            new ColumnInfo<ConfigurableJPAMapping, DBMSFamily>("DBMS") {
                @Override
                public Class getColumnClass() {
                    return DBMSFamily.class;
                }

                @NotNull
                @Override
                public DBMSFamily valueOf(ConfigurableJPAMapping configurableJPAMapping) {
                    return configurableJPAMapping.getFamily();
                }

                @Override
                public void setValue(ConfigurableJPAMapping item, DBMSFamily value) {
                    item.setFamily(value);
                }

                @NotNull
                @Override
                public TableCellRenderer getRenderer(ConfigurableJPAMapping item) {
                    return IconTableCellRenderer.ICONABLE;
                }

                @Override
                public boolean isCellEditable(ConfigurableJPAMapping item) {
                    return true;//!WebBrowserManager.getInstance().isPredefinedBrowser(item);
                }
            },
            new TableModelEditor.EditableColumnInfo<ConfigurableJPAMapping, String>("SQL data type") {
                @NotNull
                @Override
                public String valueOf(ConfigurableJPAMapping configurableJPAMapping) {
                    return configurableJPAMapping.getSqlDataType();
                }

                @Override
                public void setValue(ConfigurableJPAMapping item, String value) {
                    item.setSqlDataType(value);
                }
            },
            new TableModelEditor.EditableColumnInfo<ConfigurableJPAMapping, String>("Java data type") {
                @NotNull
                @Override
                public String valueOf(ConfigurableJPAMapping configurableJPAMapping) {
                    return configurableJPAMapping.getJavaDataType();
                }

                @Override
                public void setValue(ConfigurableJPAMapping item, String value) {
                    item.setJavaDataType(value);
                }
            },
            new TableModelEditor.EditableColumnInfo<ConfigurableJPAMapping, String>("Java columnDefinition attribute") {
                @Nullable
                @Override
                public String valueOf(ConfigurableJPAMapping configurableJPAMapping) {
                    return configurableJPAMapping.getJavaColumnDefinition();
                }

                @Override
                public void setValue(ConfigurableJPAMapping item, String value) {
                    item.setJavaColumnDefinition(value);
                }
            },
            new TableModelEditor.EditableColumnInfo<ConfigurableJPAMapping, Boolean>("Java length attribute") {
                @Nullable
                @Override
                public Boolean valueOf(ConfigurableJPAMapping configurableJPAMapping) {
                    return configurableJPAMapping.isLengthAttributeEnabled();
                }

                @Override
                public Class<?> getColumnClass() {
                    return Boolean.class;
                }

                @Override
                public void setValue(ConfigurableJPAMapping configurableJPAMapping, Boolean value) {
                    configurableJPAMapping.setLengthAttributeEnabled(value);
                }
            }
    };

    public JPAMappingPanel() {
    }

    private void createUIComponents() {
        TableModelEditor.DialogItemEditor<ConfigurableJPAMapping> itemEditor = new TableModelEditor.DialogItemEditor<ConfigurableJPAMapping>() {
            @NotNull
            @Override
            public Class<ConfigurableJPAMapping> getItemClass() {
                return ConfigurableJPAMapping.class;
            }

            @Override
            public ConfigurableJPAMapping clone(@NotNull ConfigurableJPAMapping item, boolean forInPlaceEditing) {
                return new ConfigurableJPAMapping(forInPlaceEditing ? item.getId() : UUID.randomUUID(),
                        item.getFamily(), item.getSqlDataType(), item.getJavaDataType(), item.getJavaColumnDefinition(), item.isLengthAttributeEnabled());
            }

            @Override
            public void edit(@NotNull ConfigurableJPAMapping item, @NotNull Function<? super ConfigurableJPAMapping, ? extends ConfigurableJPAMapping> mutator, boolean isAdd) {

            }

            @Override
            public void applyEdited(@NotNull ConfigurableJPAMapping oldItem, @NotNull ConfigurableJPAMapping newItem) {
                //oldItem.setSpecificSettings(newItem.getSpecificSettings());
            }

            @Override
            public boolean isEditable(@NotNull ConfigurableJPAMapping browser) {
                return false;//browser.getSpecificSettings() != null;
            }

            @Override
            public boolean isRemovable(@NotNull ConfigurableJPAMapping item) {
                return true;//!WebBrowserManager.getInstance().isPredefinedBrowser(item);
            }
        };
        jpaMappingEditor = new TableModelEditor<>(COLUMNS, itemEditor, "No JPA mapping configured")
                .modelListener(new TableModelEditor.DataChangedListener<ConfigurableJPAMapping>() {
                    @Override
                    public void tableChanged(@NotNull TableModelEvent event) {
                        update();
                    }

                    @Override
                    public void dataChanged(@NotNull ColumnInfo<ConfigurableJPAMapping, ?> columnInfo, int rowIndex) {
                        /*if (columnInfo == PATH_COLUMN_INFO || columnInfo == ACTIVE_COLUMN_INFO) {
                            update();
                        }*/
                    }

                    private void update() {
                        /*if (getDefaultBrowser() == DefaultBrowserPolicy.FIRST) {
                            setCustomPathToFirstListed();
                        }*/
                    }
                });
        jpaMappingTable = jpaMappingEditor.createComponent();
    }

    public JPanel getPanel() {
        return panel;
    }

    public TableModelEditor<ConfigurableJPAMapping> getJpaMappingEditor() {
        return jpaMappingEditor;
    }
}
