package hochang.ecommerce.service;

import hochang.ecommerce.domain.Item;
import hochang.ecommerce.dto.BoardItem;
import hochang.ecommerce.dto.BulletinItem;
import hochang.ecommerce.dto.ItemRegistrationForm;
import hochang.ecommerce.repository.ItemRepository;
import hochang.ecommerce.util.file.UploadFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    @Transactional
    public Long save(ItemRegistrationForm itemRegistrationForm, UploadFile uploadFile) throws IOException {
        Item item = toItem(itemRegistrationForm, uploadFile);
        return itemRepository.save(item).getId();
    }

    private Item toItem(ItemRegistrationForm itemRegistrationForm, UploadFile uploadFile) {
        return Item.builder()
                .name(itemRegistrationForm.getName())
                .count(itemRegistrationForm.getCount())
                .price(itemRegistrationForm.getPrice())
                .contents(itemRegistrationForm.getContents())
                .uploadFileName(uploadFile.getUploadFileName())
                .storeFileName(uploadFile.getStoreFileName())
                .build();
    }

}
