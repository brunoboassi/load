package br.com.exemplo.dataingestion.domain.producer;

import br.com.exemplo.dataingestion.adapters.events.entities.LoadEntity;

public interface ProducerService {
    public void produce(LoadEntity loadEntity);
}
