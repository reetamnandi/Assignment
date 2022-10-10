package com.ennea.assignment;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.enums.CSVReaderNullFieldIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InventoryController {

    Logger logger = LoggerFactory.getLogger(InventoryController.class);

    private final InventoryItemRepository itemRepository;

    @Autowired
    public InventoryController(InventoryItemRepository repository) {
        this.itemRepository = repository;
    }

    /**
     * @param file <i><b>(REQUIRED) (MultipartFile)</b></i> The Input CSV file
     * @return A success message with a count of rows successfully inserted into the DB
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> processCSVUpload(
            @RequestParam MultipartFile file
    ) {
        logger.info("Upload API hit for " + file.getOriginalFilename() + " having a size of " + file.getSize() / 1000 + " KB");

        List<InventoryItem> itemList;
        try {
            itemList = new CsvToBeanBuilder<InventoryItem>((new InputStreamReader(file.getInputStream()))) // Convert into InputStream
                    .withType(InventoryItem.class) // Each row of CSV maps to an Inventory Item object
                    .withFieldAsNull(CSVReaderNullFieldIndicator.BOTH) // Consider the middle element to be null for the following cases : (A,,C) & (A,"",C)
                    .build()
                    .parse();

            for (InventoryItem item : itemList) {
                itemRepository.save(item); // Save the object parsed to DB
            }
        } catch (IOException e) {
            logger.error("Could not read from file " + e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read from file", e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "Successfully processed " + itemList.size() + " records for the CSV uploaded");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param supplier    <i><b>(REQUIRED) (String)</b></i> Supplier Name / ID
     * @param productName <i><b>(OPTIONAL) (String)</b></i> Product name to search with
     * @param expiryCheck <i><b>(OPTIONAL) (Boolean)</b></i> Flag to exclude expired products
     * @param page        <i><b>(OPTIONAL) (Integer)</b></i> Page Number of result, defaults to <b>0</b>
     * @param size        <i><b>(OPTIONAL) (Integer)</b></i> Page Size of result, defaults to <b>10</b>
     * @return The list of items in inventory that matches the criteria of conditions supplied
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam String supplier, // Like search on supplier name
            @RequestParam(value = "productSearch", required = false) String productName, // Like search on product name
            @RequestParam(value = "checkForExpiry", required = false, defaultValue = "false") Boolean expiryCheck, // Boolean flag to exclude expired products
            @RequestParam(defaultValue = "0") int page, // Page number
            @RequestParam(defaultValue = "10") int size // Page size
    ) {
        String requestInfo = "Searching Inventory for supplier like \"" + supplier + "\" with available stock for products ";
        requestInfo += (productName != null) ? ("and also for product name like \"" + productName + "\" ") : "";
        requestInfo += (expiryCheck) ? ("while excluding expired products ") : "";
        logger.info(requestInfo);

        List<InventoryItem> items;
        Pageable paging = PageRequest.of(page - 1, size); // Page number starts from 0, Users want to start from Page 1
        Page<InventoryItem> pageRes;

        Specification<InventoryItem> spec = Specification.where(InventorySpecifications.checkForSupplier(supplier)).and(InventorySpecifications.isInStock());
        if (productName != null)
            spec = spec.and(InventorySpecifications.checkForProductName(productName));
        if (expiryCheck)
            spec = spec.and(InventorySpecifications.isNotExpired());

        pageRes = itemRepository.findAll(spec, paging);
        items = pageRes.getContent();

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("currentPage", pageRes.getNumber() + 1); // Page number starts from 0, Returning user-friendly page number
        response.put("currentPageSize", items.size());
        response.put("totalItems", pageRes.getTotalElements());
        response.put("totalPages", pageRes.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
