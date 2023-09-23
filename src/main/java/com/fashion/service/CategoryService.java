package com.fashion.service;

import com.fashion.dao.CategoryDao;
import com.fashion.dao.ProductDao;
import com.fashion.dto.base.Result;
import com.fashion.dto.category.CategoryDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.entity.Category;
import com.fashion.utils.ObjectUtil;
import lombok.Data;
import org.hibernate.Session;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class CategoryService extends BaseService {

    private CategoryDao categoryDao;
    private ProductDao productDao;

    public List<CategoryDTO> findAll() {
        return this.doSelect(session -> this.categoryDao.findAll(session)
                    .stream()
                    .map(entity -> ObjectUtil.map(entity, CategoryDTO.class))
                    .collect(Collectors.toList())
        );
    }

    public List<CategoryDTO> findActiveCategory() {
        return this.doSelect(session -> this.categoryDao.findActiveCategory(session)
                    .stream()
                    .map(entity -> ObjectUtil.map(entity, CategoryDTO.class))
                    .collect(Collectors.toList())
        );
    }

    public PageResponse<CategoryDTO> paginateCategoryList(PageRequest<String> pageRequest) {
        return this.doSelect(session -> this.categoryDao.searchCategory(pageRequest, session));
    }

    public Category findByCode(String code) {
        return this.doSelect(session -> this.findByCode(code, session));
    }

    public Category findByCode(String code, Session session) {
        return this.categoryDao.findByCode(code, session)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + code));
    }

    public Result<CategoryDTO> createCategory(CategoryDTO categoryDTO) {
        return this.tryCatchWithTransaction(session -> {
            Category category = new Category();

            // handle create new category
            if (Objects.isNull(categoryDTO.getId())) {
                this.checkThrowIfPresent(categoryDTO.getCode(), session);
                categoryDTO.setActive(true);
                category.setName(categoryDTO.getName());
                category.setCode(categoryDTO.getCode());
            }

            // handle update existed category
            if (Objects.nonNull(categoryDTO.getId())) {
                category = this.categoryDao.findById(categoryDTO.getId(), session).orElseThrow();
                category.setName(categoryDTO.getName());
            }
            this.categoryDao.save(category, session);
            return Result.<CategoryDTO>builder()
                    .isSuccess(true)
                    .data(categoryDTO)
                    .message((Objects.isNull(categoryDTO.getId()) ? "Thêm mới" : "Cập nhật") + " danh mục thành công")
                    .build();
        }, categoryDTO);
    }

    private void checkThrowIfPresent(String categoryCode, Session session) {
        try {
            this.findByCode(categoryCode, session);
        } catch (IllegalArgumentException ignore) {
            return;
        }
        throw new IllegalArgumentException(categoryCode + " đã tồn tại");
    }


    public Result<String> deleteCategory(String code) {
       return this.tryCatchWithTransaction(session -> {
           Category byCode = this.findByCode(code, session);
           byCode.setActive(!byCode.isActive());
           this.categoryDao.save(byCode, session);
           this.productDao.updateStatusProductByCategoryId(byCode.isActive(), byCode.getId(), session);
           return Result.<String>builder()
                   .isSuccess(true)
                   .message((byCode.isActive() ? "Mở khóa" : "Khóa") + " danh mục thành công")
                   .build();
       }, code);
    }
}
