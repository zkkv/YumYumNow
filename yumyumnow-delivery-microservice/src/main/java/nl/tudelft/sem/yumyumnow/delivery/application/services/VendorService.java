package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorCustomizerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService{
    private final VendorCustomizerRepository vendorRepository;

    @Autowired
    public VendorService(VendorCustomizerRepository vendorRepository){
        this.vendorRepository = vendorRepository;
    }
}
