package com.smart_home.manager.model;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ThresholdsFormTest {

    @Test
    void shouldStoreAndRetrieveThresholds() {
        ThresholdsForm form = new ThresholdsForm();
        assertNull(form.getThresholds());

        List<Threshold> thresholds = List.of(new Threshold());
        form.setThresholds(thresholds);

        assertSame(thresholds, form.getThresholds());
    }

    @Test
    void shouldAllowEmptyList() {
        ThresholdsForm form = new ThresholdsForm();
        form.setThresholds(List.of());
        assertTrue(form.getThresholds().isEmpty());
    }
}
