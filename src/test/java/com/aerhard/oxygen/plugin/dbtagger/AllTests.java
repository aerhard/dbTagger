package com.aerhard.oxygen.plugin.dbtagger;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ConfigTableTest.class, i18nTest.class, JsonUtilTest.class,
        SearchDialogTest.class })
public class AllTests {

}
