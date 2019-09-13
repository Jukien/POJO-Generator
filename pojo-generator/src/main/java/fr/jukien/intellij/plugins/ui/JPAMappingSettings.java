package fr.jukien.intellij.plugins.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.0.0
 * @since 2.0.0
 */
@State(
        name = "JPAMappingSettings",
        storages = {
                @Storage("JPAMappingSettings.xml")}
)
public class JPAMappingSettings implements PersistentStateComponent<JPAMappingSettings> {
    private List<ConfigurableJPAMapping> jpaMappings;

    public static List<ConfigurableJPAMapping> getPredefinedJPAMappings() {
        return Arrays.asList(
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "bigint", "Long"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "bit", "Boolean"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "date", "java.sql.Date"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "datetime", "java.sql.Timestamp"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "double", "Double"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "int", "Integer"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "json", "String"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.MYSQL, "varchar", "String"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "CHAR", "String", "CHAR"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "DATE", "java.sql.Date"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "NUMBER", "Long"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.ORACLE, "VARCHAR2", "String"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.POSTGRES, "bigint", "Long"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.POSTGRES, "boolean", "Boolean"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.POSTGRES, "date", "java.sql.Date"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.POSTGRES, "integer", "Long"),
                new ConfigurableJPAMapping(UUID.randomUUID(), DBMSFamily.POSTGRES, "varchar", "String")
        );
    }

    public JPAMappingSettings() {
        jpaMappings = new ArrayList<>(getPredefinedJPAMappings());
    }

    @Nullable
    @Override
    public JPAMappingSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull JPAMappingSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public List<ConfigurableJPAMapping> getJpaMappings() {
        return jpaMappings;
    }

    public void setJpaMappings(List<ConfigurableJPAMapping> jpaMappings) {
        this.jpaMappings = jpaMappings;
    }
}