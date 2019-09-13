package fr.jukien.intellij.plugins.ui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.UUID;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class DBMS {
    @NotNull
    public abstract String getSqlDataType();

    @NotNull
    public abstract UUID getId();

    @NotNull
    public abstract DBMSFamily getFamily();

    @NotNull
    public abstract Icon getIcon();

    @NotNull
    public abstract String getJavaDataType();

    @Nullable
    public abstract String getJavaColumnDefinition();
}
