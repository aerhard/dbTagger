package com.aerhard.oxygen.plugin.dbtagger;

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.aerhard.oxygen.plugin.dbtagger.config.ConfigStore;

@RunWith(MockitoJUnitRunner.class)
public class i18nTest {

    /**
     * Tests the default language file.
     */
    @Test
    public void testDefaultLanguage() {
        Locale.setDefault(Locale.ENGLISH);
        ResourceBundle i18n = ResourceBundle.getBundle("Tagger");
        assertEquals(ConfigStore.ITEM_LENGTH,
                i18n.getString("configTable.tableHeaders").split(",").length);
        assertEquals("Configure ...", i18n.getString("configDialog.configure"));
    }

    /**
     * Tests the German language file.
     */
    @Test
    public void testGermanLanguage() {
        ResourceBundle i18n = ResourceBundle.getBundle("Tagger", Locale.GERMAN);
        assertEquals(ConfigStore.ITEM_LENGTH,
                i18n.getString("configTable.tableHeaders").split(",").length);
        assertEquals("Konfigurations-Datei 'xxx' nicht gefunden.",
                String.format(i18n.getString("configStore.fileNotFoundError"),
                        "xxx"));
    }

}
