package db.dao;

import model.ArtProduct;
import network.dto.ArtProductDTO;

import java.util.ArrayList;
import java.util.List;

public class ArtProductDAO {
    private final PhysicalProductDAO physicalProductDAO;
    private final DigitalProductDAO digitalProductDAO;

    public ArtProductDAO(PhysicalProductDAO physicalProductDAO, DigitalProductDAO digitalProductDAO) {
        this.physicalProductDAO = physicalProductDAO;
        this.digitalProductDAO = digitalProductDAO;
    }

    public List<ArtProduct> getAllArtProducts() {
        List<ArtProduct> list = new ArrayList<>();
        list.addAll(physicalProductDAO.getAllPhysicalProducts());
        list.addAll(digitalProductDAO.getAllDigitalProducts());
        return list;
    }

    public ArtProduct getArtProductById(int id) {
        ArtProduct p = physicalProductDAO.getPhysicalProductById(id);
        return (p != null) ? p : digitalProductDAO.getDigitalProductById(id);
    }

    public synchronized void deleteArtProduct(int id) {
        if (physicalProductDAO.getPhysicalProductById(id) != null) {
            physicalProductDAO.deletePhysicalProduct(id);
        } else {
            digitalProductDAO.deleteDigitalProduct(id);
        }
    }

    public ArtProductDTO toDTO(ArtProduct product) {
        int artistId = (product.getArtist() != null) ? product.getArtist().getId() : 0;
        String username = (product.getArtist() != null) ? product.getArtist().getUsername() : "Unknown";

        return new ArtProductDTO(
                product.getId(),
                artistId,
                product.getName(),
                product.getDescription(),
                product.getBasePrice(),
                username
        );
    }
}
