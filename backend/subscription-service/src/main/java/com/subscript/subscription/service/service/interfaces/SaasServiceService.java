package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.SaasService;
import com.subscript.subscription.service.repository.SaasServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface SaasServiceService {
    List<SaasService> getAllServices();
    SaasService getServiceById(Integer id);
    SaasService createService(SaasService service);
    SaasService updateService(Integer id, SaasService updatedService);
    void deleteService(Integer id);
}
