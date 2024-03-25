package com.mycompany.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tk.class);
        Tk tk1 = new Tk();
        tk1.setId(1L);
        Tk tk2 = new Tk();
        tk2.setId(tk1.getId());
        assertThat(tk1).isEqualTo(tk2);
        tk2.setId(2L);
        assertThat(tk1).isNotEqualTo(tk2);
        tk1.setId(null);
        assertThat(tk1).isNotEqualTo(tk2);
    }
}
