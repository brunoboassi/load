package br.com.exemplo.dataingestion;

import br.com.exemplo.dataingestion.adapters.controllers.servers.GenerateLoadController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class LoadApplication implements CommandLineRunner {

	@Value("${contas.total:1000}")
	private int contas;

	@Value("${dias.total:90}")
	private int dias;

	private final GenerateLoadController generateLoadController;

	public static void main(String[] args) {
		SpringApplication.run(LoadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Iniciando a produção de {} com {} contas e com {} dias retroativos",contas,dias);
		generateLoadController.geraEvento(contas,dias);
		log.info("Liberando comando da aplicação");
	}
}
