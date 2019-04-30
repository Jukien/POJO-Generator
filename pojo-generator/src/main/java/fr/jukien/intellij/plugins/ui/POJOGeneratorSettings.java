package fr.jukien.intellij.plugins.ui;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created on 25/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
@State(
        name = "POJOGeneratorSettings",
        storages = {
                @Storage("POJOGeneratorSettings.xml")}
)
public class POJOGeneratorSettings implements PersistentStateComponent<POJOGeneratorSettings> {
    private Boolean capitalize;

    public POJOGeneratorSettings() {
        capitalize = false;
    }

    @Nullable
    @Override
    public POJOGeneratorSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull POJOGeneratorSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public Boolean getCapitalize() {
        return capitalize;
    }

    public void setCapitalize(Boolean capitalize) {
        this.capitalize = capitalize;
    }
}
