package fr.jukien.intellij.plugins.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Iconable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.5.0
 * @since 2.0.0
 */
public enum DBMSFamily implements Iconable {
    H2("H2", AllIcons.Providers.H2, "VARCHAR", "String"),
    MYSQL("MySQL", AllIcons.Providers.Mysql, "varchar", "String"),
    ORACLE("Oracle", AllIcons.Providers.Oracle, "VARCHAR2", "String"),
    POSTGRES("PostgreSQL", AllIcons.Providers.Postgresql, "varchar", "String"),
    SQLSERVER("Microsoft SQL Server", AllIcons.Providers.SqlServer, "varchar", "String");

    private final String name;
    private final Icon icon;
    private final String sqlDataType;
    private final String javaDataType;
    private final String javaColumnDefinition;
    private final Boolean isLengthAttributeEnabled;

    DBMSFamily(@NotNull String name,
               @NotNull Icon icon,
               @NotNull String sqlDataType,
               @NotNull String javaDataType) {
        this(name, icon, sqlDataType, javaDataType, null, false);
    }

    DBMSFamily(@NotNull String name,
               @NotNull Icon icon,
               @NotNull String sqlDataType,
               @NotNull String javaDataType,
               @Nullable String javaColumnDefinition,
               @Nullable Boolean isLengthAttributeEnabled) {
        this.name = name;
        this.icon = icon;
        this.sqlDataType = sqlDataType;
        this.javaDataType = javaDataType;
        this.javaColumnDefinition = javaColumnDefinition;
        this.isLengthAttributeEnabled = isLengthAttributeEnabled;
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Icon getIcon(@IconFlags int flags) {
        return getIcon();
    }

    public String getSqlDataType() {
        return sqlDataType;
    }

    public String getJavaDataType() {
        return javaDataType;
    }

    public String getJavaColumnDefinition() {
        return javaColumnDefinition;
    }

    public Boolean isLengthAttributeEnabled() {
        return isLengthAttributeEnabled;
    }
}
