package br.com.exemplo.dataingestion;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import br.com.exemplo.dataingestion.adapters.controllers.servers.GenerateLoadController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class LoadApplication implements CommandLineRunner {

	@Value("${contas.total:1000}")
	private int contas;

	@Value("${dias.total:90}")
	private int dias;

	@DateTimeFormat(iso = ISO.DATE)
	@Value("${dias.ultimo}")
	private LocalDate termino;

	@Value("${contas.id.previsiviel:false}")
	private boolean idPrevisivel;

	@Value("${contas.id.inicial:0}")
	private int idInicial;

	private final GenerateLoadController generateLoadController;

	public static void main(String[] args) {
		SpringApplication.run(LoadApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Iniciando a produção de {} contas com {} dias retroativos à partir de {}", contas, dias, termino);
		generateLoadController.geraEvento(contas, dias, termino, idPrevisivel, idInicial);
		log.info("Liberando comando da aplicação");
	}
}
