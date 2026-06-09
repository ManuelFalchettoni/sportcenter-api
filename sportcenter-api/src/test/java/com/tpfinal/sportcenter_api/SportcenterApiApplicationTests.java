package com.tpfinal.sportcenter_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest levanta el contexto COMPLETO de Spring (todos los beans).
// Es un test de integración: si algo está mal cableado (una dependencia que no
// se puede crear, una config rota), este test falla al arrancar.
@SpringBootTest
class SportcenterApiApplicationTests {

	// Test "humo": no tiene cuerpo. Pasa si el contexto de Spring se levanta sin
	// errores; sirve como chequeo básico de que la app arranca.
	@Test
	void contextLoads() {
	}

}
