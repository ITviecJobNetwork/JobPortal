package com.fashion.constant;

import java.math.BigDecimal;

public class AppConstant {

    private AppConstant() {}

    public static final BigDecimal ZERO = new BigDecimal("0");
    public static final String SESSION_USER = "SESSION_USER";
    public static final int DATE_INDICATE_NEWEST = 30;
    public static final int PERCENT_INDICATE_HOT_SALE = 35;
    public static final int NUMBER_BEST_SELLER_SHOW = 8;
    public static final int NUMBER_IMAGE_SHOW_HOME = 3; // UI only shows three images
    public static final String TRANSFORM_DATA_KEY = "_data";
    public static final String UNDER_SCORE = "_";
    public static final String PAGING_KEY = "paging";
    public static final String RESULT_KEY = "result";
    public static final String APPLICATION_CONFIG_KEY = "applicationConfig";
    public static final String BEAN_CONTAINER = "beanContainer";
    public static final String TITLE_KEY = "title";
    public static final String PAGE_CONTENT_KEY = "pageContent";
    public static final String CSS_KEY = "_css";
    public static final String JS_KEY = "_js";
    public static final String PARAMETER_KEY = "_parameter";
    public static final String STATE_KEY = "_state";
    public static final String MODAL_ID_KEY = "_openModal";
    public static final String MODAL_DATA_FAIL_KEY = "_dataFail";


    public static final String CONFIG_PACKAGE = "com.fashion.config";
    public static final String DAO_PACKAGE = "com.fashion.dao";
    public static final String SERVICE_PACKAGE = "com.fashion.service";

    public static class SessionKey {
        public static final String OPT_ACTIVE_ACCOUNT_KEY = "OPT_ACTIVE_ACCOUNT_KEY";
    }
}
