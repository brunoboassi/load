package br.com.exemplo.dataingestion.adapters.controllers.servers;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;
import br.com.exemplo.dataingestion.domain.producer.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateLoadController {

    private final ApplicationContext applicationContext;

    private ExecutorService executorService;
    private final List<ProducerService> producerServiceList;

    @Value("${processamento.threads.producao:10}")
    private int numeroThreadsProducao;

    @PostConstruct
    public void constroiProducer()
    {
        this.executorService = Executors.newFixedThreadPool(numeroThreadsProducao);
        log.debug("Inicializando {} produtores", numeroThreadsProducao);
        for(int i=0;i<numeroThreadsProducao;i++)
        {
            producerServiceList.add(applicationContext.getBean(ProducerService.class));
        }
    }

    @SneakyThrows
    public void geraEvento(int qtdConta, int qtdDias, LocalDate termino, boolean idPrevisivel, int idInicial)
    {
        AtomicInteger numeroItensThread = new AtomicInteger(qtdConta/numeroThreadsProducao);
        if(qtdConta<numeroThreadsProducao)
        {
            ProducerService producerService = producerServiceList.get(0);
            numeroItensThread.addAndGet(qtdConta);
            executorService.execute(() -> {
                log.info("Inicializando thread {} com {} registros",Thread.currentThread().getId(),numeroItensThread.get());
                createConta(idInicial, numeroItensThread.get(),producerService, qtdDias, termino, idPrevisivel);
                log.info("Finalizando Thread thread {} ",Thread.currentThread().getId(),numeroItensThread.get());
            });
        }
        else
        {
            for(int i =0; i<numeroThreadsProducao;i++)
            {
                int inicial = numeroItensThread.get()*i;
                ProducerService producerService = producerServiceList.get(i);
                if(i==numeroThreadsProducao-1)
                {
                    numeroItensThread.addAndGet(qtdConta%numeroThreadsProducao);
                }
                executorService.execute(() -> {
                    log.info("Inicializando thread {} com {} registros",Thread.currentThread().getId(),numeroItensThread.get());
                    createConta(inicial, numeroItensThread.get(),producerService, qtdDias, termino, idPrevisivel);
                    log.info("Finalizando Thread thread {} ",Thread.currentThread().getId(),numeroItensThread.get());
                });
            }
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

    }
    private UUID getIdConta(int numeroConta)
    {
        return UUID.nameUUIDFromBytes(StringUtils.leftPad(String.valueOf(numeroConta),12,'0').getBytes());
    }
    private void createConta(int inicial, int quantidadeContas, ProducerService producerService, int qtdDias, LocalDate termino, boolean idPrevisivel)
    {
        for (int j = inicial;j<(inicial+quantidadeContas);j++)
        {
            producerService.produce(
                LoadEntity
                    .builder()
                    .idConta(
                        idPrevisivel? 
                        getIdConta(j):
                        UUID.randomUUID()
                    )
                    .quantidadeDias(qtdDias)
                    .dataFim(termino)
                    .build());
        }
    }
}