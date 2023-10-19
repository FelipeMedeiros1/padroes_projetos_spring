package medeiros.felipe.service.impl;

import medeiros.felipe.model.Adress;
import medeiros.felipe.model.AdressRepository;
import medeiros.felipe.model.Client;
import medeiros.felipe.model.ClientRepository;
import medeiros.felipe.service.ClientService;
import medeiros.felipe.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementação da <b>Strategy</b> {@link ClientService}, a qual pode ser
 * injetada pelo Spring (via {@link Autowired}). Com isso, como essa classe é um
 * {@link Service}, ela será tratada como um <b>Singleton</b>.
 *
 * @author Felipe
 */
@Service
public class ClientServiceImpl implements ClientService {

    // Singleton: Injetar os componentes do Spring com @Autowired.
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AdressRepository adressRepository;
    @Autowired
    private ViaCepService viaCepService;

    // Strategy: Implementar os métodos definidos na interface.
    // Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

    @Override
    public Iterable<Client> findAll()  {
        return clientRepository.findAll();
    }

    @Override
    public Client findById(Long id) {
        Optional<Client> cliente = clientRepository.findById(id);
        return cliente.get();
    }

    @Override
    public void insert(Client client) {
        saveClientWithCep(client);

    }

    @Override
    public void update(Long id, Client client) {
        Optional<Client> clientDb = clientRepository.findById(id);
        if (clientDb.isPresent()) {
            saveClientWithCep(client);
        }

    }
    @Override
    public void remove(Long id) {
        clientRepository.deleteById(id);

    }
    private void saveClientWithCep(Client client) {
        String cep = client.getAdress().getCep();
        Adress adress = adressRepository.findById(cep).orElseGet(() -> {
            // Caso não exista, integrar com o ViaCEP e persistir o retorno.
            Adress newAdress = viaCepService.consultCep(cep);
            adressRepository.save(newAdress);
            return newAdress;
        });
        client.setAdress(adress);
        // Inserir Cliente, vinculando o Endereco (novo ou existente).
        clientRepository.save(client);
    }
}
