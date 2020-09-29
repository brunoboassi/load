package br.com.exemplo.dataingestion.adapters.controllers.servers;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;
import br.com.exemplo.dataingestion.domain.producer.ProducerService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenerateLoadController {

    private final ApplicationContext applicationContext;

    private ExecutorService executorService;
    private final List<ProducerService> producerServiceList;
    private final MeterRegistry simpleMeterRegistry;

    @Value("${processamento.threads.producao:10}")
    private int numeroThreadsProducao;

    @PostConstruct
    public void constroiProducer()
    {
        this.executorService = Executors.newFixedThreadPool(numeroThreadsProducao);
        log.debug("Inicializando produtores");
        for(int i=0;i<numeroThreadsProducao;i++)
        {
            producerServiceList.add(applicationContext.getBean(ProducerService.class));
        }
    }

    @SneakyThrows
    public void geraEvento(int qtdConta, int qtdDias)
    {
        AtomicInteger numeroItensThread = new AtomicInteger(qtdConta/numeroThreadsProducao);

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
                createConta(inicial,numeroItensThread.get(),producerService,qtdDias);
                log.info("Finalizando Thread thread {} ",Thread.currentThread().getId(),numeroItensThread.get());
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }
    private UUID getIdConta(int numeroConta)
    {
        return UUID.nameUUIDFromBytes(StringUtils.leftPad(String.valueOf(numeroConta),12,'0').getBytes());
    }
    private void createConta(int inicial, int quantidadeContas, ProducerService producerService,int qtdDias)
    {
        for (int j = inicial;j<(inicial+quantidadeContas);j++)
        {
            producerService.produce(LoadEntity.builder().idConta(getIdConta(j)).quantidadeDias(qtdDias).build());
        }
    }
}
