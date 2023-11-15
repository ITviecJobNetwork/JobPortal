package com.fashion.servlet.admin;

import com.fashion.annotation.HttpMethod;
import com.fashion.annotation.RequestObjectParam;
import com.fashion.annotation.RequestParam;
import com.fashion.constant.AppConstant;
import com.fashion.dto.base.PageContent;
import com.fashion.dto.base.Result;
import com.fashion.dto.category.CategoryDTO;
import com.fashion.dto.page.PageRequest;
import com.fashion.dto.page.PageResponse;
import com.fashion.service.CategoryService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@WebServlet(name = "adminCategoryServlet", urlPatterns = "/admin/category/*")
@Data
public class CategoryServlet extends AdminLayoutServlet {

    private static final String REDIRECT_CATEGORY_HOME = "redirect:/admin/category";

    private CategoryService categoryService;

    @HttpMethod
    public PageContent index(
            HttpServletRequest req,
            @RequestParam(value = "page", defaultVal = "1") Integer page,
            @RequestParam(value = "pageSize", defaultVal = "5") Integer pageSize,
            @RequestParam(value = "data", required = false) String codeName
    ) {
        PageRequest<String> pageRequest = new PageRequest<>();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        pageRequest.setData(codeName);
        PageResponse<CategoryDTO> pageResponse = this.categoryService.paginateCategoryList(pageRequest);
        req.setAttribute(AppConstant.PAGING_KEY, pageResponse);
        if (req.getAttribute(AppConstant.TRANSFORM_DATA_KEY) == null) {
            req.setAttribute(AppConstant.TRANSFORM_DATA_KEY, new CategoryDTO());
        }
        return PageContent.builder()
                .url("/admin/pages/category/index")
                .title("ADMIN | Quản lý danh mục")
                .build();
    }

    @HttpMethod(method = HttpMethod.Method.POST, value = "/create")
    public Result<CategoryDTO> createCategory(@RequestObjectParam @Valid CategoryDTO categoryDTO, Map<String, Object> state) {
        Result<CategoryDTO> result = this.categoryService.createCategory(categoryDTO);
        result.setSuccessUrl(REDIRECT_CATEGORY_HOME);
        result.setFailUrl(REDIRECT_CATEGORY_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        if (!result.isSuccess()) {
            state.put(AppConstant.TRANSFORM_DATA_KEY, categoryDTO);
            state.put(AppConstant.MODAL_ID_KEY, Objects.isNull(categoryDTO.getId()) ? "createCategory" : "");
        }
        return result;
    }

    @HttpMethod(method = HttpMethod.Method.GET, value = "/delete")
    public Result<String> deleteCategory(@RequestParam("code") String code, Map<String, Object> state) {
        Result<String> result = this.categoryService.deleteCategory(code);
        result.setSuccessUrl(REDIRECT_CATEGORY_HOME);
        result.setFailUrl(REDIRECT_CATEGORY_HOME);
        state.put(AppConstant.RESULT_KEY, result);
        return result;
    }
}
