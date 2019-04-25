package fr.jukien.intellij.plugins.ui;

import javax.swing.*;

/**
 * Created on 24/04/2019
 *
 * @author JDI
 * @version 1.0.0
 * @since 1.0.0
 */
public class POJOGeneratorConfigurableGUI {
    private JCheckBox capitalizeTheNameOfCheckBox;

    public POJOGeneratorConfigurableGUI(POJOGeneratorSettings pojoGeneratorSettings) {
        // Initialise l'interface graphique avec les settings qui ont été enregistrés avant
        capitalizeTheNameOfCheckBox.setSelected(pojoGeneratorSettings.getCapitalize());
    }

    public JCheckBox getCapitalizeTheNameOfCheckBox() {
        return capitalizeTheNameOfCheckBox;
    }
}
