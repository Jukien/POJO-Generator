package fr.jukien.intellij.plugins.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.2.0
 * @since 2.0.0
 */
public class ConfigurableJPAMapping extends DBMS {
    private final UUID id;
    @NotNull
    private DBMSFamily family;
    @NotNull
    private String sqlDataType;
    @NotNull
    private String javaDataType;
    @Nullable
    private String javaColumnDefinition;
    @Nullable
    private Boolean isLengthAttributeEnabled;

    ConfigurableJPAMapping() {
        this(UUID.randomUUID(), DBMSFamily.ORACLE);
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family) {
        this(id, family, family.getSqlDataType(), family.getJavaDataType(), family.getJavaColumnDefinition(), family.isLengthAttributeEnabled());
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType) {
        this(id, family, sqlDataType, javaDataType, null, false);
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType,
                           @Nullable String javaColumnDefinition) {
        this(id, family, sqlDataType, javaDataType, javaColumnDefinition, false);
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType,
                           @Nullable Boolean isLengthAttributeEnabled) {
        this(id, family, sqlDataType, javaDataType, null, isLengthAttributeEnabled);
    }

    ConfigurableJPAMapping(@NotNull UUID id,
                           @NotNull DBMSFamily family,
                           @NotNull String sqlDataType,
                           @NotNull String javaDataType,
                           @Nullable String javaColumnDefinition,
                           @Nullable Boolean isLengthAttributeEnabled) {
        this.id = id;
        this.family = family;
        this.sqlDataType = sqlDataType;
        this.javaDataType = javaDataType;
        this.javaColumnDefinition = javaColumnDefinition;
        this.isLengthAttributeEnabled = isLengthAttributeEnabled;
    }

    @NotNull
    @Override
    public UUID getId() {
        return id;
    }

    @NotNull
    @Override
    public DBMSFamily getFamily() {
        return family;
    }

    public void setFamily(@NotNull DBMSFamily value) {
        family = value;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return family.getIcon();
    }

    @NotNull
    @Override
    public String getSqlDataType() {
        return sqlDataType;
    }

    public void setSqlDataType(@NotNull String value) {
        sqlDataType = value;
    }

    @NotNull
    @Override
    public String getJavaDataType() {
        return javaDataType;
    }

    public void setJavaDataType(@NotNull String value) {
        javaDataType = value;
    }

    @Nullable
    @Override
    public String getJavaColumnDefinition() {
        return javaColumnDefinition;
    }

    public void setJavaColumnDefinition(@Nullable String value) {
        javaColumnDefinition = value;
    }

    @Nullable
    @Override
    public Boolean isLengthAttributeEnabled() {
        return isLengthAttributeEnabled;
    }

    public void setLengthAttributeEnabled(@Nullable Boolean lengthAttributeEnabled) {
        isLengthAttributeEnabled = lengthAttributeEnabled;
    }
}
