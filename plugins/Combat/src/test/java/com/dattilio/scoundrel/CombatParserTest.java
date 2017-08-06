package com.dattilio.scoundrel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static com.nhaarman.mockito_kotlin.MockitoKt.doNothing;
import static org.mockito.MockitoAnnotations.initMocks;


public class CombatParserTest {
    String string = "[Success: 95, Roll: 91] A pale white rat with crimson splotches misses you with its claws. You dodge a pale white rat with crimson splotches's attack.\n";

    private CombatParser processor;
    CombatPreProcessor mockPresenter;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        processor = new CombatParser(mockPresenter);
    }

    @Test
    public void updateEngaged() throws Exception {

        processor.processLine(string);
    }

}