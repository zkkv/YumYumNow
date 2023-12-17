package nl.tudelft.sem.yumyumnow.delivery.application.services;

import nl.tudelft.sem.yumyumnow.delivery.domain.repos.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VendorService {
    private final VendorRepository vendorRepository;

    /**
     * Create a new VendorService.
     *
     * @param vendorRepository The repository to use.
     */
    @Autowired
    public VendorService(VendorRepository vendorRepository){
        this.vendorRepository = vendorRepository;
    }
}
