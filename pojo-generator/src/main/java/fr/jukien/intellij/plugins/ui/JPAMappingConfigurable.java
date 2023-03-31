package fr.jukien.intellij.plugins.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created on 24/08/2019
 *
 * @author JDI
 * @version 2.4.0
 * @since 2.0.0
 */
public class JPAMappingConfigurable implements Configurable {
    private final JPAMappingSettings jpaMappingSettings;
    private JPAMappingPanel jpaMappingPanel;

    public JPAMappingConfigurable(Project project) {
        this.jpaMappingSettings = project.getService(JPAMappingSettings.class);
    }

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "JPA Mapping";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == jpaMappingPanel) {
            jpaMappingPanel = new JPAMappingPanel();
        }
        return jpaMappingPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return !jpaMappingPanel.getJpaMappingEditor().getModel().getItems().equals(jpaMappingSettings.getJpaMappings());
    }

    @Override
    public void apply() {
        jpaMappingSettings.setJpaMappings(jpaMappingPanel.getJpaMappingEditor().apply());
    }

    public void reset() {
        jpaMappingPanel.getJpaMappingEditor().reset(jpaMappingSettings.getJpaMappings());
    }
}
